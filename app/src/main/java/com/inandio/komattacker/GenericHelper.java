package com.inandio.komattacker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.inandio.komattacker.entities.stream.Stream;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by parodi on 22/09/2015.
 */
public class GenericHelper {


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static Polyline drawSegment(Resources resources, GoogleMap map)
    {
        // Disegno del path del segmento
        if (Common.effortStreams != null) {
            Stream streamLatLng = Common.effortStreams.get(0);  //LatLng
            PolylineOptions options = new PolylineOptions().width(7).color(resources.getColor(R.color.my_primary)).geodesic(true).visible(true);

            int numPoints = streamLatLng.getOriginal_size();
            for (int z = 0; z < numPoints; z++) {
                ArrayList point = (ArrayList) streamLatLng.getData().get(z);
                options.add(new LatLng(((double)point.get(0)), ((double)point.get(1))));
            }

            return map.addPolyline(options);
        }
        return null;
    }

    //returns true if  polylinePoints overlays  existingPoints
    boolean isMostlyCovered(LatLng[] existingPoints, LatLng[] polylinePoints) {
        for (LatLng point : polylinePoints) {
            for (LatLng existingPoint : existingPoints) {
                if (distanceBetween(existingPoint, point) > 20 /*TODO Threshold here*/) {
                    return false;
                }
            }
        }
        return true;
    }

    //returns distance in meters
    public static double distanceBetween(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
      //  int meterInDec = Integer.valueOf(newFormat.format(meter));
      // Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);

        return meter;
    }


    public static void zoomCamera(GoogleMap map,  LatLng startSegment, LatLng endSegment, LatLng currentPosition )
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (startSegment != null)
            builder.include(startSegment);
        if (endSegment != null)
            builder.include(endSegment);
        if (currentPosition != null)
            builder.include(currentPosition);

        LatLngBounds bounds = builder.build();
        // Calculate distance between northeast and southwest
        float[] results = new float[1];
        android.location.Location.distanceBetween(bounds.northeast.latitude, bounds.northeast.longitude,
                bounds.southwest.latitude, bounds.southwest.longitude, results);

        CameraUpdate cu = null;
        if (results[0] < 1000) { // distance is less than 1 km -> set to zoom level 15
            cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 15);
        } else {
            int padding = 90; // offset from edges of the map in pixels
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        }
        if (cu != null) {
            map.animateCamera(cu);
        }
    }
}
