package com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*
requests a users NewsFeed
*/
public class GetNewsFeed extends AsyncTask<String, Void, String> {

    private final String email;
    private final String userToken;
    public PressedObject profileInfoDelegate = null;

    public GetNewsFeed(String email, String userToken) {
        this.email = email;
        this.userToken = userToken;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget= new HttpGet("https://memero0m.herokuapp.com/newsfeed/" + email);
        httpget.addHeader("Authorization", "Bearer " + userToken);

        try {
            HttpResponse response =httpclient.execute(httpget);
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));

            return org.apache.commons.io.IOUtils.toString(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
    The response is later sent to memeActivity to get displayed in a list.
    */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.equals("")){
             profileInfoDelegate.noFollowingMsg();
             return;
        }

        result = result.substring(1, result.length()-1);
        List<String> myNewsFeed = new ArrayList<>();
        try {
            myNewsFeed = new ArrayList<>(Arrays.asList(result.split("\\}\\,")));
        }
        catch(Exception e){
            System.out.println("Error: " + e);
        }
        for (String following : myNewsFeed) {
            try {
                following = following+"}";
                JSONObject json = new JSONObject(following);
                String username =  json.getString("username");
                String time = json.getString("time");

                profileInfoDelegate.getNewsFeed(username, time);
            }
            catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }

    }


}
