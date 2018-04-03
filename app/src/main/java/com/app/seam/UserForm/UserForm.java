package com.app.seam.UserForm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.seam.AccountSettings.UserProfileSettings;
import com.app.seam.Adapters.UserEventAdapter;
import com.app.seam.AdminForm.AdminForm;
import com.app.seam.Constant.Constant;
import com.app.seam.Model.Event;
import com.app.seam.MyFunction.MyFunction;
import com.app.seam.MyServices.NotificationBroadcastReceiver;
import com.app.seam.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserForm extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout no_event;

    private DatabaseReference databaseEvents;

    private UserEventAdapter userEventAdapter;

    private List<Event> events;
    private ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        initViews();
        initDatabase();

        events = new ArrayList<>();
        navigationView.setCheckedItem(R.id.user_home);

        initListViewOnItemClick();
    }

    private void initListViewOnItemClick() {
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                previewEventInfo(position);
            }
        });
    }

    private void previewEventInfo(final int position) {
        final AlertDialog.Builder previewEvent = new AlertDialog.Builder(this);

        Event event = events.get(position);

        String info = "Event Name : " + event.getE_name() + "\n";
        info += "Event Date : " + event.getE_date() + "\n";
        info += "Event Time : " + event.getE_time() + "\n\n";
        info += "Time Created : " + event.getTime_created() + "\n";
        info += "Date Created : " + event.getDate_created() + "\n";

        previewEvent.setTitle("Event info");
        previewEvent.setMessage(info);

        previewEvent.setPositiveButton("OK", null);
        final AlertDialog alertDialog = previewEvent.create();

        alertDialog.show();

    }

    private void initDatabase() {
        databaseEvents = FirebaseDatabase.getInstance().getReference("Events");
    }

    private void initViews() {

        toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);

        no_event = findViewById(R.id.no_event_user);

        eventListView = findViewById(R.id.user_listview);

        drawerLayout = findViewById(R.id.drawer_layout_user);
        navigationView = findViewById(R.id.nav_view_user);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (MyFunction.isConnectedToInternet(this)){
            databaseEvents.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    events.clear();

                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                        Event event = postSnapShot.getValue(Event.class);

                        events.add(event);

                        Log.e("name", event.getE_name());

                        sendNotification(event.getE_name(), event.getE_date(), event.getE_time());
                    }

                    Collections.reverse(events);

                    userEventAdapter = new UserEventAdapter(UserForm.this, events);

                    if (userEventAdapter.isEmpty()) {
                        no_event.setVisibility(View.VISIBLE);
                        eventListView.setVisibility(View.INVISIBLE);
                    } else {
                        no_event.setVisibility(View.INVISIBLE);
                        eventListView.setVisibility(View.VISIBLE);
                        eventListView.setAdapter(userEventAdapter);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(AdminForm.class.getSimpleName(), databaseError.toException() + "");
                }
            });
        }else {
            MyFunction.showMessage("No Internet Connection!");
        }


    }

    private void sendNotification(String event_name, String event_date, String event_time) {
        Intent goAlertReceiver = new Intent(getBaseContext(), NotificationBroadcastReceiver.class);

        goAlertReceiver.putExtra(Constant.EXTRA_EVENT_NAME, event_name);
        goAlertReceiver.putExtra(Constant.EXTRA_EVENT_DATE, event_date);
        goAlertReceiver.putExtra(Constant.EXTRA_EVENT_TIME, event_time);

        int _id = 1;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, _id, goAlertReceiver, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.user_home) {
            // Handle the camera action
        } else if (id == R.id.user_settings) {
            startActivity(new Intent(UserForm.this, UserProfileSettings.class));
            finish();
        } else if (id == R.id.user_about) {
            showAbout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAbout() {
        final AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);

        final View view = LayoutInflater.from(this).inflate(R.layout.custom_about, null);

        final CardView btnAboutOk = view.findViewById(R.id.btn_about_ok);

        aboutDialog.setView(view);

        final AlertDialog alertDialog = aboutDialog.create();
        alertDialog.show();

        btnAboutOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }
}
