package com.example.tupa_mobile.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tupa_mobile.Activities.NotificationActivity;
import com.example.tupa_mobile.Connections.Connection;
import com.example.tupa_mobile.Markers.Marker;
import com.example.tupa_mobile.Markers.MarkerAdapter;
import com.example.tupa_mobile.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    private LinearLayout bottomNavigationContainer;
    private BottomSheetBehavior bottomSheetBehavior;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageButton searchBack;
    private ViewGroup searchLayout;
    private Toolbar toolbar;
    private MenuItem searchItem, notificationItem;
    private ViewGroup mapFrame;
    private ArrayList<Marker> markers;
    private MarkerAdapter markerAdapter;
    private RecyclerView bottomDrawerRecycler;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapse);
        mCollapsingToolbarLayout.setTitleEnabled(false);

        toolbar = view.findViewById(R.id.mainToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Mapa");

        bottomNavigationContainer = view.findViewById(R.id.bottomNavigationContainer);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomNavigationContainer);
        bottomSheetBehavior.setPeekHeight(100);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // do stuff when the drawer is expanded
                }

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // do stuff when the drawer is collapsed
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // do stuff during the actual drag event for example
                // animating a background color change based on the offset

                // or for example hidding or showing a fab

                /*
                if (slideOffset > 0.2) {
                    if (fab.isShown()) {
                        fab.hide();
                    }
                } else if (slideOffset < 0.15) {
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }

                 */
            }
        });

        mapFrame = view.findViewById(R.id.mapFrame);
        mapFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bottomDrawerRecycler = view.findViewById(R.id.savedLocationsRecycler);
        bottomDrawerRecycler.setLayoutManager(new LinearLayoutManager(view.getContext()));
        markers = new ArrayList<>();
        markerAdapter = new MarkerAdapter(view.getContext(), markers);
        bottomDrawerRecycler.setAdapter(markerAdapter);

        searchLayout = view.findViewById(R.id.searchLayout);
        searchBack = view.findViewById(R.id.searchBack);

        createViews();
        setCloseSearchButton(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_menu, menu);
        searchItem = menu.findItem(R.id.searchItem);
        notificationItem = menu.findItem(R.id.notificationItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()){
            case R.id.searchItem:
                expandSearchLayout();
                break;
            case R.id.notificationItem:
                startNotificationActivity();
                break;
        }
        return true;
    }


    private void expandSearchLayout() {
        searchLayout.setVisibility(View.VISIBLE);
        toolbar.setContentInsetsAbsolute(0,0);
        searchItem.setVisible(false);
        notificationItem.setVisible(false);
    }

    private void closeSearchLayout(){
        searchLayout.setVisibility(View.GONE);
        toolbar.setContentInsetsAbsolute(44,44);
        searchItem.setVisible(true);
        notificationItem.setVisible(true);
    }

    public void setCloseSearchButton(View view){
        searchBack = view.findViewById(R.id.searchBack);
        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearchLayout();
            }
        });
    }

    private void startNotificationActivity() {
        Intent intent = new Intent(getContext(), NotificationActivity.class);
        startActivity(intent);
    }

    private void createViews(){
        markers.add(new Marker(R.drawable.clock_ilustraion_white_theme, "Home"));
        markers.add(new Marker(R.drawable.stormy, "Work"));
        markers.add(new Marker(R.drawable.configuration_icon_gray_dark_theme_dimmed, "School"));
    }

}