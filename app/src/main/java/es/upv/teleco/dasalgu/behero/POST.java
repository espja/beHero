package es.upv.teleco.dasalgu.behero;

import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Javi on 16/6/18.
 */

public class POST {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static JSONObject POST(String method, JSONObject json) throws IOException, JSONException {

        Log.d("POST METHOD",method);
        Log.d("POST DATA",json.toString());

        byte[] postData = ("json="+json.toString()).getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://18.219.216.8:8080/BeHero/"+method;

        URL url = new URL(request);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);

        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
            int statusCode = conn.getResponseCode();
            Log.d("post",String.valueOf(statusCode));

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            String status = null;
            JSONObject logininfo;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();
            Log.d("post response",sb.toString());
            try {
                logininfo = new JSONObject(sb.toString());
                status = logininfo.getString("status");
            }
            catch (Exception e ){
                return null;
            }
                return logininfo;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
    }

    static String locationToString(LatLng location){

        return String.valueOf(location.latitude)+","+String.valueOf(location.longitude);
    }

    static LatLng locationToLatLng(String location){
        String[] parts = location.split(",");
        LatLng newlocation = new LatLng(Float.parseFloat(parts[0]),Float.parseFloat(parts[1]));
        return newlocation;
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


}
