package com.inandio.komattacker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Enrica on 19/10/15.
 */

public class LogOutTask extends AsyncTask<Object, Object, Object> {

    Context mContext = null;

    public LogOutTask(Context context){
        mContext = context;
    }
    @Override
    protected Object doInBackground(Object... arg0) {
        LogOutTask.deleteCache(mContext);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        Common.resetAll();
        //reset webview to clean state
        CookieSyncManager.createInstance(mContext);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        return true;
    }

    @Override
    protected void onPostExecute(Object res){
        if ((boolean)res) {
            Intent i = new Intent(mContext, LoginActivity.class);
            //clear history to avoid go back by android button
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        }
        else {
            Toast.makeText(mContext,
                    mContext.getString(R.string.toastMessage_Logoutissue),
                    Toast.LENGTH_LONG).show();
        }
    }

    public static void deleteCache(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile())
            return dir.delete();
        else {
            return false;
        }
    }

}