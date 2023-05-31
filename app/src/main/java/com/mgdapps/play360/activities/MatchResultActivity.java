package com.mgdapps.play360.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgdapps.play360.R;
import com.mgdapps.play360.adapters.MatchResultAdapter;
import com.mgdapps.play360.callbacks.UpdateResultCallback;
import com.mgdapps.play360.helper.CircularProgressBar;
import com.mgdapps.play360.helper.Constants;
import com.mgdapps.play360.models.JoinAcceptModel;

import java.util.ArrayList;
import java.util.List;

public class MatchResultActivity extends AppCompatActivity implements UpdateResultCallback {

    List<JoinAcceptModel> joinUsersModelList = new ArrayList<>();
    String matchId = "";
    RecyclerView rv_matchResultRecycler;
    TextView tv_matchResultNoData;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    MatchResultAdapter matchResultAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_result);

        initViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Update Result");

        if (getIntent().getParcelableArrayListExtra(Constants.Joined) != null) {
            joinUsersModelList = getIntent().getParcelableArrayListExtra(Constants.Joined);
        }
        if (getIntent().getStringExtra(Constants.MatchId) != null) {
            matchId = getIntent().getStringExtra(Constants.MatchId);
        }

        if (joinUsersModelList.size() <= 0) {
            tv_matchResultNoData.setVisibility(View.VISIBLE);
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        rv_matchResultRecycler.setHasFixedSize(true);
        rv_matchResultRecycler.setLayoutManager(new LinearLayoutManager(this));
        matchResultAdapter = new MatchResultAdapter(this, joinUsersModelList, this);
        rv_matchResultRecycler.setAdapter(matchResultAdapter);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rv_matchResultRecycler = findViewById(R.id.rv_matchResultRecycler);
        tv_matchResultNoData = findViewById(R.id.tv_matchResultNoData);
    }

    @Override
    public void UpdateResultClicked(final int position, final int kills) {
        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        setResult(RESULT_OK);

        firebaseFirestore.collection(Constants.Matches).document(matchId).collection(Constants.Joined)
                .document(joinUsersModelList.get(position).getUID()).update(Constants.Kills, kills)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        joinUsersModelList.get(position).setKills(kills);
                        matchResultAdapter.notifyDataSetChanged();
                        Toast.makeText(MatchResultActivity.this, "Update successful", Toast.LENGTH_LONG).show();
                        circularProgressBar.dismissProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MatchResultActivity.this, "Error in updating kills", Toast.LENGTH_LONG).show();
                circularProgressBar.dismissProgressDialog();
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
