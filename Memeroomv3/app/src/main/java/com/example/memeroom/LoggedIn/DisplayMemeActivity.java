package com.example.memeroom.LoggedIn;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.memeroom.LoggedIn.MemeInfo.DisplayMeme;
import com.example.memeroom.LoggedIn.MemeInfo.MemeInfoInterface;
import com.example.memeroom.LoggedIn.MemeInfo.MemeVote;
import com.example.memeroom.LoggedIn.MemeInfo.PostComment;
import com.example.memeroom.R;

import org.json.JSONObject;
import java.io.ByteArrayOutputStream;

/*
Handle display of uploaded pictures
*/
public class DisplayMemeActivity extends AppCompatActivity implements MemeInfoInterface {

    private String userEmail;
    private String userToken;
    private String encodedMemePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_meme);

        userEmail = getIntent().getStringExtra("USER_EMAIL");
        userToken = getIntent().getStringExtra("USER_TOKEN");
        Bitmap clickedMeme = getIntent().getParcelableExtra("CLICKED_BITMAP");

        ImageView showPic = findViewById(R.id.memePicture);
        showPic.setImageBitmap(clickedMeme);

        String encodedString = bitmapToString(clickedMeme);
        DisplayMeme dpm = new DisplayMeme(userToken,encodedString);
        dpm.memeInterface = this; //must add this interface to dpm in order for it to work
        dpm.execute();
    }


    private String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream outputImage = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputImage );
        byte[] imageBytes = outputImage.toByteArray();
        System.out.println("we are back in business");

        //byte to string
        String encodedString = Base64.encodeToString(imageBytes, 0);
        this.encodedMemePicture = encodedString;
        return encodedString;
    }

    @Override
    public void getMemeInfo(String title, String likes, String dislikes, String comments) {
        //here we set title, likes, dislikes for the meme
        TextView memeTitle = findViewById(R.id.memeTitle);
        memeTitle.setText(title);
        if (likes.equals("None")){likes = "0";}
        if (dislikes.equals("None")){dislikes = "0";}
        TextView memeLikes = findViewById(R.id.likes);
        memeLikes.setText(likes);
        TextView memeDislikes = findViewById(R.id.dislikes);
        memeDislikes.setText(dislikes);

        //here we set comments
        displayComments(comments);
    }

    public void upVote(View v){
        clickAble(false);
        LinearLayout commentSection = findViewById(R.id.commentSection);
        commentSection.removeAllViews();

        new MemeVote(userEmail, userToken, "upVote", encodedMemePicture).execute();
        DisplayMeme dpm = new DisplayMeme(userToken,encodedMemePicture);
        dpm.memeInterface = this;
        dpm.execute();
    }

    public void downVote(View v){
        clickAble(false);
        LinearLayout commentSection = findViewById(R.id.commentSection);
        commentSection.removeAllViews();

        new MemeVote(userEmail, userToken, "downVote", encodedMemePicture).execute();
        DisplayMeme dpm = new DisplayMeme(userToken,encodedMemePicture);
        dpm.memeInterface = this;
        dpm.execute();

    }

    public void AddComment(View v){
        clickAble(false);
        EditText comment = findViewById(R.id.commenttext);
        if (comment.toString().length() > 0){
            new PostComment(userToken, userEmail,comment.getText().toString(), encodedMemePicture).execute();

            comment.setText("");

            LinearLayout commentSection = findViewById(R.id.commentSection);
            commentSection.removeAllViews();

            DisplayMeme dpm = new DisplayMeme(userToken,encodedMemePicture);
            dpm.memeInterface = this;
            dpm.execute();
        }

        }

    private void displayComments(String comments) {
        System.out.println("before comments:    " + comments);
        comments = comments.substring(1, comments.length()-1);
        LinearLayout commentSection = findViewById(R.id.commentSection);

        try{
        //take every comment from the input and put it in textView then add to listView
        while (comments.length()>0){
            JSONObject json = new JSONObject(comments);

            System.out.println("this is comments   "+ comments);
            System.out.println("thw whole thing:     "+ json.toString());
            String Msg =  json.getString("Msg");
            System.out.println("Msg " + Msg);
            String user = json.getString("user");
            System.out.println("user "+ user);

            int removeLength = Msg.length() + user.length()+23;
            TextView commentTextView = new TextView(this);
            String username = user.substring(1, user.length()-1);


            SpannableString makeNiceComment = new SpannableString("Posted by: " + username + "\n"  +  Msg);
            makeNiceComment.setSpan(new RelativeSizeSpan(0.5f), 0,11, 0); // set size
            makeNiceComment.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, 11, 0);// set color
            makeNiceComment.setSpan(new RelativeSizeSpan(0.7f), 11,username.length()+11, 0); // set size
            makeNiceComment.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.smallBlueText)), 11, username.length()+11, 0);// set color


            commentTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            commentTextView.setText(makeNiceComment);
            commentTextView.setTextSize(24);
            commentTextView.setPadding(10,10,10,10);
            commentTextView.setBackgroundResource(R.drawable.commentshape);
            commentTextView.setTextColor(Color.BLACK);
            commentSection.addView(commentTextView);
            if (comments.length() == (removeLength)){break;}
            comments = comments.substring(removeLength+2);
        }}
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
        clickAble(true);


    }

        //enable or disable buttons
        private void clickAble(Boolean click){

            ImageButton like = findViewById(R.id.upVote);
            ImageButton dislike = findViewById(R.id.downVote);
            Button commentButton = findViewById(R.id.commentButton);
            like.setEnabled(click);
            dislike.setEnabled(click);
            commentButton.setEnabled(click);
        }


    }

