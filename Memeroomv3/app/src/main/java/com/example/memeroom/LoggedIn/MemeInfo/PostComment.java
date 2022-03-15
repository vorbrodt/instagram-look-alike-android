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
Sends a new comment to on a specific meme to the database
*/
public class PostComment  extends AsyncTask<String, Void, String> {

    private final String token;
    private final String email;
    private final String comment;
    private final String memePic;


    public PostComment(String token, String email, String comment, String memePic) {
        this.token = token;
        this.email = email;
        this.comment = comment;
        this.memePic = memePic;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            HttpClient httpclient = new DefaultHttpClient();


            HttpPost httppost = new HttpPost("https://memero0m.herokuapp.com/post/comment");
            httppost.addHeader("Authorization", "Bearer " + token);

            List<NameValuePair> nameValuePairs = new ArrayList<>(3);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("memePic", memePic));
            nameValuePairs.add(new BasicNameValuePair("comment", comment));


            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);

            return Integer.toString(response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }
}