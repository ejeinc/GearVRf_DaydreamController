package com.eje_c.daydreamcontroller;

import android.os.Bundle;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRMain;

public class MainActivity extends GVRActivity {
    private final GVRMain main = new Main();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMain(main);
    }
}
