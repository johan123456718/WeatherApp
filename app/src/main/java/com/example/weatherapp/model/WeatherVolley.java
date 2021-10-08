package com.example.weatherapp.model;

import java.io.Serializable;

/**
 * A volley object for fetching the data from json and patches it to recycling view
 *
 * Inspiration:
 * https://medium.com/android-grid/how-to-fetch-json-data-using-volley-and-put-it-to-recyclerview-android-studio-383059a12d1e
 *
 * Made by Johan Challita, Tidaa
 */
public class WeatherVolley implements Serializable {

    private String validTime;
    private String cloud_coverage;
    private String degrees;

    public WeatherVolley(){

    }
    public WeatherVolley( String validTime, String cloud_coverage, String degrees){
        this.validTime = validTime;
        this.cloud_coverage = cloud_coverage;
        this.degrees = degrees;
    }


    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public String getDegrees() {
        return degrees;
    }

    public void setDegrees(String degrees) {
        this.degrees = degrees;
    }

    public String getCloudCoverage() {
        return cloud_coverage;
    }

    public void setCloudCoverage(String cloud_coverage) {
        this.cloud_coverage = cloud_coverage;
    }

}
