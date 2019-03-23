package es.upv.teleco.dasalgu.behero;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class register extends AppCompatActivity {

    EditText passwordEditText,usernameEditText,mailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign);
        usernameEditText   = (EditText)findViewById(R.id.name);
        passwordEditText   = (EditText)findViewById(R.id.pass);
        mailEditText   = (EditText)findViewById(R.id.mail);
    }

    /** Called when the user taps the Send button */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void signUp(View view) throws JSONException {
        JSONObject register = new JSONObject();
        register.put("name", usernameEditText.getText().toString());
        register.put("pass",POST.md5(passwordEditText.getText().toString()));
        register.put("mail",mailEditText.getText().toString());
        if(isValidUserName(usernameEditText.getText().toString())) {
            if (isValidEmailAddress(mailEditText.getText().toString())) {
                if (isValidPass(passwordEditText.getText().toString())) {
                    try {

                        JSONObject registerData = POST.POST("registro", register);
                        String status = registerData.getString("status");
                        Log.d("register", status);
                        if (status.equals("OK")) {
                            Toast.makeText(this, "Congratulations! You're a Hero now!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, login.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Uh Oh! That username is not available.", Toast.LENGTH_SHORT).show();
                            usernameShake();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void usernameShake() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        findViewById(R.id.name).startAnimation(shake);
    }

    private void passwordShake() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        findViewById(R.id.pass).startAnimation(shake);
    }

    private void mailShake() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        findViewById(R.id.mail).startAnimation(shake);
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
            Toast.makeText(this, "Username is too short", Toast.LENGTH_SHORT).show();
            usernameShake();
            Log.d("isvalidusername", "second");
            return false;
        }
        return true;
    }

    private boolean isValidEmailAddress(String emailAddress) {
        /**
         * if mail address is NULL string
         */
        if (TextUtils.isEmpty(emailAddress)) {
            Toast.makeText(this, "Empty mail", Toast.LENGTH_SHORT).show();
            mailShake();
            return false;
        }
        // if mail address does NOT contain "@"
        if (!emailAddress.contains("@")) {
            Toast.makeText(this, "Invalid mail", Toast.LENGTH_SHORT).show();
            mailShake();
            return false;
        }
        /**
         * if mail address does NOT contain "."
         */
        if (!emailAddress.contains(".")) {
            Toast.makeText(this, "Invalid mail", Toast.LENGTH_SHORT).show();
            mailShake();
            return false;
        }

        return true;
    }

    private boolean isValidPass(String pass) {
        if (pass.length()<4) {
            Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show();
            passwordShake();
            return false;
        }

        return true;
    }
    public void openLogIn(View v){
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }

}



