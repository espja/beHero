package es.upv.teleco.dasalgu.behero;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class login extends AppCompatActivity {

    public static final String USER_LOCATION = "es.upv.teleco.dasalgu.behero.UserLocation";
    public static final String USER_ID = "es.upv.teleco.dasalgu.behero.idUser";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private FusedLocationProviderClient mFusedLocationClient;
    public static LatLng userLocation = new LatLng(0,0);
    public static int idUser;
    public static JSONObject idUserjson,loginData;

    public static JSONObject favourData;
    EditText passwordEditText,usernameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        usernameEditText   = (EditText)findViewById(R.id.username);
        passwordEditText   = (EditText)findViewById(R.id.password);


        //GET USER LOCATION
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            //Get location
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                double lat = location.getLatitude();
                                double lng = location.getLongitude();
                                userLocation = new LatLng(lat,lng);
                                Log.d("Location onCreate:", userLocation.toString());
                            }
                        }
                    });

        }
        //END GET USER LOCATION
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Context context = getApplicationContext();
                    CharSequence toasttext = "Location permission must be granted in order to make the app work. ";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, toasttext, duration);
                    toast.show();
                    askForPermission();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void askForPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    /** Called when the user taps the Send button */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void logIn(View view) throws JSONException {
        Toast.makeText(this, "Logging in... Ready to be a hero?", Toast.LENGTH_LONG).show();

        JSONObject login = new JSONObject();
        login.put("name", usernameEditText.getText().toString());
        login.put("pass",POST.md5(passwordEditText.getText().toString()));
        if(isValidUserName(usernameEditText.getText().toString())) {
            try {
                loginData = POST.POST("login", login);

                if(loginData.getString("status").equals("OK")){
                    //SUCCESSFUL LOGIN
                    idUser = loginData.getInt("idUser");
                    Log.d("idUser", String.valueOf(idUser));

                    //Get stats

                    //Get favours
                    idUserjson = new JSONObject();
                    idUserjson.put("idUser",String.valueOf(idUser));

                    favourData = POST.POST("listarfavores", idUserjson);

                    //Send location
                    JSONObject locatjson = new JSONObject();
                    locatjson.put("idUser",String.valueOf(idUser));

                    locatjson.put("location",POST.locationToString(userLocation));
                    POST.POST("actualizarubicacion", locatjson);

                    //TODO: Send favours
                    Intent intent = new Intent(this, MapsActivity.class);
                    intent.putExtra(USER_LOCATION, userLocation);
                    intent.putExtra(USER_ID, idUser);
                    startActivity(intent);
                } else {
                    //FAIL
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    usernameShake();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                Toast.makeText(this, "Error in communication with server, please try again in a few moments.", Toast.LENGTH_SHORT).show();
            }
        }
        //Log.d("click:","yes");
    }

    private void usernameShake() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        findViewById(R.id.username).startAnimation(shake);
    }

    private void passwordShake() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        findViewById(R.id.password).startAnimation(shake);
    }

    private boolean isValidUserName(String username) {
        /**
         * if mail address is NULL string
         */
        Log.d("isvalidusername", username);
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Empty username", Toast.LENGTH_SHORT).show();
            usernameShake();
            Log.d("isvalidusername", "firstif");
            return false;
        }

        if (username.length()<3) {
            Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show();
            usernameShake();
            Log.d("isvalidusername", "second");
            return false;
        }
        return true;
    }

    public void openSignUp(View v){
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }



}



