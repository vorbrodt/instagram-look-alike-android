package com.example.memeroom.LoggedIn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.memeroom.LoggedIn.FollowInfo.DisplayFollowing;
import com.example.memeroom.LoggedIn.FollowInfo.FollowInterface;
import com.example.memeroom.LoggedIn.FollowInfo.FollowUser;
import com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers.DisplaySearchProfile;
import com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers.PressedObject;
import com.example.memeroom.LoggedIn.MenuFragments.SearchedProfile;
import com.example.memeroom.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Display accounts the user is following
*/
public class FollowingActivity extends AppCompatActivity implements FollowInterface, View.OnClickListener, PressedObject {

    private String userEmail;
    private String userToken;
    private String clickedFollower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        //get data that was sent from MemeRoomActivity
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        userToken = getIntent().getStringExtra("USER_TOKEN");

        DisplayFollowing dpf = new DisplayFollowing(userEmail, userToken);
        dpf.followDelegate = this;
        dpf.execute();
    }

    //add all account names the user is following and add them to LinearLayout
    @Override
    public void getFollowingNames(String followingNames) {
        followingNames = followingNames.substring(1, followingNames.length()-1);
        followingNames = followingNames.replaceAll("\\s+","");
        List<String> listOfUsers = new ArrayList<>(Arrays.asList(followingNames.split(",")));
        LinearLayout followLayout = findViewById(R.id.listuser);

        for (String user : listOfUsers) {
            TextView followerTextView = new TextView(this);
            followerTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            followerTextView.setTextSize(24);
            followerTextView.setTextColor(Color.BLACK);
            followerTextView.setText(user);
            followerTextView.setPadding(10,10,10,10);
            followerTextView.setBackgroundResource(R.drawable.commentshape);
            followerTextView.setOnClickListener(FollowingActivity.this);
            followLayout.addView(followerTextView);
            
        }}

    @Override
    public void onClick(View v) {
    //check if user in following list was clicked or if following user's meme was clicked
        if (v instanceof TextView){
            TextView clickedUser = (TextView) v;
            String username = clickedUser.getText().toString();
            clickedFollower = username;
            if (username.equals("")){
                return;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.replacethis,
                    new SearchedProfile()).commit();

            DisplaySearchProfile dSP = new DisplaySearchProfile(username, userToken);
            dSP.searchProfileDelegate = this;
            dSP.execute();
        }
        if (v instanceof ImageButton){
            ImageButton b = (ImageButton)v;
            Drawable pic = b.getDrawable();
            Bitmap clickedMeme  = ((BitmapDrawable) pic).getBitmap();

            //create new activity
            Intent intent = new Intent(FollowingActivity.this, DisplayMemeActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            intent.putExtra("USER_TOKEN", userToken);
            intent.putExtra("CLICKED_BITMAP", clickedMeme);
            startActivity(intent);
        }

        }

    @Override
    public void getLocation(String location) {
    }

    @Override
    public void getProfilePicture(String pic) {

    }

    //handle displaying following profiles
    @Override
    public void getSearchProfile(String profileInfo) throws JSONException {
        JSONObject json = new JSONObject(profileInfo);
        String location =  json.getString("location");
        String profilePic = json.getString("profilePic");
        String profileMemes = json.getString("allMemes");
        ImageView profilePicShow = findViewById(R.id.searchProfilePic);
        Bitmap bitmap;

        try{
            byte [] encodeByte= Base64.decode(profilePic,Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            bitmap = MemeRoomActivity.getCircularBitmapFrom(bitmap);
            profilePicShow.setImageBitmap(bitmap);
        }catch(Exception e){
            e.getMessage();
            System.out.println("Error: " + e);
        }

        try {
            TextView searchProfileLocation = findViewById(R.id.searchProfileLocation);
            String expLocation = "Last know location: \n";
            SpannableString explainLocation = new SpannableString( expLocation+ location);
            explainLocation.setSpan(new RelativeSizeSpan(0.8f), 0,expLocation.length(), 0); // set size
            explainLocation.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, expLocation.length(), 0);// set color
            searchProfileLocation.setText(explainLocation);
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }
        getUploadedMemes(profileMemes);
    }

    @Override
    public void getUploadedMemes(String allMemes) {
        if (allMemes.equals("[]"))return;
        allMemes = allMemes.substring(1, allMemes.length()-1);
        List<String> myList = new ArrayList<>(Arrays.asList(allMemes.split(",")));

        GridLayout grid = findViewById(R.id.grid);


        for (String pic : myList) {
            //convert string to bitmap again
            byte [] encodeByte=Base64.decode(pic,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

            //create pic object, set it to ImageButton
            ImageButton memePic = new ImageButton(this);
            memePic.setImageBitmap(bitmap);
            memePic.setOnClickListener(FollowingActivity.this);
            //add pic to gridlayout
            grid.addView(memePic);
        }
    }

    @Override
    public void getNewsFeed(String username, String time) {

    }

    @Override
    public void getNewestMeme(String memePic) {

    }

    @Override
    public void noFollowingMsg() {

    }


    public void followProfile(View v){
        new FollowUser(userToken, userEmail, clickedFollower).execute();
    }
}
