package com.example.explorer.creation_android;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.example.explorer.creation_android.decodeJson.decode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int SHOW_RESPONSE = 0;

    public static final int SHOW_LOCATION = 1;

    private Button sendRequest;

    private TextView responseText;

    private TextView locationText;

    private LocationManager locationManager;

    private Location currentLocation;

    private String provider;
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    Data data = (Data) msg.obj;
                    String str = "PM: " + data.getPM() + "\n";
                    str += "Latitude: " + data.getLatitude() + "\n";
                    str += "Longitude" + data.getLongitude() + "\n";
                    responseText.setText(str);
                    break;
                case SHOW_LOCATION:
                    String currentPosition = (String) msg.obj;
                    locationText.setText(currentPosition);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendRequest = (Button) findViewById(R.id.send_response);
        responseText = (TextView) findViewById(R.id.response_text);
        locationText = (TextView) findViewById(R.id.location);

        sendRequest.setOnClickListener(this);

        locationInit();

    }

    private void locationInit() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "No location provider to user", Toast.LENGTH_SHORT).show();
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            showLocation(location);
        }

        locationManager.requestLocationUpdates(provider, 5000, 1, locationListenter);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListenter);
        }
    }

    LocationListener locationListenter = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void showLocation(final Location location) {

        currentLocation = location;
        StringBuilder url = new StringBuilder();

        url.append("http://maps.google.cn/maps/api/geocode/json?latlng=");
        url.append(location.getLatitude()).append(",");
        url.append(location.getLongitude());
        url.append("&sensor=false");
        getWebData.sendHttpRequest(url.toString(), new HttpCallbackListenter() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray resultArray = jsonObject.getJSONArray("results");

                    if (resultArray.length() > 0) {
                        JSONObject subObject = resultArray.getJSONObject(0);
                        String address = subObject.getString("formatted_address");

                        Message message = new Message();
                        message.what = SHOW_LOCATION;
                        message.obj = address;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_response) {
            StringBuilder url = new StringBuilder();
            url.append("http://121.42.25.113:8080/?user=explorer");
            url.append("&latitude=");
            url.append(currentLocation.getLatitude());
            url.append("&longitude=");
            url.append(currentLocation.getLongitude());

            getWebData.sendHttpRequest(url.toString(), new HttpCallbackListenter() {

                @Override
                public void onFinish(String response) {
                    Data data = decode(response);
                    Message message = new Message();
                    message.what = SHOW_RESPONSE;
                    message.obj = data;
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
















