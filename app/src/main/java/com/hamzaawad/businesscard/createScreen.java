package com.hamzaawad.businesscard;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.drm.DrmStore;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class createScreen extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PICK_IMAGE = 100;
    Uri imageURI;


    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int LOCATION_REQUEST= 2;
    private static final int READ_REQUEST = 3;

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
    EditText addressSearchEditText;
    SharedPreferences storage;
    SharedPreferences.Editor editor;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapView mapView;
    private GoogleMap gmap;

    ScrollView scrollView;

    void initAddressSearch() {
        addressSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_SEARCH
                        || actionID == EditorInfo.IME_ACTION_DONE
                        || actionID == KeyEvent.ACTION_DOWN
                        || actionID == KeyEvent.KEYCODE_ENTER) {
                    //execute search
                    geoLocate();
                }

                return false;
            }
        });
    }

    void geoLocate() {
        String searchString = addressSearchEditText.getText().toString();
        Geocoder geocoder = new Geocoder(createScreen.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d("sdlkfj", "lskdfj");
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            double lat = address.getLatitude();
            double lng = address.getLongitude();
            LatLng searchedAddress = new LatLng(lat, lng);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(searchedAddress);

            Log.d("map01", "clicked");
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchedAddress, 7));
            gmap.clear();
            gmap.addMarker(markerOptions);
            markLat = searchedAddress.latitude;
            markLong = searchedAddress.longitude;

            Log.d("hamzaS", "Found location: " + address.toString());
        }
    }

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

    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final String[] GALLERY_PERMS={
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            gmap.setMyLocationEnabled(false);
        } else {
            gmap.setMyLocationEnabled(true);

        }
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        initAddressSearch();

        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                Log.d("map01", "clicked");
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
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

                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(lng1, 16));
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
                requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20,
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

        Boolean passed = validateUserFields();

        if (!passed) {
            return false;
        }

        resetIconColors();

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
        addressSearchEditText = findViewById(R.id.address_search_edit);
        profilePicture = findViewById(R.id.profile_picture);
        profileIcon = findViewById(R.id.profile_icon);
        scrollView = findViewById(R.id.scroll_view_create_screen);
        storage = getSharedPreferences("hamza02", MODE_PRIVATE);
        editor = storage.edit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create Profile");  // provide compatibility to all the versions
        }


//        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i("hamzaS", "Place: " + place.getName());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i("hamzaS", "An error occurred: " + status);
//            }
//        });

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
                if (ContextCompat.checkSelfPermission(createScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(GALLERY_PERMS, READ_REQUEST);
                    return;
                }
                openGallery();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_REQUEST) {
            if (ContextCompat.checkSelfPermission(createScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
        if(requestCode == LOCATION_REQUEST){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                gmap.setMyLocationEnabled(true);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        resetIconColors();
        finish();
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
            Log.d("blah", data.toString());
        }

    }

    void resetIconColors() {
        Drawable inse = nameEditText.getCompoundDrawables()[0];
        inse.setTint(Color.GRAY);
        inse = phoneEditText.getCompoundDrawables()[0];
        inse.setTint(Color.GRAY);
        inse = emailEditText.getCompoundDrawables()[0];
        inse.setTint(Color.GRAY);
        inse = companyEditText.getCompoundDrawables()[0];
        inse.setTint(Color.GRAY);
        inse = professionEditText.getCompoundDrawables()[0];
        inse.setTint(Color.GRAY);
    }

    void findFailureReason(String typeFail) {
        if (typeFail.equalsIgnoreCase("name")) {
            String name = nameEditText.getText().toString().trim();
            if (name.length() <= 0) {
                Toast.makeText(this, "Name field is empty", Toast.LENGTH_LONG).show();
            } else if (!name.contains(" ")) {
                Toast.makeText(this, "First and Last required", Toast.LENGTH_LONG).show();
            }
        } else if (typeFail.equalsIgnoreCase("phone")) {
            String phone = phoneEditText.getText().toString();
            if (!phone.startsWith("+")) {
                Toast.makeText(this, "Number must start with '+'", Toast.LENGTH_LONG).show();
            } else if (phone.length() != 15 && phone.length() != 16 || !tenNumbers()) {
                Toast.makeText(this, "Format: +1-111-111-1111 or +90-111-111-1111", Toast.LENGTH_LONG).show();
            } else if (!containsProperDashes()) {
                Toast.makeText(this, "Number must use proper dashes", Toast.LENGTH_LONG).show();
            }
        } else if (typeFail.equalsIgnoreCase("email")) {
            //email.endsWith(".") || email.startsWith("@") || !emailCheck()
            String email = emailEditText.getText().toString();
            if (email.endsWith(".")) {
                Toast.makeText(this, "Email can't end with '.'", Toast.LENGTH_LONG).show();
            } else if (email.startsWith("@")) {
                Toast.makeText(this, "Email can't start with '@'", Toast.LENGTH_LONG).show();
            } else if (!email.contains("@")) {
                Toast.makeText(this, "Missing '@'", Toast.LENGTH_LONG).show();
            } else if (!email.contains(".")) {
                Toast.makeText(this, "Missing email suffix ex. '.com'", Toast.LENGTH_LONG).show();
            } else if (email.length() < 5) {
                Toast.makeText(this, "Email length must be greater than 5 characters", Toast.LENGTH_LONG).show();
            } else if (!emailCheck()) {
                Toast.makeText(this, "Email is not valid", Toast.LENGTH_LONG).show();
            }
        } else if (typeFail.equalsIgnoreCase("company")) {
            String company = companyEditText.getText().toString();
            if (company.length() <= 0) {
                Toast.makeText(this, "Enter company name", Toast.LENGTH_LONG).show();
            }
        } else if (typeFail.equalsIgnoreCase("profession")) {
            String profession = professionEditText.getText().toString();
            if (profession.length() <= 0) {
                Toast.makeText(this, "Enter profession", Toast.LENGTH_LONG).show();
            }
        } else if (typeFail.equalsIgnoreCase("picture")) {
            Toast.makeText(this, "Select a profile picture", Toast.LENGTH_LONG).show();
        }
    }

    Boolean hasDashes(int i) {
        String phoneNumber = phoneEditText.getText().toString();
        int foundDashes = 0;
        for (int j = 0; j < phoneNumber.length(); j++) {
            if (phoneNumber.charAt(j) == '-') {
                foundDashes++;
                Log.d("hamzaV", "found dash");
            }
        }
        if (foundDashes != i) {
            return false;
        }
        return true;
    }

    Boolean containsNumber(String temp) {
        if (temp.contains("1") || temp.contains("2") || temp.contains("3") || temp.contains("4") || temp.contains("5")
                || temp.contains("6") || temp.contains("7") || temp.contains("8") || temp.contains("9") || temp.contains("0")) { //If contains any number
            return true;
        }
        return false;
    }

    public static String extractNumber(final String str) {

        if (str == null || str.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
                found = true;
            } else if (found) {
                // If we already found a digit before and this char is not a digit, stop looping
                break;
            }
        }

        return sb.toString();
    }

    Boolean containsTwoNumbers(String temp) {

        String ext = extractNumber(temp);
        //Will return numbers for head

        if (ext.length() <= 1) {
            //Single digit
            return false;
        } else if (ext.length() == 2) {
            //Double digit
            return true;
        } else {
            return false;
        }

        //can't add turkish fix to check for two numbers i.e. +90-
    }

    String cutPhoneNumber(String pNumber) {

        // Check head before cut
        if (pNumber.length() == 15) {
            String head = pNumber.substring(0, 3);
            if (!head.contains("+") || !head.contains("-") || !containsNumber(head)) {
                return null;
            }
        }
        // Check head before cut
        else if (pNumber.length() == 16) {
            String head = pNumber.substring(0, 4);
            if (!head.contains("+") || !head.contains("-") || !containsTwoNumbers(head)) {
                return null;
            }
        } else {
            return null;
        }

        if (pNumber.length() == 15) {
            return pNumber.substring(3, 15);
        }
        return pNumber.substring(4, 16);
    }

    Boolean containsProperDashes() {


        String phoneNumber = phoneEditText.getText().toString().trim();

        if (phoneNumber.length() != 16 && phoneNumber.length() != 15) {
            return false;
        }

        String shortenedNumber = cutPhoneNumber(phoneNumber);


        if (shortenedNumber == null) {
            return false;
        }

        int correctDashes = 0;
        int index = 0;
        int counter = 0;


        while (index < shortenedNumber.length()) {

            if (shortenedNumber.charAt(index) != '-' && counter % 3 == 0 && counter != 0) {
                return false;
            } else if (correctDashes == 2) {
                return true;
            } else if (shortenedNumber.charAt(index) == '-' && counter % 3 == 0 && counter != 0) {
                correctDashes++;
                counter = 0;
            } else {
                counter++;
            }
            index++;
        }

        return false;
    }

    Boolean tenNumbers() {
        String phoneNumber = cutPhoneNumber(phoneEditText.getText().toString().trim());
        if(phoneNumber == null){
            return false;
        }
        char piece;
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (i != 3 && i != 7) {
                piece = phoneNumber.charAt(i);
                if (!containsNumber(Character.toString(piece))) {
                    return false;
                }
            }
        }
        return true;
    }

    Boolean americanPhone() {
        String phoneNumber = phoneEditText.getText().toString().trim();

        if (phoneNumber.trim().length() != 15 || !containsProperDashes() || !phoneNumber.startsWith("+")) {
            //If american phone doesn't have 15 digits nor has three dashes nor has a plus in the beginning
            //ex. +1-123-456-7890
            if (phoneNumber.length() != 15) {
                Log.d("hamzaV", phoneNumber.length() + " isn't 15");
            }
            if (!containsProperDashes()) {
                Log.d("hamzaV", phoneNumber + " doesn't contain proper dashes");
            }
            if (!phoneNumber.startsWith("+")) {
                Log.d("hamzaV", phoneNumber + " doesn't start with +");
            }
            return false;
        }
        return true;
    }

    Boolean turkishPhone() {
        String phoneNumber = phoneEditText.getText().toString().trim();

        if (phoneNumber.trim().length() != 16 || !containsProperDashes() || !phoneNumber.startsWith("+")) {
            //If turkish phone doesn't have 15 digits nor has three dashes nor has a plus in the beginning
            //ex. +90-212-555-1212
            if (phoneNumber.length() != 16) {
                Log.d("hamzaV", phoneNumber.length() + " isn't 16");
            }
            if (!containsProperDashes()) {
                Log.d("hamzaV", phoneNumber + " doesn't contain proper dashes");
            }
            if (!phoneNumber.startsWith("+")) {
                Log.d("hamzaV", phoneNumber + " doesn't start with +");
            }
            return false;
        }
        return true;
    }

    Boolean emailCheck(){
        String email = emailEditText.getText().toString().trim();

        int atIndex = email.indexOf("@");

        if(email.charAt(atIndex + 1) == '.'){
            return false;
        }

        return true;
    }

    Boolean validateUserFields() {
        //Name check
        String name = nameEditText.getText().toString().trim();
        if (name.length() <= 0 || !name.contains(" ")) {                //If name is empty or doesn't have a space between characters, fails
            findFailureReason("name");
            Drawable inse = nameEditText.getCompoundDrawables()[0];
            inse.setTint(Color.RED);
            Log.d("hamzaV", "name failed");
            return false;
        } else {
            Drawable inse = nameEditText.getCompoundDrawables()[0];
            inse.setTint(Color.BLUE);
            Log.d("hamzaV", "name passed");
        }


        //Phone check
        if (!americanPhone() && !turkishPhone() || !tenNumbers()) {                      //If phone number is neither American nor Turkish, fail
            findFailureReason("phone");
            Drawable inse = phoneEditText.getCompoundDrawables()[0];
            inse.setTint(Color.RED);
            Log.d("hamzaV", "phone failed");
            return false;
        } else {
            Drawable inse = phoneEditText.getCompoundDrawables()[0];
            inse.setTint(Color.BLUE);
            Log.d("hamzaV", "passed phone");
        }

        //Email check
        String email = emailEditText.getText().toString();
        if (!email.contains("@") || !email.contains(".") || !(email.length() >= 5) || email.endsWith(".") || email.startsWith("@") || !emailCheck()) {
            //If email doesn't have '@' or doesn't have '.' and isn't greater than 5 chars
            //ex. h@a.c is 5 chars and could be valid
            findFailureReason("email");
            Drawable inse = emailEditText.getCompoundDrawables()[0];
            inse.setTint(Color.RED);
            Log.d("hamzaV", "email failed: " + email);
            return false;
        } else {
            Drawable inse = emailEditText.getCompoundDrawables()[0];
            inse.setTint(Color.BLUE);
            Log.d("hamzaV", "passed email");
        }

        //Company check
        String company = companyEditText.getText().toString();
        if (company.length() <= 0) {
            //If company field is empty
            findFailureReason("company");
            Drawable inse = companyEditText.getCompoundDrawables()[0];
            inse.setTint(Color.RED);
            Log.d("hamzaV", "company failed");
            return false;
        } else {
            //If company field is empty
            Drawable inse = companyEditText.getCompoundDrawables()[0];
            inse.setTint(Color.BLUE);
            Log.d("hamzaV", "passed company");
        }

        //Profession check
        String profession = professionEditText.getText().toString();
        if (profession.length() <= 0) {
            //If company field is empty
            findFailureReason("profession");
            Drawable inse = professionEditText.getCompoundDrawables()[0];
            inse.setTint(Color.RED);
            Log.d("hamzaV", "profession failed");
            return false;
        } else {
            //If company field is empty
            Drawable inse = professionEditText.getCompoundDrawables()[0];
            inse.setTint(Color.BLUE);
            Log.d("hamzaV", "passed profession");
        }

        //Picture check

        if (imageURI == null) {
            findFailureReason("picture");
            profileIcon.setImageTintList(ColorStateList.valueOf(Color.RED));
            Log.d("hamzaV", "picture failed");
            return false;
        } else {
            profileIcon.setImageTintList(ColorStateList.valueOf(Color.BLUE));
            Log.d("hamzaV", "passed picture");
        }


        return true;
    }

}
