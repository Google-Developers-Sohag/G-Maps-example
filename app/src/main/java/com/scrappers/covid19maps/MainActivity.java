package com.scrappers.covid19maps;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    String countryName="egypt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        CountDownTimer ctd=new CountDownTimer(300,100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

                //get the map viewed
                mapFragment.getMapAsync( MainActivity.this::onMapReady);
            }
        }.start();

    }

    GoogleMap googleMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap=googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


        //Add Marker in Egypt so you can set a title and a snippet
        googleMap.addMarker(new MarkerOptions().position(new LatLng(27.18278, 31.18278)).visible(true).title("egypt").snippet(
                ""));
        //add Circle on Egypt
        googleMap.addCircle(new CircleOptions().center(new LatLng(27.18278, 31.18278)).fillColor(Color.RED).visible(true).radius(4*Math.pow(10,8)));


        //Add Marker in China so you can set a title and a snippet
        googleMap.addMarker(new MarkerOptions().position(new LatLng(35, 105)).visible(true).title("china").snippet(
                ""));
        //add Circle on China
        googleMap.addCircle(new CircleOptions().center(new LatLng(35, 105)).fillColor(Color.GREEN).visible(true).radius(4*Math.pow(10,8)));



        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                countryName=marker.getTitle();
                new Map_Data().execute("https://api.covid19api.com/live/country/"+countryName);
                return false;
            }
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    class Map_Data extends AsyncTask<String, JSONObject, JSONObject> {



        OkHttpClient client=new OkHttpClient();

        @Override
        protected JSONObject doInBackground(String... objects) {
            try {
            Request.Builder builder = new Request.Builder();
            builder.url(String.valueOf(objects[0]));
            Request request = builder.build();
                Response response = client.newCall(request).execute();
                JSONArray jsonArray= new JSONArray(response.body().string());
                org.json.JSONObject obj=new org.json.JSONObject(jsonArray.getString(2));

                return obj;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(JSONObject... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //apply data set change for specific  map marker according to the title of that map
                        try {
                            String date = String.valueOf(jsonObject.get("Date"));
                             String name= String.valueOf(jsonObject.get("Country"));
                            String cases= String.valueOf(jsonObject.get("Confirmed"));
                            String Deaths=String.valueOf(jsonObject.get("Deaths"));
                            applydataChange(name,date,cases,Deaths);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

        }

    }

    public void applydataChange(String name,String date,String cases,String Deaths){
        TextView dataText=findViewById(R.id.data);
        dataText.setText(name+" "+date+"\n"+
                "Confirmed Cases: "+cases+"\n"+
                "Deaths: "+Deaths+"\n");


    }
}
