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
Posting a users profile picture to the server
*/
public class UploadProfilePicture extends AsyncTask<String, Void, String> {
    private final String userEmail;
    private final String userToken;
    private final String userBitmap;


    public UploadProfilePicture(String email, String token, String bitmap) {
        this.userEmail = email;
        this.userToken = token;
        this.userBitmap = bitmap;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://memero0m.herokuapp.com/upload/profile/pic");
            httppost.addHeader("Authorization", "Bearer " + userToken);

            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("email", userEmail));
            nameValuePairs.add(new BasicNameValuePair("profilePic", userBitmap));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);

            return Integer.toString(response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }
}
