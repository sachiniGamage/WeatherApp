 package com.example.weatherforecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherforecast.Common.Common;
import com.example.weatherforecast.Helper.Helper;
import com.example.weatherforecast.Modell.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity2 extends AppCompatActivity implements LocationListener {

    TextView City, LastUpdate, Description, Humidity, Time, Celsius,result;
    ImageView imageView,search;
    EditText txtSearch;
    String cName;

    LocationManager locationManager;
    String provider;
    static double lat, lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int MY_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Control
        City = (TextView) findViewById(R.id.City);
        LastUpdate = (TextView) findViewById(R.id.LastUpdate);
        Description = (TextView) findViewById(R.id.Description);
        Humidity = (TextView) findViewById(R.id.Humidity);
        Time = (TextView) findViewById(R.id.Time);
        Celsius = (TextView) findViewById(R.id.Celsius);
        imageView = (ImageView) findViewById(R.id.imageView);

        txtSearch = (EditText)findViewById(R.id.txtSearch);
        search = (ImageView)findViewById(R.id.search);
        cName = txtSearch.getText().toString();
        result = (TextView) findViewById(R.id.result);

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("TAG", "Search");

                Searchh sh = new Searchh();
                sh.srch(txtSearch,search,result);

                Log.e("TAG", "Search1");
                return  true;
            }
        });

        //Get Coordinate
        locationManager = (LocationManager) getSystemService((Context.LOCATION_SERVICE));
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity2.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Log.e("TAG", "No Location");
        }
        else{
            Log.e("TAG", "location available");
            lat=location.getLatitude();
            lng = location.getLongitude();

            new GetWeather().execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lng)));
        }

        lat=location.getLatitude();
        lng = location.getLongitude();

        new GetWeather().execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lng)));

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity2.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lat=location.getLatitude();
        lng = location.getLongitude();

        new GetWeather().execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lng)));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private  class GetWeather extends AsyncTask<String,Void,String>{

        ProgressDialog pd = new ProgressDialog(MainActivity2.this);

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pd.setTitle("Please wait ...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String stream = null;
            String urlString = strings[0];


            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return  stream;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if(s==null || s.contains("Error: Not Found City")){
                Log.e("TAG", "No city");
                pd.dismiss();
                return;
            }
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);
            pd.dismiss();

            City.setText(String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
            LastUpdate.setText(String.format("Last Updated: %s", Common.getDateNow()));
            Description.setText(String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));
            Humidity.setText(String.format("Humidity: %d%%",openWeatherMap.getMain().getHumidity()));
            Time.setText(String.format("Sunrise and Sunset: %s/%s",Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            Celsius.setText(String.format("%.2f °C",openWeatherMap.getMain().getTemp()));
            Picasso.with(MainActivity2.this)
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}