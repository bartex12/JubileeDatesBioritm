package ru.bartex.screen_shot;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    Button mButton;
    ImageView mImageView;
    View main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main =  findViewById(R.id.main);
        mButton = findViewById(R.id.button);
        mImageView = findViewById(R.id.imageView);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b = Screenshot.takeScreenshotOfRootView(mImageView);
                mImageView.setImageBitmap(b);
                main.setBackgroundColor(Color.parseColor("#999999"));
            }
        });
    }

}
