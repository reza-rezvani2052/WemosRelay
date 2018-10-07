package ir.hosfa.wemos_relay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.hujiaweibujidao.wava.Techniques;
import com.github.hujiaweibujidao.wava.YoYo;

import Extra.Utility;

public class NoInternetActivity extends AppCompatActivity {

    ImageView imgNoInternet;
    Button btnCheckInternetAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        //...
        imgNoInternet = findViewById(R.id.imgNoInternet);
        btnCheckInternetAgain = findViewById(R.id.btnCheckInternetAgain);


        btnCheckInternetAgain.setTypeface(Utility.getTypeFace());
        //btnCheckInternetAgain.setTextSize(16);

        //...

        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
        //...
        onBtnCheckInternetAgain(null);

    }

    public void onBtnCheckInternetAgain(View view) {
        if (Utility.isConnectedToInternet()) {
            startActivity(new Intent(this, MainActivity.class));
            //...
            this.finish();
        } else {
            YoYo.with(Techniques.Tada).duration(750).playOn(imgNoInternet);
        }
    }

}
