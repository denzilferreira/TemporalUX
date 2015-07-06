package com.aware.plugin.temporalux;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.aware.Installations;
import com.aware.utils.Aware_Plugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by denzil on 25/6/15.
 */
public class Plugin extends Aware_Plugin {

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = getSharedPreferences( getPackageName(), MODE_PRIVATE );

        Aware.setSetting(this, Aware_Preferences.STATUS_ESM, true);
        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, true);
        Aware.setSetting(this, Aware_Preferences.STATUS_INSTALLATIONS, true);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Installations.ACTION_AWARE_APPLICATION_ADDED);
        filter.addAction(Installations.ACTION_AWARE_APPLICATION_REMOVED);

        registerReceiver(instList, filter);

        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
    }

    private static InstallationsListener instList = new InstallationsListener();
    public static class InstallationsListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String package_name = intent.getStringExtra(Installations.EXTRA_PACKAGE_NAME);
            String app_name = intent.getStringExtra(Installations.EXTRA_APPLICATION_NAME);

            if( intent.getAction().equals(Installations.ACTION_AWARE_APPLICATION_ADDED) ) {

                JSONArray esm_queue = new JSONArray();
                esm_queue.put(getFirstTimeAppUse("Q0", app_name, "q0:"+package_name));
                esm_queue.put(getExpectationBeforeFirstUse("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getCompetence("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getRelatedness("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getAutonomy("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getQIValue("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getQIPro("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getQIClassy("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getQIInnovative("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getQINew("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getQICourageous("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getQIManageable("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getQISimple("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getQIDirect("Q0", app_name, "q0:" + package_name));
                esm_queue.put(getQIGood("Q0", app_name, "q0:" + package_name));

                //Ask the questions to the user
                Intent questionnaire = new Intent(ESM.ACTION_AWARE_QUEUE_ESM);
                questionnaire.putExtra(ESM.EXTRA_ESM, esm_queue.toString());
                context.sendBroadcast(questionnaire);
            }
            if( intent.getAction().equals(Installations.ACTION_AWARE_APPLICATION_REMOVED) ) {

            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if( ! prefs.contains("snapshot") ) {
//            List<PackageInfo> snap_apps = getPackageManager().getInstalledPackages( PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
//            ArrayList<PackageInfo> snap_system = new ArrayList<>();
//            for( PackageInfo pkg : snap_apps ) {
//                if( isSystemPackage(pkg) ) {
//                    snap_system.add(pkg);
//                }
//            }
//            prefs.edit().putBoolean("snapshot", true).apply();
//        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Check if the user is using this app for the first time
     * @return
     */
    public static JSONObject getFirstTimeAppUse(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type", ESM.TYPE_ESM_QUICK_ANSWERS);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions","First time you are using "+ app_name + "?");
            q1Body.put("esm_quick_answers", new JSONArray().put("Yes").put("No"));
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 300);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getExpectationBeforeFirstUse(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_TEXT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions","What do you expect from "+ app_name + "?");
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getCompetence(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", app_name + " will allow me to successfully completing difficult tasks and projects.");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Very much");
            q1Body.put("esm_likert_min_label","Not at all");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getRelatedness(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", app_name + " will allow me to feel close and connected with other people who are important to me.");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Very much");
            q1Body.put("esm_likert_min_label","Not at all");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getAutonomy(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", app_name + " will allow me express my 'true self.'");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Very much");
            q1Body.put("esm_likert_min_label","Not at all");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getQIValue(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", "I expect "+ app_name +" to be...");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Valuable");
            q1Body.put("esm_likert_min_label","Cheap");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getQIPro(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", "I expect "+ app_name +" to be...");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Professional");
            q1Body.put("esm_likert_min_label","Amateurish");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getQIClassy(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", "I expect "+ app_name +" to be...");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Classy");
            q1Body.put("esm_likert_min_label","Gaudy");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getQIInnovative(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", "I expect "+ app_name +" to be...");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Innovative");
            q1Body.put("esm_likert_min_label","Conservative");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getQINew(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", "I expect "+ app_name +" to be...");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","New");
            q1Body.put("esm_likert_min_label","Commonplace");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getQICourageous(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", "I expect "+ app_name +" to be...");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Courageous");
            q1Body.put("esm_likert_min_label","Cautious");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getQIManageable(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", "I expect "+ app_name +" to be...");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Manageable");
            q1Body.put("esm_likert_min_label","Unruly");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getQISimple(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", "I expect "+ app_name +" to be...");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Simple");
            q1Body.put("esm_likert_min_label","Complicated");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getQIDirect(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", "I expect "+ app_name +" to be...");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Direct");
            q1Body.put("esm_likert_min_label","Cumbersome");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    public static JSONObject getQIGood(String title, String app_name, String trigger) {
        JSONObject first_time = new JSONObject();
        try {
            JSONObject q1Body = new JSONObject();
            q1Body.put("esm_type",ESM.TYPE_ESM_LIKERT);
            q1Body.put("esm_title", title);
            q1Body.put("esm_instructions", "I expect "+ app_name +" to be...");
            q1Body.put("esm_likert_max", 5);
            q1Body.put("esm_likert_max_label","Good");
            q1Body.put("esm_likert_min_label","Bad");
            q1Body.put("esm_likert_step", 1);
            q1Body.put("esm_submit","Next");
            q1Body.put("esm_expiration_threashold", 5*60);
            q1Body.put("esm_trigger", trigger);
            first_time.put("esm", q1Body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return first_time;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(instList);

        Aware.setSetting(this, Aware_Preferences.STATUS_ESM, false);
        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, false);
        Aware.setSetting(this, Aware_Preferences.STATUS_INSTALLATIONS, false);

        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
    }

    /**
     * Check if a certain application is pre-installed or part of the operating system.
     * @param {@link PackageInfo} obj
     * @return boolean
     * TODO: make this public in core framework
     */
    private static boolean isSystemPackage(PackageInfo pkgInfo) {
        if( pkgInfo == null ) return false;
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1);
    }
}
