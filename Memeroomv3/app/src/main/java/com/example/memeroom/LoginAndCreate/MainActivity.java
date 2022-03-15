package com.example.memeroom.LoginAndCreate;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.memeroom.LoggedIn.MemeRoomActivity;
import com.example.memeroom.LoginAndCreate.Handle_POST.AsyncTaskResponse;
import com.example.memeroom.LoginAndCreate.Handle_POST.CheckLogin;
import com.example.memeroom.LoginAndCreate.Handle_POST.RegisterAccount;
import com.example.memeroom.R;

/*
Handle login details and button presses on login page
*/
public class MainActivity extends AppCompatActivity implements AsyncTaskResponse {

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();
        Login loginPage = new Login();
        fm.beginTransaction().replace(R.id.container, loginPage).commit();
    }

    public void DisplayCreateAccount(View v){
        //Create fragment for creating account
        CreateAccount cA = new CreateAccount();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().addToBackStack(null).replace(R.id.container, cA).commit();
    }

    public void login(View v ){
        EditText getInputText = findViewById(R.id.email_input);
        String email = getInputText.getText().toString();
        userEmail = email;
        getInputText = findViewById(R.id.password_input);
        String password = getInputText.getText().toString();
        CheckLogin checkLogin = new CheckLogin(email, password);
        checkLogin.delegate = MainActivity.this;
        checkLogin.execute();
    }

    @Override
    public void pressedLogin(String output, String token){
        if (output.equals("200")){
            //sending ous to MemeRoomActivity and transfers some variables
            Intent intent = new Intent(MainActivity.this, MemeRoomActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            intent.putExtra("USER_TOKEN", token);
            startActivity(intent);
        }
        else {
            Toast.makeText(MainActivity.this, "Wrong email or password. Please try again!",
                    android.widget.Toast.LENGTH_LONG).show();
            }
    }


    public void registerAccount(View v){
        EditText getInputText = findViewById(R.id.username_input);
        String username = getInputText.getText().toString();
        getInputText = findViewById(R.id.email_input);
        String email = getInputText.getText().toString();
        getInputText = findViewById(R.id.password_input);
        String password = getInputText.getText().toString();
        getInputText = findViewById(R.id.confirm_password);
        String confirmedPassword = getInputText.getText().toString();


        FragmentManager fm = getFragmentManager();
        Login loginPage = new Login();
        fm.beginTransaction().replace(R.id.container, loginPage).commit();

        new RegisterAccount(username, email, password, confirmedPassword,MainActivity.this).execute();
    }


    public void backToLogin(View v) {
        FragmentManager fm = getFragmentManager();
        Login loginPage = new Login();
        fm.beginTransaction().replace(R.id.container, loginPage).commit();
    }

}