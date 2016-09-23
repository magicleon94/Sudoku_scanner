package com.example.magicleon.sudokuscanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by magicleon on 11/09/16.
 */
public class TransformActivity extends AppCompatActivity {
    TransformView transformView;
    Button transformButton;
    Bitmap bm;
    Intent intent;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        transformView = (TransformView) findViewById(R.id.transformView);
        bm = BitmapFactory.decodeFile(getIntent().getStringExtra("photoPath"));
        transformView.setBitmap(bm);
        transformButton = (Button) findViewById(R.id.transformButton);
        mProgressDialog = new ProgressDialog(this);
        transformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.setMessage("Computing");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                ArrayList<Integer> sudoku = transformView.computeSudoku();
                mProgressDialog.dismiss();
                intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
                intent.putIntegerArrayListExtra("sudoku",sudoku);
                startActivity(intent);
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
