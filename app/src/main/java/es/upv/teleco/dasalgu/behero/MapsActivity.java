package es.upv.teleco.dasalgu.behero;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import static android.view.View.GONE;

//import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMarkerClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private JSONObject locations;
    public static final String USER_FAVOURS = "es.upv.teleco.dasalgu.behero.userFavours";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private PopupWindow popUp;
    private PopupWindow newFavourPopUp;
    private RelativeLayout positionOfPopUp;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng userLocation = login.userLocation;
    private int idUser = login.idUser;
    private String myName = "Javi";
    private int markerCounter = 0;
    public JSONObject stats;
    private JSONArray favores;
    private boolean hasfavour = false;
    JSONObject favor;
    private int arrayPos;
    public static int myFavourPos;
    public int myidFav;
    public static int myfavours = 0;
    //camera
    private Button btn;
    public static ImageView imageview;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    public boolean notified = false;
    //
    //public LatLng[] = new LatLng[]
    //TODO: si la ID de usuario corresponde con la ID de creador del favor, se podrá borrar el favor
    private int myID = 1;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
            //Log.d("MESSAGE ", login.userLocation.toString());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
       mapFragment.getMapAsync(this);
        //Cogemos las ubicaciones del servidor


        //set profile image
        //try {
            //setProfileImage();
        //} catch (JSONException e) {
       //     e.printStackTrace();
        //}
        /*try {
            //Coger estadisticas
            stats = POST.POST("estadisticas", login.idUserjson);
            Log.d("stats", stats.toString());
            String myfav = stats.getString ("pedidos");
            int myrealizados = stats.getInt("solicitados");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try{
            favores = login.favourData.getJSONArray("favores");
            favores = stats.getJSONArray("favores");
        } catch (JSONException e) {

        }
*/

        //Listener para abrir nuevo favor
        final Button button = findViewById(R.id.addFavour);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    newFavour();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


       getLocation();
       // timer();


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
   @RequiresApi(api = Build.VERSION_CODES.KITKAT)
   @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
       
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            mMap.setMyLocationEnabled(true);
        }
        //Esto hace que muestre el botón de arriba a la derecha
        mMap.setMyLocationEnabled(true);
        Log.d("Location:", userLocation.toString());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        //pintamos los favores
      /* try {
           Log.d("info","Try updatefavours");
          // updateFavours();
       } catch (JSONException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }*/

   }

    private void addFavour(LatLng geo, int position, int idFav) throws JSONException {
       Log.d("addfavour",geo.toString());
        Log.d("addfavour",String.valueOf(position));
        //Necesitamos el idfav y la posicion en el array para luego coger la info del array, o borrar el favor con idfav
        JSONObject markerinfo = new JSONObject();
        markerinfo.put("position",position);
        markerinfo.put("idFav",idFav);

        mMap.addMarker(new MarkerOptions()
                .position(geo)
                .snippet(markerinfo.toString())
                //TODO: cambiar foto por icono user
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.hero))
        );
    }

    private void iterarFavores(){
       Log.d("info","En iterar favores");
      // JSONObject favor = new JSONObject();
      // for (int i = 0; i<favores.length();i++){
           try {
               //favor = favores.getJSONObject(i);
               LatLng locfavour = POST.locationToLatLng(favor.getString("location"));
               int idUserfavor = favor.getInt("idUser");
               int idFav = favor.getInt("idFav");


               if(idUserfavor==idUser){
                   Log.d("info","Hasfavoour");
                   myFavourPos = 0;
                   myidFav = idFav;
                   hasfavour = true;
                   String idSolicitado = favor.getString("idSolicitado");
                   if(idSolicitado!="" && !notified){
                        notificarFavor(0,myidFav);
                       notified = true;
                   }
               }
               addFavour(locfavour,0,idFav);
           } catch (JSONException e) {
               e.printStackTrace();
           }
       //}
    }


    public boolean onMarkerClick(final Marker marker) {
        int pos = 0;
        int idFav = 0;
       /* try {
            JSONObject markerinfo = new JSONObject(marker.getSnippet());
            pos = markerinfo.getInt("position");
            idFav = markerinfo.getInt("idFav");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        openFavour(0,0);
        return true;
    }

    private void openFavour(int position, final int idFav ) {

        newFavourPopUp();
        String titulo = "";
        String descripcion = "";
        String username = "";
        int acabado = 0;
        String idSolicitado = "";
        int idUserFavor = 0;
        try {
            //JSONObject favor = favores.getJSONObject(position);
            titulo = favor.getString("titulo");
            descripcion = favor.getString("descripcion");
            username = favor.getString("name");
            idSolicitado = favor.getString("idSolicitado");
            idUserFavor = favor.getInt("idUser");
            Log.d("idSolicitado", "ID SOLICITADOOOO " + idSolicitado);
            Log.d("nombreeee", username);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String finalusername = username;
        //title
        TextView textView = (TextView) popUp.getContentView().findViewById(R.id.favourTitle);
        textView.setText(titulo);
        //Desc
        textView = (TextView) popUp.getContentView().findViewById(R.id.favourDescription);
        textView.setText(descripcion);
        //username
        textView = (TextView) popUp.getContentView().findViewById(R.id.username);
        textView.setText(username);

        final Button button = popUp.getContentView().findViewById(R.id.close);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popUp.dismiss();
            }
        });


        final Button delete = popUp.getContentView().findViewById(R.id.delete);
        final Button solicitar = popUp.getContentView().findViewById(R.id.doFavour);

        //Si es mi favor
        if (idFav == myidFav) {
            //Puedo borrarlo

            delete.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                public void onClick(View v) {
                    try {
                        deleteFavour(idFav);
                        notified = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    popUp.dismiss();

                }
            });
            //Si ha sido solicitado, puedo completarlo
            if (!idSolicitado.equals("")) {
                solicitar.setText("Completed");
                Log.d("info", "Es mio y puedo completarlo");
                solicitar.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    public void onClick(View v) {
                        try {
                            completeFavour(idFav);
                            notified = false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        popUp.dismiss();

                    }
                });
            }
            else {
                //Si no ha sido completado, no puedo completarlo
                Log.d("info", "Es mio y NOOO puedo completarlo");
                solicitar.setVisibility(View.INVISIBLE);
            }
        } else {
            delete.setVisibility(View.INVISIBLE);
        }

        if (idFav != myidFav) {
            solicitar.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                public void onClick(View v) {
                    try {
                        JSONObject solicitarjson = new JSONObject();
                        solicitarjson.put("idUser", String.valueOf(idUser));
                        solicitarjson.put("idFav", String.valueOf(idFav));
                       // favores.put("0,new JSONObject());
                        favores.put(0,solicitarjson);
                       // POST.POST("solicitarfavor", solicitarjson);
                        toastSolicitar(finalusername);
                        updateFavours();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    popUp.dismiss();

                }
            });
            int idSol = 0;
            try {
                idSol = Integer.parseInt(idSolicitado);
            } catch (Exception e){

            }
            if (!idSolicitado.equals("")) {

                if(idSol!=login.idUser){
                    Log.d("info", "No es mio y no puedo solicitarlo");
                    //Si no lo he solicitado yo
                    solicitar.setVisibility(View.INVISIBLE);
                } else{
                    //si lo he solicitado yo
                   solicitar.setText("Cancel");
                    solicitar.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        public void onClick(View v) {
                            try {
                                JSONObject solicitarjson = new JSONObject();
                                solicitarjson.put("idFav", String.valueOf(idFav));
                                POST.POST("cancelarfavor", solicitarjson);
                                toastCancelar(finalusername);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            popUp.dismiss();

                        }
                    });
                }

            } else {
                Log.d("info", "NO Es mio y puedo solicitarlo");
            }

        }
    }
    //Function to show a new popup
    private void newFavourPopUp() {
        //If another popup is shown, it closes before the next one opens

        closeAllPopUps();
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.tobe_favour, null);
        popUp = new PopupWindow(
                customView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        popUp.showAtLocation(findViewById(R.id.map), Gravity.CENTER, 0, 0);
    }
    //funcion que comprueba si hay popups abiertas y las cierra


    private void closeAllPopUps() {
        if (popUp != null && popUp.isShowing()) {
            popUp.dismiss();
        }
        if (newFavourPopUp != null && newFavourPopUp.isShowing()) {
            newFavourPopUp.dismiss();
        }
    }


    private void newFavour() throws JSONException {
        Log.d("newfavour","estoy qui");
        //cerramos popups abiertas si había;
        closeAllPopUps();
        if(!hasfavour) {
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.new_favour, null);
            newFavourPopUp = new PopupWindow(
                    customView,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            newFavourPopUp.setFocusable(true);
            newFavourPopUp.update();
            newFavourPopUp.showAtLocation(findViewById(R.id.map), Gravity.CENTER, 0, 0);

            TextView username = (TextView)  newFavourPopUp.getContentView().findViewById(R.id.username);
            //Log.d("username",login.loginData.getString("name"));
                username.setText("Javi");

            //Close popup
            final Button button = newFavourPopUp.getContentView().findViewById(R.id.close);

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    newFavourPopUp.dismiss();
                }
            });

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            final Button sendButton = newFavourPopUp.getContentView().findViewById(R.id.sendFavour);

            sendButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                public void onClick(View v) {
                    EditText text = (EditText) newFavourPopUp.getContentView().findViewById(R.id.favourText);
                    EditText description = (EditText) newFavourPopUp.getContentView().findViewById(R.id.favourDescription);
                    if (text.length() > 0) {
                        //Log.d("Favour text:", favour);
                        JSONObject favour = new JSONObject();
                        try {
                            favour.put("idUser", String.valueOf(idUser));
                            favour.put("titulo", text.getText().toString());
                            favour.put("descripcion", description.getText().toString());
                            favor = favour;
                            //favores.put(0,favour);
                          //  JSONObject favourData = POST.POST("pedirfavor", favour);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //UPDATE FAVOURS
                        //iterarFavores();

                        mMap.addMarker(new MarkerOptions()
                                .position(userLocation)
                                .snippet("Luis necesita ayuda")
                                //TODO: cambiar foto por icono user
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.hero))

                        );


                       /* try {
                           // updateLocation();
                           // updateFavours();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        newFavourPopUp.dismiss();
                    } else {
                        //Log.d("Favour text:", favour);
                        Context context = getApplicationContext();
                        CharSequence toasttext = "You must describe the favour";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, toasttext, duration);
                        toast.show();
                    }
                }
            });
        } else{
            Context context = getApplicationContext();
            CharSequence toasttext = "You've already asked for a favour. Please, delete your current favour or wait until someone helps you.";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, toasttext, duration);
            toast.show();
            Log.d("myFavourPos",String.valueOf(myFavourPos));
            Log.d("myidFav",String.valueOf(myidFav));

            openFavour(myFavourPos,myidFav);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateFavours() throws JSONException, IOException {
        /*
        mMap.clear();
        Log.d("info","listarfavoures");
        login.favourData = POST.POST("listarfavores",login.idUserjson);
        favores=login.favourData.getJSONArray("favores");
        iterarFavores();
       */
    }



    public void openProfile(View v) throws JSONException {
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
    }

    private void startActivityForResult(Intent intent) {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void deleteFavour(int pos) throws JSONException, IOException {
        JSONObject favourID = new JSONObject();
        favourID.put("idUser",String.valueOf(idUser));
        favourID.put("idFav",String.valueOf(pos));
        stats = POST.POST("borrarfavor", favourID);
        hasfavour = false;
        updateFavours();
    }


    public void getLocation(){
        Log.d("On get location","OK");
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
                                Log.d("GETLocation:", userLocation.toString());
                            }
                        }
                    });
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateLocation() throws JSONException, IOException {
        getLocation();
        JSONObject locatjson = new JSONObject();
        locatjson.put("idUser",String.valueOf(idUser));
        locatjson.put("location",POST.locationToString(userLocation));
        POST.POST("actualizarubicacion", locatjson);
    }

    private void timer(){
        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        handler.postDelayed(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void run(){
                try {
                    updateLocation();
                    updateFavours();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){

                }
                handler.postDelayed(this, delay);
            }
        }, delay);

    }

    private void setProfileImage() throws JSONException {
        try {
            byte[] decodedString = Base64.decode(login.loginData.getString("avatar"), Base64.DEFAULT);
            //Log.d("login:base64", login.loginData.getString("avatar"));
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageview = (ImageView) findViewById(R.id.profilepic);
            Log.d("login:base64", imageview.toString());
            imageview.setImageBitmap(decodedByte);

        } catch(Exception e){

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void completeFavour(int idFav) throws JSONException, IOException {
        JSONObject completar = new JSONObject();
        completar.put("idFav",String.valueOf(idFav));
        POST.POST("completarfavor", completar);
        Toast.makeText(this, "Great! We're glad a hero helped you!", Toast.LENGTH_SHORT).show();
    }

    public void toastSolicitar(String nme){
        Toast.makeText(this, "Not all hero's wear capes... Go help " + nme + "!!", Toast.LENGTH_LONG).show();
    }

    public void toastCancelar(String nme){
        Toast.makeText(this, "Oh... We're sad you couldn't help " + nme + "!!", Toast.LENGTH_LONG).show();
    }

    public void notificarFavor(int pos, int idFav) throws JSONException {
        openFavour(pos,idFav);
        Toast.makeText(this, "A hero is coming to help!", Toast.LENGTH_LONG).show();
    }



}

