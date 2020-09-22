package com.inandio.komattacker;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.inandio.komattacker.entities.segment.LeaderBoardEntry;
import com.inandio.komattacker.entities.segment.Segment;
import com.inandio.komattacker.entities.segment.SegmentLeaderBoard;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by parodi on 09/10/2015.
 */

public class GetKOMbySegmentIDTask extends AsyncTask<Object, Object, Object> {

    private Context context;
    private Segment segment;
    private  final String genderKey = "gender";
    Boolean bConnectionException = false;

    public GetKOMbySegmentIDTask( Context context, Segment segment) {
        this.context = context;
        this.segment = segment;
    }
    @Override
    protected Object doInBackground(Object... arg0) {
        if (segment == null)
            return false;

        HashMap<String, String> parametersMap = new HashMap<String, String>();
        if (Settings.compareWithAthletesSameGender) {
            parametersMap.put(genderKey, Common.authenticatedAthlete.getSex());
        }
        SegmentLeaderBoard leaderBoard = Common.strava.findSegmentLeaderBoard(segment.getId(), parametersMap);
        if (leaderBoard.getEntries().size() > 0) {
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
            catch(IOException ex) {
                bConnectionException = true;
                return false;
            }
            return true;
        }
        Toast.makeText(context, context.getString(R.string.toastMessage_KomBySegment), Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    protected void onPostExecute(Object res){
        if (bConnectionException) {
            Common.ShowNoConnectionDialog((Activity)context);
            return;
        }
        if ((boolean)res){
            new GetStreamingKomEffortTaskAndStartHuntingActivity(context).execute();
        }
    }

}

