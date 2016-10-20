package com.example.konradbujak.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
        private final String[] urls;
        private final String[] uids;
        private ArrayList uids;
        private ArrayList urls;
        static class ViewHolder
        {
            TextView url;
            TextView uid;
            ImageView icon;
            int position;
        }

public MySimpleArrayAdapter(Context context, String [] urls, String [] uids)
        {
            super(context, R.layout.array_adapter);
            this.context = context;
            this.urls = urls;
            this.uids = uids;
        }
    public int getCount()
    {

        if(urls.size()<=0)
            return 1;
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
        holder.url.setMovementMethod(LinkMovementMethod.getInstance());
        holder.uid = (TextView) rowView.findViewById(R.id.secondLine);
        holder.icon = (ImageView) rowView.findViewById(R.id.icon);
        rowView.setTag(holder);
        if(urls.size()<=0)
          {

              holder.url.setText("No eddystone URLs");

          }
        else
        {
            holder.url.setText(Html.fromHtml(url));
            holder.uid.setText(uids);
            holder.icon.setImageDrawable(Drawable.createFromPath("res/drawable/eddystones.png"));
        }
         return rowView;
        }

}