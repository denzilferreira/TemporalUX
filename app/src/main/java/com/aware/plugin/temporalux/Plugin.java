package com.aware.plugin.temporalux;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.aware.Installations;
import com.aware.providers.Applications_Provider;
import com.aware.providers.ESM_Provider;
import com.aware.utils.Aware_Plugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by denzil on 25/6/15.
 */
public class Plugin extends Aware_Plugin {

//    private static boolean DEBUG = true;

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Installations.ACTION_AWARE_APPLICATION_ADDED);
        filter.addAction(Installations.ACTION_AWARE_APPLICATION_REMOVED);
        filter.addAction(Applications.ACTION_AWARE_APPLICATIONS_FOREGROUND);

        registerReceiver(temporalUXListener, filter);

        Cursor snapshot = getContentResolver().query(Provider.TemporalUX_Data.CONTENT_URI, null, null, null, null);
        if( snapshot == null || ! snapshot.moveToFirst() ) {
            List<PackageInfo> all_apps = getPackageManager().getInstalledPackages(0);
            for( PackageInfo pkg : all_apps ) {
                ContentValues snapData = new ContentValues();
                snapData.put(Provider.TemporalUX_Data.TIMESTAMP, System.currentTimeMillis());
                snapData.put(Provider.TemporalUX_Data.DEVICE_ID, Aware.getSetting(this, Aware_Preferences.DEVICE_ID));
                snapData.put(Provider.TemporalUX_Data.PACKAGE_NAME, pkg.packageName);
                snapData.put(Provider.TemporalUX_Data.IS_BUNDLED, isSystemPackage(pkg));
                getContentResolver().insert(Provider.TemporalUX_Data.CONTENT_URI, snapData);
            }
        }
        if( snapshot != null && ! snapshot.isClosed() ) snapshot.close();

        DATABASE_TABLES = Provider.DATABASE_TABLES;
        TABLES_FIELDS = Provider.TABLES_FIELDS;
        CONTEXT_URIS = new Uri[]{ Provider.TemporalUX_Data.CONTENT_URI };

        Aware.startPlugin(this, "com.aware.plugin.temporalux");
    }

    private static TemporalUXListener temporalUXListener = new TemporalUXListener();
    public static class TemporalUXListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //User installed a new application
            if( intent.getAction().equals(Installations.ACTION_AWARE_APPLICATION_ADDED) ) {

                String package_name = intent.getStringExtra(Installations.EXTRA_PACKAGE_NAME);
                String app_name = intent.getStringExtra(Installations.EXTRA_APPLICATION_NAME);

                JSONArray esm_queue = new JSONArray();
                esm_queue.put(getFirstTimeAppUse("Q0", app_name, "q0:"+package_name));
                esm_queue.put(getExpectationBeforeFirstUse("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedCompetence("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedRelatedness("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedAutonomy("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedQIValue("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedQIPro("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedQIClassy("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedQIInnovative("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedQINew("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedQICourageous("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedQIManageable("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedQISimple("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedQIDirect("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getExpectedQIGood("Q0", app_name, "q0:" + package_name));

                //Ask the questions to the user
                Intent questionnaire = new Intent(ESM.ACTION_AWARE_QUEUE_ESM);
                questionnaire.putExtra(ESM.EXTRA_ESM, esm_queue.toString());
                context.sendBroadcast(questionnaire);
            }

            //User removed an application
            if( intent.getAction().equals(Installations.ACTION_AWARE_APPLICATION_REMOVED) ) {

                String package_name = intent.getStringExtra(Installations.EXTRA_PACKAGE_NAME);
                String app_name = intent.getStringExtra(Installations.EXTRA_APPLICATION_NAME);

                JSONArray esm_queue = new JSONArray();
                esm_queue.put(getRemovedAppRationale("QR", app_name, "qR:" + package_name));
                esm_queue.put(getCompetence("QR", app_name, "qR:" + package_name));
                esm_queue.put(getRelatedness("QR", app_name, "qR:" + package_name));
                esm_queue.put(getAutonomy("QR", app_name, "qR:" + package_name));
                esm_queue.put(getQIValue("QR", app_name, "qR:" + package_name));
                esm_queue.put(getQIPro("QR", app_name, "qR:" + package_name));
                esm_queue.put(getQIClassy("QR", app_name, "qR:" + package_name));
                esm_queue.put(getQIInnovative("QR", app_name, "qR:" + package_name));
                esm_queue.put(getQINew("QR", app_name, "qR:" + package_name));
                esm_queue.put(getQICourageous("QR", app_name, "qR:" + package_name));
                esm_queue.put(getQIManageable("QR", app_name, "qR:" + package_name));
                esm_queue.put(getQISimple("QR", app_name, "qR:" + package_name));
                esm_queue.put(getQIDirect("QR", app_name, "qR:" + package_name));
                esm_queue.put(getQIGood("QR", app_name, "qR:" + package_name));

                //Ask the questions to the user
                Intent questionnaire = new Intent(ESM.ACTION_AWARE_QUEUE_ESM);
                questionnaire.putExtra(ESM.EXTRA_ESM, esm_queue.toString());
                context.sendBroadcast(questionnaire);
            }

            //User using applications
            if( intent.getAction().equals(Applications.ACTION_AWARE_APPLICATIONS_FOREGROUND) ) {

                String app_name = "";
                String current_app = "";
                String package_name = "";

                int app_counter = 0;
                int temporalUXQ = 0;

                int avg_app_usage = 0;

                int days_in_study = 0;
                if( Aware.getSetting(context, "study_start").length() > 0 ) {
                    days_in_study = (int)( (System.currentTimeMillis()-Double.valueOf(Aware.getSetting(context, "study_start")))/(1000*60*60*24) );
                }
                if( days_in_study == 0 ) days_in_study = 1;

                //Get the last two applications used
                Cursor app = context.getContentResolver().query(Applications_Provider.Applications_Foreground.CONTENT_URI, null, null, null, Applications_Provider.Applications_Foreground.TIMESTAMP + " DESC LIMIT 2");
                if( app != null && app.moveToFirst() ) {
                    current_app = app.getString(app.getColumnIndex(Applications_Provider.Applications_Foreground.PACKAGE_NAME));
                    if( app.moveToNext() ) {
                        package_name = app.getString(app.getColumnIndex(Applications_Provider.Applications_Foreground.PACKAGE_NAME));
                        app_name = app.getString(app.getColumnIndex(Applications_Provider.Applications_Foreground.APPLICATION_NAME));
                    }
                }
                if( app != null && ! app.isClosed() ) app.close();

                if( current_app.matches("com.aware.*") || isSystemPackage(context, current_app) ) return; //system apps are ignored

                //Check if the previous app was used more than once before
                Cursor app_launches = context.getContentResolver().query(Applications_Provider.Applications_Foreground.CONTENT_URI, new String[]{"count(*) as total_launches"}, Applications_Provider.Applications_Foreground.PACKAGE_NAME + " LIKE '"+package_name+"'", null, null);
                if( app_launches != null && app_launches.moveToFirst() ) {
                    app_counter = app_launches.getInt(0);
                }
                if( app_launches != null && ! app_launches.isClosed() ) app_launches.close();

                Cursor temporalQ = context.getContentResolver().query(ESM_Provider.ESM_Data.CONTENT_URI, new String[]{"count(*) as total_temporal_questions"}, ESM_Provider.ESM_Data.TRIGGER + " LIKE '%" + package_name + "%'", null, null);
                if( temporalQ != null && temporalQ.moveToFirst() ) {
                    temporalUXQ = temporalQ.getInt(0);
                }
                if( temporalQ != null && ! temporalQ.isClosed() ) temporalQ.close();

                Cursor avg_app_launches = context.getContentResolver().query(Applications_Provider.Applications_Foreground.CONTENT_URI, new String[]{"count(*) as app_launches", Applications_Provider.Applications_Foreground.PACKAGE_NAME }, "1) GROUP BY ( " + Applications_Provider.Applications_Foreground.PACKAGE_NAME, null, null);
                if( avg_app_launches != null && avg_app_launches.moveToFirst() ) {
                    int sum_launches = 0;
                    do {
                        sum_launches = sum_launches + avg_app_launches.getInt(avg_app_launches.getColumnIndex("app_launches"));
                    } while( avg_app_launches.moveToNext() );
                    avg_app_usage = sum_launches/avg_app_launches.getCount();
                }
                if( avg_app_launches != null && ! avg_app_launches.isClosed() ) avg_app_launches.close();

                if( DEBUG ) Log.d(TAG, "\nApp: " + current_app + "\n# launches:" + app_counter + "\nTemporalUX Q:" + temporalUXQ + "\n");

                //Used the application for the first time
                if( app_counter == 1 && ! alreadyQ(context, "q1:"+package_name) ) {

                    JSONArray esm_queue = new JSONArray();
                    esm_queue.put(getUsage("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getCompetence("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getRelatedness("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getAutonomy("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getQIValue("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getQIPro("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getQIClassy("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getQIInnovative("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getQINew("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getQICourageous("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getQIManageable("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getQISimple("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getQIDirect("Q1", app_name, "q1:" + package_name));
                    esm_queue.put(getQIGood("Q1", app_name, "q1:" + package_name));

                    //Ask the questions to the user
                    Intent questionnaire = new Intent(ESM.ACTION_AWARE_QUEUE_ESM);
                    questionnaire.putExtra(ESM.EXTRA_ESM, esm_queue.toString());
                    context.sendBroadcast(questionnaire);

                } else if( app_counter == 5 && ! alreadyQ(context, "q5:"+package_name) ) { //used the application for 5 times

                    JSONArray esm_queue = new JSONArray();
                    esm_queue.put(getUsage("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getCompetence("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getRelatedness("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getAutonomy("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getQIValue("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getQIPro("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getQIClassy("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getQIInnovative("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getQINew("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getQICourageous("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getQIManageable("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getQISimple("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getQIDirect("Q5", app_name, "q5:" + package_name));
                    esm_queue.put(getQIGood("Q5", app_name, "q5:" + package_name));

                    //Ask the questions to the user
                    Intent questionnaire = new Intent(ESM.ACTION_AWARE_QUEUE_ESM);
                    questionnaire.putExtra(ESM.EXTRA_ESM, esm_queue.toString());
                    context.sendBroadcast(questionnaire);

                } else if( days_in_study > 7 && app_counter >= avg_app_usage && ! alreadyQ(context, "qX:"+package_name) ) { //used this application for the average amount of time across all apps

                    JSONArray esm_queue = new JSONArray();
                    esm_queue.put(getUsage("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getCompetence("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getRelatedness("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getAutonomy("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getQIValue("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getQIPro("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getQIClassy("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getQIInnovative("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getQINew("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getQICourageous("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getQIManageable("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getQISimple("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getQIDirect("QX", app_name, "qX:" + package_name+":"+avg_app_usage));
                    esm_queue.put(getQIGood("QX", app_name, "qX:" + package_name+":"+avg_app_usage));

                    //Ask the questions to the user
                    Intent questionnaire = new Intent(ESM.ACTION_AWARE_QUEUE_ESM);
                    questionnaire.putExtra(ESM.EXTRA_ESM, esm_queue.toString());
                    context.sendBroadcast(questionnaire);
                }
            }
        }
    }

    public static boolean alreadyQ( Context c, String trigger ) {
        boolean answered = false;
        Cursor query = c.getContentResolver().query(ESM_Provider.ESM_Data.CONTENT_URI, null, ESM_Provider.ESM_Data.TRIGGER + " LIKE '%"+trigger+"%'", null, null);
        if( query != null && query.moveToFirst() && query.getCount() > 0 ) {
            answered = true;
        }
        if( query != null && ! query.isClosed() ) query.close();
        return answered;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Ask what the user did with this app
     * @return
     */
    public static JSONObject getUsage(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_TEXT);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "You just used the app " + app_name + ". Please briefly describe what you did.");
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    /**
     * Check why the user removed this app
     * @return
     */
    public static JSONObject getRemovedAppRationale(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_TEXT);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "Why did you remove " + app_name + "?");
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    /**
     * Check if the user is using this app for the first time
     * @return
     */
    public static JSONObject getFirstTimeAppUse(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_QUICK_ANSWERS);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "First time you are using " + app_name + "?");
            esmJSON.put("esm_quick_answers", new JSONArray().put("Yes").put("No"));
            esmJSON.put("esm_expiration_threshold", 300);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectationBeforeFirstUse(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_TEXT);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "What do you expect from " + app_name + "?");
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedCompetence(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", app_name + " will allow me to successfully completing difficult tasks and projects.");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Very much");
            esmJSON.put("esm_scale_min_label", "Not at all");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getCompetence(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "Using " + app_name + " made me feel that I successfully completed difficult tasks and projects.");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Very much");
            esmJSON.put("esm_scale_min_label", "Not at all");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedRelatedness(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", app_name + " will allow me to feel close and connected with other people who are important to me.");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Very much");
            esmJSON.put("esm_scale_min_label", "Not at all");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getRelatedness(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "Using "+app_name + " made me feel close and connected with other people who are important to me.");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Very much");
            esmJSON.put("esm_scale_min_label", "Not at all");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedAutonomy(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", app_name + " will allow me express my \'true self.\'");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Very much");
            esmJSON.put("esm_scale_min_label", "Not at all");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getAutonomy(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "Using " + app_name + " made me feel that I expressed my \'true self.\'");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Very much");
            esmJSON.put("esm_scale_min_label", "Not at all");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedQIValue(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I expect " + app_name + " to be...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Valuable");
            esmJSON.put("esm_scale_min_label", "Cheap");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getQIValue(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I perceived " + app_name + " as...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Valuable");
            esmJSON.put("esm_scale_min_label", "Cheap");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedQIPro(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I expect " + app_name + " to be...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Professional");
            esmJSON.put("esm_scale_min_label", "Amateurish");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getQIPro(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I perceived " + app_name + " as...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Professional");
            esmJSON.put("esm_scale_min_label", "Amateurish");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedQIClassy(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I expect " + app_name + " to be...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Classy");
            esmJSON.put("esm_scale_min_label", "Gaudy");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getQIClassy(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I perceived " + app_name + " as...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Classy");
            esmJSON.put("esm_scale_min_label", "Gaudy");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedQIInnovative(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I expect " + app_name + " to be...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Innovative");
            esmJSON.put("esm_scale_min_label", "Conservative");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getQIInnovative(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I perceived " + app_name + " as...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Innovative");
            esmJSON.put("esm_scale_min_label", "Conservative");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedQINew(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I expect " + app_name + " to be...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "New");
            esmJSON.put("esm_scale_min_label", "Commonplace");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getQINew(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I perceived " + app_name + " as...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "New");
            esmJSON.put("esm_scale_min_label", "Commonplace");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedQICourageous(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I expect " + app_name + " to be...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Courageous");
            esmJSON.put("esm_scale_min_label", "Cautious");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getQICourageous(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I perceived " + app_name + " as...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Courageous");
            esmJSON.put("esm_scale_min_label", "Cautious");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedQIManageable(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I expect " + app_name + " to be...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Manageable");
            esmJSON.put("esm_scale_min_label", "Unruly");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getQIManageable(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I perceived " + app_name + " as...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Manageable");
            esmJSON.put("esm_scale_min_label", "Unruly");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedQISimple(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I expect " + app_name + " to be...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Simple");
            esmJSON.put("esm_scale_min_label", "Complicated");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getQISimple(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I perceived " + app_name + " as...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Simple");
            esmJSON.put("esm_scale_min_label", "Complicated");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedQIDirect(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I expect " + app_name + " to be...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Direct");
            esmJSON.put("esm_scale_min_label", "Cumbersome");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getQIDirect(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I perceived " + app_name + " as...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Direct");
            esmJSON.put("esm_scale_min_label", "Cumbersome");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getExpectedQIGood(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I expect " + app_name + " to be...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Good");
            esmJSON.put("esm_scale_min_label", "Bad");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    public static JSONObject getQIGood(String title, String app_name, String trigger) {
        JSONObject question = new JSONObject();
        try {
            JSONObject esmJSON = new JSONObject();
            esmJSON.put("esm_type", ESM.TYPE_ESM_SCALE);
            esmJSON.put("esm_title", title);
            esmJSON.put("esm_instructions", "I perceived " + app_name + " as...");
            esmJSON.put("esm_scale_min", 1);
            esmJSON.put("esm_scale_max", 7);
            esmJSON.put("esm_scale_start", 4);
            esmJSON.put("esm_scale_max_label", "Good");
            esmJSON.put("esm_scale_min_label", "Bad");
            esmJSON.put("esm_scale_step", 1);
            esmJSON.put("esm_submit", "Next");
            esmJSON.put("esm_expiration_threshold", 5 * 60);
            esmJSON.put("esm_trigger", trigger);
            question.put("esm", esmJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return question;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(temporalUXListener);
        Aware.stopPlugin(this, "com.aware.plugin.temporalux");
    }

    /**
     * Check if a certain application is pre-installed or part of the operating system.
     * @param {@link PackageInfo} obj
     * @return boolean
     */
    private static boolean isSystemPackage(PackageInfo pkgInfo) {
        return pkgInfo != null && ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1);
    }

    private static boolean isSystemPackage(Context c, String package_name) {
        boolean is_bundled = false;
        Cursor snapshot = c.getContentResolver().query(Provider.TemporalUX_Data.CONTENT_URI, null, Provider.TemporalUX_Data.PACKAGE_NAME + " LIKE '"+ package_name +"' AND " + Provider.TemporalUX_Data.IS_BUNDLED + "=1", null, null);
        if( snapshot != null && snapshot.moveToFirst() && snapshot.getCount() > 0 ) {
            is_bundled = true;
        }
        if( snapshot != null && ! snapshot.isClosed() ) snapshot.close();
        return is_bundled;
    }
}
