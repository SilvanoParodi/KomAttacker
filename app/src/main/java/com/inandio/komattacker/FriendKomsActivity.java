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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.inandio.komattacker.entities.segment.Segment;
import com.inandio.komattacker.entities.segment.SegmentEffort;
import com.inandio.komattacker.progressDialog.SpotsDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by parodi on 30/06/2015.
 */

public class FriendKomsActivity extends AppCompatActivity {

    private int  friendID = -1;
    private ListView lvKomsOfFriends;
    private ArrayAdapter<Segment> adapter;
    LinearLayout mLinlaHeaderProgress;


    private class GetFriendKomTask extends AsyncTask<Object, Object, Object> {
        Activity mActivity;
        Boolean bConnectionException = false;

        public GetFriendKomTask (Activity activity)
        {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            mLinlaHeaderProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... arg0) {

            ArrayList<Segment> segmentsRelatedToKoms = new ArrayList<Segment>();
            ArrayList<SegmentEffort> komsList = null;
            try {
                komsList = new ArrayList<SegmentEffort>(Common.strava.findAthleteKOMs(friendID, 1, 200));
            }
            catch(Exception ex)
            {
                bConnectionException = true;
                return null;
            }
            //purge climb not categorized
            Iterator<SegmentEffort> i = komsList.iterator();
            while (i.hasNext()) {
                SegmentEffort o = i.next();
                if (o.getSegment().getClimb_category() != 0)
                {
                    segmentsRelatedToKoms.add(o.getSegment());
                }
                else
                {
                    i.remove();
                }
            }
            Common.mKomsSegmentByFriendMap.put(friendID, segmentsRelatedToKoms);
            Common.mKomsByFriendMap.put(friendID, komsList);

            return friendID;
        }

        @Override
        protected void onPostExecute(Object res){
            if (bConnectionException) {
                Common.ShowNoConnectionDialog(mActivity);
                return;
            }
            PopulateKomListView((Integer) res);
        }

    }

    private void PopulateKomListView(final int friendID) {
        lvKomsOfFriends = (ListView) findViewById(R.id.list_view_friend_koms);
        // Adding items to listview
        lvKomsOfFriends.setEmptyView(findViewById(R.id.empty));

        adapter = new SegmentAdapterArray(this, Common.mKomsSegmentByFriendMap.get(friendID));
        lvKomsOfFriends.setAdapter(adapter);

        // HIDE THE SPINNER AFTER LOADING FEEDS
        mLinlaHeaderProgress.setVisibility(View.GONE);
        final Activity outerActivity = this;
        lvKomsOfFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Common.loadingDialog = new SpotsDialog(outerActivity);
                Common.loadingDialog.show();
                // In the following line "v" refers to the View returned by the `getView()` method; meaning the clicked View.
                Segment segment = (Segment)lvKomsOfFriends.getItemAtPosition(position);
                List<SegmentEffort> listFriendKoms = Common.mKomsByFriendMap.get(friendID);
                for (SegmentEffort segmentEffort : listFriendKoms)
                {
                    if (segmentEffort.getSegment().getId() == segment.getId()) {
                        Common.targetKom = segmentEffort;
                        break;
                    }
                }
                new GetStreamingKomEffortTaskAndStartHuntingActivity(outerActivity).execute();

            }
        });
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarKoms);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(this.getString(R.string.friendKom) + " " + Common.komDefender.getFirstname() + " " + Common.komDefender.getLastname());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_koms);

        setupToolbar();
        mLinlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgressKom);
        friendID = Common.komDefender.getId();

        if (Common.mKomsSegmentByFriendMap.containsKey(friendID))
        {
            PopulateKomListView(friendID);
        }
        else
        {
            new GetFriendKomTask(this).execute();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search2));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint(getString(R.string.searchHint_segment));
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
