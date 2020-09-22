
package com.inandio.komattacker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.inandio.komattacker.serviceclass.AbstractService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class GPSService extends AbstractService implements LocationListener
{
    private static final String TAG = GPSService.class.getSimpleName();

    // *** messages
    // send
    public static final int MSG_INCREMENT = 1;
    public static final int MSG_COUNTER = 2;
    public static final int MSG_SET_INIT_LOCATION = 3;
    public static final int MSG_APPROACH_COMPLETE = 4;
    //public static final int MSG_TIMER = 5;
    //public static final int MSG_TIMER_APPROACH = 6;


    public static final int MSG_DISTANCE = 201;
    public static final int MSG_LOCATION = 202;
    public static final int MSG_WRONGDIRECTION = 203;


    // receive side
    public static final int MSG_START_LOG = 101;
    public static final int MSG_STOP_LOG = 102;

    public static final int MSG_SIMULATE_GPS = 104;
    public static final int MSG_START_APPROACH = 107;
    public static final int MSG_START_RACE = 108;
    public static final int MSG_STOP_RACE = 108;

    public static final int MSG_SIMULATE_ACCELERAZIONE = 300;
    public static final int MSG_SIMULATE_DECELERAZIONE = 301;


    // variables

    private Location _currentLocation = null;

    private NotificationManager nm;
    private Timer timer = new Timer(); // used to simulate GPS
    private int _counter = 0;

    // filelog stuff
    private FileOutputStream _streamFile = null;
    private boolean _logActive = false;
    private int _sessionFile = 0;


    LocationManager _locationManager = null;


    private static  Location _targetStartLocation = null; // start segmento kom
    private static  Location _targetEndLocation = null; // end segmento kom

    private boolean _simulateGps = false;
    //private boolean _racing = false;

    //private static final float _rangeEngageinMeters = 5.0f;



    private float _simulatedDistance = 0.0f;
    private float _distance = 0.0f;
    private boolean _approaching = false;

   //private float _lastDistanceToEnd = 0;

    private static float _distanceSegment = 0;

    public static float _simulatedDistanceStep = 8.0f; // 20 m/sec perche' il timer ha un intervallo di 1 secondo
    public static float _simulatedDistanceAcc = 1.0f;

    static public void setTargetStartLocation(double lat, double lon)
    {
        _targetStartLocation = new Location("TargetStartLocation");
        _targetStartLocation.setLatitude(lat);
        _targetStartLocation.setLongitude(lon);
    }

    static public void setTargetEndLocation(double lat, double lon)
    {
        _targetEndLocation = new Location("TargetEndLocation");
        _targetEndLocation.setLatitude(lat);
        _targetEndLocation.setLongitude(lon);
    }

    static public void setDistanceSegment(float distance)
    {
        _distanceSegment = distance;
    }



    public void writeToFile(FileOutputStream fileStream, String strData)
    {
        try
        {
            byte[] buffer = new byte[1024];
            buffer = strData.getBytes();
            int strLen = strData.length();
            fileStream.write(buffer, 0, strLen);
        }
        catch (IOException e)
        {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }

    private void showNotification()
    {
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        String text = getString(R.string.service_started, getClass().getSimpleName());
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher2);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setLargeIcon(bm)
                .setWhen(System.currentTimeMillis())
                .getNotification();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, HuntingActivity.class), 0);

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.setLatestEventInfo(this, getClass().getSimpleName(), text, contentIntent);

        nm.notify(getClass().getSimpleName().hashCode(), notification);

    }

    private void updateNotification(String text)
    {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher2);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setLargeIcon(bm)
                .setWhen(System.currentTimeMillis())
                .getNotification();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, HuntingActivity.class), 0);
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.setLatestEventInfo(this, getClass().getSimpleName(), text, contentIntent);
        nm.notify(getClass().getSimpleName().hashCode(), notification);
    }

	@Override 
	public void onStartService()
    {
        showNotification();

        TimerTask timerTask = new TimerTask(){
                                public void run()
                                {
                                    onTimerTick();
                                }
                            };
        timer.scheduleAtFixedRate(timerTask, 0, 1000L);

        _locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, GPSService.this);


       Common.initializeVocalAssistant(getApplicationContext());
	}
	
   @Override
    public void onStopService()
   {
        if (timer != null) {timer.cancel();}
       _counter=0;
        nm.cancel(getClass().getSimpleName().hashCode());
        Log.i("MyService", "Service Stopped.");

       _locationManager.removeUpdates(GPSService.this);
       _locationManager = null;
    }   

	@Override
	public void onReceiveMessage(Message msg)
    {
		if (msg.what == MSG_INCREMENT)  //TODO: cambiare sistema, info...
        {
            // start log
			if (msg.arg1 == MSG_START_LOG)
            {
                try
                {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File (sdCard.getAbsolutePath() + "/dir1");
                    dir.mkdirs();
                    File file = new File(dir, "logServiceGps_"  + String.valueOf(_sessionFile) +  ".txt");

                    _streamFile = new FileOutputStream(file);
                    _logActive = true;

                    writeToFile(_streamFile, "coordinates" +  String.valueOf(_sessionFile) + "\n");

                }
                catch (Exception e)
                {
                    System.out.println("e: " + e);
                }

                updateNotification("Log!");
            }

            // stop log
            if (msg.arg1 == MSG_STOP_LOG)
            {

                if (_logActive)
                {
                    try
                    {
                        _streamFile.close();
                        _logActive = false;
                        _sessionFile++;
                    }
                    catch (Exception e)
                    {
                        System.out.println("e: " + e);
                    }
                    updateNotification("Stop Log!");
                }
            }


            if (msg.arg1 == MSG_SIMULATE_GPS)
            {
                _simulateGps = true;
                //_racing = true;
                _counter = 0;
            }




            // usato per debug-simulazione
            if (msg.arg1 == MSG_SIMULATE_ACCELERAZIONE)
            {
                _simulatedDistanceStep += _simulatedDistanceAcc;
            }

            // usato per debug-simulazione
            if (msg.arg1 == MSG_SIMULATE_DECELERAZIONE)
            {
                _simulatedDistanceStep -= _simulatedDistanceAcc;

                if (_simulatedDistanceStep <=0 )
                {
                    _simulatedDistanceStep = 0.1f;
                }
            }

            if (msg.arg1 == MSG_START_APPROACH)
            {

                _simulatedDistance = 200.0f;
                _approaching = true;
            }
            if (msg.arg1 == MSG_START_RACE)
            {
                _simulatedDistance = 0.0f;
                _approaching = false;
                _counter = 0;
            }

            if (msg.arg1 == MSG_STOP_RACE)
            {

            }
		}
	}
   




    private void onTimerTick()
    {

        try
        {
            _counter++;




            // ad ogni intervallo simulo avanzamento di distanza regolare
            if (_simulateGps)
            {
                Message msg = Message.obtain(null, MSG_DISTANCE, _counter, 0);
                Bundle b = new Bundle();
                b.putFloat("GPS_SERVICE_DistanceToStartKom", _simulatedDistance);
                msg.setData(b);
                send(msg);

                if (_approaching)
                {
                    _simulatedDistance -= _simulatedDistanceStep;
                }
                else
                {
                    _simulatedDistance += _simulatedDistanceStep;
                }
            }
            else // true GPS
            {
                if (_currentLocation != null) {
                    float distance = getDistanceBetween(_currentLocation, _targetStartLocation);

                    Message msg = Message.obtain(null, MSG_DISTANCE, _counter, 0);
                    Bundle b = new Bundle();
                    b.putFloat("GPS_SERVICE_DistanceToStartKom", distance);
                    msg.setData(b);
                    send(msg);

                    // check right direction
                    if (_counter%5 == 0)    // each 5 seconds
                    {
                        float distanceToEnd = getDistanceBetween(_currentLocation, _targetEndLocation);

                        if ( ((distanceToEnd + distance) - _distanceSegment*2) > 200)
                        {
                            Message msgWD = Message.obtain(null, MSG_WRONGDIRECTION, _counter, 0);
                            Bundle bwd = new Bundle();
                            bwd.putBoolean("GPS_SERVICE_WrongDirection", true); // now always true
                            msg.setData(bwd);
                            send(msgWD);
                        }

                    }
                }
            }

            if (_counter%5 == 0)    // each 5 seconds
            {
                Log.i("TimerTick", "Aggiornamento posizione");

                if ( (!_simulateGps) && (_currentLocation != null) )
                {
                    Message msg = Message.obtain(null, MSG_LOCATION, _counter, 0);
                    Bundle b = new Bundle();
                    double[] locArr = new double[2];
                    locArr[0] = _currentLocation.getLatitude();
                    locArr[1] = _currentLocation.getLongitude();

                    b.putDoubleArray("GPS_SERVICE_CurrentLocation", locArr);
                    msg.setData(b);
                    send(msg);
                }

            }




          
          /*
            // avvcinamento
            else // nella fase approach ad ogni intervallo (1 sec) invio messaggio MSG_TIMER_APPROACH
            {
                Message msg = Message.obtain(null, MSG_TIMER_APPROACH, _counter, 0);
                Bundle b = new Bundle();
                b.putFloat("GPS_SERVICE_DistanceToStartKom", distance);
                msg.setData(b);
                send(msg);

                if (distance <= _rangeEngageinMeters) //target is here!
                {
                    Message msgAp = Message.obtain(null, MSG_APPROACH_COMPLETE, _counter, 0);
                    send(msgAp);
                }
            }
*/
            if (_logActive)
            {
                String dbgInfoString =
                        String.valueOf(_currentLocation.getLatitude()) + "," +
                        String.valueOf(_currentLocation.getLatitude()) + "," +
                        String.valueOf(_targetStartLocation.getLatitude()) + "," +
                        String.valueOf(_targetStartLocation.getLatitude()) + "," +
                        "\n";
                writeToFile(_streamFile, dbgInfoString);
            }

        } 
        catch (Throwable t)
        {
            Log.e("TimerTick", "Timer Tick Failed.", t);            
        }
    }


    private float getDistanceBetween(Location loc_a, Location loc_b)
    {
        /*
        float lat_a = (float)loc_a.getLatitude();
        float lng_a = (float)loc_a.getLongitude();
        float lat_b = (float)loc_b.getLatitude();
        float lng_b = (float)loc_b.getLongitude();

        float pk = (float) (180/3.14169);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        float t1 = FloatMath.cos(a1)* FloatMath.cos(a2)*FloatMath.cos(b1)*FloatMath.cos(b2);
        float t2 = FloatMath.cos(a1)*FloatMath.sin(a2)*FloatMath.cos(b1)*FloatMath.sin(b2);
        float t3 = FloatMath.sin(a1)*FloatMath.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000*(float)tt;

        */

        /*
        // metodo 1 (use old api gps)
        float distance = loc_a.distanceTo(loc_b);
        return distance;
        */

        float[] result = new float[1];
        Location.distanceBetween(loc_a.getLatitude(), loc_a.getLongitude(),
                                 loc_b.getLatitude(), loc_b.getLongitude(),result);
        float distance3 = result[0];
        return distance3;

    }




        public void onLocationChanged(Location location) {


            if (location == null) {
                return;
            }
            _currentLocation = location;

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }



}
