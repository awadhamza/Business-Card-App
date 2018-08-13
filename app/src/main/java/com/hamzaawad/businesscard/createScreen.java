package com.hamzaawad.businesscard;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.List;


public class createScreen extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PICK_IMAGE = 100;
    Uri imageURI;

    Button submitButton;
    Button pictureButton;

    ImageView profilePicture;

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

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);


        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                Log.d("map01", "clicked");
                gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                gmap.clear();
                gmap.addMarker(markerOptions);
            }
        });

        gmap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Enable Scrolling by removing the OnTouchListner
                scrollView = findViewById(R.id.scroll_view_create_screen);
                scrollView.setOnTouchListener(null);
                Log.d("map01", "idle map");
            }
        });

        gmap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                //Turn off scrollview scrolling
                Log.d("map01", "moving");
                scrollView = findViewById(R.id.scroll_view_create_screen);
                // Disable Scrolling by setting up an OnTouchListener to do nothing
                scrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
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
                        professionEditText.getText().toString(), imageURI != null ? imageURI.toString() : null);

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
