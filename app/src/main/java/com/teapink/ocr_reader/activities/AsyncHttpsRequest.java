package com.teapink.ocr_reader.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONException;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kevin Niranjani on 29/05/18.
 */
public class AsyncHttpsRequest extends AsyncTask<String, Void, String> {

    ProgressDialog dialog;
    Context context;
    CompleteTaskListner listener;
    int response_code;
    List<Pair<String, String>> listParams;
    String dialogMessage;
    String URL;
    boolean isGet;
    boolean chkIsCancel;
    Request request;

    public AsyncHttpsRequest() {
    }

    public AsyncHttpsRequest(String dialogMessage, Context context, List<Pair<String, String>> listParams, Object obj, int res_code, boolean isGet) {
        this.dialogMessage = dialogMessage;
        this.context = context;
        listener = (CompleteTaskListner) obj;
        this.response_code = res_code;
        this.listParams = listParams;
        this.isGet = isGet;
    }

    @Override
    protected void onPreExecute() {

        if (hasConnection(context)) {
            dialog = new ProgressDialog(context);
            dialog.setMessage(dialogMessage);
            dialog.setCancelable(false);

            dialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                    cancel(true);
                    Toast.makeText(context, "request cancelled by user!", Toast.LENGTH_LONG).show();
                }
            });
            if (dialogMessage.equalsIgnoreCase("show")) {
                dialog.setMessage("");
            }
            if (!dialogMessage.equalsIgnoreCase(""))
                dialog.show();
        } else {
            cancel(true);
//			Toast.makeText(context,context.getApplicationContext().getString(R.string.no_internet) ,Toast.LENGTH_LONG).show();
        }


        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urls) {
        // TODO Auto-generated method stub

        URL = urls[0];

        if (chkIsCancel == true) {
//			AsyncHttpsRequest request=new AsyncHttpsRequest();
//			request.cancel(true);
        }

        try {
            OkHttpClient client = new OkHttpClient();
            // Initialize Builder (not RequestBody)


            if (isGet) {
                request = new Request.Builder()
                        .url(URL)
                        .build();
            } else {

                FormBody.Builder builder = new FormBody.Builder();

                for (int i = 0; i < listParams.size(); i++) {
                    Pair<String, String> pair = listParams.get(i);
                    builder.add(pair.first, pair.second);
                }

                RequestBody formBody = builder.build();

                request = new Request.Builder()
                        .url(URL)
                        .post(formBody)
                        .build();
            }

            Response response = client.newCall(request).execute();

            return response.body().string();

        } catch (final Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if (result != null) {

            try {
                listener.completeTask(result, response_code);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onPostExecute(result);
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }

    /*********************************************************************************************************/

}