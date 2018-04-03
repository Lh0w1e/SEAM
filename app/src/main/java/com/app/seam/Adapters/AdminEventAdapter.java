package com.app.seam.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.seam.Model.Event;
import com.app.seam.R;

import java.util.List;

/**
 * Created by Colinares on 4/1/2018.
 */

public class AdminEventAdapter extends BaseAdapter{

    private Activity context;
    private List<Event> events;

    public AdminEventAdapter(Activity context, List<Event> events){
        this.context = context;
        this.events = events;

    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.custom_admin_event_list, parent, false);

        TextView txtEventName = convertView.findViewById(R.id.custom_event_name);
        TextView txtEventDate = convertView.findViewById(R.id.custom_event_date);

        final Event event = events.get(position);

        txtEventName.setText(""+event.getE_name());
        txtEventDate.setText(""+event.getE_date());

        return convertView;

    }
}
