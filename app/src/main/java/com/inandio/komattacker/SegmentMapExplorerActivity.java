package com.inandio.komattacker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inandio.komattacker.entities.segment.Bound;
import com.inandio.komattacker.entities.segment.Segment;
import com.inandio.komattacker.progressDialog.SpotsDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by parodi on 09/07/2015.
 */

public class SegmentMapExplorerActivity extends FragmentActivity implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    GoogleApiClient mGoogleApiClient;
    private Bound mMapBound = null;
    private List<Segment> retrievedSegment = null;
    private final Object mLock = new Object();
    private Segment selectedSegment = null;

    private static final long ONE_MIN = 1000 * 60;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;

    private LocationRequest mLocationRequest;
    private Location mBestReading;

    private HashMap<Marker, Segment> markerSegmentMap = new HashMap<Marker, Segment>();
    Float hueColor_Default = 339f;


    @Override
    public void onLocationChanged(Location location) {
        // Determine whether new location is better than current best
        // estimate
        if (null == mBestReading || location.getAccuracy() < mBestReading.getAccuracy()) {
            mBestReading = location;
            MoveCamera();


            if (mBestReading.getAccuracy() < MIN_ACCURACY) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
    }

    private void MoveCamera() {

        if (mMap == null) {
            //TODO Diagnostic
            return;
        }

        if (mBestReading == null) {
            return;
        }
        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mBestReading.getLatitude(), mBestReading.getLongitude())));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    private class GetSegmentsForMapTask extends AsyncTask<Object, Object, Object> {
        Activity mActivity;
        Boolean  bConnectionException = false;

        public GetSegmentsForMapTask(Activity activity)
        {
            mActivity = activity;
        }
        @Override
        protected Object doInBackground(Object... arg0) {
            if (mMapBound != null) {
                synchronized(mLock) {
                    if (Common.strava == null)
                    {
                         return null;
                    }
                    try {
                        retrievedSegment = Common.strava.findSegments(mMapBound, 1, 5);
                    }
                    catch(IOException ex)
                    {
                        bConnectionException = true;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object res){
            if (bConnectionException) {
                Common.ShowNoConnectionDialog(mActivity);
                return;
            }
            synchronized(mLock) {
                markerSegmentMap.clear();
                if (retrievedSegment == null)
                    return;

                for (Segment s : retrievedSegment) {

                    String[] endPoint = s.getEnd_latlng();
                    double endLat = Double.parseDouble(endPoint[0]);
                    double endLng = Double.parseDouble(endPoint[1]);

                    //Add the Marker to the Map and keep track of it
                    String sCategory = "";
                    switch(s.getClimb_category()) {
                        case 1: sCategory = "4"; break;
                        case 2: sCategory = "3"; break;
                        case 3: sCategory = "2"; break;
                        case 4: sCategory = "1"; break;
                        case 5: sCategory = "HC"; break;
                        case 0: sCategory = getString(R.string.catergory_unknow); break;
                    }

                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(endLat, endLng)).title(s.getName()).snippet(String.format(getString(R.string.climbCategory), sCategory)));
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(hueColor_Default));
                    markerSegmentMap.put(marker, s);
                }
            }
         }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segmentmapexplorer);

        Common.cleanSelectedTarget();

        mBestReading = new Location("");
        mBestReading.setLatitude(44.550695);
        mBestReading.setLongitude(8.887786);

        if (!servicesAvailable()) {
            //todo graceful termination
            finish();
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(POLLING_FREQ);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        setUpMapIfNeeded();

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        final Activity outerActivity = this;
        //request new segment depending on map visible region
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                LatLngBounds googleBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                mMapBound = new Bound(
                        googleBounds.southwest.latitude,
                        googleBounds.southwest.longitude,
                        googleBounds.northeast.latitude,
                        googleBounds.northeast.longitude);

                mMap.clear();
                new GetSegmentsForMapTask(outerActivity).execute();
            }
        });


        //management on marker click
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                synchronized (mLock) {
                    selectedSegment = markerSegmentMap.get(marker);
                }
                marker.showInfoWindow();
                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                if (selectedSegment != null)
                {
                    Common.loadingDialog = new SpotsDialog(outerActivity);
                    Common.loadingDialog.show();
                    new GetKOMbySegmentIDTask(outerActivity, selectedSegment).execute();
                }

            }
        });


        LatLngBounds googleBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        mMapBound = new Bound(
                googleBounds.southwest.latitude,
                googleBounds.southwest.longitude,
                googleBounds.northeast.latitude,
                googleBounds.northeast.longitude );

        new GetSegmentsForMapTask(outerActivity).execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    @Override
    public void onConnected(Bundle dataBundle) {
        // Get first reading. Get additional location updates if necessary
        if (servicesAvailable()) {
            // Get best last location measurement meeting criteria
            mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);

            if (null == mBestReading
                    || mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY
                    || mBestReading.getTime() < System.currentTimeMillis() - TWO_MIN) {

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                // Schedule a runnable to unregister location listeners
                Executors.newScheduledThreadPool(1).schedule(new Runnable() {

                    @Override
                    public void run() {
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, SegmentMapExplorerActivity.this);
                    }

                }, ONE_MIN, TimeUnit.MILLISECONDS);
            }

            MoveCamera();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        }
        else {
            return bestResult;
        }
    }

    private boolean servicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }




}

