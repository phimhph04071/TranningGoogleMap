package com.example.phimau.tranninggooglemap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mMarker;
    private LatLng mlLatLng;
    private LocationManager mLocationManager;
    private boolean isMapReadly = false;
    private FloatingActionButton btnCurrnentLocation;
    private FloatingActionButton btnDirection;
    private Toolbar toolbar;
    private ValueAnimator mVaActionBar;
    private int toolbarHight;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            mlLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        String provider = mLocationManager.getBestProvider(new Criteria(), true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, mLocationListener);
        Location location = mLocationManager.getLastKnownLocation(provider);
        mlLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnCurrnentLocation = (FloatingActionButton) findViewById(R.id.fabCurenntLocation);
        btnCurrnentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backCurrentLocation();
                showActionBar();
                Toast.makeText(getApplicationContext(), toolbar.getHeight() + "", Toast.LENGTH_SHORT).show();
            }
        });
        btnDirection = (FloatingActionButton) findViewById(R.id.fabDirection);
        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "btnDirection", Toast.LENGTH_SHORT).show();
                hideActionBar();
//                toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        getSupportActionBar().hide();
//                    }
//                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReadly = true;
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mlLatLng, 15));
        mMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_location)))
                .position(mlLatLng));
        PolylineOptions polylineOptions = new PolylineOptions();
        List<LatLng> list = decodePoly("cvm_Cqo`dSBjID~QkDCaIGw@@O\\m@xAZLZPoArCSj@?HGZKLGBg@v@CJV^JLJZBTK\\DXh@~Av@vBt@rBdAfCHPM~@]dCSbBa@tBJBl@ZY?wDs@sBe@qBE");
        for (int i = 0; i < list.size(); i++) {
            polylineOptions.add(list.get(i));

        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
        Polyline polyline = mMap.addPolyline(polylineOptions);
//             Ion.with(getApplicationContext())
//                .load("https://maps.googleapis.com/maps/api/directions/json?origin=21.045933,%20105.660890&destination=21.044545,%20105.669273&region=es&key=AIzaSyCtssVj9zblsw-XYWC6sCPQ_gdMRgbaX5c")
//                .asString()
//                .setCallback(new FutureCallback<String>() {
//                    @Override
//                    public void onCompleted(Exception e, String result) {
//                        try {
//                            JSONObject object = new JSONObject(result);
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                });

    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void backCurrentLocation() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mlLatLng, 15), 500, null);
        mMarker.setPosition(mlLatLng);
    }

    private void hideActionBar() {
        toolbarHight = toolbar.getHeight();
        mVaActionBar = ValueAnimator.ofInt(toolbarHight, 0);
        mVaActionBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ((AppBarLayout.LayoutParams) toolbar.getLayoutParams()).height
                        = (Integer) valueAnimator.getAnimatedValue();
                toolbar.requestLayout();
            }
        });
        mVaActionBar.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                getSupportActionBar().hide();
            }
        });
        mVaActionBar.setDuration(300);
        mVaActionBar.start();
    }

    private void showActionBar() {
        if (!getSupportActionBar().isShowing()) {
            mVaActionBar = ValueAnimator.ofInt(0, toolbarHight);
            mVaActionBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    toolbar.getLayoutParams().height = (Integer) valueAnimator.getAnimatedValue();
                    toolbar.requestLayout();
                }
            });
            mVaActionBar.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    getSupportActionBar().show();
                }
            });
            mVaActionBar.setDuration(300);
            mVaActionBar.start();
        }
    }
}
