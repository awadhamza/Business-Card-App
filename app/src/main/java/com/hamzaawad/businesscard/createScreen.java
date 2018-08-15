package com.hamzaawad.businesscard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.drm.DrmStore;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

    private double markLat;
    private double markLong;

    MenuInflater menuInflater;

    LocationListener locationListener;
    LocationManager locationManager;
    ImageView profilePicture;
    ImageView profileIcon;
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

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("dddddd", location.getLatitude() + "");

                Log.d("dddddd", location.getLongitude() + "");

                LatLng lng1 = new LatLng(location.getLatitude(), location.getLongitude());

                MarkerOptions markerOptions1 = new MarkerOptions();
                markerOptions1.position(lng1);

                gmap.moveCamera(CameraUpdateFactory.newLatLng(lng1));
                gmap.clear();
                gmap.addMarker(markerOptions1);
                markLat = location.getLatitude();
                markLong = location.getLongitude();

                Log.d("dddddd", "added marker");

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100,
                    100, locationListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.save_menu, menu);
        MenuItem menuItem = menu.getItem(0);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.opt_save) {
            // Save Pressed
            Log.d("actionbar", "SAVED!");
            final LatLng tempLatLng = new LatLng(markLat, markLong);
            Log.d("retrieve", markLat + "");
            Log.d("retrieve", markLong + "");
            userObject userObj = new userObject(nameEditText.getText().toString(), phoneEditText.getText().toString(), emailEditText.getText().toString(), companyEditText.getText().toString(),
                    professionEditText.getText().toString(), imageURI != null ? imageURI.toString() : null, tempLatLng);

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
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        profileIcon = findViewById(R.id.profile_icon);
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

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
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
            Glide.with(this).load(imageURI).apply(RequestOptions.circleCropTransform()).into(profilePicture);
            //profileIcon.setVisibility(View.GONE);
        }
    }

}
