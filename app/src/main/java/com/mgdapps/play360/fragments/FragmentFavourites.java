package com.mgdapps.play360.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mgdapps.play360.R;
import com.mgdapps.play360.activities.MatchDetailActivity;
import com.mgdapps.play360.adapters.HomeAdapter;
import com.mgdapps.play360.callbacks.AllMatchesCallback;
import com.mgdapps.play360.helper.CircularProgressBar;
import com.mgdapps.play360.helper.Constants;
import com.mgdapps.play360.models.CreateMatchModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFavourites extends Fragment implements AllMatchesCallback {


    List<CreateMatchModel> matchModelList = new ArrayList<>();

    HomeAdapter homeAdapter;

    RecyclerView rv_favouriteList;

    LinearLayout lay_resultNotFound;

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        lay_resultNotFound = view.findViewById(R.id.lay_resultNotFound);
        rv_favouriteList = view.findViewById(R.id.rv_favouriteList);

        homeAdapter = new HomeAdapter(getActivity(), matchModelList, this);
        rv_favouriteList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_favouriteList.setHasFixedSize(true);
        rv_favouriteList.setAdapter(homeAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        loadRoom();

        return view;

    }

    private void loadRoom() {
        matchModelList.clear();

        final CircularProgressBar circularProgressBar = new CircularProgressBar(getActivity());
        circularProgressBar.showProgressDialog();

        firebaseFirestore.collection(Constants.UserDB).document(firebaseUser.getUid())
                .collection(Constants.Joined).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                        firebaseFirestore.collection(Constants.Matches).document(documentSnapshot.getId())
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    CreateMatchModel createMatchModel = documentSnapshot.toObject(CreateMatchModel.class);
                                    matchModelList.add(createMatchModel);
                                    homeAdapter.notifyDataSetChanged();

                                    if (matchModelList.size() <= 0) {
                                        lay_resultNotFound.setVisibility(View.VISIBLE);
                                    } else {
                                        lay_resultNotFound.setVisibility(View.GONE);
                                    }
                                    circularProgressBar.dismissProgressDialog();
                                } else {
                                    if (matchModelList.size() <= 0) {
                                        lay_resultNotFound.setVisibility(View.VISIBLE);
                                    } else {
                                        lay_resultNotFound.setVisibility(View.GONE);
                                    }
                                    circularProgressBar.dismissProgressDialog();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (matchModelList.size() <= 0) {
                                    lay_resultNotFound.setVisibility(View.VISIBLE);
                                } else {
                                    lay_resultNotFound.setVisibility(View.GONE);
                                }
                                circularProgressBar.dismissProgressDialog();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "Seems you have not joined any match yet", Toast.LENGTH_LONG).show();
                    if (matchModelList.size() <= 0) {
                        lay_resultNotFound.setVisibility(View.VISIBLE);
                    } else {
                        lay_resultNotFound.setVisibility(View.GONE);
                    }
                    circularProgressBar.dismissProgressDialog();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "There was an error in getting data", Toast.LENGTH_LONG).show();
                if (matchModelList.size() <= 0) {
                    lay_resultNotFound.setVisibility(View.VISIBLE);
                } else {
                    lay_resultNotFound.setVisibility(View.GONE);
                }
                circularProgressBar.dismissProgressDialog();
            }
        });

    }

    @Override
    public void clicked(int position) {
        Intent intent = new Intent(getActivity(), MatchDetailActivity.class);
        intent.putExtra("match", matchModelList.get(position));
        startActivity(intent);
    }
}
