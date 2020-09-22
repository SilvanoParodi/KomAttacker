package com.inandio.komattacker;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.inandio.komattacker.entities.athlete.Athlete;

import java.io.IOException;

/**
 * Created by parodi on 30/06/2015.
 */

public class StravaFriendsActivity extends AppCompatActivity {

    //ListView and search variable section
    // List view
    private ListView lv;
    // Listview Adapter
    AthleteAdapterArray adapter;
    // ArrayList for Listview

    LinearLayout  mLinlaHeaderProgress;

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarFriends);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.friends);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class GetFriendsTask extends AsyncTask<Object, Object, Object> {

        Activity mActivity;

        public GetFriendsTask(Activity activity)
        {
            mActivity = activity;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
           if (Common.authenticatedAthlete == null)
           {
               return false;
           }
            try {
                Common.listFriends = Common.strava.findAthleteFriends(Common.authenticatedAthlete.getId());
            }
            catch(IOException ex)
            {
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mLinlaHeaderProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object res) {
            if ((boolean) res)
            {
                PopulateStravaFriendsListView();
            }
            else
            {
                Common.ShowNoConnectionDialog(mActivity);
            }
        }

    }

    private void PopulateStravaFriendsListView() {
        lv = (ListView) findViewById(R.id.list_view);

        // Adding items to listview
        lv.setEmptyView(findViewById(R.id.emptyFriend));

        adapter = new AthleteAdapterArray(getApplicationContext(), Common.listFriends);
        lv.setAdapter(adapter);
        // HIDE THE SPINNER AFTER LOADING FEEDS
        mLinlaHeaderProgress.setVisibility(View.GONE);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                // In the following line "v" refers to the View returned by the `getView()` method; meaning the clicked View.
                Common.komDefender = (Athlete) lv.getItemAtPosition(position);
                Intent intent = new Intent(StravaFriendsActivity.this, FriendKomsActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stravafriends);
        setupToolbar();

        Common.cleanSelectedTarget();

        mLinlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        if (Common.listFriends == null)
        {
            new GetFriendsTask(this).execute();
        }
        else
        {
            PopulateStravaFriendsListView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search2));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint(getString(R.string.hintSearch_EnterName));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                // this is your adapter that will be filtered
                adapter.getFilter().filter(newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                adapter.getFilter().filter(query);
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), UserSettingActivity.class);
            startActivityForResult(i, Common.RESULT_SETTINGS);
            Settings.readSettings(getApplicationContext());
            return true;
        }

        if (id == R.id.action_account) {
            Intent i = new Intent(getApplicationContext(), AccountActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.action_about) {
            Intent i = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Common.RESULT_SETTINGS:
                Settings.readSettings(this);
                break;

        }
    }
}
