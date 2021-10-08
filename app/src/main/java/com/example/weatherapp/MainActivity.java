package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.model.FetchJson;
import com.example.weatherapp.model.InternalFile;
import com.example.weatherapp.model.NetworkClient;
import com.example.weatherapp.model.WeatherListAdapter;
import com.example.weatherapp.model.WeatherVolley;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * Information about Connectivity manager:
 *
 * LIFE CYCLES
 * ------
 * https://developer.android.com/codelabs/android-training-activity-lifecycle-and-state?index=..%2F..%2Fandroid-training#2
 * ------
 *
 * Anders Lindström Volley code
 * ---------
 * https://gits-15.sys.kth.se/anderslm/Joke-on-Volley-with-RecyclerView
 * ---------
 * Made by Johan Challita, Tidaa
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FetchJson fetchJson;
    private NetworkClient networkClient;
    private InternalFile internalFile;

    private Button submit;
    private EditText longitudeEditField;
    private EditText latitudeEditField;


    private RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;

    private List<WeatherVolley> weatherList;
    private static long lastDownloaded = 0;

    private WeatherListAdapter adapter;
    private TextView approvedTime;
    private TextView longitudeText;
    private TextView latitudeText;

    private RequestQueue requestQueue;
    private File dir;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkClient = new NetworkClient(this);
        submit = findViewById(R.id.submitButton);
        longitudeEditField = findViewById(R.id.longitudeText);
        latitudeEditField = findViewById(R.id.latitudeText);

        approvedTime = findViewById(R.id.approvedTimeText);
        longitudeText = findViewById(R.id.longitudeValue);
        latitudeText = findViewById(R.id.latitudeValue);
        recyclerView = findViewById(R.id.recyclerView);

        weatherList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new WeatherListAdapter(getApplicationContext(), weatherList);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        fetchJson = FetchJson.getInstance();
        internalFile = InternalFile.getInstance();
        submit.setOnClickListener(this);
        requestQueue = Volley.newRequestQueue(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        dir = getFilesDir();
        internalFile.loadData(dir, fetchJson);
        if( weatherList != null &&
            fetchJson.getApprovedTimeDate() != null&&
            fetchJson.getApprovedTimeCurrentTime() != null&&
            fetchJson.getLongitude() != null&&
            fetchJson.getLatitude() != null){
            printSavedValues();
        }


        if(fetchJson.getLongitude() == null && fetchJson.getLatitude() == null){
            url = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/11.5/lat/60/data.json";
        }else{
            url = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/"+fetchJson.getLongitude()+"/lat/"+fetchJson.getLatitude()+"/data.json";
        }

        if (networkClient.isConnected()) {
            if (System.currentTimeMillis() - lastDownloaded > 3_600_000) {
                downloadNewWeather(null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        String longitudeText = longitudeEditField.getText().toString();
        String latitudeText = latitudeEditField.getText().toString();

        if(networkClient.isConnected()) {
            if (!longitudeText.equals("") && !latitudeText.equals("")) {
                fetchJson.setLongitude(longitudeText);
                fetchJson.setLatitude(latitudeText);
                url = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/"+fetchJson.getLongitude()+"/lat/"+fetchJson.getLatitude()+"/data.json";
                downloadNewWeather(null );
                longitudeEditField.getText().clear();
                latitudeEditField.getText().clear();
            } else if (latitudeText.equals("")) {
                latitudeEditField.setError("Missing latitude");
            } else {
                longitudeEditField.setError("Missing longitude");
            }
        }else{
            Toast.makeText(this, "Need internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadNewWeather(View view){
        postWeather();
    }

    protected void postWeather(){
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                responseListener,
                errorListener);
        request.setTag(this);
        requestQueue.add(request);
    }

    @Override
    protected void onStop() {
        super.onStop();

        new Thread(new Runnable() {
            @Override
            public void run() {
                dir = getFilesDir();
                internalFile.saveData(
                        dir,
                        weatherList,
                        fetchJson.getApprovedTimeDate(),
                        fetchJson.getApprovedTimeCurrentTime(),
                        fetchJson.getLongitude(),
                        fetchJson.getLatitude());
            }
        }).start();

        if(requestQueue != null){
            requestQueue.cancelAll(this);
        }
    }

    /**
     * A helper method for fetching
     */
    private Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            List<WeatherVolley> tmp = fetchJson.parseWeatherInfoFromFetch(response);
            approvedTime.setText("Approved Time:");
            printEditBox();
            adapter.notifyDataSetChanged();
            weatherList.clear();
            weatherList.addAll(tmp);
            lastDownloaded = System.currentTimeMillis();
            requestQueue.cancelAll(this);
        }
    };

    /**
     * A helper method for error in fetch
     */
    private Response.ErrorListener errorListener = error -> {
        Toast.makeText(this, "Erroneous values, which means data is missing", Toast.LENGTH_SHORT).show();
    };

    /**
     * A helper method that prints the loaded values
     */
    private void printSavedValues(){
        try {
            approvedTime.setText("Approved Time:");
            printEditBox();
            adapter.notifyDataSetChanged();
            weatherList.clear();
            weatherList = fetchJson.getCurrentWeatherList();
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            adapter = new WeatherListAdapter(getApplicationContext(), weatherList);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * A helper method that prints out the Editbox
     */
    private void printEditBox(){
        if (fetchJson.getApprovedTimeDate() != null && fetchJson.getApprovedTimeCurrentTime() != null) {
            approvedTime.append(" ");
            approvedTime.append(fetchJson.getApprovedTimeDate());
            approvedTime.append(" ");
            approvedTime.append(fetchJson.getApprovedTimeCurrentTime());
        }

        if (fetchJson.getLongitude() != null && fetchJson.getLatitude() != null) {

            latitudeText.setText("Latitude: ");
            latitudeText.append(" ");
            latitudeText.append(fetchJson.getLatitude());

            longitudeText.setText("Longitude: ");
            longitudeText.append(" ");
            longitudeText.append(fetchJson.getLongitude());

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }
}