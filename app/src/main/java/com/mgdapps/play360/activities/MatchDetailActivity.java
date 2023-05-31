package com.mgdapps.play360.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mgdapps.play360.BuildConfig;
import com.mgdapps.play360.R;
import com.mgdapps.play360.adapters.JoinedUsersAdapter;
import com.mgdapps.play360.adapters.MessagesAdapter;
import com.mgdapps.play360.broadcast.MyAlarmReceiver;
import com.mgdapps.play360.callbacks.AllMatchesCallback;
import com.mgdapps.play360.helper.CircularProgressBar;
import com.mgdapps.play360.helper.Constants;
import com.mgdapps.play360.helper.Preferences;
import com.mgdapps.play360.models.CreateMatchModel;
import com.mgdapps.play360.models.JoinAcceptModel;
import com.mgdapps.play360.models.JoinRequestModel;
import com.mgdapps.play360.models.MessageModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MatchDetailActivity extends AppCompatActivity implements View.OnClickListener, AllMatchesCallback {

    CreateMatchModel matchModel;

    RecyclerView rv_matchDetailsJoinedUsers, rv_matchDetails_messages;
    TextView tv_matchDetails_title, tv_matchDetails_date, tv_matchDetails_Type, tv_matchDetails_fee,
            tv_matchDetails_Mode, tv_matchDetails_Map, tv_matchDetails_joinedCount, tv_matchDetails_joinButton,
            tv_matchDetails_JoinedUpdate, tv_matchDetails_gotoRequests, tv_matchDetailsCompleted,
            tv_matchDetailsShowPassword, tv_matchDetails_joinCode, tv_MatchDetails_notifications;
    AppCompatSeekBar sb_matchDetails_seekbar;
    SwipeRefreshLayout swipe_matchDetails;
    Toolbar toolbar;
    FloatingActionButton fab_MatchDetails_notifications;
    LinearLayout lay_MatchDetailsMessages, lay_matchDetailsEditLayout;
    EditText et_matchDetails_sendMesasge, et_updatePassword, et_updateMatchId;
    ImageView img_matchDetails_sendMesasge, img_edit, img_cancel;

    CardView card_homeRow, card_MatchDetailsList, card_matchNotFound;

    FirebaseFirestore firebaseFirestore;
    DatabaseReference firebaseDatabase;
    FirebaseUser firebaseUser;

    Preferences preferences;

    final List<JoinAcceptModel> joinUsersModelList = new ArrayList<>();
    List<MessageModel> messageModelList = new ArrayList<>();
    MessagesAdapter messagesAdapter;

    JoinedUsersAdapter joinedUsersAdapter;
    boolean isExpired = false;

    SharedPreferences sharedPreferences;

    int notificationCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        initViews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Match Details");

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        preferences = new Preferences();
        preferences.loadPreferences(this);
        sharedPreferences = getSharedPreferences(Constants.MatchId, MODE_PRIVATE);

        if (getIntent().getStringExtra(Constants.MatchId) != null) {
            loadMatch(getIntent().getStringExtra(Constants.MatchId));
        } else if (getIntent().getParcelableExtra("match") != null) {
            matchModel = getIntent().getParcelableExtra("match");
            setData();
            checkRequestSent();
        } else {
            card_homeRow.setVisibility(View.GONE);
            card_MatchDetailsList.setVisibility(View.GONE);
            card_matchNotFound.setVisibility(View.VISIBLE);
        }

        rv_matchDetailsJoinedUsers.setLayoutManager(new LinearLayoutManager(this));
        rv_matchDetailsJoinedUsers.setHasFixedSize(true);

        swipe_matchDetails.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_matchDetails.setRefreshing(false);
                loadMatch(matchModel.getMatchId());
            }
        });

        messagesAdapter = new MessagesAdapter(this, messageModelList);
        rv_matchDetails_messages.setHasFixedSize(true);
        rv_matchDetails_messages.setLayoutManager(new LinearLayoutManager(this));
        rv_matchDetails_messages.setAdapter(messagesAdapter);

        setupNotifications();

    }


    private void initViews() {
        img_edit = findViewById(R.id.img_btnEdit);
        img_cancel = findViewById(R.id.img_btnCancel);

        toolbar = findViewById(R.id.toolbar);
        rv_matchDetailsJoinedUsers = findViewById(R.id.rv_matchDetailsJoinedUsers);
        tv_matchDetails_title = findViewById(R.id.tv_matchDetails_title);
        tv_matchDetails_date = findViewById(R.id.tv_matchDetails_date);
        tv_matchDetails_Type = findViewById(R.id.tv_matchDetails_Type);
        tv_matchDetails_fee = findViewById(R.id.tv_matchDetails_fee);
        tv_matchDetails_Mode = findViewById(R.id.tv_matchDetails_Mode);
        tv_matchDetails_Map = findViewById(R.id.tv_matchDetails_Map);
        tv_matchDetails_joinedCount = findViewById(R.id.tv_matchDetails_joinedCount);
        tv_matchDetails_joinButton = findViewById(R.id.tv_matchDetails_joinButton);
        tv_matchDetails_JoinedUpdate = findViewById(R.id.tv_matchDetails_JoinedUpdate);
        tv_matchDetails_gotoRequests = findViewById(R.id.tv_matchDetails_gotoRequests);
        tv_matchDetailsCompleted = findViewById(R.id.tv_matchDetailsCompleted);
        tv_matchDetailsShowPassword = findViewById(R.id.tv_matchDetailsShowPassword);
        tv_matchDetails_joinCode = findViewById(R.id.tv_matchDetails_joinCode);
        rv_matchDetails_messages = findViewById(R.id.rv_matchDetails_messages);

        tv_MatchDetails_notifications = findViewById(R.id.tv_MatchDetails_notifications);
        fab_MatchDetails_notifications = findViewById(R.id.fab_MatchDetails_notifications);
        lay_MatchDetailsMessages = findViewById(R.id.lay_MatchDetailsMessages);
        img_matchDetails_sendMesasge = findViewById(R.id.img_matchDetails_sendMesasge);
        et_matchDetails_sendMesasge = findViewById(R.id.et_matchDetails_sendMesasge);

        sb_matchDetails_seekbar = findViewById(R.id.sb_matchDetails_seekbar);
        swipe_matchDetails = findViewById(R.id.swipe_matchDetails);

        card_MatchDetailsList = findViewById(R.id.card_MatchDetailsList);
        card_homeRow = findViewById(R.id.card_homeRow);
        card_matchNotFound = findViewById(R.id.card_matchNotFound);


        et_updatePassword = findViewById(R.id.et_updatePassword);
        et_updateMatchId = findViewById(R.id.et_updateMatchId);

        lay_matchDetailsEditLayout = findViewById(R.id.lay_matchDetailsEditLayout);

        tv_matchDetails_joinButton.setOnClickListener(this);
        tv_matchDetails_gotoRequests.setOnClickListener(this);
        img_matchDetails_sendMesasge.setOnClickListener(this);

        img_edit.setOnClickListener(this);
        img_cancel.setOnClickListener(this);


        sb_matchDetails_seekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        fab_MatchDetails_notifications.setOnClickListener(this);

    }


    private void loadMatch(
            String matchId) {
        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        firebaseFirestore.collection(Constants.Matches).document(matchId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                circularProgressBar.dismissProgressDialog();
                if (documentSnapshot.exists()) {
                    matchModel = documentSnapshot.toObject(CreateMatchModel.class);
                    checkRequestSent();
                    setData();
                } else {
                    card_homeRow.setVisibility(View.GONE);
                    card_MatchDetailsList.setVisibility(View.GONE);
                    card_matchNotFound.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                circularProgressBar.dismissProgressDialog();
                card_homeRow.setVisibility(View.GONE);
                card_MatchDetailsList.setVisibility(View.GONE);
                card_matchNotFound.setVisibility(View.VISIBLE);
            }
        });
    }


    private void checkRequestSent() {
        if (!matchModel.getUID().equalsIgnoreCase(firebaseUser.getUid())) {
            final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
            circularProgressBar.showProgressDialog();

            firebaseFirestore.collection(Constants.Matches).document(matchModel.getMatchId())
                    .collection(Constants.JoinRequests).document(firebaseUser.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            circularProgressBar.dismissProgressDialog();
                            if (documentSnapshot.exists()) {
                                JoinRequestModel joinRequestModel = documentSnapshot.toObject(JoinRequestModel.class);
                                if (joinRequestModel.getSTATUS().equalsIgnoreCase(Constants.Pending)) {
                                    tv_matchDetails_joinButton.setText(Constants.Pending);
                                    if (!matchModel.getUID().equalsIgnoreCase(firebaseUser.getUid())) {
                                        tv_matchDetails_JoinedUpdate.setText(R.string.pendingMessageToUser);
                                    }
                                } else if (joinRequestModel.getSTATUS().equalsIgnoreCase(Constants.Joined)) {
                                    tv_matchDetails_joinButton.setText(Constants.Joined);
                                    loadJoinedUsers();
                                } else if (joinRequestModel.getSTATUS().equalsIgnoreCase(Constants.Rejected)) {
                                    tv_matchDetails_joinButton.setText(Constants.Rejected);
                                    tv_matchDetails_JoinedUpdate.setText(R.string.rejectedJoinInvitaion);
                                    tv_matchDetails_joinButton.setBackground(getResources().getDrawable(R.drawable.red_button_background));
                                } else {
                                    if (!matchModel.getUID().equalsIgnoreCase(firebaseUser.getUid())) {
                                        tv_matchDetails_JoinedUpdate.setText(R.string.notJoinedMessageToUser);
                                    }
                                }
                            } else {
                                if (!matchModel.getUID().equalsIgnoreCase(firebaseUser.getUid())) {
                                    tv_matchDetails_JoinedUpdate.setText(R.string.notJoinedMessageToUser);
                                } else {
                                    tv_matchDetails_JoinedUpdate.setText("No users joined yet, Please check View Requests for pending.");
                                }
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    circularProgressBar.dismissProgressDialog();
                }
            });
        }
    }

    private void loadJoinedUsers() {
        joinUsersModelList.clear();
        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        firebaseFirestore.collection(Constants.Matches).document(matchModel.getMatchId())
                .collection(Constants.Joined).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                circularProgressBar.dismissProgressDialog();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    JoinAcceptModel joinAcceptModel = snapshot.toObject(JoinAcceptModel.class);
                    joinUsersModelList.add(joinAcceptModel);
                }
                joinedUsersAdapter.notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                circularProgressBar.dismissProgressDialog();
                Toast.makeText(MatchDetailActivity.this, "Error in loading Joined members", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setData() {

        if (matchModel != null) {

            setUpMessages();

            sharedPreferences = getSharedPreferences(Constants.MatchId, MODE_PRIVATE);
            if (menu_notification != null) {
                if (sharedPreferences.contains(matchModel.getMatchId())) {
                    menu_notification.setIcon(getResources().getDrawable(R.drawable.ic_notifications_icon));
                } else {
                    menu_notification.setIcon(getResources().getDrawable(R.drawable.ic_notifications_off));
                }
            }

            Date date = new Date(String.valueOf(matchModel.getMatchTime()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            setDateAndTimeToText(calendar, tv_matchDetails_date);


            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                tv_matchDetailsCompleted.setVisibility(View.VISIBLE);
                tv_matchDetails_gotoRequests.setText("Update Kills");
                isExpired = true;
            }

        /*    if (matchModel.getMatchShowPassword() == 1) {
                tv_matchDetailsShowPassword.setText("Room Id: " + matchModel.getRoomId() + "\nPassword: No Password");
            } else if (matchModel.getMatchShowPassword() == 2) {
                if ((System.currentTimeMillis()) > (calendar.getTimeInMillis()) - (60 * 5 * 1000)) {
        */
            tv_matchDetailsShowPassword.setText("Room Id: " + matchModel.getRoomId() + "\n" + "Password: " + matchModel.getPassword());
          /*      } else {
                    tv_matchDetailsShowPassword.setText("Room Id &" + " Password will be shared 5 mins before match");
                }
            } else if (matchModel.getMatchShowPassword() == 3) {
                if ((System.currentTimeMillis()) > (calendar.getTimeInMillis()) - (60 * 10 * 1000)) {
                    tv_matchDetailsShowPassword.setText("Room Id: " + matchModel.getRoomId() + "\n" + "Password: " + matchModel.getPassword());
                } else {
                    tv_matchDetailsShowPassword.setText("Room Id &" + " Password  will be shared 10 mins before the match");
                }
            } else if (matchModel.getMatchShowPassword() == 4) {
                if ((System.currentTimeMillis()) > (calendar.getTimeInMillis()) - (60 * 15 * 1000)) {
                    tv_matchDetailsShowPassword.setText("Room Id: " + matchModel.getRoomId() + "\n" + "Password: " + matchModel.getPassword());
                } else {
                    tv_matchDetailsShowPassword.setText("Room Id &" + " Password will be shared 15 mins before the match");
                }
            }
*/
            if (matchModel.getUID().equalsIgnoreCase(firebaseUser.getUid())) {

                tv_matchDetails_joinButton.setVisibility(View.GONE);
                loadJoinedUsers();
                joinedUsersAdapter = new JoinedUsersAdapter(MatchDetailActivity.this, true, isExpired, joinUsersModelList, this);
                tv_matchDetails_gotoRequests.setVisibility(View.VISIBLE);
                img_edit.setVisibility(View.VISIBLE);
            } else {
                joinedUsersAdapter = new JoinedUsersAdapter(MatchDetailActivity.this, false, isExpired, joinUsersModelList, this);
            }

            tv_matchDetails_joinCode.setText("Join Code: " + matchModel.getJoinCode());

            rv_matchDetailsJoinedUsers.setAdapter(joinedUsersAdapter);

            tv_matchDetails_joinedCount.setText(matchModel.getJoined() + "/100 joined");
            sb_matchDetails_seekbar.setProgress(matchModel.getJoined());
            String type = "";
            String mode = "";
            String map = "";

            if (matchModel.getMatchAA() != 0) {
                type = getResources().getStringArray(R.array.aaItems)[matchModel.getMatchAA()];
                mode = getResources().getStringArray(R.array.modeItems)[matchModel.getMatchMode()];
                map = getResources().getStringArray(R.array.serverItems)[matchModel.getMatchServer()];

            } else {
                type = getResources().getStringArray(R.array.typeItems)[matchModel.getMatchType()];
                mode = getResources().getStringArray(R.array.modeItems)[matchModel.getMatchMode()];
                map = getResources().getStringArray(R.array.mapItems)[matchModel.getMatchMap()];
            }

            tv_matchDetails_Type.setText(type);
            tv_matchDetails_Mode.setText(mode);
            tv_matchDetails_Map.setText(map);
            tv_matchDetails_title.setText(matchModel.getMatchTitle());

        }
    }

    private void setDateAndTimeToText(Calendar calendar, TextView textView) {
        try {
            String hours = "";
            if (calendar.get(Calendar.HOUR_OF_DAY) != 0 || calendar.get(Calendar.HOUR_OF_DAY) != 00) {
                if (calendar.get(Calendar.HOUR_OF_DAY) > 12) {
                    hours = String.format("%02d", (calendar.get(Calendar.HOUR_OF_DAY) - 12));
                } else {
                    hours = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
                }

            } else {
                hours = "12";
            }
            String Mins = String.format("%02d", calendar.get(Calendar.MINUTE));
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 12) {
                textView.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" +
                        calendar.get(Calendar.YEAR) + " " + hours + ":" + Mins + " PM");
            } else {
                textView.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" +
                        calendar.get(Calendar.YEAR) + " " + hours + ":" + Mins + " AM");
            }

        } catch (Exception e) {
            Log.e("ErrorinsettingDateTime", e.getLocalizedMessage());
        }

    }

    @Override
    public void onClick(View v) {
        Date date = new Date(String.valueOf(matchModel.getMatchTime()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        switch (v.getId()) {
            case R.id.tv_matchDetails_joinButton: {

                if (tv_matchDetails_joinButton.getText().equals(Constants.Join)) {
                    if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                        joinService();
                    } else {
                        Toast.makeText(MatchDetailActivity.this, "Cannot join Expired match", Toast.LENGTH_LONG).show();
                    }
                } else if (tv_matchDetails_joinButton.getText().equals(Constants.Rejected)) {
                    Toast.makeText(MatchDetailActivity.this, "Cannot send Request once got Rejected", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.tv_matchDetails_gotoRequests: {
                if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                    Intent intent = new Intent(MatchDetailActivity.this, JoinRequestsActivity.class);
                    intent.putExtra(Constants.MatchId, matchModel.getMatchId());
                    startActivityForResult(intent, 321);
                } else {
                    Intent intent = new Intent(MatchDetailActivity.this, MatchResultActivity.class);
                    intent.putExtra(Constants.MatchId, matchModel.getMatchId());
                    intent.putParcelableArrayListExtra(Constants.Joined, (ArrayList<? extends Parcelable>) joinUsersModelList);
                    startActivityForResult(intent, 243);
                }
                break;
            }
            case R.id.fab_MatchDetails_notifications: {
                if (lay_MatchDetailsMessages.getVisibility() == View.VISIBLE) {
                    hideMessagesLayout();
                } else {
                    showMessageslayout();
                }
                break;
            }
            case R.id.img_matchDetails_sendMesasge: {
                if (!TextUtils.isEmpty(et_matchDetails_sendMesasge.getText().toString().trim())) {
                    sendMessage(et_matchDetails_sendMesasge.getText().toString());
                } else {
                    Toast.makeText(MatchDetailActivity.this, "Cannot sent Empty message", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.img_btnEdit: {
                if (lay_matchDetailsEditLayout.getVisibility() == View.VISIBLE) {
                    lay_matchDetailsEditLayout.setVisibility(View.GONE);
                    img_edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_icon));
                    img_cancel.setVisibility(View.GONE);

                    if (!TextUtils.isEmpty(et_updatePassword.getText().toString().trim()) && !TextUtils.isEmpty(et_updatePassword.getText().toString().trim())) {

                        final CircularProgressBar circularProgressBar = new CircularProgressBar(MatchDetailActivity.this);
                        circularProgressBar.showProgressDialog();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(Constants.RoomId, et_updateMatchId.getText().toString());
                        hashMap.put(Constants.Password, et_updatePassword.getText().toString());
                        firebaseFirestore.collection(Constants.Matches).document(matchModel.getMatchId())
                                .update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                circularProgressBar.dismissProgressDialog();
                                if (task.isSuccessful()) {
                                    tv_matchDetailsShowPassword.setText("Room Id: " + et_updateMatchId.getText().toString()
                                            + "\nPassword: " + et_updatePassword.getText().toString());
                                    Toast.makeText(MatchDetailActivity.this, "Updated successful", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MatchDetailActivity.this, "Updating failed", Toast.LENGTH_LONG).show();
                                }
                                et_updateMatchId.setText("");
                                et_updatePassword.setText("");
                            }
                        });
                    } else {
                        Toast.makeText(MatchDetailActivity.this, "Cant update MatchId/password empty", Toast.LENGTH_LONG).show();
                    }
                } else {
                    lay_matchDetailsEditLayout.setVisibility(View.VISIBLE);
                    img_edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_done));
                    img_cancel.setVisibility(View.VISIBLE);
                    et_updateMatchId.setText(matchModel.getRoomId());
                    et_updatePassword.setText(matchModel.getPassword());
                }
                break;
            }
            case R.id.img_btnCancel: {
                lay_matchDetailsEditLayout.setVisibility(View.GONE);
                img_edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_icon));
                img_cancel.setVisibility(View.GONE);
                et_updateMatchId.setText("");
                et_updatePassword.setText("");
                break;
            }
        }
    }

    void sendMessage(String message) {

        final CircularProgressBar circularProgressBar = new CircularProgressBar(MatchDetailActivity.this);
        circularProgressBar.showProgressDialog();
        MessageModel messageModel = new MessageModel();
        messageModel.setMessage(message);
        messageModel.setUid(firebaseUser.getUid());
        messageModel.setUserName(preferences.getDisplayName());
        if (!preferences.isProfilePrivare()) {
            messageModel.setProfilePic(preferences.getProfilePic());
        }

        firebaseDatabase.child(Constants.mesasges).child(matchModel.getMatchId()).push().setValue(messageModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        circularProgressBar.dismissProgressDialog();
                        et_matchDetails_sendMesasge.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MatchDetailActivity.this, "Mesasge not sent " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                circularProgressBar.dismissProgressDialog();
            }
        });

    }


    private void joinService() {
        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        firebaseFirestore.collection(Constants.Matches).document(matchModel.getMatchId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        JoinRequestModel joinRequestModel = new JoinRequestModel();
                        joinRequestModel.setNAME(preferences.getDisplayName());
                        joinRequestModel.setUID(preferences.getUid());
                        joinRequestModel.setSTATUS(Constants.Pending);
                        if (!preferences.isProfilePrivare()) {
                            joinRequestModel.setProfilePic(preferences.getProfilePic());
                        }

                        CreateMatchModel createMatchModel = documentSnapshot.toObject(CreateMatchModel.class);
                        if (createMatchModel.getJoined() < 100) {
                            firebaseFirestore.collection(Constants.Matches).document(matchModel.getMatchId())
                                    .collection(Constants.JoinRequests).document(firebaseUser.getUid()).set(joinRequestModel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MatchDetailActivity.this, "Join Request sent successful, Please wait for approval", Toast.LENGTH_LONG).show();
                                            circularProgressBar.dismissProgressDialog();
                                            tv_matchDetails_joinButton.setText(Constants.Pending);
                                            tv_matchDetails_JoinedUpdate.setText(R.string.pendingMessageToUser);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MatchDetailActivity.this, "Failed in sending Request", Toast.LENGTH_LONG).show();
                                    circularProgressBar.dismissProgressDialog();
                                }
                            });

                        } else {
                            Toast.makeText(MatchDetailActivity.this, "Can't join, Match already full", Toast.LENGTH_LONG).show();
                            circularProgressBar.dismissProgressDialog();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                circularProgressBar.dismissProgressDialog();
            }
        });
    }

    MenuItem menu_notification, menu_share;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_matchdetails, menu);
        menu_notification = menu.findItem(R.id.menu_notification);
        menu_share = menu.findItem(R.id.menu_share);

        if (matchModel != null) {
            sharedPreferences = getSharedPreferences(Constants.MatchId, MODE_PRIVATE);
            if (sharedPreferences.contains(matchModel.getMatchId())) {
                menu_notification.setIcon(getResources().getDrawable(R.drawable.ic_notifications_icon));
            } else {
                menu_notification.setIcon(getResources().getDrawable(R.drawable.ic_notifications_off));
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    int alarmCount = 0;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                if (lay_MatchDetailsMessages.getVisibility() == View.VISIBLE) {
                    hideMessagesLayout();
                } else {
                    finish();
                }
                break;
            }
            case R.id.menu_share: {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Play360");
                    String shareMessage = "Click to join match: http://www.play360.com/launch/" + matchModel.getMatchId() + " \n or \n download from playstore https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share Via"));
                } catch (Exception e) {
                    //e.toString();
                }
                break;
            }
            case R.id.menu_notification: {
                if (!isExpired) {
                    if (alarmCount == 0) {
                        alarmCount++;
                        if (!sharedPreferences.contains(matchModel.getMatchId())) {
                            showBatteryDialog();
                        }
                    } else {
                        if (matchModel != null && matchModel.getMatchId() != null) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            if (sharedPreferences.contains(matchModel.getMatchId())) {
                                menu_notification.setIcon(getResources().getDrawable(R.drawable.ic_notifications_off));
                                editor.clear();
                                editor.apply();
                                cancelAlarm();
                            } else {
                                editor.putString(matchModel.getMatchId(), "true");
                                editor.apply();
                                menu_notification.setIcon(getResources().getDrawable(R.drawable.ic_notifications_icon));
                                setAlarm();
                            }
                        }
                    }
                } else {
                    Toast.makeText(MatchDetailActivity.this, "Cannot set Notification to Expired match", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAlarm() {
        try {
            Intent intent = new Intent(this, MyAlarmReceiver.class);
            intent.putExtra(Constants.MatchId, matchModel.getMatchId());
            intent.putExtra(Constants.tournamentTitle, matchModel.getMatchTitle());
            intent.setAction(matchModel.getMatchId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, matchModel.getJoinCode(), intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(matchModel.getMatchTime());
            calendar.setTimeInMillis(calendar.getTimeInMillis() - (5 * 60 * 1000));
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
            Toast.makeText(MatchDetailActivity.this, "Notification triggers 5 mins before the match", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(MatchDetailActivity.this, "Error in setting Alarm", Toast.LENGTH_LONG).show();
        }
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, matchModel.getJoinCode(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(MatchDetailActivity.this, "Notification cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321 && resultCode == RESULT_OK) {
            loadMatch(matchModel.getMatchId());
            checkRequestSent();
        }
        if (requestCode == 243 && resultCode == RESULT_OK) {
            loadJoinedUsers();
        }
    }

    //Delete member Clicked
    @Override
    public void clicked(final int position) {
        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        firebaseFirestore.collection(Constants.Matches).document(matchModel.getMatchId())
                .collection(Constants.Joined).document(joinUsersModelList.get(position).getUID())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateJoinRequest(position);
                circularProgressBar.dismissProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                circularProgressBar.dismissProgressDialog();
                Toast.makeText(MatchDetailActivity.this, "Something went wrong in deleting user, Please try again later", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateJoinRequest(final int position) {
        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        JoinAcceptModel joinAcceptModel = joinUsersModelList.get(position);
        joinAcceptModel.setSTATUS(Constants.Rejected);

        firebaseFirestore.collection(Constants.Matches).document(matchModel.getMatchId())
                .collection(Constants.JoinRequests).document(joinAcceptModel.getUID()).set(joinAcceptModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateJoinCount(position);
                        circularProgressBar.dismissProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                circularProgressBar.dismissProgressDialog();
                Toast.makeText(MatchDetailActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateJoinCount(final int position) {
        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        firebaseFirestore.collection(Constants.Matches).document(matchModel.getMatchId())
                .update(Constants.Joined, FieldValue.increment(-1)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MatchDetailActivity.this, "Deleting " + joinUsersModelList.get(position).getNAME()
                        + " scucessful", Toast.LENGTH_LONG).show();
                joinUsersModelList.remove(position);
                joinedUsersAdapter.notifyDataSetChanged();
                circularProgressBar.dismissProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                circularProgressBar.dismissProgressDialog();
            }
        });
    }

    private void showBatteryDialog() {

        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setCancelable(false);

                alert.setTitle(getString(R.string.app_name));
                alert.setMessage("Please disable Battery Optimization to get Notifications without any interruption.\nTo disable " +
                        "GoTo: " +
                        "\n Settings -> " +
                        "\n Battery -> " +
                        "\n Battery Optimization -> " +
                        "\n " + getString(R.string.app_name) + " -> " +
                        "\n Select - don't optimize -");
                alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;
                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        }
    }

    private void setupNotifications() {

        if (notificationCount == 0) {
            tv_MatchDetails_notifications.setVisibility(View.GONE);
        } else {
            tv_MatchDetails_notifications.setVisibility(View.VISIBLE);
            tv_MatchDetails_notifications.setText(notificationCount + "");
        }
    }

    private void showMessageslayout() {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        lay_MatchDetailsMessages.setVisibility(View.VISIBLE);
        lay_MatchDetailsMessages.startAnimation(slideUp);
        notificationCount = 0;
        setupNotifications();
    }

    private void hideMessagesLayout() {
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        lay_MatchDetailsMessages.startAnimation(slideDown);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                lay_MatchDetailsMessages.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (lay_MatchDetailsMessages.getVisibility() == View.VISIBLE) {
            hideMessagesLayout();
        } else {
            finish();
        }

    }


    void setUpMessages() {
        messageModelList.clear();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                messageModelList.add(messageModel);
                if (lay_MatchDetailsMessages.getVisibility() == View.GONE) {
                    notificationCount++;
                    setupNotifications();
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        firebaseDatabase.child(Constants.mesasges).child(matchModel.getMatchId()).limitToLast(20).addChildEventListener(childEventListener);
    }
}
