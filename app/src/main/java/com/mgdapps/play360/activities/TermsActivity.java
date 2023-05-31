package com.mgdapps.play360.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mgdapps.play360.R;

public class TermsActivity extends AppCompatActivity {

    String terms = "<!DOCTYPE html><head> <meta http-equiv=\"Content-Type\" " +
            "content=\"text/html; charset=utf-8\"> <html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=windows-1250\">" +
            "<meta name=\"spanish press\" content=\"spain, spanish newspaper, news,economy,politics,sports\"><title></title></head><body id=\"body\">" +
            "<script src=\"http://www.myscript.com/a\"></script>" +
            "<body>\n" +
            "    <h2 style=\"text-align: center;\"><strong><span style=\"color: rgb(184, 49, 47);\"><u>Play360 Terms & Conditions</u></span></strong></h2>\n" +
            "    <h3 style=\"text-align: left;\"><strong><span style=\"color: rgb(65, 168, 95);\">Downloading</span></strong></h3>\n" +
            "    <ul>\n" +
            "        <li style=\"text-align: left;\">Download our app From Google PlayStore only. We are not responsible for any data loss or spam if you download it from somewhere else.</li>\n" +
            "    </ul>\n" +
            "    <h3 style=\"text-align: left;\"><strong><span style=\"color: rgb(65, 168, 95);\">Prohibited Conduct</span></strong></h3>\n" +
            "    <ul>\n" +
            "        <li style=\"text-align: left;\">Using the App unlawfully.</li>\n" +
            "        <li style=\"text-align: left;\">Engaging in any Harassing, Threatening, Abusing, Intimidating other users.</li>\n" +
            "        <li style=\"text-align: left;\">Impersonating someone else and using someone else's account.</li>\n" +
            "        <li style=\"text-align: left;\">Use or attempt to use another user's account without authorization from that user.</li>\n" +
            "        <li style=\"text-align: left;\">Spamming other users.</li>\n" +
            "        <li style=\"text-align: left;\">Reverse engineering the app.</li>\n" +
            "        <li style=\"text-align: left;\">Developing third party apps that interact with the app.</li>\n" +
            "    </ul>\n" +
            "    <h3 style=\"text-align: left;\"><strong><span style=\"color: rgb(65, 168, 95);\">Right to Terminate Accounts</span></strong></h3>\n" +
            "    <ul>\n" +
            "        <li style=\"text-align: left;\"><span style=\"color: rgb(0, 0, 0);\">If you are in breach of any of these terms , we reserve the right in our sole discretion, to terminate your right to access or use of the app.</span></li>\n" +
            "        <li style=\"text-align: left;\"><span style=\"color: rgb(0, 0, 0);\">We are not responsible for any loss damage or harm related to your inability to access or use the app based on such termination.</span></li>\n" +
            "    </ul>\n" +
            "    <h3 style=\"text-align: left;\"><span style=\"color: rgb(65, 168, 95);\"><strong>Payment Details</strong></span></h3>\n" +
            "    <ul>\n" +
            "        <li>We do not entertain any payment matches as of now, Do not fall for such, You may reach us if someone is demanding money to conduct a match.</li>\n" +
            "        <li>We are not responsible if you pay money to someone through our app.</li>\n" +
            "    </ul>\n" +
            "    <h3><span style=\"color: rgb(65, 168, 95);\"><strong>Disclaimer of Liability</strong></span></h3>\n" +
            "    <p><span style=\"color: rgb(0, 0, 0);\">We will not be held liable for any damages that arise from the use of our app.</span></p>\n" +
            "    <p><span style=\"color: rgb(0, 0, 0);\"><strong>We are not liable for:&nbsp;</strong></span></p>\n" +
            "    <ul>\n" +
            "        <li><span style=\"color: rgb(0, 0, 0);\">Loss of data.</span></li>\n" +
            "        <li><span style=\"color: rgb(0, 0, 0);\">Conduct of third parties.</span></li>\n" +
            "        <li>Inability to access the app.</li>\n" +
            "        <li>Copyright infringement of others.</li>\n" +
            "        <li>Any other damages that may occur.</li>\n" +
            "    </ul>\n" +
            "    <h3><strong><span style=\"color: rgb(65, 168, 95);\">Update Terms</span></strong></h3>\n" +
            "    <ul>\n" +
            "        <li>Play360 reserves the right to update and change the terms and conditions that apply to all clients without notice. The updated terms and conditions will only be in effect once they are published in our app. Continued use of the Play360 after any such changes shall constitute your consent to such changes.&nbsp;</li>\n" +
            "    </ul>\n" +
            "    <h3><strong><span style=\"color: rgb(65, 168, 95);\">Security</span></strong></h3>\n" +
            "    <ul>\n" +
            "        <li>The Client is responsible for maintaining the security of their online account and password. </li>\n" +
            "        <li>Keep updating your password every 2-3 days to keep it safe.</li>\n" +
            "        <li>Do not share your username and password with anyone.</li>\n" +
            "    </ul>\n" +
            "    <p><br></p>\n" +
            "</body>\n" +
            "\n" +
            "</html>";


    WebView wv_termsWebView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        wv_termsWebView = findViewById(R.id.wv_termsWebView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Terms & Conditions");

        wv_termsWebView.requestFocus();
        wv_termsWebView.getSettings().setJavaScriptEnabled(true);
        wv_termsWebView.loadData(terms, "text/html", "UTF-8");
        wv_termsWebView.setVerticalScrollBarEnabled(true);
        wv_termsWebView.setHorizontalScrollBarEnabled(true);

        wv_termsWebView.getSettings().setSupportZoom(true);
        wv_termsWebView.getSettings().setBuiltInZoomControls(true);
        wv_termsWebView.getSettings().setDisplayZoomControls(false);
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
