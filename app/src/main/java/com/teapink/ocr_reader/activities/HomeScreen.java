package com.teapink.ocr_reader.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.teapink.ocr_reader.R;

public class HomeScreen extends Activity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
    }
}
