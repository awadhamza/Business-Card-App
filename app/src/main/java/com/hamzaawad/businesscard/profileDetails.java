package com.hamzaawad.businesscard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class profileDetails extends AppCompatActivity implements OnMapReadyCallback {
    ImageView profilePhoto;
    SharedPreferences storage;
    SharedPreferences.Editor editor;

    UserObjectListModel arrayListHolder;
    ArrayList<userObject> dummyMode;
    static userObject tempErase;

    MenuInflater menuInflater;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapView mapView;
    private GoogleMap gmap;

    private double markLat;
    private double markLong;

    TextView nameProfile;
    TextView phoneProfile;
    TextView emailProfile;
    TextView companyProfile;
    TextView professionProfile;

    ScrollView scrollView;

    String coordinates;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {

            gmap = googleMap;

            LatLng latLng = new LatLng(markLat, markLong);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            Log.d("retrieveMap", "here");
            gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            gmap.clear();
            gmap.addMarker(markerOptions);

            UiSettings uiSettings = googleMap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);

            gmap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    // Enable ScrollView scrolling
                    scrollView.requestDisallowInterceptTouchEvent(false);
                }
            });

            gmap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    //Turn off ScrollView scrolling
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
            });


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.erase_menu, menu);
        MenuItem menuItem = menu.getItem(0);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.opt_erase) {
            // Erase menu option pressed

            dummyMode = new ArrayList<>();
            String temp = storage.getString("usermodel", "");
            Gson gson = new Gson();
            // Get arrayListHolder from storage
            arrayListHolder = gson.fromJson(temp, UserObjectListModel.class);
            // Find profile to delete


            List<userObject> tempList = arrayListHolder.getUserObjectArrayList();

            for (int i = 0; i < tempList.size(); i++) {
                if (tempList.get(i).fullName.equals(tempErase.fullName) &&
                        tempList.get(i).phoneNumber.equals(tempErase.phoneNumber) &&
                        tempList.get(i).email.equals(tempErase.email) &&
                        tempList.get(i).companyName.equals(tempErase.companyName) &&
                        tempList.get(i).profession.equals(tempErase.profession)) {
                    Log.d("deleteProf", "objects are equal");
                    Log.d("salihdel", i + "");
                    tempList.remove(i);
                    arrayListHolder.setUserObjectArrayList(tempList);

                    // Put new usermodel object back into storage
                    Gson gson2 = new Gson();
                    String json = gson2.toJson(arrayListHolder);
                    editor = storage.edit();
                    editor.putString("usermodel", json);
                    editor.commit();

                    Log.d("deleteProf", "deleted, going home");
                    // Go back home after delete
                    finish();
                }
            }

            // Put new usermodel object back into storage
            Gson gson2 = new Gson();
            String json = gson2.toJson(arrayListHolder);
            editor = storage.edit();
            editor.putString("usermodel", json);
            editor.commit();

            // Go back home after delete
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        nameProfile = findViewById(R.id.name_profile_detail);
        phoneProfile = findViewById(R.id.phone_profile_detail);
        emailProfile = findViewById(R.id.email_profile_detail);
        companyProfile = findViewById(R.id.company_profile_detail);
        professionProfile = findViewById(R.id.profession_profile_detail);
        profilePhoto = findViewById(R.id.chosen_picture);
        mapView = findViewById(R.id.coordinates_profile_detail);
        scrollView = findViewById(R.id.scroll_view_profile_detail);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        storage = getSharedPreferences("hamza02", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = storage.getString("nameClicked", "");
        final userObject obj = gson.fromJson(json, userObject.class);
        tempErase = obj;

        nameProfile.setText(obj.fullName);
        phoneProfile.setText(obj.phoneNumber);
        emailProfile.setText(obj.email);
        companyProfile.setText(obj.companyName);
        professionProfile.setText(obj.profession);
        if (obj.image != null) {
            Glide.with(this).load(obj.image).apply(RequestOptions.circleCropTransform()).into(profilePhoto);
        }


        markLat = obj.coordinates.latitude;
        markLong = obj.coordinates.longitude;

        Log.d("retrieveMap", "here1");
        if (obj.coordinates != null && gmap != null) {
//            coordinates = "Latitude: " + obj.coordinates.latitude + ", Longitude: " + obj.coordinates.longitude;
            LatLng latLng = new LatLng(obj.coordinates.latitude, obj.coordinates.longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            Log.d("retrieveMap", "here");
            gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            gmap.clear();
            gmap.addMarker(markerOptions);
            markLat = latLng.latitude;
            markLong = latLng.longitude;
//            coordinatesProfile.setText(coordinates);
        }

    }
}
