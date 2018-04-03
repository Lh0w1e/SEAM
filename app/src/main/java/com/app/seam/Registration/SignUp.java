package com.app.seam.Registration;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.seam.Constant.Constant;
import com.app.seam.Database.MyDatabase;
import com.app.seam.Login.LoginForm;
import com.app.seam.MyFunction.MyFunction;
import com.app.seam.R;
import com.app.seam.TableStructure.UserTable;
import com.app.seam.Util.DateTimeUtil;

public class SignUp extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText txtFullName, txtUserType, txtUsername, txtPassword, txtConPassword;
    private CardView btnSubmit;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initDatabase();

        initViews();
        initOnClickListener();

    }

    private void initDatabase() {
        MyFunction.init(this);
        mDb = MyDatabase.getInstance(getApplicationContext()).getReadableDatabase();
    }

    private void initOnClickListener() {
        txtUserType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserType();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmptyFields())
                    MyFunction.showMessage("Please complete all fields");
                else if (passwordNotMatch())
                    MyFunction.showMessage("Password did not match!");
                else if (isUsernameExists())
                    MyFunction.showMessage("Username already exists. Please try another username.");
                else
                    insertNewUser();
            }
        });
    }

    private boolean isUsernameExists(){
        String userName = txtUsername.getText().toString();

        if (MyFunction.isUsernameExists(userName)){
            return true;
        }else {
            return false;
        }

    }

    private void insertNewUser() {
        String saveFullName = txtFullName.getText().toString();
        String saveUserType = txtUserType.getText().toString();
        String saveUsername = txtUsername.getText().toString();
        String savePassword = txtPassword.getText().toString();
        String saveCurrTime = DateTimeUtil.currentTime();
        String saveCurrDate = DateTimeUtil.currentDate();

        ContentValues values = new ContentValues();

        values.put(UserTable.COLUMN_FULLNAME, saveFullName);
        values.put(UserTable.COLUMN_USER_TYPE, saveUserType);
        values.put(UserTable.COLUMN_USERNAME, saveUsername);
        values.put(UserTable.COLUMN_PASSWORD, savePassword);
        values.put(UserTable.COLUMN_TIME_CREATED, saveCurrTime);
        values.put(UserTable.COLUMN_DATE_CREATED, saveCurrDate);

        try {

            MyFunction.insert(UserTable.TABLE_NAME, values);

            Toast.makeText(this, "Registration Successful.", Toast.LENGTH_LONG).show();

            showLoginForm();

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void selectUserType() {
        final String[] userType = new String[]{Constant.TYPE_ADMIN, Constant.TYPE_USER};

        final AlertDialog.Builder select = new AlertDialog.Builder(this);

        select.setTitle("Select User Type");
        select.setItems(userType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    inputMasterPassword(userType, which);
                }else {
                    txtUserType.setText(userType[which]);
                }
            }
        });
        select.create().show();

    }

    private void inputMasterPassword(final String[] userType, final int pos) {
        final AlertDialog.Builder masterDialog = new AlertDialog.Builder(this);

        final View view = LayoutInflater.from(this).inflate(R.layout.custom_admin_pass, null);

        masterDialog.setView(view);

        final AlertDialog dialog = masterDialog.create();
        dialog.show();

        final EditText txtMasterPass = view.findViewById(R.id.txt_master_pass);
        final CardView btnSubmitPass = view.findViewById(R.id.btn_submit_master_pass);
        final CardView btnCancelPass = view.findViewById(R.id.btn_cancel_master_pass);

        btnSubmitPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String masterPass = txtMasterPass.getText().toString();
                if (masterPass.isEmpty()){
                    MyFunction.showMessage("Master Password is required!");
                }else if (!masterPass.equals(Constant.MASTER_PASSWORD)){
                    MyFunction.showMessage("Wrong Password!");
                }else if (masterPass.equals(Constant.MASTER_PASSWORD)){
                    txtUserType.setText(userType[pos]);
                    dialog.dismiss();
                }
            }
        });

        btnCancelPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    private boolean passwordNotMatch(){
        if (!txtConPassword.getText().toString().equals(txtPassword.getText().toString()))
            return true;
        else
            return false;
    }

    private boolean isEmptyFields(){
        if (txtFullName.getText().toString().isEmpty()
                || txtUserType.getText().toString().isEmpty()
                || txtUsername.getText().toString().isEmpty()
                || txtPassword.getText().toString().isEmpty()
                || txtConPassword.getText().toString().isEmpty())
            return true;
        else
            return false;

    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtFullName = findViewById(R.id.txt_full_name);
        txtUserType = findViewById(R.id.txt_user_type);
        txtUsername = findViewById(R.id.txt_username);
        txtPassword = findViewById(R.id.txt_pass);
        txtConPassword = findViewById(R.id.txt_repass);

        btnSubmit = findViewById(R.id.btn_submit);

    }

    @Override
    public void onBackPressed() {
        showLoginForm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            showLoginForm();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLoginForm(){
        startActivity(new Intent(SignUp.this, LoginForm.class));
        finish();
    }
}
