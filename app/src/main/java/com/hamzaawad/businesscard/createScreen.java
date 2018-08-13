package com.hamzaawad.businesscard;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.util.List;


public class createScreen extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PICK_IMAGE = 100;
    Uri imageURI;

    double markLat;
    double markLong;

    Button submitButton;
    Button pictureButton;

    LocationManager locationManager;

    ImageView profilePicture;
    FusedLocationProviderClient mFusedLocationClient;
    EditText nameEditText;
    EditText phoneEditText;
    EditText emailEditText;
    EditText companyEditText;
    EditText professionEditText;
    SharedPreferences storage;
    SharedPreferences.Editor editor;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapView mapView;
    private GoogleMap gmap;


    ScrollView scrollView;
    //TODO: Add google maps pin-marker selection option


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
        gmap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            gmap.setMyLocationEnabled(true);
        }

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        try {
//
//            mFusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            // Got last known location. In some rare situations this can be null.
//                            if (location != null) {
//                                // Logic to handle location object
//                                LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
//                                MarkerOptions markerOptions1 = new MarkerOptions();
//                                markerOptions1.position(latLng1);
//
//                                Log.d("map02", "found");
//                                gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
//                                gmap.clear();
//                                gmap.addMarker(markerOptions1);
//
//                            }
//                        }
//                    });
//        }  catch (SecurityException e) {
//            Toast.makeText(this, "GPS FAILURE", Toast.LENGTH_LONG).show();
//        }

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                Log.d("map01", "clicked");
                gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                gmap.clear();
                gmap.addMarker(markerOptions);
                markLat = latLng.latitude;
                markLong = latLng.longitude;
            }
        });

        gmap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Enable Scrolling by removing the OnTouchListner
                Log.d("map01", "idle map");
                scrollView.requestDisallowInterceptTouchEvent(false);
            }
        });

        gmap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                //Turn off scrollview scrolling
                Log.d("map01", "moving");
                // Disable Scrolling by setting up an OnTouchListener to do nothing
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_screen);
        nameEditText = findViewById(R.id.name_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        companyEditText = findViewById(R.id.company_edit_text);
        professionEditText = findViewById(R.id.profession_edit_text);
        profilePicture = findViewById(R.id.profile_picture);

        scrollView = findViewById(R.id.scroll_view_create_screen);
        storage = getSharedPreferences("hamza02", MODE_PRIVATE);
        editor = storage.edit();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        pictureButton = findViewById(R.id.buttonLoadPicture);
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });


        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userObject userObj = new userObject(nameEditText.getText().toString(), phoneEditText.getText().toString(), emailEditText.getText().toString(), companyEditText.getText().toString(),
                        professionEditText.getText().toString(), imageURI != null ? imageURI.toString() : null, new LatLng(markLat, markLong));

                String temp = storage.getString("usermodel", "");
                Log.d("salih2", temp);
                Gson gson = new Gson();
                UserObjectListModel obj = gson.fromJson(temp, UserObjectListModel.class);
                List<userObject> tempObj = obj.getUserObjectArrayList();
                tempObj.add(userObj);

                Gson gson22 = new Gson();
                String json = gson22.toJson(new UserObjectListModel(tempObj));
                editor.putString("usermodel", json);
                editor.commit();

                finish();
            }
        });

    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageURI = data.getData();
            profilePicture.setImageURI(imageURI);
            Glide.with(this).load(imageURI).into(profilePicture);

        }
    }

}
