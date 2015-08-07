package com.aware.plugin.temporalux;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by denzil on 07/08/15.
 */
public class Settings extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Button end_participation = (Button) findViewById(R.id.end_participation);
        end_participation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent finalQuestionnaire = new Intent(getApplicationContext(), Questionnaire_end.class);
                startService(finalQuestionnaire);

                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
