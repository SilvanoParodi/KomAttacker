package com.inandio.komattacker;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;



public class SelectFilterTypeActivity extends AppCompatActivity {

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.startingActivityTitle);
    }


    private void displayLastAttempts() {

        Common.attempts =  AttemptPersister.retrieveFromSharedPreferences(getApplicationContext(), Common.authenticatedAthlete.getId());
        LinearLayout ll_noAttempts = (LinearLayout) findViewById(R.id.l_Layout_noAttempt);

        ListView listview = (ListView) findViewById(R.id.listViewAttempts);
        listview.setAdapter(new AttemptsAdapter(this, Common.attempts));

        if(listview.getCount() == 0) {

            setAttemptsVisible(ll_noAttempts, listview, false);
        }
        else
        {
            setAttemptsVisible(ll_noAttempts, listview, true);
        }
        //listview.setEmptyView(findViewById(R.id.EmptyAttempts));
    }

    private void setAttemptsVisible(LinearLayout ll_noAttempts, ListView listview, Boolean visible) {
        if(visible){
            ll_noAttempts.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
        }

        else {
            ll_noAttempts.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_selectfiltertype);

        displayLastAttempts();

        Settings.readSettings(this);

      final  FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
      frameLayout.getBackground().setAlpha(0);

    final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
    fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
        @Override
        public void onMenuExpanded() {
            frameLayout.getBackground().setAlpha(240);
            frameLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    fabMenu.collapse();
                    return true;
                }
            });
        }
        @Override
        public void onMenuCollapsed() {
            frameLayout.getBackground().setAlpha(0);
            frameLayout.setOnTouchListener(null);
        }
    });

        com.getbase.floatingactionbutton.FloatingActionButton fb = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_starred);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CurrentAthleteSegmentsActivity.class);
                startActivity(i);
            }
        });
            fb = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_friends);
            fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StravaFriendsActivity.class);
                startActivity(i);
            }
        });

        fb = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_maps);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SegmentMapExplorerActivity.class);
                startActivity(i);
            }
        });
        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Common.loadingDialog != null)
        {
            if (Common.loadingDialog.isShowing()) {
                //get the Context object that was used to great the dialog
                Context context = ((ContextWrapper)Common.loadingDialog.getContext()).getBaseContext();

                //if the Context used here was an activity AND it hasn't been finished or destroyed
                //then dismiss it
                if(context instanceof Activity) {
                    if(!((Activity)context).isFinishing() && !((Activity)context).isDestroyed())
                        Common.loadingDialog.dismiss();
                } else //if the Context used wasnt an Activity, then dismiss it too
                    Common.loadingDialog.dismiss();
            }
            Common.loadingDialog = null;
        }
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
