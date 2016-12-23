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
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mMarker;
    private ImageView imgDragLocation;
    private LatLng mlLatLng;
    private LocationManager mLocationManager;
    private boolean isMapReadly = false;
    private FloatingActionButton btnCurrnentLocation;
    private FloatingActionButton btnDirection;
    private Toolbar toolbar;
    private ValueAnimator mVaActionBar;
    private int toolbarHight;
    private LinearLayout linearLayout;
    private EditText etOrigin;
    private EditText etdestination;
    private CoordinatorLayout coordinatorLayout;
    private Button btnCanel;
    private Button btnOk;
    private String provider;
    private String ogLocation;
    private String desnLocation;
    private Marker dirMaker;
    private ArrayList<Polyline> listPolyOption;
    private boolean isgetDesLocation;
    private static final String TAG = "local";

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
        etOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "etOrigin", Toast.LENGTH_SHORT).show();
                btnDirection.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                imgDragLocation.setVisibility(View.VISIBLE);
            }
        });
        etdestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.VISIBLE);
                isgetDesLocation = true;
                imgDragLocation.setVisibility(View.VISIBLE);
            }
        });



    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etdestination = (EditText) findViewById(R.id.etDestination);
        imgDragLocation = (ImageView) findViewById(R.id.ivDragLocation);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainlayout);
        linearLayout = (LinearLayout) findViewById(R.id.choose);
        btnCanel = (Button) findViewById(R.id.btnCancel);
        btnOk = (Button) findViewById(R.id.btnOk);
        listPolyOption = new ArrayList<>();
        btnCurrnentLocation = (FloatingActionButton) findViewById(R.id.fabCurenntLocation);
        btnCurrnentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backCurrentLocation();
            }
        });
        btnDirection = (FloatingActionButton) findViewById(R.id.fabDirection);
        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDirection.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), toolbarHight + "", Toast.LENGTH_SHORT).show();
                showActionBar();
                desnLocation=null;
                ogLocation=null;
                for(Polyline line : listPolyOption)
                {
                    line.remove();
                }
                listPolyOption.clear();
            }
        });
        btnCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDirection.setVisibility(View.VISIBLE);
                imgDragLocation.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);

            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgDragLocation.setVisibility(View.GONE);
                LatLng curLocation = getLocationCenterCamrare();
                if (isgetDesLocation) {
                    desnLocation = curLocation.latitude+","+curLocation.longitude;
                    dirMaker = mMap.addMarker(new MarkerOptions()
                    .position(curLocation));
                } else {
                    ogLocation = curLocation.latitude+","+curLocation.longitude;
                }
                checkDone();
                linearLayout.setVisibility(View.GONE);
            }
        });

    }

    private void checkDone() {
        if (desnLocation!=null&&ogLocation!=null){
            String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+ogLocation+"&destination="+desnLocation+"&region=es&alternatives=true&key=AIzaSyCtssVj9zblsw-XYWC6sCPQ_gdMRgbaX5c";
            drawDirection(url);
            Log.d(TAG, "checkDone: "+url);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReadly = true;
        mMap = googleMap;

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = mLocationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(provider);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000,
                30, mLocationListener);
        mlLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d(TAG, "onCreate: " + location.getLatitude());
        Log.d("Location1", mlLatLng.longitude + "");
        Log.d("Location1", mlLatLng.latitude + "");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mlLatLng, 15));
        mMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_location)))
                .position(mlLatLng));
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                for(Polyline line : listPolyOption)
                {
                    line.setColor(getResources().getColor(R.color.colorAlternatives));
                    line.setZIndex(1);
                }
                polyline.setZIndex(2);
                polyline.setColor(getResources().getColor(R.color.colorPrimaryDark));

            }
        });



    }

    private void drawDirection(String url){

        Ion.with(getApplicationContext())
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {


                        try {
                            Log.d(TAG, "onCompleted: "+result);
                            JSONObject object = new JSONObject(result);
                            if ( object.getString("status").trim().equals("OK")){
                                Log.d(TAG, "onCompleted: "+object.getString("status"));
                                JSONArray routes = object.getJSONArray("routes");
                                for (int i =0; i<routes.length();i++){
                                    JSONObject route = routes.getJSONObject(i);
                                    JSONObject ovPolyline = route.getJSONObject("overview_polyline");
                                    String points =ovPolyline.getString("points");
                                    PolylineOptions polylineOptions = new PolylineOptions();
                                    List<LatLng> list = decodePoly(points);
                                    for (int j = 0; j< list.size(); j++) {
                                        polylineOptions.add(list.get(j));
                                    }

                                    Polyline polyline = mMap.addPolyline(polylineOptions);
                                    listPolyOption.add(polyline);
                                    polyline.setColor(getResources().getColor(R.color.colorAlternatives));
                                    polyline.setClickable(true);
                                    polyline.setWidth(15);
                                    polyline.setZIndex(1);
                                }
                                listPolyOption.get(0).setColor(getResources().getColor(R.color.colorPrimaryDark));
                                listPolyOption.get(0).setZIndex(2);

                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        hideActionBar();
                        btnDirection.setVisibility(View.VISIBLE);
                    }
                });
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
        mMap.animateCamera(CameraUpdateFactory.newLatLng(mlLatLng),500,null);
        mMarker.setPosition(mlLatLng);
    }

    private void hideActionBar() {
        if (getSupportActionBar().isShowing()) {
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

    private LatLng getLocationCenterCamrare() {
        double latitude = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().latitude;
        double longtitude = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter().longitude;
        return new LatLng(latitude, longtitude);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(mMap);
                } else {

                }
                return;
            }

        }
    }


}
