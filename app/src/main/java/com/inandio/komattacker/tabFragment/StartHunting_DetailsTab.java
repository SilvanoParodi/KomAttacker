package com.inandio.komattacker.tabFragment;

/**
 * Created by eme on 11/2/2015.
 */

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inandio.komattacker.AthleteImageLoadFromURLTask;
import com.inandio.komattacker.CircularImageView;
import com.inandio.komattacker.Common;
import com.inandio.komattacker.R;
import com.inandio.komattacker.entities.athlete.Athlete;
import com.inandio.komattacker.entities.segment.SegmentEffort;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StartHunting_DetailsTab extends Fragment {
    View retView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        retView = inflater.inflate(R.layout.start_hunting_details_tab, container, false);

        populateDetails(Common.targetKom, Common.komDefender);
        return retView;
    }

    private String getFormattedTime(int movingTimeSeconds){
        String time = "";
        Calendar calendar;

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, movingTimeSeconds);

        time = String.format("%s", new SimpleDateFormat("HH:mm:ss").format(calendar.getTime()));

        return  time;
    }
    private void populateDetails(SegmentEffort effort, Athlete defender) {
        String time = "";
        int movingTimeSeconds;
        String distance = "";
        String avg = "";


        TextView tv = (TextView) retView.findViewById(R.id.txtSegmentName);
        tv.setText(effort.getName());
        if (effort != null) {
            movingTimeSeconds = effort.getMoving_time();
            time = getFormattedTime(movingTimeSeconds);
        }

        tv = (TextView) retView.findViewById(R.id.targetted_distance);
        tv.setText(String.format("%.3f Km", Common.getDistanceSegment()/1000));

        tv = (TextView) retView.findViewById(R.id.targetted_time);
        tv.setText(time);

        tv = (TextView) retView.findViewById(R.id.txtName);
        tv.setText(defender.getFirstname() + " " + defender.getLastname());

        tv = (TextView) retView.findViewById(R.id.targetted_min_elevation);
        tv.setText(String.format("%d m", (int)Common.targetKom.getSegment().getElevation_low()));

        tv = (TextView) retView.findViewById(R.id.targetted_max_elevation);
        tv.setText(String.format("%d m", (int)Common.targetKom.getSegment().getElevation_high()));

        tv = (TextView) retView.findViewById(R.id.targetted_avg_level);
        tv.setText(String.format("%d %%", (int)Common.targetKom.getSegment().getAverage_grade()));

        /*  Checkpoint 1*/

        int ck = 1;
        time = getFormattedTime((int)Common.checkPoint_Time[ck] - (int)Common.checkPoint_Time[ck-1]);
        distance = String.format("0,00 - %.2f", Common.checkPoint_Distance[ck] / 1000);
        avg = String.format("%.2f", Common.checkPoint_Average[ck-1]);

        tv = (TextView) retView.findViewById(R.id.r1_c1);
        tv.setText(distance);

        tv = (TextView) retView.findViewById(R.id.r1_c2);
        tv.setText(time);

        tv = (TextView) retView.findViewById(R.id.r1_c3);
        tv.setText(avg);


        /*  Checkpoint 2*/
        ck = 2;
        time = getFormattedTime((int)Common.checkPoint_Time[ck] - (int)Common.checkPoint_Time[ck-1]);
        distance = String.format("%.2f - %.2f", Common.checkPoint_Distance[ck-1] / 1000, Common.checkPoint_Distance[ck] / 1000);
        avg = String.format("%.2f", Common.checkPoint_Average[ck-1]);

        tv = (TextView) retView.findViewById(R.id.r2_c1);
        tv.setText(distance);

        tv = (TextView) retView.findViewById(R.id.r2_c2);
        tv.setText(time);

        tv = (TextView) retView.findViewById(R.id.r2_c3);
        tv.setText(avg);

        /*  Checkpoint 3*/
        ck = 3;
        time = getFormattedTime((int)Common.checkPoint_Time[ck] - (int)Common.checkPoint_Time[ck-1]);
        distance = String.format("%.2f - %.2f",  Common.checkPoint_Distance[ck-1] / 1000, Common.checkPoint_Distance[ck] / 1000);
        avg = String.format("%.2f", Common.checkPoint_Average[ck-1]);

        tv = (TextView) retView.findViewById(R.id.r3_c1);
        tv.setText(distance);

        tv = (TextView) retView.findViewById(R.id.r3_c2);
        tv.setText(time);

        tv = (TextView) retView.findViewById(R.id.r3_c3);
        tv.setText(avg);

        /*  Checkpoint 4*/
        ck = 4;
        time = getFormattedTime((int)Common.checkPoint_Time[ck] - (int)Common.checkPoint_Time[ck-1]);
        distance = String.format("%.2f - %.2f",  Common.checkPoint_Distance[ck-1] / 1000, Common.checkPoint_Distance[ck] / 1000);
        avg = String.format("%.2f", Common.checkPoint_Average[ck-1]);

        tv = (TextView) retView.findViewById(R.id.r4_c1);
        tv.setText(distance);

        tv = (TextView) retView.findViewById(R.id.r4_c2);
        tv.setText(time);

        tv = (TextView) retView.findViewById(R.id.r4_c3);
        tv.setText(avg);

        /*  Checkpoint 5*/
        ck = 5;
        time = getFormattedTime((int)Common.checkPoint_Time[ck] - (int)Common.checkPoint_Time[ck-1]);
        distance = String.format("%.2f - %.2f", Common.checkPoint_Distance[ck-1] / 1000, Common.checkPoint_Distance[ck] / 1000);
        avg = String.format("%.2f", Common.checkPoint_Average[ck-1]);

        tv = (TextView) retView.findViewById(R.id.r5_c1);
        tv.setText(distance);

        tv = (TextView) retView.findViewById(R.id.r5_c2);
        tv.setText(time);

        tv = (TextView) retView.findViewById(R.id.r5_c3);
        tv.setText(avg);


        float generalSpeedAvg = 0f;
        generalSpeedAvg = speedAvg(effort.getSegment().getDistance(), effort.getMoving_time());
        tv = (TextView) retView.findViewById(R.id.txtAvgSpeed);
        tv.setText(String.format("%.2f Km/h", generalSpeedAvg));

        CircularImageView img = (CircularImageView) retView.findViewById(R.id.cirImageDefender);

        if (Common.mImageAvatarMap.containsKey(defender.getId())) {
            img.setImageBitmap((Bitmap) Common.mImageAvatarMap.get (defender.getId()));
        } else
            AthleteImageLoadFromURLTask.download(Common.komDefender, img, getActivity().getApplicationContext());
    }
    private float speedAvg(float meters, int seconds){
        //Km/h
        float retValue;

        retValue = meters/seconds * 3.6f;
        return  retValue;
    }
}
