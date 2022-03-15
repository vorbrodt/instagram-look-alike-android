package com.example.memeroom.LoggedIn.MenuFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.memeroom.R;

/*
Fragment displayed whenever a user has searched for an account
*/
public class SearchedProfile extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_searched_profile, container, false);
    }


}
