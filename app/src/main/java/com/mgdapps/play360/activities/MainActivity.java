package com.mgdapps.play360.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgdapps.play360.BuildConfig;
import com.mgdapps.play360.R;
import com.mgdapps.play360.fragments.FragmentFavourites;
import com.mgdapps.play360.fragments.FragmentHome;
import com.mgdapps.play360.fragments.FragmentNotifications;
import com.mgdapps.play360.fragments.FragmentPrivateRooms;
import com.mgdapps.play360.helper.CircularProgressBar;
import com.mgdapps.play360.helper.Constants;
import com.mgdapps.play360.helper.Preferences;
import com.mgdapps.play360.models.RegisterModel;
import com.mgdapps.play360.models.ReportUsModel;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Constants constants;
    CircularProgressBar circularProgressBar;
    FragmentManager fragmentManager;
    Preferences preferences;

    ImageView img_profilePic;
    TextView tv_displayname, tv_email;
    FrameLayout lay_frame_main;
    LinearLayout lay_main_Activity;
    DrawerLayout drawer;
    RecyclerView rv_homeRecyclerView;
    MenuItem btnCreateRoom;

    FragmentHome fragmentHome;

    public String gotoMatch = "";

    TextView textNotificationCount;
    SharedPreferences sharedPreferences;
    BroadcastReceiver broadcastReceiver;

    boolean isAdmin = false;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gotoMatch = getIntent().getStringExtra(Constants.MatchId);

        rv_homeRecyclerView = findViewById(R.id.rv_homeRecyclerView);
        sharedPreferences = getSharedPreferences(Constants.notifications, MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lay_frame_main = findViewById(R.id.lay_frame_main);
        lay_main_Activity = findViewById(R.id.lay_main_Activity);
        fragmentManager = getSupportFragmentManager();

        circularProgressBar = new CircularProgressBar(this);
        preferences = new Preferences();
        preferences.loadPreferences(this);
        constants = new Constants();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nView = navigationView.getHeaderView(0);

        img_profilePic = nView.findViewById(R.id.img_profilePic);
        tv_displayname = nView.findViewById(R.id.tv_displayname);
        tv_email = nView.findViewById(R.id.tv_email);

        BottomNavigationInitialize();
        fragmentHome = new FragmentHome();
        fragmentManager.beginTransaction().replace(R.id.lay_frame_main, fragmentHome).commit();
        getSupportActionBar().setTitle("Home");

        if (firebaseUser != null) {
            if (TextUtils.isEmpty(preferences.getEmail().trim())) {
                saveUserIfNotExists();
            } else {
                loadData();
            }
        }
        firebaseFirestore.collection(Constants.UserDB).document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.contains(Constants.isAdmin)) {
                    isAdmin = (boolean) documentSnapshot.get(Constants.isAdmin);
                    if (isAdmin) {
                        btnCreateRoom.setVisible(true);
                    }
                }
                Log.e("isAdmin", isAdmin + "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("isAdmin", e.getLocalizedMessage());
            }
        });


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("CalledLocalBroadcast", "Received");
                setupBadge();
                Log.e("NotificationList", String.valueOf(sharedPreferences.getStringSet(Constants.notificationsList, null)));
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.notifications.receiver");
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.lay_frame_main);
            if (!(fragment instanceof FragmentHome)) {
                getSupportActionBar().setTitle("Home");
                bottomNavView.setSelectedItemId(R.id.bottomNav_home);
            } else {
                //Exit Dialog
                AlertDialog.Builder settingsDialogue = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater layoutInflater = MainActivity.this.getLayoutInflater();
                View view1 = layoutInflater.inflate(R.layout.exit_dialogue, null);
                final AlertDialog dialog = settingsDialogue.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setView(view1);

                Button btn_exit = view1.findViewById(R.id.btn_exit);
                Button btn_cancel = view1.findViewById(R.id.btn_cancel);

                btn_exit.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View view) {

                        dialog.cancel();
                        moveTaskToBack(true);
                        finishAndRemoveTask();
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                dialog.show();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        btnCreateRoom = menu.findItem(R.id.action_settings);
        btnCreateRoom.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings: {
                startActivityForResult(new Intent(MainActivity.this, CreateRoomActivity.class), 123);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivityForResult(intent, 555);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_adminRequest) {
            adminRequestAccessDialog();
        } else if (id == R.id.nav_share) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Play360");
                String shareMessage = "Hey, Download Play360 now to join our matches https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share Via"));
            } catch (Exception e) {
                //e.toString();
            }

        } else if (id == R.id.nav_aboutus) {
            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
        } else if (id == R.id.nav_TermsAndConditions) {
            startActivity(new Intent(MainActivity.this, TermsActivity.class));
        } else if (id == R.id.nav_report) {
            reportUs();
        } else if (id == R.id.nav_logout) {
            logOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void adminRequestAccessDialog() {

        AlertDialog.Builder settingsDialogue = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater layoutInflater = MainActivity.this.getLayoutInflater();
        View view1 = layoutInflater.inflate(R.layout.reportus_dialog, null);
        final AlertDialog dialog = settingsDialogue.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setView(view1);

        TextView tv_reportUsDialog = view1.findViewById(R.id.tv_reportUsDialog);
        final EditText et_reportUsDialog = view1.findViewById(R.id.et_reportUsDialog);
        Button btn_reportUsDialogCancel = view1.findViewById(R.id.btn_reportUsDialogCancel);
        Button btn_reportUsDialogSend = view1.findViewById(R.id.btn_reportUsDialogSend);

        tv_reportUsDialog.setText("Give us a good reason why you wan to become an admin.");
        tv_reportUsDialog.setVisibility(View.VISIBLE);
        et_reportUsDialog.setHint("Enter your reason");

        btn_reportUsDialogSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(et_reportUsDialog.getText().toString().trim())) {
                    ReportUsModel reportUsModel = new ReportUsModel();
                    reportUsModel.setMessage(et_reportUsDialog.getText().toString());
                    reportUsModel.setUid(firebaseUser.getUid());
                    String id = firebaseFirestore.collection(Constants.adminRequests).getId();
                    firebaseFirestore.collection(Constants.adminRequests).document(id).set(reportUsModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Thanks, We've got your request, We will get back to you soon.", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed sending request, " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Cannot send empty Complaint", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_reportUsDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void logOut() {
        preferences.clearPreferences(MainActivity.this);
        firebaseAuth.signOut();
        LoginManager.getInstance().logOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this,
                new OnCompleteListener<Void>() {  //signout Google
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseAuth.signOut();
                    }
                });


        finish();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    /*private void authListner() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(MainActivity.this, "LogOut Successful", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };
    }*/

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void saveUserIfNotExists() {

        circularProgressBar.showProgressDialog();

        firebaseFirestore.collection(Constants.UserDB).document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                circularProgressBar.dismissProgressDialog();

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        RegisterModel registerModel = documentSnapshot.toObject(RegisterModel.class);
                        preferences.setDisplayName(registerModel.getDisplayName());
                        preferences.setEmail(registerModel.getEmail());
                        preferences.setUid(registerModel.getUid());
                        preferences.setProfilePic(registerModel.getPhotoURL());
                        preferences.savePreferences(MainActivity.this);
                        loadData();
                    } else {

                        RegisterModel registerModel = new RegisterModel();
                        if (firebaseUser.getDisplayName() != null) {
                            registerModel.setDisplayName(firebaseUser.getDisplayName());
                        } else {
                            registerModel.setDisplayName("Anonymous");
                        }
                        registerModel.setEmail(firebaseUser.getEmail());
                        registerModel.setUid(firebaseUser.getUid());
                        if (firebaseUser.getPhotoUrl() != null) {
                            registerModel.setPhotoURL(firebaseUser.getPhotoUrl().toString());
                        }

                        FirebaseFirestore.getInstance().collection(Constants.UserDB)
                                .document(registerModel.getUid())
                                .set(registerModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                circularProgressBar.dismissProgressDialog();
                                if (task.isSuccessful()) {
                                    saveUserIfNotExists();
                                } else {
                                    Toast.makeText(MainActivity.this, "Something went wrong, Please try another method of signIn", Toast.LENGTH_LONG).show();
                                    logOut();
                                }
                            }
                        });

                    }
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadData() {

        preferences.loadPreferences(this);

        if (!TextUtils.isEmpty(preferences.getDisplayName())) {
            tv_displayname.setText(preferences.getDisplayName() + "");
        } else {
            tv_displayname.setText("Anonymous");
        }

        if (!TextUtils.isEmpty(preferences.getEmail())) {
            tv_email.setText(preferences.getEmail());
        }
        if (!TextUtils.isEmpty(preferences.getProfilePic())) {
            Picasso.get().load(preferences.getProfilePic()).error(R.drawable.ic_person).into(img_profilePic);
        } else {
            Picasso.get().load(R.drawable.ic_person).error(R.drawable.ic_person).into(img_profilePic);
        }
    }

    BottomNavigationView bottomNavView;

    private void BottomNavigationInitialize() {

        bottomNavView = findViewById(R.id.main_mybottombar);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(3);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;

        View badge = LayoutInflater.from(this)
                .inflate(R.layout.custom_action_item_layout, itemView, true);
        textNotificationCount = badge.findViewById(R.id.cart_badge);


        setupBadge();

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.lay_frame_main);
                switch (item.getItemId()) {

                    case R.id.bottomNav_home: {
                        if (!(fragment instanceof FragmentHome)) {
                            fragmentManager.beginTransaction().replace(R.id.lay_frame_main, fragmentHome).commit();
                            getSupportActionBar().setTitle("Home");
                        }
                        break;
                    }
                    case R.id.bottomNav_notifications: {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constants.notifications, 0);
                        editor.apply();

                        setupBadge();

                        if (!(fragment instanceof FragmentNotifications)) {
                            fragmentManager.beginTransaction().replace(R.id.lay_frame_main, new FragmentNotifications()).commit();
                            getSupportActionBar().setTitle("Notifications");
                        }

                        break;
                    }
                    case R.id.bottomNav_favourites: {
                        if (!(fragment instanceof FragmentFavourites)) {
                            fragmentManager.beginTransaction().replace(R.id.lay_frame_main, new FragmentFavourites()).commit();
                            getSupportActionBar().setTitle("Joined Matches");

                        }

                        break;
                    }
                    case R.id.bottomNav_private: {
                        if (!(fragment instanceof FragmentPrivateRooms)) {
                            fragmentManager.beginTransaction().replace(R.id.lay_frame_main, new FragmentPrivateRooms()).commit();
                            getSupportActionBar().setTitle("Search Room");
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }

                return true;
            }
        });

    }

    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.lay_frame_main), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        if (circularProgressBar != null) {
            circularProgressBar.dismissProgressDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            getSupportActionBar().setTitle("Home");
            bottomNavView.setSelectedItemId(R.id.bottomNav_home);
        } else if (requestCode == 555 && resultCode == RESULT_OK) {
            loadData();
        }
    }

    private void setupBadge() {

        if (textNotificationCount != null) {
            if (sharedPreferences.getInt(Constants.notifications, 0) == 0) {
                if (textNotificationCount.getVisibility() != View.GONE) {
                    textNotificationCount.setVisibility(View.GONE);
                }
            } else {
                textNotificationCount.setText(String.valueOf(sharedPreferences.getInt(Constants.notifications, 0)));
                if (textNotificationCount.getVisibility() != View.VISIBLE) {
                    textNotificationCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void reportUs() {
        AlertDialog.Builder settingsDialogue = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater layoutInflater = MainActivity.this.getLayoutInflater();
        View view1 = layoutInflater.inflate(R.layout.reportus_dialog, null);
        final AlertDialog dialog = settingsDialogue.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setView(view1);

        final EditText et_reportUsDialog = view1.findViewById(R.id.et_reportUsDialog);
        Button btn_reportUsDialogCancel = view1.findViewById(R.id.btn_reportUsDialogCancel);
        Button btn_reportUsDialogSend = view1.findViewById(R.id.btn_reportUsDialogSend);

        btn_reportUsDialogSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(et_reportUsDialog.getText().toString().trim())) {
                    ReportUsModel reportUsModel = new ReportUsModel();
                    reportUsModel.setMessage(et_reportUsDialog.getText().toString());
                    reportUsModel.setUid(firebaseUser.getUid());
                    String id = firebaseFirestore.collection(Constants.reportUs).getId();
                    firebaseFirestore.collection(Constants.reportUs).document(id).set(reportUsModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Thanks for your complaint, We will look in to it.", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed sending complaint, " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Cannot send empty Complaint", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_reportUsDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }
}
