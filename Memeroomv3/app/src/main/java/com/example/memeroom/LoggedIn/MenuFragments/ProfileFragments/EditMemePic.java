package com.example.memeroom.LoggedIn.MenuFragments.ProfileFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.memeroom.R;

/*
Fragment displayed before user can upload picture, where user are 
allowed to edit their picture before upload
*/
public class EditMemePic extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try{
            View fragmentEdit = inflater.inflate(R.layout.fragment_edit_meme_pic, container, false);
            Bundle args = getArguments();
            assert args != null;
            byte[] byteArray = args.getByteArray("bytemap");
            assert byteArray != null;
            Bitmap memePic = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            ImageView pic = fragmentEdit.findViewById(R.id.EditMeme);
            pic.setImageBitmap(memePic);
            return fragmentEdit;
        }
        catch (Exception e){
            System.out.println("Error:" +e);
        }
        return inflater.inflate(R.layout.fragment_edit_meme_pic, container, false);
    }


}
