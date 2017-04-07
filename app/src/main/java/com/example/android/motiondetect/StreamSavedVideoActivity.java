package com.example.android.motiondetect;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by alex on 4/7/2017.
 */

public class StreamSavedVideoActivity extends AppCompatActivity{
    VideoView vidView;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream_saved_video);
        vidView = (VideoView)findViewById(R.id.myVideo);
        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(vidView);

        // create a progress bar while the video file is loading
        progressDialog = new ProgressDialog(this);
        // set a message for the progress bar
        progressDialog.setMessage("Loading...");
        //set the progress bar not cancelable on users' touch
        progressDialog.setCancelable(false);
        // show the progress bar
        progressDialog.show();

        String data = "";
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            data = extras.getString("video_url");
        Uri vidUri = Uri.parse(data);
        vidView.setVideoURI(vidUri);

        vidView.setMediaController(vidControl);

        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progressDialog.dismiss();
                vidView.start();

            }
        });


    }
}
