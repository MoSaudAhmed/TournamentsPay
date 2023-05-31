package com.mgdapps.play360.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mgdapps.play360.R;
import com.mgdapps.play360.helper.Preferences;

public class SettingsActivity extends AppCompatActivity {

    ToggleButton tb_settings_sound, tb_settings_vibration, tb_settings_photoPrivate;
    Button btn_SettingsUpdate;
    Preferences preferences;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        tb_settings_sound = findViewById(R.id.tb_settings_sound);
        tb_settings_vibration = findViewById(R.id.tb_settings_vibration);
        tb_settings_photoPrivate = findViewById(R.id.tb_settings_photoPrivate);
        btn_SettingsUpdate = findViewById(R.id.btn_SettingsUpdate);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        preferences = new Preferences();
        preferences.loadPreferences(this);

        if (preferences.isProfilePrivare()) {
            tb_settings_photoPrivate.setChecked(true);
        }
        if (preferences.isSound()) {
            tb_settings_sound.setChecked(true);
        }
        if (preferences.isVibration()) {
            tb_settings_vibration.setChecked(true);
        }

        btn_SettingsUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tb_settings_photoPrivate.isChecked()) {
                    preferences.setProfilePrivare(true);
                } else {
                    preferences.setProfilePrivare(false);
                }
                if (tb_settings_vibration.isChecked()) {
                    preferences.setVibration(true);
                } else {
                    preferences.setVibration(false);
                }
                if (tb_settings_sound.isChecked()) {
                    preferences.setSound(true);
                } else {
                    preferences.setSound(false);
                }
                preferences.savePreferences(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, "Settigns updated", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
