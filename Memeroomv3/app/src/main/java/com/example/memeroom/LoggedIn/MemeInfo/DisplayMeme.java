package com.example.memeroom.LoggedIn.MemeInfo;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
Request the server to send back information about a "meme"
*/
public class DisplayMeme extends AsyncTask<String, Void, String> {

    private final String token;
    private final String pressedPicture;

    public MemeInfoInterface memeInterface;

    public DisplayMeme(String token, String pressedPicture) {
        this.token = token;
        this.pressedPicture = pressedPicture;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {
        try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("https://memero0m.herokuapp.com/display/meme");

                httppost.addHeader("Authorization", "Bearer " + token);

                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair("memePic", pressedPicture));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

                HttpResponse response = httpclient.execute(httppost);

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), StandardCharsets.UTF_8));

            return org.apache.commons.io.IOUtils.toString(in);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
    Sends the new information about the meme to the clint and runs a function in
    DisplayMemeActivity
    */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null) {
            try {
                JSONObject json = new JSONObject(result);
                String title =  json.getString("memeTitle");
                String likes = json.getString("likes");
                String dislikes = json.getString("dislikes");
                String comments = json.getString("comments");
                memeInterface.getMemeInfo(title,likes,dislikes, comments);

            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("Error: " + e);
            }
        }
    }




}