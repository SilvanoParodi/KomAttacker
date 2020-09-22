package com.inandio.komattacker;

/**
 * Created by parodi on 22/06/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.inandio.komattacker.authenticator.AuthResponse;
import com.inandio.komattacker.authenticator.StravaAuthenticator;
import com.inandio.komattacker.connector.JStravaV3;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;

    /*    import com.ctctlabs.ctctwsjavalib.CTCTConnection;
        import com.ctctlabs.ctctwsjavalib.Contact;
        import com.ctctlabs.ctctwsjavalib.ModelObject;*/

public class LoginActivity extends Activity {

    private static final String TAG				= LoginActivity.class.getSimpleName();
    private static final String AUTHORIZE_PATH	= "https://www.strava.com/oauth/authorize";
    private static final String CLIENT_ID		= "6638"; // ** enter your own Constant Contact developer API Key here **
    private static final String REDIRECT_URI	= "http://www.inandio.com/KomAttacker";

    private WebView webview;
    private String accessToken;
    private String userName;


    //=========================================================================
    private class PingUseToServerTask extends AsyncTask<Object, Object, Object> {

        public PingUseToServerTask() {
        }
        @Override
        protected Object doInBackground(Object... arg0) {
            //Add data to be send.
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
            nameValuePairs.add(new BasicNameValuePair("athleteId", Integer.toString(Common.authenticatedAthlete.getId())));
            nameValuePairs.add(new BasicNameValuePair("FirstName",Common.authenticatedAthlete.getFirstname()));
            nameValuePairs.add(new BasicNameValuePair("LastName",Common.authenticatedAthlete.getLastname()));

            // 1) Connect via HTTP. 2) Encode data. 3) Send data.
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://www.inandio.com/KomAttackerPing.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                Log.i("postData", response.getStatusLine().toString());
                //Could do something better with response.
            }
            catch(Exception e)
            {
                Log.e("log_tag", "Error:  "+e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object res){
        }

    }

    private class AskToStravaTask extends AsyncTask<Object, Object, Object> {
        Activity mCallingActivity = null;
        Boolean  bConnectionException = false;

        public AskToStravaTask(Activity activity)
        {
            mCallingActivity = activity;
        }
        @Override
        protected Object doInBackground(Object... arg0) {
            Common.stravaAuthenticator = new StravaAuthenticator(6638, "http://www.inandio.com/komattacker", "741d2f89bfeba6c35703af3158ca8f34bac363a4");

            if(Common.stravaAuthenticator == null)
            {
                bConnectionException = true;
                return false;
            }
            AuthResponse authResponse = Common.stravaAuthenticator.getToken(Common.accessToken);
            if(authResponse == null)
            {
                bConnectionException = true;
                return false;
            }
            String userToken = authResponse.getAccess_token();
            try {
                Common.strava = new JStravaV3(userToken);
                Common.authenticatedAthlete = Common.strava.getCurrentAthlete();
            }
            catch(IOException ex)
            {
                bConnectionException = true;
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Object res){
            if (bConnectionException) {
                Common.ShowNoConnectionDialog(mCallingActivity);
                return;
            }
            // The results of the above method
            // Processing the results here
            if ((boolean)res && Common.strava != null) {
                //Ping al server per registrare utilizzo
                new PingUseToServerTask().execute();
                launchMainActivity();
            }
        }

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        webview = (WebView) findViewById(R.id.item_webview);


        // check whether access token already saved
        accessToken = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Common.SHPREF_KEY_ACCESS_TOKEN, null);
        if (accessToken == null || accessToken.isEmpty()) {

            if (!GenericHelper.isOnline(getApplicationContext()))
            {
                try {
                    Common.ShowNoConnectionDialog(this);
                    return;
                }
                catch(Exception e)
                {
                    Log.d("dialogFailed" , "Show Dialog: "+e.getMessage());
                }
            }
           final Activity localLoginActivity = this;
            // need to get access token with OAuth2.0
            webview.setVisibility(View.VISIBLE);
            // set up webview for OAuth2 login
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //Log.d(TAG, "** in shouldOverrideUrlLoading(), url is: " + url);
                    if ( url.startsWith(REDIRECT_URI) ) {

                        // extract OAuth2 access_token appended in url
                        if ( url.indexOf("code=") != -1 ) {
                            accessToken = mExtractToken(url);

                            // store in default SharedPreferences
                            Editor e = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                            e.putString(Common.SHPREF_KEY_ACCESS_TOKEN, accessToken);
                            e.commit();

                            Common.accessToken = accessToken;
                            new AskToStravaTask(localLoginActivity).execute();
                        }

                        // don't go to redirectUri
                        return true;
                    }

                    // load the webpage from url (login and grant access)
                    return super.shouldOverrideUrlLoading(view, url); // return false;
                }
            });

            // do OAuth2 login
            String authorizationUri = mReturnAuthorizationRequestUri();
            webview.loadUrl("https://www.strava.com/oauth/authorize?client_id="+CLIENT_ID+"&response_type=code&redirect_uri="+REDIRECT_URI+"&approval_prompt=force");

        } else {
            Common.accessToken = accessToken;
            new AskToStravaTask(this).execute();
        }
    }




    private void launchMainActivity() {
        Intent intent = new Intent(getApplicationContext(), SelectFilterTypeActivity.class);
        startActivity(intent);
    }

    private String mExtractToken(String url) {
        // url has format https://localhost/#access_token=<tokenstring>&token_type=Bearer&expires_in=315359999
        String[] sArray = url.split("code=");
        return (sArray[1].split("&token_type=Bearer"))[0];
    }

    private void mShowContacts() {
        webview.setVisibility(View.GONE);

     /*   ItemListFragment fragment = ((ItemListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.item_list));
        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
            fragment.setActivateOnItemClick(true);
        }

        fragment.doSetListAdapter();*/

    }

    private String mReturnAuthorizationRequestUri() {
        StringBuilder sb = new StringBuilder();
        sb.append(AUTHORIZE_PATH);
        sb.append("?response_type=token");
        sb.append("&client_id="+CLIENT_ID);
        sb.append("&redirect_uri="+REDIRECT_URI);
        return sb.toString();
    }



}
