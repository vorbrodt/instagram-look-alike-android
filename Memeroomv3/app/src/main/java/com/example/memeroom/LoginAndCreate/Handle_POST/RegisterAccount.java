package com.example.memeroom.LoginAndCreate.Handle_POST;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
/*
Sends the server a new user
*/
public class RegisterAccount extends AsyncTask<String, Void, String> {
    private String username;
    private String email;
    private String password;
    private String confirmedPassword;
    private final WeakReference<Context> context;

    public RegisterAccount(String username, String email, String password, String confirmedPassword,Context context){
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmedPassword = confirmedPassword;
        this.context = new WeakReference<>(context);
        if (!checkPassword()){
            resetAll();
        }
    }

    /*
    Checks if password matches the criteria
    */
    private boolean checkPassword(){
        if (!password.equals(confirmedPassword)){
            Toast.makeText(context.get(), "Password did not match",
                    android.widget.Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.length()<8){
            Toast.makeText(context.get(), "Password to weak at least 8 characters",
                    android.widget.Toast.LENGTH_LONG).show();
            return false;
        }
        return true; }

    /*
    If the user fail to create an account make every thing null
    */
    private void resetAll(){
        username = null;
        email = null;
        password = null;
        confirmedPassword = null;
    }
    /*
    posting users information to the database
    */
    @Override
    protected String doInBackground(String... strings) {

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://memero0m.herokuapp.com/create_account");

            List<NameValuePair> nameValuePairs = new ArrayList<>(3);
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);


            return Integer.toString(response.getStatusLine().getStatusCode());

        }
        catch (Exception e) {
            System.out.println("Error: " + e);

        }


        return null;
    }
    /*
    Depending on the result form the server the user gets different options
    */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.equals("400")){
            Toast.makeText(context.get(), "Email or Username already taken",
                    android.widget.Toast.LENGTH_LONG).show();
        }
        else if (result.equals("200")){
            Toast.makeText(context.get(), "Account Created",
                    android.widget.Toast.LENGTH_LONG).show();
        }
    }
}
