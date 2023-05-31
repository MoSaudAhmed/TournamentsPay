package com.mgdapps.play360.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mgdapps.play360.R;

public class AboutUsActivity extends AppCompatActivity {

    String aboutUs = "<h2 style=\"text-align: center;\"><span style=\"color: rgb(184, 49, 47);\">About Us</span></h2>\n" +
            "<p style=\"text-align: center;\">Play360 is all about Tournaments management and community. You can create match with room id and password. Once created or joined a match you can chat with other users of that match. Get notifications before your match starts, with Play360 never miss a match.</p>\n" +
            "<p><br></p>\n" +
            "<h3><span style=\"color: rgb(65, 168, 95);\">Icons</span></h3>\n" +
            "<ul>\n" +
            "    <li>We have used few icons made by Pixel perfect from www.flatiocn.com</li>\n" +
            "</ul>\n" +
            "<h3><span style=\"color: rgb(65, 168, 95);\">Contact</span></h3>\n" +
            "<ul>\n" +
            "    <li>For business reach us at: md.saudahmed@gmail.com</li>\n" +
            "</ul>";

    WebView wb_aboutUs;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        wb_aboutUs = findViewById(R.id.wb_aboutUs);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("About Us");

        wb_aboutUs.requestFocus();
        wb_aboutUs.getSettings().setJavaScriptEnabled(true);
        wb_aboutUs.loadData(aboutUs, "text/html", "UTF-8");
        wb_aboutUs.setVerticalScrollBarEnabled(true);
        wb_aboutUs.setHorizontalScrollBarEnabled(true);

        wb_aboutUs.getSettings().setSupportZoom(true);
        wb_aboutUs.getSettings().setBuiltInZoomControls(true);
        wb_aboutUs.getSettings().setDisplayZoomControls(false);

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
