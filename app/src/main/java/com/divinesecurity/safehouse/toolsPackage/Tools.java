package com.divinesecurity.safehouse.toolsPackage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * Created by ang3l on 23/2/2018.
 */

public class Tools {

    private static String screen_active = "none";
    public static String getScreen_active() {
        return screen_active;
    }

    public static void setScreen_active(String _screen) {
        screen_active = _screen;
    }
    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     */
    public static String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            //return readIt(is, len);
            return convertStreamtoString(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        }catch (Exception e){
            Log.d(TAG, "downloadUrl: " + e.toString());
            if (is != null)
                return convertStreamtoString(is);
            else
                return "error";
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /*public static String POSTUrl(String myurl, String urlParameters) throws IOException {

        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(myurl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }*/


    private static String convertStreamtoString(InputStream stream){
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        return s.hasNext()? s.next() :"";
    }


    public static boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


    public static boolean checkConnectivity(Context context){
        ConnectivityManager check = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //get the information of all the networks
        NetworkInfo[] info = new NetworkInfo[0];
        if (check != null) {
            info = check.getAllNetworkInfo();
        }
        //check Connected State
        boolean flag = false;
        for (NetworkInfo anInfo : info) {
            if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                flag = true;
                break;
            }
        }
        return flag;
    }

}
