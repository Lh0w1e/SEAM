package com.app.seam.Login;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.seam.AdminForm.AdminForm;
import com.app.seam.Constant.Constant;
import com.app.seam.Database.MyDatabase;
import com.app.seam.MyFunction.MyFunction;
import com.app.seam.Model.User;
import com.app.seam.R;
import com.app.seam.Registration.SignUp;
import com.app.seam.UserForm.UserForm;

public class LoginForm extends AppCompatActivity {

    private Button btnCreate;
    private CardView btnLogin;

    private EditText txtUsername, txtPassword;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        initDatabase();
        initViews();
        initOnClickListener();

    }

    private void initDatabase() {
        mDb = MyDatabase.getInstance(this).getReadableDatabase();
        MyFunction.init(this);
    }

    private void initOnClickListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmptyFields()) {
                    MyFunction.showMessage("Please complete fields!");
                } else {

                    String username = txtUsername.getText().toString();
                    String password = txtPassword.getText().toString();

                    if (MyFunction.hasAccount(username, password)) {
                        //MyFunction.showMessage("Correct Account.");
                        String userType = MyFunction.getUserType(username);

                        if (userType.equals(Constant.TYPE_ADMIN)){

                            User.userName = username;
                            User.userType = userType;

                            startActivity(new Intent(LoginForm.this, AdminForm.class));
                            finish();
                        }else if (userType.equals(Constant.TYPE_USER)){
                            User.userName = username;
                            User.userType = userType;

                            startActivity(new Intent(LoginForm.this, UserForm.class));
                            finish();
                        }

                    } else {
                        MyFunction.showMessage("Username/Password is incorrect!");
                    }
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginForm.this, SignUp.class));
                finish();
            }
        });
    }

    private boolean isEmptyFields() {
        if (txtUsername.getText().toString().isEmpty() || txtPassword.getText().toString().isEmpty())
            return true;
        else
            return false;
    }

    private void initViews() {
        txtUsername = findViewById(R.id.txt_login_username);
        txtPassword = findViewById(R.id.txt_login_pass);
        btnLogin = findViewById(R.id.btn_login);
        btnCreate = findViewById(R.id.btn_new_acc);
    }

}
