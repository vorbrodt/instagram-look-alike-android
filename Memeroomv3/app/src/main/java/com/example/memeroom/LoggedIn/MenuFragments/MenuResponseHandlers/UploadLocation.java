package com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
/*
posting current location to the server
*/
public class UploadLocation extends AsyncTask<String, Void, String> {

    private final String userEmail;
    private final String userToken;
    private final String userLocation;

    public UploadLocation(String userEmail, String userToken, String location) {
        this.userEmail = userEmail;
        this.userToken = userToken;
        this.userLocation = location;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://memero0m.herokuapp.com/add_location");

            httppost.addHeader("Authorization", "Bearer " + userToken);

            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("email", userEmail));
            nameValuePairs.add(new BasicNameValuePair("location", userLocation));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);

            return Integer.toString(response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }}
