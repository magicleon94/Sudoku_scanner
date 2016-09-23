package com.example.magicleon.sudokuscanner;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by magicleon on 23/09/16.
 */

public class ConfirmationActivity extends AppCompatActivity {
    TextView sampleResult;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_layout);
        sampleResult = (TextView) findViewById(R.id.sudokuString);
        ArrayList<Integer> sudoku = getIntent().getIntegerArrayListExtra("sudoku");
        String text = "";
        for(int i=0; i<sudoku.size();i++){
            if(i%9==0 && i>0){
                text += "\n\n\n";
            }

            text += sudoku.get(i) + "\t\t\t\t";
        }
        sampleResult.setText(text);
    }
}
