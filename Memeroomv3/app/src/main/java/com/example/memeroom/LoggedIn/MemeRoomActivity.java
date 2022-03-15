package com.example.memeroom.LoggedIn;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memeroom.LoggedIn.FollowInfo.FollowUser;
import com.example.memeroom.LoggedIn.MenuFragments.CameraFragment;
import com.example.memeroom.LoggedIn.MenuFragments.HomeFragment;
import com.example.memeroom.LoggedIn.MenuFragments.MemePicHandler.UploadMemePic;
import com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers.DisplaySearchProfile;
import com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers.GetNewestMeme;
import com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers.PressedObject;
import com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers.UploadLocation;
import com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers.UploadProfilePicture;
import com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers.UserNotFound;
import com.example.memeroom.LoggedIn.MenuFragments.NoFollowingMsg;
import com.example.memeroom.LoggedIn.MenuFragments.ProfileFragments.EditMemePic;
import com.example.memeroom.LoggedIn.MenuFragments.ProfileFragments.ProfileFragment;
import com.example.memeroom.LoggedIn.MenuFragments.SearchFragment;
import com.example.memeroom.LoggedIn.MenuFragments.SearchedProfile;
import com.example.memeroom.LoginAndCreate.MainActivity;
import com.example.memeroom.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import xdroid.toaster.Toaster;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
    Handle logged in activities such as NewsFeed, upload and edit pictures,
    searching and displaying profiles, logging out, location and menu interactions
*/
@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class MemeRoomActivity extends AppCompatActivity implements PressedObject,View.OnClickListener {


    private FusedLocationProviderClient fusedLocationClient;
    private ImageButton profilePicButton;

    private String userEmail;
    private String userToken;
    private String usernameSearch;
    private String encodedString;
    private SpannableString explainLocation;


    public final List<String> menuFragments = new ArrayList<>();

    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_memeroom);
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        userToken = getIntent().getStringExtra("USER_TOKEN");

        menuFragments.add("Home");
        menuFragments.add("Upload");
        menuFragments.add("Search");
        menuFragments.add("Profile");


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(menuListener);

        menu = bottomNav.getMenu();

        getSupportFragmentManager().beginTransaction().replace(R.id.showed_page,
                new HomeFragment()).commit();
    }

    //create a menu listener that will switch between menu fragments
    private final BottomNavigationView.OnNavigationItemSelectedListener menuListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    android.support.v4.app.Fragment clickedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.home:
                            clickedFragment = new HomeFragment();
                            break;
                        case R.id.upload:
                            clickedFragment = new CameraFragment();
                            AddMeme();
                            break;
                        case R.id.search:
                            clickedFragment = new SearchFragment();
                            break;
                        case R.id.profile:
                            clickedFragment = new ProfileFragment();
                            break;
                    }
                    assert clickedFragment != null;

                    String pressedMenuItem = menuItem.toString();
                    handleMenuClicks(pressedMenuItem);


                    getSupportFragmentManager().beginTransaction().replace(R.id.showed_page,
                            clickedFragment).commit();



                    return true;
                }
            };

    //disable already pressed menu button
    private void handleMenuClicks(String pressedMenuItem){
        int numOfMenuItems = 4;
        for (int menuItem = 0; menuItem < numOfMenuItems; menuItem++) {
            if (menu.getItem(menuItem).toString().equals(pressedMenuItem)){
                menu.getItem(menuItem).setEnabled(false);
            }
            else {
                menu.getItem(menuItem).setEnabled(true);
            }

        }
    }


    //get users location
    public void getLocation(View v) {

        if (ActivityCompat.checkSelfPermission(MemeRoomActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(MemeRoomActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    TextView tw = findViewById(R.id.profile_location);

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(MemeRoomActivity.this, Locale.getDefault());
                    String county = "";
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        county = addresses.get(0).getAdminArea();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stringExplainLocation(county);
                    tw.setText(explainLocation);
                    String userLocation = county;

                    new UploadLocation(userEmail, userToken, userLocation).execute();
                }
            }
        });
    }


    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[] {ACCESS_FINE_LOCATION}, 1);
    }


    //Function for creating round bitmap found at: https://stackoverflow.com/questions/21904229/making-image-circular-in-android
    public static Bitmap getCircularBitmapFrom(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        float radius = bitmap.getWidth() > bitmap.getHeight() ? ((float) bitmap
                .getHeight()) / 2f : ((float) bitmap.getWidth()) / 2f;
        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                radius, paint);

        return canvasBitmap;
    }


    public void takeProfilePic(View v) {

        profilePicButton = findViewById(R.id.addProfilePic);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0); }
        
        
        
    //Handle uploading profile picture or editing pictures
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        if (requestCode==0){
        Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
        Bitmap roundBitmap = getCircularBitmapFrom(bitmap);

        profilePicButton.setImageBitmap(roundBitmap);

        //bitmap to byte
        assert bitmap != null;
        ByteArrayOutputStream outputImage = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputImage );
        byte[] imageBytes = outputImage.toByteArray();

        //byte to string
        String encodedString = Base64.encodeToString(imageBytes, 0);

        new UploadProfilePicture(userEmail, userToken, encodedString).execute();}

        if (requestCode==1){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                Toaster.toast("Downloaded image");
                Toast.makeText(MemeRoomActivity.this, "Downloaded image",
                        android.widget.Toast.LENGTH_LONG).show();

                Bitmap editBitmap = bitmap;
                bitmap = Bitmap.createScaledBitmap(bitmap, 275, 275, false);

                //bitmap to byte
                ByteArrayOutputStream outputImage = new ByteArrayOutputStream(bitmap.getByteCount());
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputImage );
                byte[] imageBytes = outputImage.toByteArray();

                //byte to string
                this.encodedString = Base64.encodeToString(imageBytes, 0);

                ByteArrayOutputStream editOutputImage = new ByteArrayOutputStream(editBitmap.getByteCount());
                editBitmap.compress(Bitmap.CompressFormat.PNG, 50, editOutputImage );
                byte[] editImageBitmap = outputImage.toByteArray();


                EditMemePic editMemePic = new EditMemePic();
                Bundle args = new Bundle();
                args.putByteArray("bytemap", editImageBitmap);
                editMemePic.setArguments(args);

                getSupportFragmentManager().beginTransaction().replace(R.id.showed_page,
                        editMemePic).commit();


            } catch (Exception e) {
                getSupportFragmentManager().beginTransaction().replace(R.id.showed_page,
                        new HomeFragment()).commit();
                handleMenuClicks("Home");
                BottomNavigationView navigationView;
                navigationView= findViewById(R.id.bottom_navigation);
                navigationView.getMenu().getItem(0).setChecked(true);
            }
        }

    }


    
    @Override
    public void onClick(View v) {
        //change activity to show picture
        if (v instanceof ImageButton) {
            Toast.makeText(MemeRoomActivity.this, "Loading..",
                    android.widget.Toast.LENGTH_LONG).show();

            ImageButton b = (ImageButton) v;
            Drawable pic = b.getDrawable();
            Bitmap clickedMeme = ((BitmapDrawable) pic).getBitmap();

            //create new activity
            Intent intent = new Intent(MemeRoomActivity.this, DisplayMemeActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            intent.putExtra("USER_TOKEN", userToken);
            intent.putExtra("CLICKED_BITMAP", clickedMeme);
            startActivity(intent);
        }
       //display pressed following activities
       if (v instanceof TextView){
           List<String> myList = new ArrayList<>(Arrays.asList(((TextView) v).getText().toString().split(" ")));
           GetNewestMeme gnm = new GetNewestMeme(myList.get(0),userToken);
           gnm.profileInfoDelegate = this;
           gnm.execute();
       }

    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserToken() {
        return userToken;
    }


    //add all uploaded pictures to a grid layout
    @Override
    public void getUploadedMemes(String allMemes){
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
            memePic.setOnClickListener(MemeRoomActivity.this);
            //add pic to gridlayout
            grid.addView(memePic);
        }

    }


    @Override
    public void getLocation(String location) {
        if (location.equals("None")){
            location = " ";}
        TextView profileLocation = findViewById(R.id.profile_location);
        stringExplainLocation(location);
        profileLocation.setText(explainLocation);
    }

    @Override
    public void getProfilePicture(String pic) {
        ImageButton profilePicV = findViewById(R.id.addProfilePic);
        Bitmap bitmap;
        try{
            byte [] encodeByte=Base64.decode(pic,Base64.DEFAULT);
            bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Bitmap roundBitmap = getCircularBitmapFrom(bitmap);
            profilePicV.setImageBitmap(roundBitmap);

        }
        catch(Exception e){
            System.out.println("Error: " + e);
        }
    }

    public void searchProfile(View v){
        EditText getSearchText = findViewById(R.id.searchtext);
        usernameSearch = getSearchText.getText().toString();

        if (usernameSearch.equals("")){
            return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.searchContainer,
                new SearchedProfile()).commit();

        DisplaySearchProfile dSP = new DisplaySearchProfile(usernameSearch, userToken);
        dSP.searchProfileDelegate = this;
        dSP.execute();}

    //display searched profile    
    @Override
    public void getSearchProfile(String profileInfo) throws JSONException {
        RelativeLayout searchLayout = findViewById(R.id.displaysearch);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchLayout.getWindowToken(), 0);

        if (profileInfo.equals("")){
            getSupportFragmentManager().beginTransaction().replace(R.id.searchContainer,
                    new UserNotFound()).commit();
            return;}

        JSONObject json = new JSONObject(profileInfo);
        String location =  json.getString("location");
        String profilePic = json.getString("profilePic");
        String profileMemes = json.getString("allMemes");
        ImageView profilePicShow = findViewById(R.id.searchProfilePic);
        Bitmap bitmap;

        if (location.equals("null")){
            location = " ";
        }

        try{
            byte [] encodeByte= Base64.decode(profilePic,Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Bitmap roundBitmap = getCircularBitmapFrom(bitmap);
            profilePicShow.setImageBitmap(roundBitmap);
        }catch(Exception e){
            e.getMessage();
            System.out.println("Error: " + e);
        }

        try {
            TextView searchProfileLocation = findViewById(R.id.searchProfileLocation);
            stringExplainLocation(location);
            searchProfileLocation.setText(explainLocation);
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }
        getUploadedMemes(profileMemes);
    }

    //allow user to choose picture from gallery 
    @SuppressLint("IntentReset")
    private void AddMeme(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        startActivityForResult(chooserIntent, 1);
    }

    
    public void displayFollowing(View v){
        //create new activity
        Intent intent = new Intent(MemeRoomActivity.this, FollowingActivity.class);
        intent.putExtra("USER_EMAIL", userEmail);
        intent.putExtra("USER_TOKEN", userToken);
        startActivity(intent);
    }


    public void noUpload(View v){
        Toast.makeText(MemeRoomActivity.this, "Canceled upload",
                android.widget.Toast.LENGTH_LONG).show();
        getSupportFragmentManager().beginTransaction().replace(R.id.showed_page,
                new ProfileFragment()).commit();
        BottomNavigationView navigationView;
        navigationView= findViewById(R.id.bottom_navigation);
        navigationView.getMenu().getItem(3).setChecked(true);
    }

    public void yesUpload(View v){
        Toast.makeText(MemeRoomActivity.this, "Sharing upload",
                android.widget.Toast.LENGTH_LONG).show();
        EditText getInputText = findViewById(R.id.TitleMeme);
        String memeTitle = getInputText.getText().toString();
        new UploadMemePic(userEmail,userToken,encodedString,memeTitle).execute();
        getSupportFragmentManager().beginTransaction().replace(R.id.showed_page,
                new ProfileFragment()).commit();
        handleMenuClicks("Profile");

        //set profile to highlighted menu item
        BottomNavigationView navigationView;
        navigationView= findViewById(R.id.bottom_navigation);
        navigationView.getMenu().getItem(3).setChecked(true);
    }

    public void followProfile(View v){
        new FollowUser(userToken, userEmail, usernameSearch).execute();
    }

    //display NewsFeed in a LinearLayout
    @SuppressLint("SetTextI18n")
    @Override
    public void getNewsFeed(String username, String time) {
        LinearLayout newsFeed = findViewById(R.id.newsfeedcontainer);
        TextView followerTextView = new TextView(this);
        followerTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        followerTextView.setTextSize(24);
        followerTextView.setTextColor(Color.BLACK);
        followerTextView.setText(username +" Time: " + time);
        followerTextView.setPadding(10,10,10,10);
        followerTextView.setBackgroundResource(R.drawable.commentshape);
        followerTextView.setOnClickListener(this);
        newsFeed.addView(followerTextView);

    }
    
    @Override
    public void getNewestMeme(String memePic){
        Bitmap bitmap = null;
        try{
            byte [] encodeByte= Base64.decode(memePic,Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch(Exception e){
            e.getMessage();
            System.out.println("Error: " + e);
        }
        Intent intent = new Intent(MemeRoomActivity.this, DisplayMemeActivity.class);
        intent.putExtra("USER_EMAIL", userEmail);
        intent.putExtra("USER_TOKEN", userToken);
        intent.putExtra("CLICKED_BITMAP", bitmap);
        startActivity(intent);
    }

    @Override
    public void noFollowingMsg(){
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.showed_page,
                   new NoFollowingMsg()).commit();
        }
        catch (Exception e){
            System.out.println("Error:  " + e);

        }
    }


    public void logOut(View v){
        new Logout(userToken).execute();
        Toaster.toast("Logging out");
        Intent intent = new Intent(MemeRoomActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void stringExplainLocation(String location){
        String expLocation = "Last know location: \n";
        explainLocation = new SpannableString( expLocation+ location);
        explainLocation.setSpan(new RelativeSizeSpan(0.8f), 0,expLocation.length(), 0); // set size
        explainLocation.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, expLocation.length(), 0);// set color
}

}
