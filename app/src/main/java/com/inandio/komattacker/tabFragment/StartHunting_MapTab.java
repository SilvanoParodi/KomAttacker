package com.inandio.komattacker.tabFragment;

/**
 * Created by eme on 11/2/2015.
 */

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.inandio.komattacker.Common;
import com.inandio.komattacker.GenericHelper;
import com.inandio.komattacker.R;

public class StartHunting_MapTab extends android.support.v4.app.Fragment implements GoogleMap.OnMarkerClickListener {

    private static View view;
    private static GoogleMap mMap;
    private static Button btnBackToMap;

    private LatLng startSegmentPoint = null;
    private LatLng endSegmentPoint = null;
    private Polyline linePath = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = (RelativeLayout) inflater.inflate(R.layout.start_hunting_map_tab, container, false);
        btnBackToMap = (Button) view.findViewById(R.id.btn_back_to_map);
        btnBackToMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnBackToMap.setVisibility(View.GONE);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                GenericHelper.zoomCamera(mMap, startSegmentPoint, endSegmentPoint, null);
            }
        });

        if (Common.targetKom != null && Common.targetKom.getSegment() != null) {

            String[] point = Common.targetKom.getSegment().getStart_latlng();
            startSegmentPoint = new LatLng( Double.parseDouble(point[0]),  Double.parseDouble(point[1]));
            point = Common.targetKom.getSegment().getEnd_latlng();
            endSegmentPoint = new LatLng( Double.parseDouble(point[0]),  Double.parseDouble(point[1]));
        }

        setUpMapIfNeeded();


        return view;
    }




    /***** Sets up the map if it is possible to do so *****/
    public  void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.

            FragmentManager fm = getChildFragmentManager();
            SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.location_map);
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.location_map, fragment).commit();
            }
            mMap = fragment.getMap();
        }
        // Check if we were successful in obtaining the map.
        if (mMap != null)
        {
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    setUpMap();
                }
            });
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap}
     * is not null.
     */
    private void setUpMap() {
        if (linePath != null) {
            linePath.remove();
        }
        mMap.clear();

        btnBackToMap.setVisibility(View.GONE);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        linePath = GenericHelper.drawSegment(getResources(), mMap);

        mMap.setOnMarkerClickListener(this);

        if (startSegmentPoint != null && endSegmentPoint != null) {
            mMap.addMarker(new MarkerOptions().position(startSegmentPoint).title("Start").snippet("Tap to see detailed view")).setIcon(BitmapDescriptorFactory.defaultMarker(Common.hueColor_Start));
            mMap.addMarker(new MarkerOptions().position(endSegmentPoint).title("End").snippet("Tap to see detailed view")).setIcon(BitmapDescriptorFactory.defaultMarker(Common.hueColor_End));
            GenericHelper.zoomCamera(mMap, startSegmentPoint, endSegmentPoint, null);
        }
    }

    private void showSatelliteAtPoint(double lat, double lon)
    {
        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(lat, lon);
            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

            btnBackToMap.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        showSatelliteAtPoint(marker.getPosition().latitude, marker.getPosition().longitude);
        return true;
    }


    /**** The mapfragment's id must be removed from the FragmentManager
     **** or else if the same it is passed on the next time then
     **** app will crash ****/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            FragmentManager fm = getChildFragmentManager();
            SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.location_map);
            if (fragment == null) {
                fm.beginTransaction().remove(null).commit();
            }
            mMap = null;
        }
    }
}
