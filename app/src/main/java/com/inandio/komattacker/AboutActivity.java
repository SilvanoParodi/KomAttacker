package com.inandio.komattacker;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by Enrica on 08/01/16.
 */
public class AboutActivity extends AppCompatActivity

    {
        private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAbout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
    }

        private void displayVersionName() {
            String versionName = "";
            PackageInfo packageInfo;
            try {
                packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                versionName = "ver:" + packageInfo.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
            TextView tv = (TextView) findViewById(R.id.ver_name);
            tv.setText(versionName);
        }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        displayVersionName();
        setupToolbar();
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
}
