package com.inandio.komattacker;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inandio.komattacker.serviceclass.ServiceManager;


public class HuntingActivity extends ActionBarActivity
{
    private static final String TAG = HuntingActivity.class.getSimpleName();

    // *** Controls
    private Button _btTest1;
    private Button _btTest2;
    private Button _btTest3;
    private Button _btTest4;
    private Button _btnStopService;

    private TextView _tvDistanceToStart;


    LocationManager _locationManager = null;


    Vibrator _vibrate = null;

    private GoogleMap mMapHunting;

    private ServiceManager _service;

    public ProgressPipeControl _progressControl = null;

    private int _indexCurrentCheckPoint = 0;
    private float _distanceRace = 0.0f;
    //private int _counterCheckpoint = 0;

    //private float simulatedStepSpeed = 0.0f;

    private int counterCheckpoint = 0; //TODO: cambiare nome


    private boolean _racing = false; // fase di avvicinamento
    
    private static float _rangeEngageinMeters = 20.0f;

    LatLng _lastknownLocation = null;
    LatLng _startSegmentLocation = null;
    LatLng _endSegmentLocation = null;

    Marker  _positionMarker = null;


    private void addSegmentMarker(LatLng position, String title, String snippet, Float color)
    {
        MarkerOptions m = new MarkerOptions();
        m.position(new LatLng(position.latitude, position.longitude));
        m.title(title);
        m.snippet(snippet);
        mMapHunting.addMarker(m).setIcon(BitmapDescriptorFactory.defaultMarker(color));
    }

    private void updateTrackingPositionMarker(double lat, double lon, String title, String snippet)
    {
        if (_positionMarker != null)
        {
            _positionMarker.remove();
        }

        MarkerOptions m= new MarkerOptions();
        m.position(new LatLng(lat, lon));
        m.title(title);
        m.snippet(snippet);
        _positionMarker = mMapHunting.addMarker(m);
        _positionMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
    }


    public void stopService() {
        if (_service != null && _service.isRunning())
        {
            _service.stop();
        }
        _racing = false;
        finish();
    }

    @Override
    public void onBackPressed() {
        //Se sono sul segmento, non posso toranre indietro senza stoppare
        //(per evitare che uno per sbaglio si annulli il tentativo)
        if (_racing) {
            Toast.makeText(this, "Devi stoppare", Toast.LENGTH_SHORT).show();
        }
        return;
    }

    private void setUpMap()
    {
        if (_lastknownLocation == null)
        {
            Location loc = mMapHunting.getMyLocation();
            if (loc != null)
            {
                _lastknownLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
            }
        }
        mMapHunting.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        GenericHelper.drawSegment(getResources(), mMapHunting);
        addSegmentMarker(_startSegmentLocation, "Your target is here", "Go Go Go", Common.hueColor_Start);
        addSegmentMarker(_endSegmentLocation, "", "", Common.hueColor_End);


        GenericHelper.zoomCamera(mMapHunting, _startSegmentLocation, _endSegmentLocation, _lastknownLocation);
    }

    private void sendMessageToService(int value)
    {
        try
        {
            _service.send(Message.obtain(null, GPSService.MSG_INCREMENT, value, 0));
        }
        catch (RemoteException e)
        {
        }
    }

    private void dateAnalysis()
    {
        //String dt = (String) effortStreams.get(1).getData().toString();
        /*
        int len = effortStreams.size();
        long initialSeconds = (long) effortStreams.get(1).getData().get(0);
        long[] seconds = {};
        for(int i=0;i<len;i++) {
            seconds[i] =(long)effortStreams.get(1).getData().get(i) - initialSeconds;
        }
        */
    }

    // cerco la distanza fatta dal kom a un certo intervallo di tempo
    private double getDistanceKomAtTime(int time)
    {
        int indexTimeFound = -1;
        double distanceKom = 0.0f;

        for(int i = 0; i < Common._streamSize; i++)
        {
            if (Common._tableTimes[i] >= Double.valueOf(time))
            {
                indexTimeFound = i;
                break;
            }

            distanceKom = Common._tableDistance[i];
        }
        return distanceKom;
    }

    private void showRaceController()
    {
        // *** Update controls ***
        _progressControl.setVisibility(View.VISIBLE);


        LinearLayout llSimulation = (LinearLayout)findViewById(R.id.llSimulationRace);
        llSimulation.setVisibility(View.VISIBLE);

        LinearLayout llRace = (LinearLayout)findViewById(R.id.llRace);
        llRace.setVisibility(View.VISIBLE);


        // hide map
        SupportMapFragment mMapFragment = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapHunting));
        mMapFragment.getView().setVisibility(View.INVISIBLE);
        ShowSimulationButton(View.INVISIBLE);


        _tvDistanceToStart.setVisibility(View.INVISIBLE);
    }

    private void ShowSimulationButton(int showState) {
        // hide simulation buttons
        _btTest1.setVisibility(showState);
        _btTest2.setVisibility(showState);
        _btTest3.setVisibility(showState);
        _btTest4.setVisibility(showState);
    }

    private void showApproachController()
    {
        // *** Update controls ***
        _progressControl.setVisibility(View.INVISIBLE);

        LinearLayout llSimulation = (LinearLayout)findViewById(R.id.llSimulationRace);
        llSimulation.setVisibility(View.VISIBLE);

        LinearLayout llRace = (LinearLayout)findViewById(R.id.llRace);
        llRace.setVisibility(View.INVISIBLE);

         // show map
        SupportMapFragment mMapFragment = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapHunting));
        mMapFragment.getView().setVisibility(View.VISIBLE);

        // hide simulation buttons
        ShowSimulationButton(View.INVISIBLE);
        _tvDistanceToStart.setVisibility(View.VISIBLE);
    }

    private void initializeProgressControl()
    {
        _progressControl = (ProgressPipeControl) findViewById(R.id.progressControl);
        _progressControl.setHandTarget(0.0f);

        _progressControl.setTotalDistance(Common.getDistanceSegment());
        _progressControl.setNumberOfCheckpoints(Common.numberOfChecks);

        // update Control checkpoints
        for(int i =0; i < Common.numberOfChecks+1; i++) {
            String checkPointString = String.valueOf(Common.checkPoint_Time[i]);
            _progressControl.setCheckpointTimes(i, checkPointString);
        }

        // compute average for each checkpoint for test
        // and update the control

        for(int i = 0; i < Common.numberOfChecks; i++)
        {
            //String.valueOf(checkPoint_Average[i])
            String avg = String.format("%.02f", Common.checkPoint_Average[i]);
            _progressControl.setCheckpointDistancesExtra(i+1,avg);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunting);

        _vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (Common.targetKom == null)
        {
            return;
        }

        //initialize progress control
        initializeProgressControl();




        // Punto di partenza
        String[] targetStartLatlng = Common.targetKom.getSegment().getStart_latlng();
        double startLat = Double.valueOf(targetStartLatlng[0]);
        double startLon = Double.valueOf(targetStartLatlng[1]);
        _startSegmentLocation = new LatLng(startLat, startLon);

        // ricavo location dell' arrivo (usato per calcolo approssimativo di corretta direzione)
        String[] targetEndLatlng = Common.targetKom.getSegment().getEnd_latlng();
        double endLat = Double.valueOf(targetEndLatlng[0]);
        double endLon = Double.valueOf(targetEndLatlng[1]);
        _endSegmentLocation = new LatLng(endLat, endLon);

        //Map section
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment suppFrag = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapHunting);
        mMapHunting = suppFrag.getMap();
        mMapHunting.setMyLocationEnabled(true);
        mMapHunting.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                setUpMap();
            }
        });

        _tvDistanceToStart = (TextView) findViewById(R.id.tvDistanceToTarget); // modalita' raggiungimento

        // update il valore del primo checkpoint sul controllo in alto
        final TextView tvCheckPointTime = (TextView) findViewById(R.id.tvCheckPointTime);
        int checkPointLimit = (int)Common.checkPoint_Time[_indexCurrentCheckPoint+1];
        String strCheckpoint = "CheckPoint Time:" + String.valueOf(checkPointLimit);
        tvCheckPointTime.setText(strCheckpoint);

        final TextView tvDistantRace = (TextView) findViewById(R.id.tvDistanceRace);
        final TextView tvDistanceToNextCP = (TextView) findViewById(R.id.tvDistanceToNext);
        final TextView tvDeltaSeconds = (TextView) findViewById(R.id.tvDeltaSeconds);
        final TextView tvDeltaMeters = (TextView) findViewById(R.id.tvDeltaMeters);

        //service section
        GPSService.setTargetStartLocation(startLat, startLon);
        GPSService.setTargetEndLocation(endLat, endLon);
        GPSService.setDistanceSegment(Common.getDistanceSegment());

         _service = new ServiceManager(this, GPSService.class, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {

                    
                    // set initial position (non usato)
                    case GPSService.MSG_SET_INIT_LOCATION:

                        double initLat = msg.getData().getDouble("GPS_SERVICE_Latitude");
                        double initLon = msg.getData().getDouble("GPS_SERVICE_Longitude");


                        //addMarker(initLat, initLon, "You are here", "Go Go Go", R.drawable.point);
                        break;
                 /*       
                    case GPSService.MSG_APPROACH_COMPLETE:
                    
                        _vibrate.vibrate(500);
                        tvDistanceToNextCP.setText("Target is Here, GOGOGOOOOO");
                        break;
                        
                    case GPSService.MSG_TIMER_APPROACH:

                        float dstToStart = msg.getData().getFloat("GPS_SERVICE_DistanceToStartKom");
                        _tvDistanceToStart.setText("Distance to target: " + String.valueOf(dstToStart) + "(" + msg.arg1 + ")"); //ok
                        break;
                   */
                    case GPSService.MSG_LOCATION:
                        double[] curLocArr = msg.getData().getDoubleArray("GPS_SERVICE_CurrentLocation");
                        updateTrackingPositionMarker(curLocArr[0], curLocArr[1], "-", "-");

                        _lastknownLocation = new LatLng(curLocArr[0], curLocArr[1]);

                        //zoom camera following rider movement
                        GenericHelper.zoomCamera(mMapHunting, _startSegmentLocation, _endSegmentLocation, _lastknownLocation);

                    break;

                    case GPSService.MSG_WRONGDIRECTION:

                        tvDistantRace.setText("Wrong direction");   //todo cambiare controllo
                        Common.ttsProviderImpl.say("Direzione sbagliata");
                        Toast.makeText(getApplicationContext(), "Wrong Direction!", Toast.LENGTH_LONG).show();
                    break;



                    case GPSService.MSG_DISTANCE:

                        // avanzamento sul controllo
                        // leggere la distanza dal controllo, anziche farla globale?
                        _distanceRace =  msg.getData().getFloat("GPS_SERVICE_DistanceToStartKom");
                        
                        if (!_racing) // fase di avvicinamento   
                        {
                           // update control
                            String distanceToStart = String.format("%.02f", _distanceRace);
                            _tvDistanceToStart.setText("Distance to target: " + distanceToStart + "(" + msg.arg1 + ")");
                            _tvDistanceToStart.setTextSize(22.0f);
                            _tvDistanceToStart.setBackgroundColor(Color.WHITE);

                           if (_distanceRace <= _rangeEngageinMeters) //target is here!
                           {

                               Common.ttsProviderImpl.say("Inizio segmento GO GO GO!!");

                               tvDistanceToNextCP.setText("Target is Here, GOGOGOOOOO");

                               _racing = true;

                               sendMessageToService(GPSService.MSG_START_RACE);

                               // change Controls
                               showRaceController();
                           }
                        }
                        else // racing phase
                        {
                           _progressControl.setCurrentDistance(_distanceRace);

                           // distanza percorsa
                           //TODO: ogni tanto viene 0!
                           // update controllo superiore
                           tvDistantRace.setText("Distance Race: " + String.valueOf(_distanceRace));

                           // distanza mancante al checkpoint
                           float distanceToCp = (float)Common.checkPoint_Distance[_indexCurrentCheckPoint+1] - _distanceRace;
                           if(distanceToCp < 0.0f)
                           {
                               distanceToCp = 0.0f;
                           }
                           String strDistanceTOCp = String.format("%.02f", distanceToCp);
                           tvDistanceToNextCP.setText("Distance to Next Check Point: " + strDistanceTOCp);


                           // traccia fantasma del kom
                           double ghostTrace = getDistanceKomAtTime(counterCheckpoint);
                           _progressControl.setParallelTrace((float)ghostTrace);

                           // check raggiungimento traguardo
                            /* MOVED
                           if (_distanceRace >= distanceSegment)
                           {
                               Toast.makeText(getApplicationContext(),
                                       "Stop Race",
                                       Toast.LENGTH_LONG).show();
                               _progressControl.showReport(1); // todo:enum
                               _service.stop();
                           }
                           */
                           
                           // check end race
                           if (_indexCurrentCheckPoint == Common.numberOfChecks )
                           {
                               // non usato,messaggio a vuoto
                               sendMessageToService(GPSService.MSG_STOP_RACE);
                               if (_vibrate != null)
                               {
                                   _vibrate.vibrate(500);
                               }
                               break;
                           }
                      
                           counterCheckpoint = msg.arg1;

                           // update checkpoint
                           int timeLimit = (int)Common.checkPoint_Time[_indexCurrentCheckPoint+1];
                           int checkTime =  timeLimit - counterCheckpoint;
                           String strCheckpoint = "CheckPoint Time:     " + String.valueOf(checkTime);
                           tvCheckPointTime.setText(strCheckpoint);

                           // controllo raggiungimento checkpoint
                           double checkDst = Common.checkPoint_Distance[_indexCurrentCheckPoint+1];
                           if (_distanceRace >= checkDst)
                           {
                               // punto della situazione / statistica
                               String strCondition = "";
                               if (checkTime >=0)
                               {
                                   strCondition = "vantaggio";
                                   tvDeltaSeconds.setTextColor(Color.rgb(63,127,71));
                                   tvDeltaMeters.setTextColor(Color.rgb(63,127,71));
                               }
                               else
                               {
                                   strCondition = "svantaggio"; // todo; cambiare solo s
                                   tvDeltaSeconds.setTextColor(Color.rgb(255,100,100));
                                   tvDeltaMeters.setTextColor(Color.rgb(255,100,100));
                               }

                               // calcolo il vantaggio/svantaggio in distanza (in secondi lo so gia' da checkTime)   
                               double distanceKom = getDistanceKomAtTime(counterCheckpoint); //TODO: usare quella di prima
                               double deltaDistance = checkDst - distanceKom;
                               String strDeltaDistance = String.format("%.02f", deltaDistance);

                               // update deltas in controls
                               String lastCheckPointIndex = String.valueOf(_indexCurrentCheckPoint+1);
                               tvDeltaSeconds.setText("Delta seconds @" + lastCheckPointIndex + " : " + String.valueOf(checkTime));
                               tvDeltaMeters.setText("Delta meters: @" + lastCheckPointIndex + " : " + strDeltaDistance);

                               // info toast  
                               Toast.makeText(getApplicationContext(),
                                               "Checkpoint raggiunto con " +  String.valueOf(checkTime) + " secondi di " + strCondition +
                                               " e con " + strDeltaDistance + " metri di " + strCondition,
                                               Toast.LENGTH_LONG).show();

                               Common.ttsProviderImpl.say( String.format("%s %s secondi",strCondition, String.valueOf(checkTime)));

                              //controllo direzione corretta
                               Location locationCP = Common.checkPoint_Location[_indexCurrentCheckPoint];

                               float[] result = new float[1];

                               //in simulazione puo essere nullo
                               if (_lastknownLocation != null) {
                                   Location.distanceBetween(locationCP.getLatitude(), locationCP.getLongitude(),
                                           _lastknownLocation.latitude, _lastknownLocation.longitude, result);
                               }

                               if (result[0] > 200) // 200metri o _distanceRace/2
                               {
                                   tvDistantRace.setText("Wrong direction2");   //todo cambiare controllo
                                   Common.ttsProviderImpl.say("Direzione sbagliata 2 ");
                                   Toast.makeText(getApplicationContext(), "Wrong Direction 2 !", Toast.LENGTH_LONG).show();
                               }

                               // update
                               _indexCurrentCheckPoint++;
                               
                           }

                            if (_distanceRace >= Common.getDistanceSegment())
                            {
                                Toast.makeText(getApplicationContext(),
                                        "Stop Race, You win",
                                        Toast.LENGTH_LONG).show();
                                _progressControl.showReport(1); // todo:enum
                                AttemptPersister.saveAttempt(Common.authenticatedAthlete.getId(), true, Common.targetKom, getApplicationContext());
                                stopService();
                            }
                        }
                        break;

                    default:
                        super.handleMessage(msg);
                }
            }
        });


        _service.start();



        // Stop the service
        _btnStopService = (Button) findViewById(R.id.btnStopService);
        _btnStopService.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                stopService();
            }
        });

        // start log Gps
        _btTest1 = (Button) findViewById(R.id.btTestStart);
        _btTest1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                sendMessageToService(GPSService.MSG_START_LOG);

            }
        });

        // stop gps
        _btTest2 = (Button) findViewById(R.id.btTestStop);
        _btTest2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                sendMessageToService(GPSService.MSG_STOP_LOG);

            }
        });

        // simulazione raggiungimento segmento
        _btTest3 = (Button) findViewById(R.id.btTestSimul1);
        _btTest3.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                _racing = false;

                // *** Update controls ***
                showApproachController();

                // *** send message 1 ***
                sendMessageToService(GPSService.MSG_SIMULATE_GPS);

                // *** send message 2 ***
                // todo: permettere impostazione distanza
                sendMessageToService(GPSService.MSG_START_APPROACH);
                /*
                Message msg = Message.obtain(null, GPSService.MSG_INCREMENT, GPSService.MSG_SET_DISTANCE_SIMULATED, 0);
                Bundle b = new Bundle();
                b.putFloat("GPS_SERVICE_DistanceToStartKom", _simulatedDistance);
                msg.setData(b);
                send(msg);
                */
            }
        });

        // simulazione segmento raggiunto - inizio race
        _btTest4 = (Button) findViewById(R.id.btTestSimul2);
        _btTest4.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                _racing = true;
                // *** Update controls ***
                showRaceController();

                // *** send message ***
                sendMessageToService(GPSService.MSG_SIMULATE_GPS);

                // *** send message ***
                sendMessageToService(GPSService.MSG_START_RACE);

            }
        });



        final TextView tvSimulatedSpeed = (TextView) findViewById(R.id.tvSimulatedSpeed);
        // aggiorno velocita' media

        float avg = GPSService._simulatedDistanceStep * 3.6f;
        String strAvg = String.format("%.02f", avg);
        tvSimulatedSpeed.setText("Speed: " + strAvg);


        final Button btSimulaAccelerazione = (Button) findViewById(R.id.btSimulAccelera);
        btSimulaAccelerazione.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                float avg = GPSService._simulatedDistanceStep * 3.6f;
                avg += GPSService._simulatedDistanceAcc * 3.6f;
                ;
                String strAvg = String.format("%.02f", avg);
                tvSimulatedSpeed.setText("Speed: " + strAvg);

                // *** send message ***
                sendMessageToService(GPSService.MSG_SIMULATE_ACCELERAZIONE);
            }
        });
        final Button btSimulaDecelerazione = (Button) findViewById(R.id.btSimulDecelera);
        btSimulaDecelerazione.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                float avg = GPSService._simulatedDistanceStep * 3.6f;
                avg -= GPSService._simulatedDistanceAcc * 3.6f;
                String strAvg = String.format("%.02f", avg);
                tvSimulatedSpeed.setText("Speed: " + strAvg);

                // *** send message ***
                sendMessageToService(GPSService.MSG_SIMULATE_DECELERAZIONE);
                //_service.stop();
            }
        });


        //hide simulation button when not in developer mode (setting)
        if (!Settings.showSimulationButtons)
        {
            ShowSimulationButton(View.INVISIBLE);
        }

   }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try
        {
            _service.unbind();
        }
        catch (Throwable t)
        {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
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
