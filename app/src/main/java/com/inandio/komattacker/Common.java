package com.inandio.komattacker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.inandio.komattacker.TextToSpeech.TtsProviderFactory;
import com.inandio.komattacker.authenticator.StravaAuthenticator;
import com.inandio.komattacker.connector.JStravaV3;
import com.inandio.komattacker.entities.athlete.Athlete;
import com.inandio.komattacker.entities.segment.Segment;
import com.inandio.komattacker.entities.segment.SegmentEffort;
import com.inandio.komattacker.entities.stream.Stream;
import com.inandio.komattacker.progressDialog.SpotsDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by parodi on 30/06/2015.
 */
public final class Common {

    public static final String SHPREF_KEY_ACCESS_TOKEN = "Access_Token";
    public static final int RESULT_SETTINGS = 1;

    public static ArrayList<Attempt> attempts = new ArrayList<Attempt>();

    public static String accessToken = "";
    public static JStravaV3 strava = null;
    public static StravaAuthenticator stravaAuthenticator = null;
    public static Athlete authenticatedAthlete = null;

    public static SegmentEffort targetKom = null; //should become an array, if i'm allowed to target more segment
    public static SegmentEffort targetPersonalRecord = null;
    public static Athlete komDefender = null;
    public static List<Stream> effortStreams = null;

    //Assistente vocale
    public static TtsProviderFactory ttsProviderImpl = null;

    //Loading dialog da usare nel cambio tra activity
    public static SpotsDialog loadingDialog = null;


    //Dati per intermedi e progress control
    public static int numberOfChecks = 5;
    public static double[] checkPoint_Distance = new double[numberOfChecks + 1]; // used as input
    public static double[] checkPoint_Time = new double[numberOfChecks + 1];     // output
    public static double[] checkPoint_Average = new double[numberOfChecks];     // output
    public static Location[] checkPoint_Location = new Location[numberOfChecks + 1];     // output
    public static double[] _tableTimes = null;
    public static double[] _tableDistance = null;


    //cached data, fo the entire lifecycle of app, not persisted yet
    public static Map<Integer, Bitmap> mImageAvatarMap = new HashMap<Integer, Bitmap>();
    public static List<Athlete> listFriends = null;
    public static Map<Integer, List<Segment>> mKomsSegmentByFriendMap = new HashMap<Integer, List<Segment>>();
    public static Map<Integer, List<SegmentEffort>> mKomsByFriendMap = new HashMap<Integer, List<SegmentEffort>>();
    public static ArrayList<Segment> currentAthleteSegments = null;
    public static int _streamSize = 0;

    //Colori per marker Mappa
    public static Float hueColor_Start = 165f;
    public static Float hueColor_End = 339f;

    public static float getDistanceSegment()
    {
        return Common.targetKom.getSegment().getDistance();
    }


    public static void initializeVocalAssistant(Context context) {
        ttsProviderImpl = TtsProviderFactory.getInstance();
        if (ttsProviderImpl != null) {
            ttsProviderImpl.init(context);
        }
    }

    public static void ShowNoConnectionDialog(final Activity activity) {

        // se activity si sta distruggendo, la dialog crasha. sostituisco ocn un piu leggero toast
        if (activity.isFinishing())
        {
            Toast.makeText(activity,R.string.loginDialog_InternetIssue, Toast.LENGTH_SHORT );
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.NoConnectionAlertDialogStyle);
        builder.setTitle(activity.getString(R.string.loginDialog_Title));
        builder.setMessage(activity.getString(R.string.loginDialog_InternetIssue));
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton(activity.getString(R.string.dialog_OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.show();
    }


    public static void initializeStructuresForProgressControl()
    {
        if (Common.effortStreams == null)
            return;

        Stream streamTime = Common.effortStreams.get(1);  // 1 is Time
        Stream streamDistance = Common.effortStreams.get(2); // 2 is Distance
        // copio i dati di time e distance in 2 array separati
        _streamSize = streamTime.getOriginal_size();

        _tableTimes = new double[_streamSize];
        _tableDistance = new double[_streamSize];

        for(int i = 0; i < _streamSize; i++) {
            _tableTimes[i] = (double) streamTime.getData().get(i);
            _tableDistance[i] = (double) streamDistance.getData().get(i);
        }

        // normalizzo i 2 array in base al tempo e distanza iniziale
        double startTime = _tableTimes[0];
        double startDistance = _tableDistance[0];
        for(int i = 0; i < _streamSize; i++) {
            _tableTimes[i] = _tableTimes[i] - startTime;
            _tableDistance[i] = _tableDistance[i] - startDistance;
        }

        //calcolo indici per passare dagli 'n' punti di stream size ai 'numberOfChecks' del controllo

        //0
        checkPoint_Distance[0] = _tableDistance[0];
        checkPoint_Time[0] = _tableTimes[0];

        //intermediate
        for(int i = 1; i < Common.numberOfChecks; i++) {
            int index = (_streamSize / 5) * i;
            checkPoint_Distance[i] = _tableDistance[index];
            checkPoint_Time[i] = _tableTimes[index];
        }

        //last
        checkPoint_Distance[numberOfChecks] = _tableDistance[_streamSize-1];
        checkPoint_Time[numberOfChecks] = _tableTimes[_streamSize-1];



        // compute average for each checkpoint for test
        // and update the control

        for(int i = 0; i < Common.numberOfChecks; i++)
        {
            double distance2 = Common.checkPoint_Distance[i+1];
            double distance1 = Common.checkPoint_Distance[i];

            double time2 = Common.checkPoint_Time[i+1];
            double time1 = Common.checkPoint_Time[i];

            double average = (distance2-distance1)/(time2-time1);
            Common.checkPoint_Average[i] = average * 3.6f; // in Km/h
        }

        // carico array checkPoint_Location per avere info su location dei checkpoint
        // per avere info su mantenimento direzione corretta
        Stream streamLatLng = Common.effortStreams.get(0); // 0 is LatLong
        int c = 0;
        for(int i = 0; i < _streamSize; i++)
        {
            if ( c < checkPoint_Distance.length && _tableDistance[i] >= checkPoint_Distance[c] )
            {
                ArrayList point = (ArrayList) streamLatLng.getData().get(i);
                Location loc = new Location("Check_" + String.valueOf(i));
                loc.setLatitude((double)point.get(0));
                loc.setLongitude((double)point.get(1));
                //LatLng loc = new LatLng((double)point.get(0), (double)point.get(1));
                checkPoint_Location[c] = loc;
                c++;
            }
        }

    }

    public static void cleanSelectedTarget()
    {
        targetKom = null;
        targetPersonalRecord = null;
        komDefender = null;
    }

    public static void resetAll()
    {
        accessToken = "";
        strava = null;
        stravaAuthenticator = null;
        Athlete authenticatedAthlete = null;

        targetKom = null; //should become an array, if i'm allowed to target more segment
        targetPersonalRecord = null;
        komDefender = null;
        currentAthleteSegments = null;

        mImageAvatarMap = new HashMap<Integer, Bitmap>();;
        listFriends=null;

        mKomsSegmentByFriendMap = new HashMap<Integer, List<Segment>> ();
        mKomsByFriendMap = new HashMap<Integer, List<SegmentEffort>> ();

        currentAthleteSegments = null;
    }

}
