package com.inandio.komattacker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * Created by Enrica on 14/11/15.
 */

public class GetStreamingKomEffortTaskAndStartHuntingActivity extends AsyncTask<Object, Object, Object> {

        Context mContext;
        Boolean bConnectionException = false;

        public GetStreamingKomEffortTaskAndStartHuntingActivity(Context context)
        {
            mContext = context;
        }

        @Override
        protected Object doInBackground(Object... arg0) {

            //IMPORTANT DATA, stream used by service to do the comparison
            /*
            - time:  integer seconds
            * - latlng:  floats [latitude, longitude]
            * - distance:  float meters
            * - altitude:  float meters
            * - velocity_smooth:  float meters per second
            * - heartrate:  integer BPM
            * - cadence:  integer RPM
            * - watts:  integer watts
            * - temp:  integer degrees Celsius
                    * - moving:  boolean
            * - grade_smooth:  float percent
           */
            //EffortStream for the Kom selected
            try {
                Common.effortStreams = Common.strava.findEffortStreams(Common.targetKom.getId(), new String[]{"latlng", "distance", "altitude", "time"}, "high", "distance");
            }
            catch(Exception ex)
            {
                bConnectionException = true;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Object res) {
            if (bConnectionException) {
                Common.ShowNoConnectionDialog((Activity)mContext);
                return;
            }
            Intent i = new Intent(mContext, StartHuntingActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            Common.loadingDialog.dismiss();
    }
}