package com.hamzaawad.businesscard;

import android.net.Uri;
import android.widget.ImageView;

public class userObject {
    String fullName;
    String phoneNumber;
    String email;
    String companyName;
    String profession;
    String image;



    userObject(String fN, String pN, String e, String cN, String p, String i){
        fullName = fN;
        phoneNumber = pN;
        email = e;
        companyName = cN;
        profession = p;
        image = i;
    }

    @Override
    public String toString() {
        return "userObject{" +
                "fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", companyName='" + companyName + '\'' +
                ", profession='" + profession + '\'' +
                '}';
    }
}

