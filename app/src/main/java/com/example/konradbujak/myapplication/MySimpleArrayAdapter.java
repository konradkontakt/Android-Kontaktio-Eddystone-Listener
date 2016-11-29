package com.example.konradbujak.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.data;

/**
 * Created by Konrad.Bujak on 20.10.2016.
 */

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
    private static final String TAG = "XYZ";
    private final ArrayList<String> urls = new ArrayList<>();
        private final ArrayList<String> uids = new ArrayList<>();
/*        private ArrayList uids;
        private ArrayList urls;*/
        static class ViewHolder
        {
            TextView url;
            TextView uid;
            ImageView icon;
            int position;
        }

public MySimpleArrayAdapter(Context context)
        {
            super(context, R.layout.array_adapter);
            this.context = context;
        }

    public void updateUrls(ArrayList<String> newUrls, ArrayList<String> newUIDs) {
        urls.clear();
        uids.clear();
        urls.addAll(newUrls);
        uids.addAll(newUIDs);
        notifyDataSetChanged();
        Log.d(TAG, "updateUrls: " +getCount());
    }

    public int getCount()
    {
        return urls.size();
    }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
        LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.array_adapter, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.url = (TextView) rowView.findViewById(R.id.firstLine);
        holder.uid = (TextView) rowView.findViewById(R.id.secondLine);
        holder.icon = (ImageView) rowView.findViewById(R.id.icon);
        rowView.setTag(holder);
        if(urls.size() <=0)
          {
              holder.url.setText("No eddystone URLs");
          }
        else
        {
            holder.url.setMovementMethod(LinkMovementMethod.getInstance());
            holder.url.setText(Html.fromHtml(urls.get(position)));
            holder.uid.setText(uids.get(position));
            holder.icon.setImageResource(R.drawable.eddystone);
        }
         return rowView;
        }

}