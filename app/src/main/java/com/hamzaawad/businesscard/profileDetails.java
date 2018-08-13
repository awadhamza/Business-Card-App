package com.hamzaawad.businesscard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class profileDetails extends Activity {
    ImageView profilePhoto;
    SharedPreferences storage;
    SharedPreferences.Editor editor;

    UserObjectListModel arrayListHolder;
    ArrayList<userObject> dummyMode;
    Button deleteButton;

    TextView nameProfile;
    TextView phoneProfile;
    TextView emailProfile;
    TextView companyProfile;
    TextView professionProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        nameProfile = findViewById(R.id.name_profile_detail);
        phoneProfile = findViewById(R.id.phone_profile_detail);
        emailProfile = findViewById(R.id.email_profile_detail);
        companyProfile = findViewById(R.id.company_profile_detail);
        professionProfile = findViewById(R.id.profession_profile_detail);
        deleteButton = findViewById(R.id.delete_button);
        profilePhoto = findViewById(R.id.chosen_picture);

        storage = getSharedPreferences("hamza02", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = storage.getString("nameClicked", "");
        final userObject obj = gson.fromJson(json, userObject.class);

        nameProfile.setText(obj.fullName);
        phoneProfile.setText(obj.phoneNumber);
        emailProfile.setText(obj.email);
        companyProfile.setText(obj.companyName);
        professionProfile.setText(obj.profession);
//        profilePhoto.setImageURI(obj.image);
        if (obj.image != null)
            Glide.with(this).load(obj.image).into(profilePhoto);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dummyMode = new ArrayList<>();
                String temp = storage.getString("usermodel", "");
                Gson gson = new Gson();
                // Get arrayListHolder from storage
                arrayListHolder = gson.fromJson(temp, UserObjectListModel.class);
                // Find profile to delete


                List<userObject> tempList = arrayListHolder.getUserObjectArrayList();

                for (int i = 0; i < tempList.size(); i++) {
                    if (tempList.get(i).fullName.equals(obj.fullName) &&
                            tempList.get(i).phoneNumber.equals(obj.phoneNumber) &&
                            tempList.get(i).email.equals(obj.email) &&
                            tempList.get(i).companyName.equals(obj.companyName) &&
                            tempList.get(i).profession.equals(obj.profession)) {
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
        });

    }
}
