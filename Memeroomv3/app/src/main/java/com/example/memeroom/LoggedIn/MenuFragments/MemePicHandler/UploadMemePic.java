package com.example.memeroom.LoggedIn.MenuFragments.MemePicHandler;

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
Send post request to database to upload picture
*/
public class UploadMemePic  extends AsyncTask<String, Void, String> {

    private final String email;
    private final String token;
    private final String memePic;
    private final String memeTitle;


    public UploadMemePic(String email, String token, String memePic, String memeTitle) {
        this.email = email;
        this.token = token;
        this.memePic = memePic;
        this.memeTitle = memeTitle;
    }


    @Override
    protected String doInBackground(String... strings) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://memero0m.herokuapp.com/upload/meme");

            httppost.addHeader("Authorization", "Bearer " + token);

            List<NameValuePair> nameValuePairs = new ArrayList<>(3);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("memePic", memePic));
            nameValuePairs.add(new BasicNameValuePair("memeTitle", memeTitle));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);



            return Integer.toString(response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }
}
