package com.aware.plugin.temporalux;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;

import com.aware.Aware;
import com.aware.ESM;
import com.aware.providers.Installations_Provider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by denzil on 07/08/15.
 */
public class Questionnaire_end extends IntentService {
    public Questionnaire_end() {
        super("TemporalUX Questionnaire End");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int days_in_study = 0;
        if( Aware.getSetting(this, "study_start").length() > 0 ) {
            days_in_study = (int)( (System.currentTimeMillis()-Double.valueOf(Aware.getSetting(getApplicationContext(), "study_start")))/(1000*60*60*24) );
        }
        if( days_in_study == 0 ) days_in_study = 1;

        JSONArray esm_queue = new JSONArray();
        Cursor installations = getContentResolver().query(Installations_Provider.Installations_Data.CONTENT_URI, null, null, null, Installations_Provider.Installations_Data.APPLICATION_NAME + " ASC");
        if( installations != null && installations.moveToFirst() ) {
            try {
                JSONObject question = new JSONObject();
                JSONObject esmJSON = new JSONObject();
                esmJSON.put("esm_type", ESM.TYPE_ESM_QUICK_ANSWERS);
                esmJSON.put("esm_title", "Final questionnaire");
                esmJSON.put("esm_instructions", "During the last " + days_in_study + " day(s), you have installed " + installations.getCount() + " apps. We will now ask you to rate each one, one last time.");
                esmJSON.put("esm_quick_answers", new JSONArray().put("OK"));
                esmJSON.put("esm_expiration_threshold", 300);
                esmJSON.put("esm_trigger", "qEnd");
                question.put("esm", esmJSON);
                esm_queue.put(question);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            do {
                try {
                    JSONObject question = new JSONObject();
                    JSONObject esmJSON = new JSONObject();
                    esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
                    esmJSON.put("esm_instructions", "Overall, I perceived " + installations.getString(installations.getColumnIndex(Installations_Provider.Installations_Data.APPLICATION_NAME)) + " as...");
                    esmJSON.put("esm_scale_min", 1);
                    esmJSON.put("esm_scale_max", 7);
                    esmJSON.put("esm_scale_start", 4);
                    esmJSON.put("esm_scale_max_label", "Good");
                    esmJSON.put("esm_scale_min_label", "Bad");
                    esmJSON.put("esm_scale_step", 1);
                    esmJSON.put("esm_submit", "Next");
                    esmJSON.put("esm_expiration_threshold", 5 * 60);
                    esmJSON.put("esm_trigger", "qEnd:" + installations.getString(installations.getColumnIndex(Installations_Provider.Installations_Data.PACKAGE_NAME)));
                    question.put("esm", esmJSON);
                    esm_queue.put(question);

                    question = new JSONObject();
                    esmJSON = new JSONObject();
                    esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
                    esmJSON.put("esm_instructions", "Would you start using " + installations.getString(installations.getColumnIndex(Installations_Provider.Installations_Data.APPLICATION_NAME)) + " over again (assuming that you can start again and you know what you now know)?");
                    esmJSON.put("esm_scale_min", 1);
                    esmJSON.put("esm_scale_max", 7);
                    esmJSON.put("esm_scale_start", 4);
                    esmJSON.put("esm_scale_max_label", "Not at all");
                    esmJSON.put("esm_scale_min_label", "Very much");
                    esmJSON.put("esm_scale_step", 1);
                    esmJSON.put("esm_submit", "Next");
                    esmJSON.put("esm_expiration_threshold", 5 * 60);
                    esmJSON.put("esm_trigger", "qEnd:" + installations.getString(installations.getColumnIndex(Installations_Provider.Installations_Data.PACKAGE_NAME)));
                    question.put("esm", esmJSON);
                    esm_queue.put(question);

                    question = new JSONObject();
                    esmJSON = new JSONObject();
                    esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
                    esmJSON.put("esm_instructions", "If your friends were planning to use " + installations.getString(installations.getColumnIndex(Installations_Provider.Installations_Data.APPLICATION_NAME)) + ", how likely is it that you would recommend it to them?");
                    esmJSON.put("esm_scale_min", 1);
                    esmJSON.put("esm_scale_max", 7);
                    esmJSON.put("esm_scale_start", 4);
                    esmJSON.put("esm_scale_max_label", "Not at all");
                    esmJSON.put("esm_scale_min_label", "Very much");
                    esmJSON.put("esm_scale_step", 1);
                    esmJSON.put("esm_submit", "Next");
                    esmJSON.put("esm_expiration_threshold", 5 * 60);
                    esmJSON.put("esm_trigger", "qEnd:" + installations.getString(installations.getColumnIndex(Installations_Provider.Installations_Data.PACKAGE_NAME)));
                    question.put("esm", esmJSON);
                    esm_queue.put(question);

                    question = new JSONObject();
                    esmJSON = new JSONObject();
                    esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
                    esmJSON.put("esm_instructions", "Did " + installations.getString(installations.getColumnIndex(Installations_Provider.Installations_Data.APPLICATION_NAME)) + " meet your expectations?");
                    esmJSON.put("esm_scale_min", 1);
                    esmJSON.put("esm_scale_max", 7);
                    esmJSON.put("esm_scale_start", 4);
                    esmJSON.put("esm_scale_max_label", "Not at all");
                    esmJSON.put("esm_scale_min_label", "Very much");
                    esmJSON.put("esm_scale_step", 1);
                    esmJSON.put("esm_submit", "Next");
                    esmJSON.put("esm_expiration_threshold", 5 * 60);
                    esmJSON.put("esm_trigger", "qEnd:" + installations.getString(installations.getColumnIndex(Installations_Provider.Installations_Data.PACKAGE_NAME)));
                    question.put("esm", esmJSON);
                    esm_queue.put(question);

                    question = new JSONObject();
                    esmJSON = new JSONObject();
                    esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
                    esmJSON.put("esm_instructions", "How willing are you to continue using " + installations.getString(installations.getColumnIndex(Installations_Provider.Installations_Data.APPLICATION_NAME)) + "?");
                    esmJSON.put("esm_scale_min", 1);
                    esmJSON.put("esm_scale_max", 7);
                    esmJSON.put("esm_scale_start", 4);
                    esmJSON.put("esm_scale_max_label", "Not at all");
                    esmJSON.put("esm_scale_min_label", "Very much");
                    esmJSON.put("esm_scale_step", 1);
                    esmJSON.put("esm_submit", "Next");
                    esmJSON.put("esm_expiration_threshold", 5 * 60);
                    esmJSON.put("esm_trigger", "qEnd:" + installations.getString(installations.getColumnIndex(Installations_Provider.Installations_Data.PACKAGE_NAME)));
                    question.put("esm", esmJSON);
                    esm_queue.put(question);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while(installations.moveToNext());

            //Ask the questions to the user
            Intent questionnaire = new Intent(ESM.ACTION_AWARE_QUEUE_ESM);
            questionnaire.putExtra(ESM.EXTRA_ESM, esm_queue.toString());
            sendBroadcast(questionnaire);
        }
        if( installations != null && ! installations.isClosed() ) installations.close();
    }
}
