package com.sxx.vlctest;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Menu;
import android.view.MenuItem;

import com.sxx.vlctest.util.VLCInstance;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    private String mUrl = "/sdcard/test.mp4";

    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        mSurfaceView = (SurfaceView) findViewById(R.id.player);

        mMediaPlayer = new MediaPlayer(VLCInstance.get(this));
        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                switch (event.type) {
                    case MediaPlayer.Event.Opening:
                        Log.d("onEvent", "Opening");
                        break;
                    case MediaPlayer.Event.Playing:
                        Log.d("onEvent", "Playing");
                        break;
                    case MediaPlayer.Event.Paused:
                        Log.d("onEvent", "Paused");
                        break;
                    case MediaPlayer.Event.Stopped:
                        Log.d("onEvent", "Stopped");
                        break;
                    case MediaPlayer.Event.EndReached:
                        Log.d("onEvent", "EndReached");
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        Log.d("onEvent", "EncounteredError");
                        break;
                    case MediaPlayer.Event.TimeChanged:
                        Log.d("onEvent", "TimeChanged");
                        break;
                    case MediaPlayer.Event.PositionChanged:
                        Log.d("onEvent", "PositionChanged");
                        break;
                    case MediaPlayer.Event.SeekableChanged:
                        Log.d("onEvent", "SeekableChanged");
                        break;
                    case MediaPlayer.Event.PausableChanged:
                        Log.d("onEvent", "PausableChanged");
                        break;
                    case MediaPlayer.Event.Vout:
                        Log.d("onEvent", "Vout");
                        break;
                    case MediaPlayer.Event.ESAdded:
                        Log.d("onEvent", "ESAdded");
                        break;
                    case MediaPlayer.Event.ESDeleted:
                        Log.d("onEvent", "ESDeleted");
                        break;
                }
            }
        });

        vlcVout.detachViews();
        vlcVout.setVideoView(mSurfaceView);

        vlcVout.attachViews();

        mSurfaceView.setKeepScreenOn(true);

        play(mUrl);
    }

    private void play(String path) {
        try {
            Media media = new Media(VLCInstance.get(this), path);
            mMediaPlayer.setMedia(media);
            mMediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
