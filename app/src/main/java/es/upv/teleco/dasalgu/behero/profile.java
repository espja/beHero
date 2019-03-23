package es.upv.teleco.dasalgu.behero;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static es.upv.teleco.dasalgu.behero.MapsActivity.*;
import static es.upv.teleco.dasalgu.behero.login.idUser;

public class profile extends AppCompatActivity {
    //camera
    private Button btn;
    public static ImageView imageview;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    int myfavours =0;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myfavours = MapsActivity.myfavours;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView username = (TextView) findViewById(R.id.username);
        TextView karma = (TextView)  findViewById(R.id.karma);
        TextView favores = (TextView)  findViewById(R.id.favores);
        TextView city = (TextView)  findViewById(R.id.city);

        imageview = (ImageView) findViewById(R.id.profilepic);
        //karma.setText(stats.getInt("karma"));
        //favores.setText(stats.getInt("realizados"));
        //username.setText(login.loginData.getString("name"));

        //karma.setText(stats.getInt("karma"));
        //favores.setText(stats.getInt("realizados"));
        //username.setText(login.loginData.getString("name"));

        try {
            karma.setText(login.loginData.getString("karma"));
            favores.setText(String.valueOf(myfavours));
            username.setText(login.loginData.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }




        Geocoder gcd = new Geocoder(profile.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(login.userLocation.latitude, login.userLocation.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            System.out.println(addresses.get(0).getLocality());
            city.setText(addresses.get(0).getLocality());
        }
        else {
            // do your stuff
        }




        final Button button = findViewById(R.id.close);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }

        });

        final Button deletebtn = findViewById(R.id.deleteProfile);

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                JSONObject borrar = new JSONObject();
                try {
                    borrar.put("idUser",String.valueOf(login.idUser));
                    JSONObject subirjson = POST.POST("borrarcuenta", borrar);
                    toLogin();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        });


        final Button changeAvatar = findViewById(R.id.changeAvatar);

        changeAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPictureDialog();
            }
        });}

        //CAMERA
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });

        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);

    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Log.d("info","in inffffff");
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);

                    Toast.makeText(profile.this, "Image Saved!", Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(profile.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            //imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(profile.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 10, bytes);
        byte[] byteArray = bytes.toByteArray();
        try {
            //send image to server
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            JSONObject image = new JSONObject();
            image.put("idUser",String.valueOf(login.idUser));
            image.put("avatar",encoded);
            Log.d("AVATAR LENGTH BEF", String.valueOf(encoded.length()));
            JSONObject subirjson = POST.POST("subirfoto", image);

            byte[] decodedString = Base64.decode(image.getString("avatar"), Base64.DEFAULT);
            //Log.d("profile:base64",image.getString("avatar"));
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Log.d("registerbyte:base64", imageview.toString());
            imageview.setImageBitmap(decodedByte);
            MapsActivity.imageview.setImageBitmap(decodedByte);
        }
            catch
         (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){

        }
        return "";
    }

    public void toLogin(){
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }

}
