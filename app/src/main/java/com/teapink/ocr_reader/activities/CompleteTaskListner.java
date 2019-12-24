package com.teapink.ocr_reader.activities;

import org.json.JSONException;

/**
 * Created by Paras Valera on 30/01/17.
 */
public interface CompleteTaskListner
{
    public void completeTask(String result, int response_code) throws JSONException;
}