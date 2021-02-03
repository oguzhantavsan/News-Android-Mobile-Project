package edu.sabanciuniv.cs310oguzhantavsanhw3;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by atanaltay on 28/03/2017.
 */

public class CommentItems implements Serializable{

    private String name;
    private String message;

    public CommentItems(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }
}
