package com.hamzaawad.businesscard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.List;


public class createScreen extends AppCompatActivity {
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

    ScrollView scrollView;
    //TODO: Add google maps pin-marker selection option


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
