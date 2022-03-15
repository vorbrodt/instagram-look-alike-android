package com.example.memeroom.LoggedIn.MemeInfo;


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
Sends a request to the server to upp/down vote or undo previous actions
on a posted meme.
*/
public class MemeVote  extends AsyncTask<String, Void, String> {

    private final String email;
    private final String token;
    private final String memePic;
    private final String vote;


    public MemeVote(String email, String token, String vote, String memePic) {
        this.email = email;
        this.token = token;
        this.memePic = memePic;
        this.vote = vote;
    }


    @Override
    protected String doInBackground(String... strings) {
        try {
            HttpClient httpclient = new DefaultHttpClient();


            HttpPost httppost = new HttpPost("https://memero0m.herokuapp.com/meme/" + vote);
            httppost.addHeader("Authorization", "Bearer " + token);

            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("memePic", memePic));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);

            return Integer.toString(response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }
}
