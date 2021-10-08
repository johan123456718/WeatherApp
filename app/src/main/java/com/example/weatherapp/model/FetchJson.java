package com.example.weatherapp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fetches the data from a specific URL.
 * Saves and loads also the file
 * Made by Johan Challita, Tidaa
 *
 * Inspiration for doing the fetch
 * ---------
 * http://griosf.github.io/android-volley/com/android/volley/toolbox/JsonObjectRequest.html
 * https://android--examples.blogspot.com/2017/02/android-volley-json-object-request.html
 * https://android--examples.blogspot.com/2017/02/android-volley-json-array-request.html
 * https://medium.com/android-grid/how-to-fetch-json-data-using-volley-and-put-it-to-recyclerview-android-studio-383059a12d1e
 * ---------
 *
 */
public class FetchJson {

    private String approvedTimeDate;
    private String approvedTimeCurrentTime;

    private String longitude;
    private String latitude;

    private List<WeatherVolley> currentWeatherList;

    private static FetchJson fetchJson;

    private FetchJson(){

    }

    public static FetchJson getInstance(){
        if(fetchJson == null){
            fetchJson = new FetchJson();
        }
        return fetchJson;
    }

    /**
     * Gets the weather information
     * @param response
     */
    public List<WeatherVolley> parseWeatherInfoFromFetch(JSONObject response) {
        currentWeatherList = new ArrayList<>();
        try {
            approvedTimeDate = response.getString("approvedTime").substring(0, 10);
            approvedTimeCurrentTime = response.getString("approvedTime").substring(11, 19);

            fetchCoordinates(response);
            JSONArray timeSeries = response.getJSONArray("timeSeries");
            for (int i = 0; i < timeSeries.length(); i++) {
                WeatherVolley weatherVolley = new WeatherVolley();
                JSONObject parametersAtTime = timeSeries.getJSONObject(i);
                String date = parametersAtTime.getString("validTime").substring(0, 10);
                String time = parametersAtTime.getString("validTime").substring(11,19);
                weatherVolley.setValidTime("Valid time: " + date + " " + time);

                JSONArray parameters = parametersAtTime.getJSONArray("parameters");

                for (int j = 0; j < parameters.length(); j++) {
                    JSONObject parameter = parameters.getJSONObject(j);

                    JSONArray values = parameter.getJSONArray("values");

                    String degrees = values.getString(0);
                    if(parameter.getString("name").equals("t")) {
                        weatherVolley.setDegrees("Temperature: " + degrees + "°C");
                    }

                    if(parameter.getString("name").equals("tcc_mean")){
                        String cloud_coverage = values.getString(0);
                        weatherVolley.setCloudCoverage("Cloud coverage: " + cloud_coverage + " oktas");
                    }
                }
                currentWeatherList.add(weatherVolley);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentWeatherList;
    }

    /**
     * Gets the coordinates from the fetch
     * @param response
     */
    private void fetchCoordinates(JSONObject response){
        try {

            JSONObject geometry = response.getJSONObject("geometry");
            JSONArray coordinates = geometry.getJSONArray("coordinates");
            longitude = coordinates.getString(0).substring(1, 9);
            latitude = coordinates.getString(0).substring(11,19);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getApprovedTimeDate() {
        return approvedTimeDate;
    }

    public void setApprovedTimeDate(String approvedTimeDate) {
        this.approvedTimeDate = approvedTimeDate;
    }

    public String getApprovedTimeCurrentTime() {
        return approvedTimeCurrentTime;
    }

    public void setApprovedTimeCurrentTime(String approvedTimeCurrentTime) {
        this.approvedTimeCurrentTime = approvedTimeCurrentTime;
    }

    public List<WeatherVolley> getCurrentWeatherList() {
        return currentWeatherList;
    }

    public void setCurrentWeatherList(List<WeatherVolley> currentWeatherList) {
        this.currentWeatherList = currentWeatherList;
    }
}