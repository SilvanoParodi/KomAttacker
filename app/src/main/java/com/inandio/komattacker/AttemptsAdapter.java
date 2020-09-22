package com.inandio.komattacker;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inandio.komattacker.entities.segment.Segment;
import com.inandio.komattacker.progressDialog.SpotsDialog;

import java.io.IOException;
import java.util.List;

/**
 * Created by parodi on 09/10/2015.
 */
class AttemptsAdapter extends ArrayAdapter<Attempt> {

    Context context;
    private List<Attempt> data = null;
    private static LayoutInflater inflater = null;


    public AttemptsAdapter(Context context, List<Attempt> attemptList) {
        super(context, R.layout.list_item_attempt, attemptList);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.data = attemptList;
    }

    @Override
    public int getCount() {
        if (data == null)
            return 0;
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Attempt getItem(int position) {
        if (data == null)
            return null;
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.list_item_attempt, null);

        final Attempt attempt = getItem(position);
        TextView text = (TextView) vi.findViewById(R.id.segmentName);
        text.setText(attempt.segmentName);

        ImageView imageView = (ImageView) vi.findViewById(R.id.successStatusAttempt);
        if (attempt.isSuccessful)
            imageView.setImageResource(R.drawable.ic_thumb_up_black_48dp);
        else
            imageView.setImageResource(R.drawable.ic_thumb_down_black_48dp);

        com.getbase.floatingactionbutton.FloatingActionButton retryImage = ( com.getbase.floatingactionbutton.FloatingActionButton) vi.findViewById(R.id.retryButton);

        retryImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Common.loadingDialog = new SpotsDialog(context);
                Common.loadingDialog.show();
                new GetSegmentTask(context, attempt.segmentID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });


        return vi;
    }

    private class GetSegmentTask extends AsyncTask<Object, Object, Object> {
        private Context context;
        private long segmentId;
        Segment retrievedSegment = null;

        public GetSegmentTask( Context context, long segmentId) {
            this.context = context;
            this.segmentId = segmentId;
        }
        @Override
        protected Object doInBackground(Object... arg0) {

            if (Common.strava == null)
            {
                return false;
            }
            try {
                retrievedSegment = Common.strava.findSegment(segmentId);
            }
            catch (IOException ex)
            {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Object res){
            if ((Boolean)res == false)
            {
                Common.ShowNoConnectionDialog((Activity)context);
                return;
            }

            if (retrievedSegment != null)
                new GetKOMbySegmentIDTask(context, retrievedSegment).execute();;
        }

    }
}