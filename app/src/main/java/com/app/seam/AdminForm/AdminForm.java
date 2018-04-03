package com.app.seam.AdminForm;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

import com.app.seam.AccountSettings.AdminProfileSettings;
import com.app.seam.Adapters.AdminEventAdapter;
import com.app.seam.Database.MyDatabase;
import com.app.seam.Model.Event;
import com.app.seam.MyFunction.MyFunction;
import com.app.seam.R;
import com.app.seam.Util.DateTimeUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class AdminForm extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private FloatingActionButton fab_add;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout no_event;

    //for add custom views
    private EditText txtAddEventName, txtAddEventDate, txtAddEventTime;
    private CardView btnSave, btnCancel;

    private TimePicker custom_time_picker;
    private DatePicker custom_date_picker;

    private CardView custom_date_btn_ok, custom_time_btn_ok;

    //for update custom views
    private EditText txtUpdateEventName, txtUpdateEventDate, txtUpdateEventTime;
    private CardView btnUpdate, btnUpdateCancel;

    private TimePicker custom_update_time_picker;
    private DatePicker custom_update_date_picker;

    private CardView custom_update_date_btn_ok, custom_update_time_btn_ok;

    private SQLiteDatabase mDb;
    private DatabaseReference databaseEvents;

    private AdminEventAdapter adminEventAdapter;


    private List<Event> events;
    private ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_form);

        initViews();
        initDatabase();

        events = new ArrayList<>();

        navigationView.setCheckedItem(R.id.nav_home);
        addNewEventOnClick();
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

        final String e_id = event.getE_id();

        String info = "Event Name : " + event.getE_name() + "\n";
        info += "Event Date : " + event.getE_date() + "\n";
        info += "Event Time : " + event.getE_time() + "\n\n";
        info += "Time Created : " + event.getTime_created() + "\n";
        info += "Date Created : " + event.getDate_created() + "\n";


        previewEvent.setTitle("Event info");
        previewEvent.setMessage(info);

        previewEvent.setNeutralButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateEventDialog(e_id, position);
            }
        });

        previewEvent.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                warningDialog(e_id, position);
            }
        });

        previewEvent.setPositiveButton("CLOSE", null);
        final AlertDialog alertDialog = previewEvent.create();

        alertDialog.show();

    }

    private void warningDialog(final String e_id, final int position ) {

        Event event = events.get(position);

        final AlertDialog.Builder warning = new AlertDialog.Builder(this);
        warning.setTitle("Warning");
        warning.setMessage("Are you sure you want to delete " + event.getE_name() + " ?");
        warning.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseEvents.child(e_id).removeValue();
                events.remove(position);

                adminEventAdapter.notifyDataSetChanged();

                dialog.dismiss();

                MyFunction.showMessage("Event has been deleted.");

            }
        });
        warning.setPositiveButton("NO", null);
        warning.create().show();

    }

    private void updateEventDialog(String e_id, final int position) {
        final View view = LayoutInflater.from(this).inflate(R.layout.custom_admin_preview_event, null);
        final AlertDialog.Builder updateEvent = new AlertDialog.Builder(this);

        updateEvent.setView(view);

        final AlertDialog alertDialog = updateEvent.create();

        alertDialog.show();

        initCustomUpdateViews(view);
        setTextInfo(position);

        initCustomUpdateViewClickListener(e_id, position, alertDialog);

    }

    private void initCustomUpdateViewClickListener(final String e_id, final int position, final AlertDialog alertDialog) {

        txtUpdateEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(txtUpdateEventDate);
            }
        });

        txtUpdateEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(txtUpdateEventTime);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmptyUpdateFields()) {
                    MyFunction.showMessage("Unable to update event with incomplete fields. Please fill all fields.");
                } else {
                    String updateName = txtUpdateEventName.getText().toString();
                    String updateDate = txtUpdateEventDate.getText().toString();
                    String updateTime = txtUpdateEventTime.getText().toString();

                    Event updateEvent = new Event(e_id, updateName, updateDate, updateTime, DateTimeUtil.currentTime(), DateTimeUtil.currentDate());

                    databaseEvents.child(e_id).setValue(updateEvent);

                    events.remove(position);
                    events.add(position, updateEvent);

                    adminEventAdapter.notifyDataSetChanged();

                    alertDialog.dismiss();

                    MyFunction.showMessage("Event updated!");

                }
            }
        });

        btnUpdateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private boolean isEmptyUpdateFields() {
        if (txtUpdateEventName.getText().toString().isEmpty()
                || txtUpdateEventDate.getText().toString().isEmpty()
                || txtUpdateEventTime.getText().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private void setTextInfo(final int position) {
        Event event = events.get(position);

        txtUpdateEventName.setText(event.getE_name());
        txtUpdateEventDate.setText(event.getE_date());
        txtUpdateEventTime.setText(event.getE_time());

    }

    private void initCustomUpdateViews(View view) {
        txtUpdateEventName = view.findViewById(R.id.preview_event_name);
        txtUpdateEventDate = view.findViewById(R.id.preview_event_date);
        txtUpdateEventTime = view.findViewById(R.id.preview_event_time);

        btnUpdate = view.findViewById(R.id.btn_update_event);
        btnUpdateCancel = view.findViewById(R.id.btn_cancel_update_event);
    }

    private void initDatabase() {
        //for sqlite
        mDb = MyDatabase.getInstance(this).getReadableDatabase();

        //for firebase
        databaseEvents = FirebaseDatabase.getInstance().getReference("Events");

        MyFunction.init(this);
    }

    private void addNewEventOnClick() {
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEventDialog();
            }
        });
    }

    private void showAddEventDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.custom_admin_add_event, null);
        final AlertDialog.Builder addEvent = new AlertDialog.Builder(this);

        addEvent.setView(view);

        final AlertDialog alertDialog = addEvent.create();

        alertDialog.show();

        initCustomViews(view);
        initCustomOnClick(alertDialog);
    }

    private void initCustomOnClick(final AlertDialog alertDialog) {
        txtAddEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(txtAddEventDate);
            }
        });

        txtAddEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtAddEventDate.getText().toString().isEmpty()) {
                    MyFunction.showMessage("Select date first.");
                } else {
                    showTimePickerDialog(txtAddEventTime);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmptyFields()) {
                    MyFunction.showMessage("Please complete all fields.");
                } else {

                    String e_id = databaseEvents.push().getKey();
                    String e_name = txtAddEventName.getText().toString();
                    String e_date = txtAddEventDate.getText().toString();
                    String e_time = txtAddEventTime.getText().toString();

                    Event event = new Event(e_id, e_name, e_date, e_time, DateTimeUtil.currentTime(), DateTimeUtil.currentDate());

                    databaseEvents.child(e_id).setValue(event);

                    //NewEventInfo.new_event_id = e_id;

                    newDataListener(e_id);

                    alertDialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void newDataListener(final String e_id) {
        databaseEvents.child(e_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);

                if (event != null) {
                    Log.e("New Data", event.getE_name() + " " + event.getE_date());
                    /*NewEventInfo.new_event_id = e_id;
                    NewEventInfo.new_event_name = event.getE_name();
                    NewEventInfo.new_event_date = event.getE_date();
                    NewEventInfo.new_event_time = event.getE_time();*/

                    //sendNotification(e_id, event.getE_name(), event.getE_date(), event.getE_time());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("data change", "" + databaseError.toException());

            }
        });
    }

    private boolean isEmptyFields() {
        if (txtAddEventName.getText().toString().isEmpty()
                || txtAddEventDate.getText().toString().isEmpty()
                || txtAddEventTime.getText().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private void showTimePickerDialog(final EditText txtTime) {
        final AlertDialog.Builder timeDialog = new AlertDialog.Builder(this);

        //set custom layout
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View mView = layoutInflater.inflate(R.layout.custom_time_picker, null);

        timeDialog.setView(mView);
        timeDialog.setCancelable(true);

        final AlertDialog dialog = timeDialog.create();

        custom_time_picker = mView.findViewById(R.id.custom_add_time);
        custom_time_btn_ok = mView.findViewById(R.id.custom_add_time_ok);

        final Calendar now = Calendar.getInstance();
        custom_time_picker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        custom_time_picker.setCurrentMinute(now.get(Calendar.MINUTE));

        custom_time_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(0, 0, 0, custom_time_picker.getCurrentHour(), custom_time_picker.getCurrentMinute());

                int hour = selectedTime.get(Calendar.HOUR_OF_DAY);
                int minute = selectedTime.get(Calendar.MINUTE);

                String status = "AM";

                if (hour > 11) {
                    status = "PM";
                }
                int hour_of_12_hour_format;

                if (hour > 11) {
                    hour_of_12_hour_format = hour - 12;
                } else {
                    hour_of_12_hour_format = hour;
                }

                Calendar current = Calendar.getInstance();
                Calendar currentDateTime = Calendar.getInstance();

                currentDateTime.set(current.get(Calendar.YEAR),
                        current.get(Calendar.MONTH),
                        current.get(Calendar.DAY_OF_MONTH),
                        current.get(Calendar.HOUR_OF_DAY),
                        current.get(Calendar.MINUTE),
                        0);

                Calendar selectedDate = Calendar.getInstance();

                selectedDate.set(custom_date_picker.getYear(),
                        custom_date_picker.getMonth(),
                        custom_date_picker.getDayOfMonth(),
                        custom_time_picker.getCurrentHour(),
                        custom_time_picker.getCurrentMinute(),
                        0);

                if (selectedDate.before(currentDateTime)) {
                    MyFunction.showMessage("Invalid Time.");
                    txtTime.setText("");
                    //dialog.dismiss();
                } else {
                    String finalTime = (hour_of_12_hour_format == 0 ? "12:" + (minute <= 9 ? "0" + minute : minute) + " " + status
                            :
                            hour_of_12_hour_format + ":" + (minute <= 9 ? "0" + minute : minute) + " " + status);

                    txtTime.setText(finalTime);
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

    private void showDatePickerDialog(final EditText txtDate) {
        final AlertDialog.Builder dateDialog = new AlertDialog.Builder(this);

        //set custom layout
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View mView = layoutInflater.inflate(R.layout.custom_date_picker, null);

        dateDialog.setView(mView);
        dateDialog.setCancelable(true);

        final AlertDialog dialog = dateDialog.create();

        custom_date_picker = mView.findViewById(R.id.custom_add_date);
        custom_date_btn_ok = mView.findViewById(R.id.custom_add_date_ok);

        final Calendar now = Calendar.getInstance();

        custom_date_picker.init(now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH), null);

        custom_date_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar current = Calendar.getInstance();

                final Calendar date = Calendar.getInstance();

                date.set(custom_date_picker.getYear(),
                        custom_date_picker.getMonth(),
                        custom_date_picker.getDayOfMonth());

                //if the selected date is from previous date, error message will display
                if (date.compareTo(current) < 0) {
                    MyFunction.showMessage("Previous date cannot be select.");
                } else {

                    int month = date.get(Calendar.MONTH);
                    int day = date.get(Calendar.DAY_OF_MONTH);
                    int year = date.get(Calendar.YEAR);

                    String selectedDate = (month + 1) + "/" + (day > 31 ? 1 : day) + "/" + year;

                    txtDate.setText(selectedDate);

                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

    private void initCustomViews(View view) {

        txtAddEventName = view.findViewById(R.id.add_event_name);
        txtAddEventDate = view.findViewById(R.id.add_event_date);
        txtAddEventTime = view.findViewById(R.id.add_event_time);


        btnSave = view.findViewById(R.id.btn_save_event);
        btnCancel = view.findViewById(R.id.btn_cancel_add_event);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);

        no_event = findViewById(R.id.no_event);

        eventListView = findViewById(R.id.admin_listview);

        fab_add = findViewById(R.id.fab_add_event);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

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
                    }

                    Collections.reverse(events);

                    adminEventAdapter = new AdminEventAdapter(AdminForm.this, events);

                    if (adminEventAdapter.isEmpty()) {
                        no_event.setVisibility(View.VISIBLE);
                    } else {
                        no_event.setVisibility(View.INVISIBLE);
                        eventListView.setAdapter(adminEventAdapter);
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


        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();

                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                    Event event = postSnapShot.getValue(Event.class);

                    events.add(event);
                }

                Collections.reverse(events);

                adminEventAdapter = new AdminEventAdapter(AdminForm.this, events);

                if (adminEventAdapter.isEmpty()) {
                    no_event.setVisibility(View.VISIBLE);
                } else {
                    no_event.setVisibility(View.INVISIBLE);
                    eventListView.setAdapter(adminEventAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(AdminForm.class.getSimpleName(), databaseError.toException() + "");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            exitMessage();
        }
    }

    private void exitMessage() {
        final AlertDialog.Builder exit = new AlertDialog.Builder(this);

        exit.setTitle(R.string.app_name);
        exit.setMessage("Do you want to exit?");
        exit.setPositiveButton("No", null);
        exit.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        exit.create().show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(AdminForm.this, AdminProfileSettings.class));
            finish();
        } else if (id == R.id.nav_about) {
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
