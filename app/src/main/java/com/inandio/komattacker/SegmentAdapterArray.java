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

import com.inandio.komattacker.entities.segment.Segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by parodi on 08/07/2015.
 */

public class SegmentAdapterArray extends ArrayAdapter<Segment> {
    private final Context context;
    private ArrayList<Segment> mOriginalValues;
    private List<Segment> mObjects;

    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();
    private Filter filter;
    private static Map<Integer, Bitmap> mImageSegmentCategoryMap = new HashMap<Integer, Bitmap>();

    public static class ViewHolder {
        public TextView komName;
        public ImageView komCategory;
    }

    public SegmentAdapterArray(Context context, List<Segment> values) {
        super(context, R.layout.list_item_kom, values);
        this.context = context;
        this.mObjects = values;
        mImageSegmentCategoryMap.put( R.drawable.kom1_cut, GenericHelper.decodeSampledBitmapFromResource(context.getResources(), R.drawable.kom1_cut, 100, 100));
        mImageSegmentCategoryMap.put( R.drawable.kom2_cut, GenericHelper.decodeSampledBitmapFromResource(context.getResources(), R.drawable.kom2_cut, 100, 100));
        mImageSegmentCategoryMap.put( R.drawable.kom3_cut, GenericHelper.decodeSampledBitmapFromResource(context.getResources(), R.drawable.kom3_cut, 100, 100));
        mImageSegmentCategoryMap.put( R.drawable.kom4_cut, GenericHelper.decodeSampledBitmapFromResource(context.getResources(), R.drawable.kom4_cut, 100, 100));
        mImageSegmentCategoryMap.put( R.drawable.komhc_cut, GenericHelper.decodeSampledBitmapFromResource(context.getResources(), R.drawable.komhc_cut, 100, 100));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_kom, parent, false);
            holder = new ViewHolder();
            holder.komName = (TextView) v.findViewById(R.id.kom_name);
            holder.komCategory = (ImageView) v.findViewById(R.id.icon_climb_category);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        Segment komSegment = (Segment) mObjects.get(position);
        if (komSegment != null) {
            holder.komName.setText(komSegment.getName());

            int climbCat = komSegment.getClimb_category();

            switch (climbCat) {
                case 1:
                    holder.komCategory.setImageBitmap(mImageSegmentCategoryMap.get(R.drawable.kom4_cut));
                    break;
                case 2:
                    holder.komCategory.setImageBitmap(mImageSegmentCategoryMap.get(R.drawable.kom3_cut));
                    break;
                case 3:
                    holder.komCategory.setImageBitmap(mImageSegmentCategoryMap.get(R.drawable.kom2_cut));
                    break;
                case 4:
                    holder.komCategory.setImageBitmap(mImageSegmentCategoryMap.get(R.drawable.kom1_cut));
                    break;
                case 5:
                    holder.komCategory.setImageBitmap(mImageSegmentCategoryMap.get(R.drawable.komhc_cut));
                    break;
                case 0:
                    holder.komCategory.setImageResource(R.drawable.kom_u_cut2);
                    break;
            }

        }
        return v;
    }


    //   @Override
    public Filter getFilter()
    {
        if (filter == null)
            filter = new SegmentFilter();

        return filter;
    }

    //   @Override
    public int getCount() {
        return mObjects.size();
    }

    public Segment getItem(int position) {
        return mObjects.get(position);
    }
    private class SegmentFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<Segment>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<Segment> list;
                synchronized (mLock) {
                    list = new ArrayList<Segment>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<Segment> values;
                synchronized (mLock) {
                    values = new ArrayList<Segment>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<Segment> newValues = new ArrayList<Segment>();

                for (int i = 0; i < count; i++) {
                    final Segment value = values.get(i);
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
            mObjects = (List<Segment>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}