package com.example.taxi_full.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class HttpApi {

    public static int post(String URLAdd, String args) {

        HttpURLConnection httpURLConnection;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        URL url;

        try {
            byte[] out = args.toString().getBytes(StandardCharsets.UTF_8);

            url = new URL(URLAdd);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0");
            httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");


            httpURLConnection.setConnectTimeout(200);
            httpURLConnection.setReadTimeout(200);
            httpURLConnection.connect();

            try {
                outputStream = httpURLConnection.getOutputStream();
                outputStream.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {
                return HttpURLConnection.HTTP_OK;
            }
            if (HttpURLConnection.HTTP_NOT_FOUND == httpURLConnection.getResponseCode()) {
                return HttpURLConnection.HTTP_NOT_FOUND;
            }
            if (HttpURLConnection.HTTP_SERVER_ERROR == httpURLConnection.getResponseCode()) {
                return HttpURLConnection.HTTP_SERVER_ERROR;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getId(String URLAdd) throws IOException {
        URLConnection urlConnection = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(URLAdd);
            urlConnection = url.openConnection();

            inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            return bufferedReader.readLine();

        } catch (IOException e) {
            return "0";
        } finally {
            if (inputStreamReader != null)
                inputStreamReader.close();
            if (bufferedReader != null)
                bufferedReader.close();
        }

    }

    public static int put(String URLAdd, String args){
        HttpURLConnection httpURLConnection;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        URL url;

        try {
            byte[] out = args.toString().getBytes(StandardCharsets.UTF_8);

            url = new URL(URLAdd);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0");
            httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");


            httpURLConnection.setConnectTimeout(500);
            httpURLConnection.setReadTimeout(500);
            httpURLConnection.connect();

            try {
                outputStream = httpURLConnection.getOutputStream();
                outputStream.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {
                return HttpURLConnection.HTTP_OK;
            }
            if (HttpURLConnection.HTTP_NOT_FOUND == httpURLConnection.getResponseCode()) {
                return HttpURLConnection.HTTP_NOT_FOUND;
            }
            if (HttpURLConnection.HTTP_SERVER_ERROR == httpURLConnection.getResponseCode()) {
                return HttpURLConnection.HTTP_SERVER_ERROR;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String sendGET(String GET_URL) throws IOException {
        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
           return "GET request did not work.";
        }

    }

}
