package com.example.memeroom.LoggedIn;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/*
Handle user logout
*/
public class Logout extends AsyncTask<String, Void, String> {

    private final String userToken;


    public Logout(String userToken) {
        this.userToken = userToken;

    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://memero0m.herokuapp.com/logout");
            httppost.addHeader("Authorization", "Bearer " + userToken);
            HttpResponse response = httpclient.execute(httppost);

            return Integer.toString(response.getStatusLine().getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        return null;
    }
}
