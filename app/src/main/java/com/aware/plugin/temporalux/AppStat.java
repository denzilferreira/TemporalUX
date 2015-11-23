package com.aware.plugin.temporalux;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.aware.Aware;

public class AppStat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.aware_toolbar);
        setSupportActionBar(toolbar);

        Intent aware = new Intent( this, Aware.class);
        startService(aware);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app_stat, menu);
        return true;
    }

    //    Button end_participation = (Button) findViewById(R.id.end_participation);
//    end_participation.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent finalQuestionnaire = new Intent(getApplicationContext(), Questionnaire_end.class);
//            startService(finalQuestionnaire);
//
//            Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
//            finish();
//        }
//    });
}
