package com.example.magicleon.sudokuscanner;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity {
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        if (savedInstanceState==null){
            getFragmentManager().beginTransaction().add(R.id.mainFragmentContainer, new MainFragment()).commit();
        }
    }


}
