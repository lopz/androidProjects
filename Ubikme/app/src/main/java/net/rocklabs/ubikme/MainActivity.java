package net.rocklabs.ubikme;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.PolylineOptions;


public class MainActivity extends Activity {

    private GoogleMap mMap;
    private TextView txtMessage;
    private Button btnMyLocation;
    public LocationClient lClient;
    public Location dstLocation;
    public Circle circle;

    private NotificationManager notificationManager;

    public Notification.Builder notification;

    public static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(16)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtMessage = (TextView) findViewById(R.id.message_text);
//        setUpMapIfNeeded();


        PolylineOptions polyline = new PolylineOptions();

        mMap.clear();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification.Builder(this)
                            .setContentTitle("Ofertas")
                            .setContentText("Tienes ofertas cerca de tu alcance!!")
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setTicker("Ticker");
//                            .setSound(Notification.STREAM_DEFAULT);
//                            .setLargeIcon(R.drawable.)
//                            .build();



        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
//        notification.setLatestEventInfo(this, "Ofertas", "Información", pendingIntent);
        notification.setContentIntent(pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        lClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (lClient != null) {
            lClient.disconnect();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        notificationManager.cancel(1);
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpLocationClientIfNeeded() {
        if (lClient == null) {
            lClient = new LocationClient(
                    this,
                    new ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            lClient.requestLocationUpdates(
                                    REQUEST,
                                    new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            int distanceTo = (int) location.distanceTo(dstLocation);
                                            Log.d("TAG", "onLocationChanged: " +
                                                    location.getLatitude() + " " +
                                                    location.getLongitude() + " " +
                                                    distanceTo);
                                            txtMessage.setText("DistanceTo: " + distanceTo);

                                            if (distanceTo < 10)
                                                notificationManager.notify(1, notification.getNotification());
                                        }
                                    });  // LocationListener
                        }

                        @Override
                        public void onDisconnected() {

                        }
                    },  // ConnectionCallbacks
                    new OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                        }
                    }); // OnConnectionFailedListener
        }
    }
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
/*        mMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (lClient != null && lClient.isConnected()) {
                    String msg = "Location = " + lClient.getLastLocation();
//                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lClient.getLastLocation().getLatitude(), lClient.getLastLocation().getLongitude()), 15));
                }
                return false;
            }
        });*/

        dstLocation = new Location("A");
        dstLocation.setLatitude(-20.3684482);
        dstLocation.setLongitude(-64.0025499);
        LatLng latLong = new LatLng(dstLocation.getLatitude(), dstLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLong).title("Marker"));

        circle = mMap.addCircle(new CircleOptions()
                .center(latLong)
                .radius(10)
                .strokeWidth(2)
                .strokeColor(Color.BLACK));
//                .fillColor(Color.alpha(Color.BLUE)));


    }
}
