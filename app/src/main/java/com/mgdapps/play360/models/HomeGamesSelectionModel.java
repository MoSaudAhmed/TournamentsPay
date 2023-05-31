package com.mgdapps.play360.models;

import android.graphics.drawable.Drawable;

public class HomeGamesSelectionModel {

    private String name = "";
    private Drawable image = null;

    public HomeGamesSelectionModel(String name, Drawable image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
