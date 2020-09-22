package com.inandio.komattacker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Enrica on 19/10/15.
 */
public class AccountActivity extends AppCompatActivity {
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAccount);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.action_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account);
        setupToolbar();

        ImageView avatar = (ImageView) findViewById(R.id.accountImageView);
        if (Common.mImageAvatarMap.containsKey(Common.authenticatedAthlete.getId())) {
            avatar.setImageBitmap((Bitmap) Common.mImageAvatarMap.get(Common.authenticatedAthlete.getId()));
        } else
            AthleteImageLoadFromURLTask.download(Common.authenticatedAthlete, avatar, this);


        TextView name = (TextView) findViewById(R.id.tvUserName);
        name.setText(Common.authenticatedAthlete.getFirstname() + " " + Common.authenticatedAthlete.getLastname());
        TextView email = (TextView) findViewById(R.id.tvUserMail);
        email.setText(Common.authenticatedAthlete.getEmail());

        final Activity outerActivity = this;
        Button button = (Button) findViewById(R.id.btnLogout);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new LogOutTask(outerActivity).execute();
            }
        });
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
