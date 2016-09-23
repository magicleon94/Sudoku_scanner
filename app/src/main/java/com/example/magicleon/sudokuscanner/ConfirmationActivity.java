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
    SudokuView  sudokuView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sudokuView = new SudokuView(getApplicationContext(),getIntent().getIntegerArrayListExtra("sudoku"));
        setContentView(sudokuView);
    }
}
