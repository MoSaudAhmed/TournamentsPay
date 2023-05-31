package com.mgdapps.play360.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mgdapps.play360.R;
import com.mgdapps.play360.helper.Constants;

public class SplashScreenActivity extends AppCompatActivity {

    String gotoMatch = "";

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (getIntent().getStringExtra(Constants.MatchId) != null) {
            gotoMatch = getIntent().getStringExtra(Constants.MatchId);
        }
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            String s = uri;
            String[] split = s.split("launch/");
            if (split.length > 0) {

                gotoMatch = split[1];
            }
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.MatchId, gotoMatch);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(Constants.MatchId, gotoMatch);
            startActivity(intent);
        }
        finish();
    }
}
