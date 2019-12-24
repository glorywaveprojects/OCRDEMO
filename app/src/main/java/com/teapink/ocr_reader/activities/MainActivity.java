package com.teapink.ocr_reader.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.teapink.ocr_reader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CompleteTaskListner
{

    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView textValue;
    private Button copyButton;
    private Button mailTextButton;

    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";
    String itemname;
    String key="AIzaSyBflKFosCFLmnDTdxA-81oQV_m1U4LFE6c";
    String qry="Android";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        statusMessage = (TextView)findViewById(R.id.status_message);
        textValue = (TextView)findViewById(R.id.text_value);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        Button readTextButton = (Button) findViewById(R.id.read_text_button);
        readTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch Ocr capture activity.
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, true);
                intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

                startActivityForResult(intent, RC_OCR_CAPTURE);

            }
        });
        Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
        intent.putExtra(OcrCaptureActivity.AutoFocus, true);
        intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

        startActivityForResult(intent, RC_OCR_CAPTURE);
        copyButton = (Button) findViewById(R.id.copy_text_button);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard =
                            (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(textValue.getText().toString());


                } else {
                    android.content.ClipboardManager clipboard =
                            (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", textValue.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }
                itemname=textValue.getText().toString();
                callGETDATA(textValue.getText().toString());

            }
        });

        mailTextButton = (Button) findViewById(R.id.mail_text_button);
        mailTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_SUBJECT, "Text Read");
                i.putExtra(Intent.EXTRA_TEXT, textValue.getText().toString());
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.mail_intent_chooser_text)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(),
                            R.string.no_email_client_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (textValue.getText().toString().isEmpty()) {
            copyButton.setVisibility(View.GONE);
//            mailTextButton.setVisibility(View.GONE);
        } else {
            copyButton.setVisibility(View.VISIBLE);
//            mailTextButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    statusMessage.setText(R.string.ocr_success);
                    textValue.setText(text);
                    Log.d(TAG, "Text read: " + text);
                } else {
                    statusMessage.setText(R.string.ocr_failure);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void callGETDATA(String qry)
    {
        List<Pair<String, String>> params = new ArrayList<>();

        new AsyncHttpsRequest("Please Wait..", MainActivity.this, params, this, 1, true).execute("https://www.googleapis.com/customsearch/v1?key=AIzaSyD0tG4V0Inir2LUBZNqMDw4x6fFYjO4r-o&cx=006664225944792425499:jkhczmuxna2&q="+qry+"&alt=json");
    }

    @Override
    public void completeTask(String result, int response_code) throws JSONException
    {
//        Log.e("RESULT==>",result);
        try
        {
            JSONObject jdata=new JSONObject(result);
            JSONArray items=jdata.getJSONArray("items");
            for(int i=0;i<items.length();i++)
            {
                JSONObject job=items.getJSONObject(i);
                JSONObject pagemap=job.getJSONObject("pagemap");

                JSONArray metatagsArray=pagemap.getJSONArray("metatags");
                for(int k=0;k<metatagsArray.length();k++)
                {
                    JSONObject jmeta=metatagsArray.getJSONObject(k);
                    String desc=jmeta.getString("dc.description");
                    Log.e("pagemap=>",""+desc);
                    Intent i1=new Intent(getApplicationContext(),Description.class);
                    i1.putExtra("itemname",itemname);
                    i1.putExtra("desc",desc);
                    startActivity(i1);
                }
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }
}
