package com.example.weatherapp.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Inspiration for saving & read from a file
 * --------
 * https://howtodoinjava.com/java/collections/arraylist/serialize-deserialize-arraylist/
 * --------
 */
public class InternalFile {
    private static InternalFile internalFile;

    private InternalFile(){

    }

    public static InternalFile getInstance(){
        if(internalFile == null){
            internalFile = new InternalFile();
        }
        return internalFile;
    }

    private static final String NAME_OF_FILE = "weatherData";

    /**
     * Saves the fetched data into a file
     */
    public void saveData(File dir, List<WeatherVolley> weatherList, String getDate, String getTime, String longitude, String latitude){

        try{
            FileOutputStream fileOutputStream = new FileOutputStream(new File(dir, NAME_OF_FILE));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 1024*8);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            objectOutputStream.writeObject(weatherList);
            objectOutputStream.writeObject(getDate);
            objectOutputStream.writeObject(getTime);
            objectOutputStream.writeObject(longitude);
            objectOutputStream.writeObject(latitude);
            objectOutputStream.close();
            objectOutputStream.reset();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads the data
     * @param dir
     * @param fetchJson
     */
    public void loadData(File dir, FetchJson fetchJson){

        try{
            FileInputStream fileInputStream = new FileInputStream(new File(dir, NAME_OF_FILE));
            ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
            fetchJson.setCurrentWeatherList((ArrayList<WeatherVolley>)objectInputStream.readObject());
            fetchJson.setApprovedTimeDate((String)objectInputStream.readObject());
            fetchJson.setApprovedTimeCurrentTime((String)objectInputStream.readObject());
            fetchJson.setLongitude((String)objectInputStream.readObject());
            fetchJson.setLatitude((String)objectInputStream.readObject());
            objectInputStream.close();
            objectInputStream.reset();
            fileInputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
