package com.example.weatherforecast;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherforecast.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class Searchh  extends AppCompatActivity {

    TextView result;
    EditText txtSearch;
    String cName;
    ImageView search;


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_up);
//
//        search.OnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("TAG", "Search");
//                Searchh sh = new Searchh();
//                sh.srch();
//
//
//                Log.e("TAG", "Search1");
//            }
//        });
//
//    }









    class  W extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... address) {

            try{
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //Establish connection with address
                connection.connect();

                //retrieve data from url
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                //retrieve data and return it as String
                int data  = isr.read();
                String content = "";
                char ch;
                while (data != -1){
                    ch = (char) data;
                    content = content+ch;
                    data = isr.read();
                }
                return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }




    }






    public void srch(EditText txtSearch, ImageView search, TextView result){
//        txtSearch = (EditText)findViewById(R.id.txtSearch);
        cName = txtSearch.getText().toString();
//        search = (ImageView)findViewById(R.id.search);
//        result = (TextView) findViewById(R.id.result);


        String  content;
        W wether = new W();
        try {
            content = wether.execute("https://api.openweathermap.org/data/2.5/weather?q="
                    +cName+"&appid=e8c9a3b6d156f95003a53a3398e77495").get();
            Log.e("TAG", "retrieve successfully");

            //json
            JSONObject jsonObject = new JSONObject(content);
            String weatherDate = jsonObject.getString("weather");
            Log.e("TAG", "weatherData");
            JSONArray array= new JSONArray(weatherDate);

            String main = "";
            String description = "";


            for (int i = 0; i<array.length();i++){
                JSONObject weatherPart = array.getJSONObject(i);
                main = weatherPart.getString("main");
                description = weatherPart.getString("description");
            }
            Log.e("TAG", "Weather");


            JSONObject jsonObject1 = jsonObject.getJSONObject("main");
            String temp= jsonObject1.getString("temp");

            JSONObject jsonObject2 = jsonObject.getJSONObject("main");
            int Humidity= jsonObject2.getInt("humidity");



            result.setText("City: "+cName+"\nMain: "+main+"\nDescription: "+description+"\nTemp  : "+temp+"\nHumidity: "+Humidity +"%");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
