package com.inandio.komattacker.tabFragment;

/**
 * Created by eme on 11/1/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.inandio.komattacker.AthleteImageLoadFromURLTask;
import com.inandio.komattacker.Common;
import com.inandio.komattacker.HuntingActivity;
import com.inandio.komattacker.R;
import com.inandio.komattacker.entities.athlete.Athlete;
import com.inandio.komattacker.entities.segment.SegmentEffort;
import com.inandio.komattacker.progressDialog.SpotsDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StartHunting_MainTab extends Fragment {
    Athlete[] player1;
    Athlete[] player2;
    View retView;
    int numberRow;

    Spinner mySpinner;

    public void showDropDown(View view) {
               mySpinner.performClick();
           }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        retView = inflater.inflate(R.layout.start_hunting_main_tab, container, false);

        LinearLayout lnMyBestTime = (LinearLayout) retView.findViewById(R.id.my_best_layout);

        numberRow = 1;
        if (Common.targetPersonalRecord != null) {
            lnMyBestTime.setVisibility(View.VISIBLE);
            numberRow = 2;
        }
        player1 = new Athlete[numberRow];
        player2 = new Athlete[numberRow];


        player1[0] = Common.authenticatedAthlete;
        player2[0] = Common.komDefender;


        if (Common.targetPersonalRecord != null) {
            player1[1] = Common.authenticatedAthlete;
            player2[1] = Common.authenticatedAthlete;
        }

        mySpinner = (Spinner) retView.findViewById(R.id.spinnerChallenge);
        mySpinner.setAdapter(new MyAdapter(getActivity().getApplicationContext(), R.layout.challenge_spinner_item, player1));
        if(numberRow == 1)
        {
            disableSpinner();
        }
        else {enableSpinner();}
        //mySpinner.setOnItemSelectedListener( new onItemSelected());

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                LinearLayout bt = (LinearLayout) retView.findViewById(R.id.spinnerChallengeOut);

                if (position == 1) {
                    bt.setBackgroundColor(getResources().getColor(R.color.my_primary_light));
                } else {
                    bt.setBackgroundColor(getResources().getColor(R.color.my_secondary_light));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        populateSegmentEffortDescription(Common.targetKom);


        com.getbase.floatingactionbutton.FloatingActionButton bt_startHunting = (com.getbase.floatingactionbutton.FloatingActionButton) retView.findViewById(R.id.fab);
        bt_startHunting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Common.loadingDialog = new SpotsDialog(getActivity());
                Common.loadingDialog.show();
                Intent itn = new Intent(getActivity().getApplicationContext(), HuntingActivity.class);
                startActivity(itn);
                Common.loadingDialog.dismiss();
            }
        });


        return retView;
    }
    private void disableSpinner() {
        mySpinner.setClickable(false);
        ImageView img = (ImageView) retView.findViewById(R.id.spinnerDropDown);
        img.setVisibility(View.GONE);
    }

    private void enableSpinner() {
        mySpinner.setClickable(true);
        ImageView img = (ImageView) this.retView.findViewById(R.id.spinnerDropDown);
        img.setVisibility(View.VISIBLE);
    }

    private void populateSegmentEffortDescription(SegmentEffort effort) {

        String time = "";

        if (effort != null) {
            int movingTimeSeconds = effort.getMoving_time();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, movingTimeSeconds);

            time = String.format("%s", new SimpleDateFormat("HH:mm:ss").format(calendar.getTime()));
        }
        TextView tv = (TextView) retView.findViewById(R.id.targetted_segment);
        tv.setText(effort.getName());

        tv = (TextView) retView.findViewById(R.id.targetted_distance);
        tv.setText(String.format("%.2f mt", Common.getDistanceSegment()));

        tv = (TextView) retView.findViewById(R.id.targetted_time);
        tv.setText(time);

        if (Common.targetPersonalRecord != null) {
            int movingTimeSeconds = Common.targetPersonalRecord.getMoving_time();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, movingTimeSeconds);

            time = String.format("%s", new SimpleDateFormat("HH:mm:ss").format(calendar.getTime()));
        }
        tv = (TextView) retView.findViewById(R.id.my_targetted_time);
        tv.setText(time);
    }

    public class MyAdapter extends ArrayAdapter<Athlete> {

        public MyAdapter(Context context, int textViewResourceId, Athlete[] objects) {
            super(context, textViewResourceId, objects);
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getActivity().getLayoutInflater();

            View row = inflater.inflate(R.layout.challenge_spinner_item, parent, false);


            if (position == 1) {
                LinearLayout ln = (LinearLayout) row.findViewById(R.id.challengerowBackround);

                ln.setBackgroundColor(getResources().getColor(R.color.my_primary_light));

            }

            TextView labelPlayer1 = (TextView) row.findViewById(R.id.description_player1_challenge);
            labelPlayer1.setText(player1[position].getFirstname() + " " + player1[position].getLastname());

            TextView labelPlayer2 = (TextView) row.findViewById(R.id.description_player2_Challenge);
            labelPlayer2.setText(player2[position].getFirstname() + " " + player2[position].getLastname());

            ImageView icon1 = (ImageView) row.findViewById(R.id.image_player1_challenge);

            if (Common.mImageAvatarMap.containsKey(player1[position].getId())) {
                icon1.setImageBitmap((Bitmap) Common.mImageAvatarMap.get(player1[position].getId()));
            } else
                AthleteImageLoadFromURLTask.download(Common.authenticatedAthlete, icon1, getActivity().getApplicationContext());


            ImageView icon2 = (ImageView) row.findViewById(R.id.image_player2_Challenge);
            if (Common.mImageAvatarMap.containsKey(player2[position].getId())) {
                icon2.setImageBitmap((Bitmap) Common.mImageAvatarMap.get(player2[position].getId()));
            } else
                AthleteImageLoadFromURLTask.download(Common.komDefender, icon2, getActivity().getApplicationContext());

            return row;
        }
    }
    }

