package com.inandio.komattacker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.inandio.komattacker.entities.athlete.Athlete;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by parodi on 06/08/2015.
 */

public class AthleteImageLoadFromURLTask extends AsyncTask<Void, Void, Bitmap> {

    private String mUrl;
    private final WeakReference<ImageView> imageViewReference;
    private Integer mId;
    private Context mContext;

    public String imageUrl()
    {
        return mUrl;
    }

    public AthleteImageLoadFromURLTask(Athlete athlete, ImageView imageView, Context context) {
        this.mUrl = athlete.getProfile();
        this.mId = athlete.getId();
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.mContext = context;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Common.mImageAvatarMap.put(mId, myBitmap);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return  BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.user_strava);
        }
    }

   public static class DownloadedDrawable extends BitmapDrawable {
        private final WeakReference<AthleteImageLoadFromURLTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(AthleteImageLoadFromURLTask bitmapDownloaderTask, Context context, Bitmap bitmap) {
             super(context.getResources(), bitmap);
            bitmapDownloaderTaskReference =
                    new WeakReference<AthleteImageLoadFromURLTask>(bitmapDownloaderTask);
        }

        public AthleteImageLoadFromURLTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }
    public static AthleteImageLoadFromURLTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    public static void download(Athlete athlete, ImageView imageView, Context context) {
        if (cancelPotentialDownload(athlete.getProfile(), imageView)) {
            AthleteImageLoadFromURLTask task = new AthleteImageLoadFromURLTask(athlete, imageView, context);
            Bitmap bm =  BitmapFactory.decodeResource(context.getResources(), R.drawable.user_strava);
            AthleteImageLoadFromURLTask.DownloadedDrawable downloadedDrawable = new AthleteImageLoadFromURLTask.DownloadedDrawable(task, context, bm);
            imageView.setImageDrawable(downloadedDrawable);
            task.execute();
        }
    }

    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        AthleteImageLoadFromURLTask bitmapDownloaderTask = AthleteImageLoadFromURLTask.getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.imageUrl();
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            AthleteImageLoadFromURLTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
            // Change bitmap only if this process is still associated with it
            if (this == bitmapDownloaderTask) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    }

