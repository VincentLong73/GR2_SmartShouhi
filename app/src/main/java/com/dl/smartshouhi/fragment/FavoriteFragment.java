package com.dl.smartshouhi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dl.smartshouhi.R;


public class FavoriteFragment extends Fragment{
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            return inflater.inflate(R.layout.layout_profile, container, false);
            return inflater.inflate(R.layout.activity_dashboard, container, false);
        }
}
