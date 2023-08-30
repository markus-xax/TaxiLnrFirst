package com.example.taxi_full.API;

import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonWriter {

    public static void jsonFileWriter(String text){
        try(FileWriter writer = new FileWriter("user.json", false)) {
            writer.write(text);
            writer.flush();
        } catch(IOException ex){
            Log.d("---FIleWriter---",ex.getMessage());
        }

    }

    public static void jsonClear(){
        String fileName = "user.json";
        try {
            PrintWriter pw = new PrintWriter(fileName);
            pw.close();
        } catch (IOException e) {
            Log.d("---FileCleaner---",e.getMessage());
        }
    }
}
