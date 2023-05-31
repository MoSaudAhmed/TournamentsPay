package com.mgdapps.play360.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mgdapps.play360.R;
import com.mgdapps.play360.adapters.JoinRequestsAdapter;
import com.mgdapps.play360.callbacks.JoinRejectCallback;
import com.mgdapps.play360.helper.CircularProgressBar;
import com.mgdapps.play360.helper.Constants;
import com.mgdapps.play360.helper.Preferences;
import com.mgdapps.play360.models.CreateMatchModel;
import com.mgdapps.play360.models.JoinAcceptModel;
import com.mgdapps.play360.models.JoinRequestModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JoinRequestsActivity extends AppCompatActivity implements JoinRejectCallback {

    String matchId = "";
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    RecyclerView rv_JoinRequests;
    TextView tv_noResult;

    List<JoinRequestModel> joinRequestModelList = new ArrayList<>();
    JoinRequestsAdapter joinRequestsAdapter;

    SwipeRefreshLayout swipe_JoinRequest;

    Toolbar toolbar;

    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_requests);

        initViews();

        preferences = new Preferences();
        preferences.loadPreferences(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Pending Requests");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (getIntent().getStringExtra(Constants.MatchId) != null) {
            matchId = getIntent().getStringExtra(Constants.MatchId);
            loadJoinRequests();
        }

        rv_JoinRequests.setLayoutManager(new LinearLayoutManager(this));
        rv_JoinRequests.setHasFixedSize(true);
        joinRequestsAdapter = new JoinRequestsAdapter(this, this, joinRequestModelList);
        rv_JoinRequests.setAdapter(joinRequestsAdapter);

        swipe_JoinRequest.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_JoinRequest.setRefreshing(false);
                loadJoinRequests();
            }
        });
    }

    private void initViews() {
        rv_JoinRequests = findViewById(R.id.rv_JoinRequests);
        tv_noResult = findViewById(R.id.tv_noResult);
        toolbar = findViewById(R.id.toolbar);
        swipe_JoinRequest = findViewById(R.id.swipe_JoinRequest);
    }

    public void loadJoinRequests() {

        joinRequestModelList.clear();

        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        Query query = firebaseFirestore.collection(Constants.Matches).document(matchId)
                .collection(Constants.JoinRequests).whereEqualTo(Constants.Status, Constants.Pending);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                circularProgressBar.dismissProgressDialog();
                if (queryDocumentSnapshots.isEmpty() || queryDocumentSnapshots == null) {
                    tv_noResult.setText("No requests.");
                }
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    JoinRequestModel joinRequestModel = snapshot.toObject(JoinRequestModel.class);
                    joinRequestModelList.add(joinRequestModel);
                }
                joinRequestsAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                circularProgressBar.dismissProgressDialog();
                tv_noResult.setText("Problem in getting data");
                Toast.makeText(JoinRequestsActivity.this, "Error in getting data", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void JoinRequestService(final int position) {

        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        JoinAcceptModel joinAcceptModel = new JoinAcceptModel();
        joinAcceptModel.setKills(0);
        joinAcceptModel.setNAME(joinRequestModelList.get(position).getNAME());
        joinAcceptModel.setSTATUS(Constants.Joined);
        joinAcceptModel.setUID(joinRequestModelList.get(position).getUID());
        joinAcceptModel.setProfilePic(joinRequestModelList.get(position).getProfilePic());

        firebaseFirestore.collection(Constants.Matches).document(matchId)
                .collection(Constants.Joined).document(joinRequestModelList.get(position).getUID())
                .set(joinAcceptModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        modifyPendingToJoined(position);
                        circularProgressBar.dismissProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                circularProgressBar.dismissProgressDialog();
                Toast.makeText(JoinRequestsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void modifyPendingToJoined(final int position) {
        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        JoinRequestModel joinRequestModel = joinRequestModelList.get(position);
        joinRequestModel.setSTATUS(Constants.Joined);

        firebaseFirestore.collection(Constants.Matches).document(matchId)
                .collection(Constants.JoinRequests).document(joinRequestModelList.get(position).getUID()).set(joinRequestModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        updateJoinedMatchToUser(position);
                        circularProgressBar.dismissProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                circularProgressBar.dismissProgressDialog();
                Toast.makeText(JoinRequestsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateJoinedMatchToUser(int position) {
        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        HashMap<String, Boolean> hashMap = new HashMap<>();
        hashMap.put(matchId, true);
        firebaseFirestore.collection(Constants.UserDB).document(joinRequestModelList.get(position).getUID())
                .collection(Constants.Joined).document(matchId).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                circularProgressBar.dismissProgressDialog();
                updateJoinCount();
                loadJoinRequests();
            }
        });
    }

    private void updateJoinCount() {
        firebaseFirestore.collection(Constants.Matches).document(matchId)
                .update(Constants.Joined, FieldValue.increment(1));
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

    @Override
    public void joinClicked(final int position) {
        setResult(RESULT_OK);
        firebaseFirestore.collection(Constants.Matches).document(matchId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        CreateMatchModel createMatchModel = new CreateMatchModel();
                        createMatchModel = documentSnapshot.toObject(CreateMatchModel.class);
                        if (createMatchModel.getJoined() < 100) {
                            JoinRequestService(position);
                        } else {
                            Toast.makeText(JoinRequestsActivity.this, "cannot join, Match already full", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void rejectClicked(final int position) {
        setResult(RESULT_OK);

        JoinRequestModel joinRequestModel = joinRequestModelList.get(position);
        joinRequestModel.setSTATUS(Constants.Rejected);

        firebaseFirestore.collection(Constants.Matches).document(matchId)
                .collection(Constants.JoinRequests).document(joinRequestModelList.get(position).getUID())
                .set(joinRequestModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                joinRequestModelList.remove(position);
                joinRequestsAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JoinRequestsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });

    }
}
