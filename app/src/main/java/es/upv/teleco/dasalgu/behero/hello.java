package es.upv.teleco.dasalgu.behero;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

public class hello extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hello);
    }

    public void login(View view){
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }
    public void signup(View view){
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }

}
