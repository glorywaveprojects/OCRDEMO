package com.teapink.ocr_reader.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teapink.ocr_reader.R;

public class Description extends Activity
{
    String itemname,desc;
    TextView txtitem,txtDesc;
    RelativeLayout backlayout;
    Button read_text_button;
    private static final int RC_OCR_CAPTURE = 9003;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);
        Intent i1=getIntent();
        itemname=i1.getStringExtra("itemname");
        desc=i1.getStringExtra("desc");

        backlayout=(RelativeLayout)findViewById(R.id.backlayout);
        backlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        read_text_button=(Button)findViewById(R.id.read_text_button);
        read_text_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        txtitem=(TextView)findViewById(R.id.txtitem);
        txtDesc=(TextView)findViewById(R.id.txtDesc);
        txtitem.setText(itemname);
        txtDesc.setText(desc);



    }
}
