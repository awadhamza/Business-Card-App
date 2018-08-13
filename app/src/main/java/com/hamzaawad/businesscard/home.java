package com.hamzaawad.businesscard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class home extends Activity {
    Button addCard;
    SharedPreferences storage;
    SharedPreferences.Editor editor;
    UserObjectListModel arrayListHolder;
    RelativeLayout rowHolder;
    MyAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        storage = getSharedPreferences("hamza02", MODE_PRIVATE);
        editor = storage.edit();
        addCard = findViewById(R.id.add_card_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<userObject> dummyMode = new ArrayList<>();
        arrayListHolder = new UserObjectListModel(dummyMode);
//        /** Set Up Array List of Existing Keys **/
//        String key = "a";
//        while(storage.contains(key)){
//
//            Gson gson = new Gson();
//            String json = storage.getString(key, "");
//            profile = gson.fromJson(json, userObject.class);
//            profileArrayList.add(key);
//            key += "a";
//        }

//        editor = storage.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(arrayListHolder);
//        editor.putString("usermodel", json);
//        editor.commit();
        String temp = storage.getString("usermodel", "");

        Gson gson = new Gson();
        final UserObjectListModel parent = gson.fromJson(temp, UserObjectListModel.class);

        if (parent == null || parent.getUserObjectArrayList() == null) {
            Gson gson22 = new Gson();
            String json = gson22.toJson(arrayListHolder);
            editor.putString("usermodel", json);
            editor.commit();
            adapter = new MyAdapter(this, null);

            recyclerView.setAdapter(adapter);
        } else if (parent.getUserObjectArrayList().size() > 0) {
            Toast.makeText(this, "list size is : " + parent.getUserObjectArrayList().size(), Toast.LENGTH_SHORT).show();
            String current;

            ArrayList<String> profiles = new ArrayList<>();
            for (int i = 0; i < parent.getUserObjectArrayList().size(); i++) {
                current = parent.getUserObjectArrayList().get(i).fullName;
                profiles.add(current);
            }
            adapter = new MyAdapter(this, profiles);

            recyclerView.setAdapter(adapter);
            Log.d("salihsize", profiles.size()+ "");

            adapter.setClickListener(new MyAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (parent.getUserObjectArrayList() != null && parent.getUserObjectArrayList().size() > 0) {    /** Check if null first **/
                        Log.d("salihclick", position+ "");

                        Log.d("adapterListener", "position: " + position);

                        String nameClicked = adapter.getItem(position);
                        Log.d("adapterListenerString", nameClicked);

                        Intent openDetails = new Intent(home.this, profileDetails.class);

                        String temp = storage.getString("usermodel", "");
                        Gson gson = new Gson();
                        UserObjectListModel parent = gson.fromJson(temp, UserObjectListModel.class);

                        List<userObject> profList = parent.getUserObjectArrayList();
                        userObject obj = profList.get(position);

                        String json = gson.toJson(obj);
                        Log.d("errorAbove?", "made it here");

                        editor.putString("nameClicked", json);
                        editor.commit();
                        startActivity(openDetails);
                    }
                }
            });
        }else{

            adapter = new MyAdapter(this, null);

            recyclerView.setAdapter(adapter);
        }




        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextPage = new Intent(home.this, createScreen.class);
                startActivity(nextPage);

            }
        });


    }
}
