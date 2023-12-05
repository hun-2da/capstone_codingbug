package com.capstone.codingbug.pagerFragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.capstone.codingbug.R;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;


public class ReadLocation_Fragment extends Fragment {
    public static TMapView tMapView2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_read_location_, container, false);

        LinearLayout linearLayoutTmap = (LinearLayout) view.findViewById(R.id.read_llt);
        tMapView2 = new TMapView(getActivity().getApplicationContext());

        tMapView2.setSKTMapApiKey("6QqIU9fnZUao65WJCM7ptafry6XfQovT1PoVoB4a");
        linearLayoutTmap.addView(tMapView2);

        // Inflate the layout for this fragment
        return view;
    }

}