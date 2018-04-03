package com.app.seam.AccountSettings;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.seam.Database.MyDatabase;
import com.app.seam.Model.Profile;
import com.app.seam.Model.User;
import com.app.seam.MyFunction.MyFunction;
import com.app.seam.R;
import com.app.seam.TableStructure.UserTable;
import com.app.seam.UserForm.UserForm;

public class UserProfileSettings extends AppCompatActivity {

    private EditText txtFullName, txtUserType, txtUsername, txtPassword, txtConPassword;
    private FloatingActionButton fab_edit_user, fab_save_user;
    private Toolbar toolbar;
    private TextView lblConfirmPass;
    private CardView cardView_confirm_pass;

    private SQLiteDatabase mDb;

    private boolean isEditing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_settings);

        initDatabase();
        initViews();
        getProfileDetails();
        initSetTextFields();
        disableFields();
        initOnClickListener();

    }

    private void initOnClickListener() {
        fab_edit_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditing = true;
                fab_edit_user.setVisibility(View.INVISIBLE);
                enableFields();

            }
        });

        fab_save_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmptyFields())
                    MyFunction.showMessage("Please complete all fields");
                else if (passwordNotMatch())
                    MyFunction.showMessage("Password didn't match!");
                else
                    updateProfileSettings();
            }
        });
    }

    private void updateProfileSettings() {
        int profile_id = Profile.profile_id;
        String updateFullname = txtFullName.getText().toString();
        String updateUsername = txtUsername.getText().toString();
        String updatePassword = txtPassword.getText().toString();

        ContentValues values = new ContentValues();

        values.put(UserTable.COLUMN_FULLNAME, updateFullname);
        values.put(UserTable.COLUMN_USERNAME, updateUsername);
        values.put(UserTable.COLUMN_PASSWORD, updatePassword);

        boolean isUpdated = MyFunction.update(UserTable.TABLE_NAME, profile_id, values);

        if (isUpdated) {
            MyFunction.showMessage("User Profile Updated.");
            disableFields();
            fab_edit_user.setVisibility(View.VISIBLE);
            isEditing = false;
        } else
            MyFunction.showMessage("Error occur while updating user profile.");

    }

    private boolean passwordNotMatch() {
        if (!txtConPassword.getText().toString().equals(txtPassword.getText().toString()))
            return true;
        else
            return false;
    }

    private boolean isEmptyFields() {
        if (txtFullName.getText().toString().isEmpty()
                || txtUsername.getText().toString().isEmpty()
                || txtPassword.getText().toString().isEmpty()
                || txtConPassword.getText().toString().isEmpty())
            return true;
        else
            return false;

    }

    private void enableFields() {
        txtFullName.setEnabled(true);
        //txtUserType.setEnabled(true);
        txtUsername.setEnabled(true);
        txtPassword.setEnabled(true);

        lblConfirmPass.setVisibility(View.VISIBLE);
        cardView_confirm_pass.setVisibility(View.VISIBLE);

        fab_save_user.setVisibility(View.VISIBLE);
    }

    private void disableFields() {
        txtFullName.setEnabled(false);
        txtUserType.setEnabled(false);
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);

        lblConfirmPass.setVisibility(View.INVISIBLE);
        cardView_confirm_pass.setVisibility(View.INVISIBLE);

        fab_save_user.setVisibility(View.INVISIBLE);

    }

    private void initSetTextFields() {
        String fullname = Profile.fullName;
        String userType = Profile.userType;
        String username = Profile.userName;
        String password = Profile.password;

        txtFullName.setText(fullname);
        txtUserType.setText(userType);
        txtUsername.setText(username);
        txtPassword.setText(password);
    }

    private void getProfileDetails() {
        String userName = User.userName;
        String userType = User.userType;

        MyFunction.getUserProfileInfo(userName, userType);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lblConfirmPass = findViewById(R.id.acct_user_txt_confirm_pass);
        cardView_confirm_pass = findViewById(R.id.acct_card_re_pass);

        txtFullName = findViewById(R.id.acct_user_txt_full_name);
        txtUserType = findViewById(R.id.acct_user_txt_user_type);
        txtUsername = findViewById(R.id.acct_user_txt_username);
        txtPassword = findViewById(R.id.acct_user_txt_pass);
        txtConPassword = findViewById(R.id.acct_user_txt_repass);

        fab_edit_user = findViewById(R.id.fab_edit_user_profile);
        fab_save_user = findViewById(R.id.fab_save_profile_user);
    }

    private void initDatabase() {
        mDb = MyDatabase.getInstance(this).getReadableDatabase();
        MyFunction.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (isEditing) {
                disableFields();
                fab_edit_user.setVisibility(View.VISIBLE);
                isEditing = false;
            } else {
                startActivity(new Intent(UserProfileSettings.this, UserForm.class));
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isEditing) {
            disableFields();
            fab_edit_user.setVisibility(View.VISIBLE);
            isEditing = false;
        } else {
            startActivity(new Intent(UserProfileSettings.this, UserForm.class));
            finish();
        }

    }

}
