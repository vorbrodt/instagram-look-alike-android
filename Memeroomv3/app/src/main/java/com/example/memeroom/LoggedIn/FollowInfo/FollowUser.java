package com.example.memeroom.LoggedIn.FollowInfo;

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
Sends the server that a user has followed/UnFollowed an other user.
*/
public class FollowUser extends AsyncTask<String, Void, String> {

    private final String userToken;
    private final String userEmail;
    private final String followUser;


    public FollowUser(String userToken, String userEmail, String followUser) {
        this.userToken = userToken;
        this.userEmail = userEmail;
        this.followUser = followUser;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://memero0m.herokuapp.com/follow/user");

            httppost.addHeader("Authorization", "Bearer " + userToken);

            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("email", userEmail));
            nameValuePairs.add(new BasicNameValuePair("follow", followUser));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);

            return Integer.toString(response.getStatusLine().getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        return null;
    }
}
