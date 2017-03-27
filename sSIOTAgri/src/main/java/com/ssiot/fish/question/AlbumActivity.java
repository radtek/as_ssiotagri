package com.ssiot.fish.question;

import android.os.Bundle;
import android.widget.ImageView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;

public class AlbumActivity extends HeadActivity{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ImageView imageView = (ImageView) findViewById(R.id.album_img);
        
    }
}