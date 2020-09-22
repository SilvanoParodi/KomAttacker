package com.inandio.komattacker;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.inandio.komattacker.tabFragment.StartHuntingPageAdapter;
import com.inandio.komattacker.tabFragment.StartHunting_MapTab;

/**
 * Created by parodi on 30/06/2015.
 */

public class StartHuntingActivity extends AppCompatActivity {
    private StartHuntingPageAdapter adapter = null;
    public static FragmentManager fragmentManager;
    private TabLayout tabLayout = null;
    private TabLayout.Tab tabMain;
    private TabLayout.Tab tabMap;
    private TabLayout.Tab tabDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_hunting);

        Common.initializeStructuresForProgressControl();

        // initialising the object of the FragmentManager. Here I'm passing getSupportFragmentManager(). You can pass getFragmentManager() if you are coding for Android 3.0 or above.
        fragmentManager = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHunting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setEnabled(false);

        tabMain = tabLayout.newTab().setText(R.string.tab_startHunting_main);
        tabLayout.addTab(tabMain);

        tabMap = tabLayout.newTab().setText(R.string.tab_startHunting_map);
        tabLayout.addTab(tabMap);

        tabDetails = tabLayout.newTab().setText(R.string.tab_startHunting_details);
        tabLayout.addTab(tabDetails);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new StartHuntingPageAdapter
                (fragmentManager, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));



        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int nPos = tab.getPosition();
                viewPager.setCurrentItem(nPos);
                if (nPos == 1)
                {
                    StartHunting_MapTab fragmentMap = adapter.getFragmentMap();
                    if (fragmentMap != null)
                    {
                       fragmentMap.setUpMapIfNeeded();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    public void showDropDown(View v) {
        adapter.getFragmentStartHunting().showDropDown(v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        if (id == android.R.id.home) {
            finish();
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
