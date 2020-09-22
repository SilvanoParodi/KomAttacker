package com.inandio.komattacker;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inandio.komattacker.entities.athlete.Athlete;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parodi on 08/07/2015.
 */

public class AthleteAdapterArray extends ArrayAdapter<Athlete> {
    private final Context context;
    private ArrayList<Athlete> mOriginalValues;
    private List<Athlete> mObjects;

    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();
    private Filter filter;

    public static class ViewHolder {
        public TextView friendName;
        public ImageView friendAvatar;
    }

    public AthleteAdapterArray(Context context, List<Athlete> rawAthletes) {
        super(context, R.layout.list_item, rawAthletes);
        this.context = context;
        this.mObjects = rawAthletes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.friendName = (TextView) v.findViewById(R.id.friend_name);
            holder.friendAvatar = (ImageView) v.findViewById(R.id.icon);
            v.setTag(holder);
        }else {
            holder = (ViewHolder) v.getTag();
        }
        if (mObjects != null && position < mObjects.size()) {
           Athlete athlete = (Athlete) mObjects.get(position);
            holder.friendName.setText(String.format("%s %s", athlete.getFirstname(), athlete.getLastname()));
           if (Common.mImageAvatarMap.containsKey((athlete.getId())))
           {
               holder.friendAvatar.setImageBitmap((Bitmap)Common.mImageAvatarMap.get(athlete.getId()));
           }
           else
               AthleteImageLoadFromURLTask.download(athlete, holder.friendAvatar, context);
        }
        else {
            holder.friendName.setText("");
        }

        return v;
    }


 //   @Override
    public Filter getFilter()
    {
        if (filter == null)
            filter = new AthleteFilter();

        return filter;
    }

    //   @Override
    public int getCount() {
        return mObjects == null ? 0 : mObjects.size();
    }

    //   @Override
    public Athlete getItem(int position) {
        return  mObjects == null ? null : mObjects.get(position);
    }



    private class AthleteFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<Athlete>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<Athlete> list;
                synchronized (mLock) {
                    list = new ArrayList<Athlete>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<Athlete> values;
                synchronized (mLock) {
                    values = new ArrayList<Athlete>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<Athlete> newValues = new ArrayList<Athlete>();

                for (int i = 0; i < count; i++) {
                    final Athlete value = values.get(i);
                    final String valueText = value.toString().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.contains(prefixString)) {
                        newValues.add(value);
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<Athlete>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
