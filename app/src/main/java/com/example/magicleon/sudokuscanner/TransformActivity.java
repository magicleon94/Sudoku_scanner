package com.example.magicleon.sudokuscanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by magicleon on 11/09/16.
 */
public class TransformActivity extends AppCompatActivity {
    TransformView transformView;
    Button transformButton;
    Bitmap bm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        transformView = (TransformView) findViewById(R.id.transformView);
        bm = BitmapFactory.decodeFile(getIntent().getStringExtra("photoPath"));
        transformView.setBitmap(bm);
        transformButton = (Button) findViewById(R.id.lullo);

        transformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transformView.showResult();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        onBackPressed();
        return true;
    }

}
