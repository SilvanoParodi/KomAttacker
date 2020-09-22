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

import com.inandio.komattacker.entities.segment.LeaderBoardEntry;
import com.inandio.komattacker.entities.segment.Segment;
import com.inandio.komattacker.entities.segment.SegmentLeaderBoard;
import com.inandio.komattacker.progressDialog.SpotsDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by parodi on 30/06/2015.
 */

public class CurrentAthleteSegmentsActivity extends AppCompatActivity {

    private final String genderKey = "gender";
    private int  friendID = -1;
    private ListView lvCurrentAthleteSegments;
    private ArrayAdapter<Segment> adapter;
    private Segment selectedSegment = null;
    LinearLayout mLinlaHeaderProgress;


    private class GetCurrentAthleteSegmentsTask extends AsyncTask<Object, Object, Object> {
        Activity    mActivity;
        Boolean     bConnectionException = false;

        public GetCurrentAthleteSegmentsTask(Activity activity)
        {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            mLinlaHeaderProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... arg0) {

            try {
                Common.currentAthleteSegments = new ArrayList<Segment>(Common.strava.getCurrentStarredSegment());
            }
            catch(IOException ex)
            {
                bConnectionException = true;
                return null;
            }
            //purge climb not categorized
            Iterator<Segment> i = Common.currentAthleteSegments.iterator();
            while (i.hasNext()) {
                Segment o = i.next();
                if (o.getHazardous())
                {
                    i.remove();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object res){

            if (bConnectionException) {
                Common.ShowNoConnectionDialog(mActivity);
                return;
            }
            PopulateCurrentAthleteSegmentsListView();
        }

    }

    private class GetKomTask extends AsyncTask<Object, Object, Object> {
        Activity mActivity;

        public GetKomTask(Activity activity)
        {
            mActivity = activity;
        }
        @Override
        protected Object doInBackground(Object... arg0) {
            if (selectedSegment == null)
                return false;

            HashMap<String, String> parametersMap = new HashMap<String, String>();
            if (Settings.compareWithAthletesSameGender) {
                parametersMap.put(genderKey, Common.authenticatedAthlete.getSex());
            }
            SegmentLeaderBoard leaderBoard = Common.strava.findSegmentLeaderBoard(selectedSegment.getId(), parametersMap);
            if (leaderBoard!= null && leaderBoard.getEntries() != null && leaderBoard.getEntries().size() > 0) {
                LeaderBoardEntry entry = leaderBoard.getEntries().get(0);
                try {

                    Common.targetKom = Common.strava.findSegmentEffort(entry.getEffort_id());
                    Common.komDefender = Common.strava.findAthlete(entry.getAthlete_id());
                    Common.targetPersonalRecord = null;
                    //search for current athlete himself, if he is in the rank, I offer the chance to win against himself
                    for (int i = 1; i < leaderBoard.getEntries().size(); i++) {
                        entry = leaderBoard.getEntries().get(i);
                        if (entry.getAthlete_id() == Common.authenticatedAthlete.getId()) {
                            Common.targetPersonalRecord = Common.strava.findSegmentEffort(entry.getEffort_id());
                            break;
                        }
                    }
                }
                catch(Exception ex)
                {
                   return false;
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Object res){
           if ((Boolean)res)
           {
               new GetStreamingKomEffortTaskAndStartHuntingActivity(mActivity).execute();
           }
           else
           {
               Common.ShowNoConnectionDialog(mActivity);
           }
        }

    }

    private void PopulateCurrentAthleteSegmentsListView() {
        lvCurrentAthleteSegments = (ListView) findViewById(R.id.list_view_current_athlete_segments);
        // Adding items to listview
        lvCurrentAthleteSegments.setEmptyView(findViewById(R.id.emptyStarred));

        adapter = new SegmentAdapterArray(getApplicationContext(), Common.currentAthleteSegments);
        lvCurrentAthleteSegments.setAdapter(adapter);

        final Activity  outerActivity = this;
        // HIDE THE SPINNER AFTER LOADING FEEDS
        mLinlaHeaderProgress.setVisibility(View.GONE);
        lvCurrentAthleteSegments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Common.loadingDialog = new SpotsDialog(outerActivity);
                Common.loadingDialog.show();
                // In the following line "v" refers to the View returned by the `getView()` method; meaning the clicked View.
                selectedSegment = (Segment)lvCurrentAthleteSegments.getItemAtPosition(position);
                new GetKomTask(outerActivity).execute();
            }
        });
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCurrentAthleteSegments);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.starred);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentathletesegments);
        setupToolbar();
        mLinlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgressSegment);


        if (Common.currentAthleteSegments != null)
        {
            PopulateCurrentAthleteSegmentsListView();
        }
        else
        {
            new GetCurrentAthleteSegmentsTask(this).execute();
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
