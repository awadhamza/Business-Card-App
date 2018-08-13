package com.hamzaawad.businesscard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserObjectListModel{
    private List<userObject> userObjectArrayList;

    public UserObjectListModel(List<userObject> userObjectArrayList) {
        this.userObjectArrayList = userObjectArrayList;
    }

    public List<userObject> getUserObjectArrayList() {
        return userObjectArrayList;
    }

    public void setUserObjectArrayList(List<userObject> userObjectArrayList) {
        this.userObjectArrayList = userObjectArrayList;
    }

    @Override
    public String toString() {
        return "UserObjectListModel{" +
                "userObjectArrayList=" + userObjectArrayList +
                '}';
    }
}
