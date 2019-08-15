package org.altbeacon.beaconreference;

import java.util.Collection;
import java.util.Iterator;

import android.app.Activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.enums.TipoNotificacion;

public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private TipoNotificacion tipoNotificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);

        tipoNotificacion = TipoNotificacion.values()[getIntent().getIntExtra("TipoNotificacion", 0)];
    }

    @Override 
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override 
    protected void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    @Override 
    protected void onResume() {
        super.onResume();
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

        RangeNotifier rangeNotifier = new RangeNotifier() {
           @Override
           public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
              if (beacons.size() > 0) {
                  Iterator<Beacon> iterador = beacons.iterator();

                  //while (iterador.hasNext()) {
                      Beacon beacon = iterador.next();
                      logToDisplay("Beacon " + beacon.getBluetoothName() + " is about " + beacon.getDistance() + " meters away.");

                  switch (tipoNotificacion) {
                      case Sonido:
                          ReproducirSonido();
                          break;
                      case Video:
                          ReproducirVideo();
                          break;
                      case Url:
                          AbrirUrl();
                          break;
                  }
                  MostrarNotificacion("Beacon detectado:" + beacon.getBluetoothName());
                  //}
              }
           }

        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {   }
    }

    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                editText.append(line+"\n");
            }
        });
    }

    private void MostrarNotificacion(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void ReproducirSonido(){
        MediaPlayer mp = MediaPlayer.create(this, R.raw.la_atmosfera_4);
        mp.start();
    }

    private void ReproducirVideo(){
        VideoView mVideoView = findViewById(R.id.videoView);
        Uri videoUri = getMedia("video_museo");
        mVideoView.setVisibility(View.VISIBLE);
        mVideoView.setVideoURI(videoUri);
        mVideoView.start();
    }

    private void AbrirUrl(){
        WebView wv = findViewById(R.id.webView);
        wv.setVisibility(View.VISIBLE);
        wv.loadUrl("https://www.inah.gob.mx/zonas/13-museos/121-museo-regional-de-queretaro");
    }

    private Uri getMedia(String mediaName) {
        return Uri.parse("android.resource://" + getPackageName() +
                "/raw/" + mediaName);
    }
}
