package com.example.task61d;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.task61d.databinding.ActivityGooglemapBinding;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Googlemap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityGooglemapBinding binding;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng>listPoints;
    Button Call;
    Button Book;
    EditText pick,drop;
    TextView fee,time;
    LatLng a;
    LatLng b;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGooglemapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        findid();
        askPermissions();
        Places.initialize(getApplicationContext(),"AIzaSyDOoEC0olIAHCMYSBI2TPt5gGzx43LVB-s");
        //When the call button is pressed, the simulator will be used to call the set number
        Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_CALL);
                intent2.setData(Uri.parse("tel:0468800718"));
                startActivity(intent2);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listPoints=new ArrayList<>();
        pick.setFocusable(false);
        drop.setFocusable(false);
        //Fill in the place of dispatch
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<com.google.android.libraries.places.api.model.Place.Field> fieldList= Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ADDRESS,com.google.android.libraries.places.api.model.Place.Field.LAT_LNG,com.google.android.libraries.places.api.model.Place.Field.NAME);
                Intent intent2=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(Googlemap.this);
                startActivityForResult(intent2,100);
            }
        });
        //Fill in the place of receipt
        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<com.google.android.libraries.places.api.model.Place.Field> fieldList= Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ADDRESS,com.google.android.libraries.places.api.model.Place.Field.LAT_LNG,com.google.android.libraries.places.api.model.Place.Field.NAME);
                Intent intent2=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(Googlemap.this);
                startActivityForResult(intent2,200);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Once all prices and times are displayed, you can click
        //on the booking button to jump to the payment screen
        Book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count >= 2) {
                    Intent intent2 = new Intent(Googlemap.this, pay.class);
                    startActivity(intent2);
                }
            }
        });
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @SuppressLint({"MissingSuperCall", "MissingPermission"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }
    //Get the direction from the google server side
    private String getRequestUrl(LatLng origin, LatLng dest) {
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=driving";
        String param = str_org + "&" + str_dest  + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param+"&key=AIzaSyDOoEC0olIAHCMYSBI2TPt5gGzx43LVB-s";
        return url;
    }
    private String requestDirection(String reqUrl) throws IOException {
        String responseString=null ;
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream!=null)
            {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }
    public class TaskRequestDirections extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            String responseString="";
            try {
                responseString=requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }
        protected  void onPostExecute(String s){
            super.onPostExecute(s);
            TaskParser taskParser=new TaskParser();
            taskParser.execute(s);
        }
    }
    public class TaskParser extends  AsyncTask<String,Void,List<List<HashMap<String,String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> rountes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                rountes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return rountes;
        }

        protected void onPostExecute(List<List<HashMap<String,String>>>lists) {
            ArrayList points=null;
            PolylineOptions polylineOptions=null;
            for(List<HashMap<String,String>>path:lists)
            {
                points=new ArrayList();
                polylineOptions=new PolylineOptions();
                for(HashMap<String,String>point:path){
                    double lat=Double.parseDouble(point.get("lat"));
                    double lon=Double.parseDouble(point.get("lon"));
                    points.add(new LatLng(lat,lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            if(polylineOptions!=null)
            {
                mMap.addPolyline(polylineOptions);
            }
        }
    }

        protected void askPermissions() {
            String[] permissions = {
                    "android.permission.CALL_PHONE"
            };
            int requestCode = 300;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, requestCode);
            }
        }

        public void findid() {
            Call = findViewById(R.id.Call);
            Book = findViewById(R.id.Book);
            pick = findViewById(R.id.pick);
            drop = findViewById(R.id.drop);
            fee = findViewById(R.id.fee);
            time = findViewById(R.id.time);
        }

        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            //Get the location and latitude and longitude of the sending location
            if (requestCode == 100 && resultCode == RESULT_OK) {
                Place place = (Place) Autocomplete.getPlaceFromIntent(data);
                pick.setText(place.getName());
                 a=new LatLng(place.getLatLng().latitude,place.getLatLng().longitude);
                count++;
                 mMap.addMarker(new MarkerOptions().position(a).title("pick up location"));
            //Get the location and latitude and longitude of the receiving site
            } else if (requestCode == 200 && resultCode == RESULT_OK) {
                Place place = (Place) Autocomplete.getPlaceFromIntent(data);
                drop.setText(place.getName());
                b = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(b, 15f));
                mMap.addMarker(new MarkerOptions().position(b).title("drop off location"));
                count++;
                //If both the receiving and sending locations have been
                // acquired then route acquisition begins
                if (count >= 2) {
                    String url = getRequestUrl(a, b);
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                    Global.time=distance(a.longitude,a.latitude,b.longitude,b.latitude);
                    double money=Global.time*0.8;
                    BigDecimal b = new BigDecimal(money);
                    //Set a minimum spend
                    if(money<10)
                    {
                        Global.money=10;
                    }
                    else{
                    Global.money= b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    }
                    //Shows the estimated amount to be spent and the estimated time
                    time.setText(String.valueOf( Global.time)+" Minutes");
                    fee.setText(String.valueOf( Global.money)+" Dollar");
                }
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
                }
            }
    //Calculate the distance by two coordinates
    private static int distance(double lon1,double lat1,double lon2, double lat2) {
        double EARTH_RADIUS=6378137;
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 *Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        double d = (s * EARTH_RADIUS/1000*2);
        int h= (int) Math.round(d);
        return h;
    }
    private static double rad(double d){
        return d * Math.PI / 180.0;
    }
}

