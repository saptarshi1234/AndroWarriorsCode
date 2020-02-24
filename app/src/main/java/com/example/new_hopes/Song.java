package com.example.new_hopes;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Song implements Serializable {
    String name;
    File imageLocation;
    File songLocation;
    boolean isDownloaded;

}


