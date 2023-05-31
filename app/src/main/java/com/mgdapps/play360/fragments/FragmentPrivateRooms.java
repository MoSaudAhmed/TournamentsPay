package com.mgdapps.play360.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
public class FragmentPrivateRooms extends Fragment implements AllMatchesCallback {


    ImageView img_PrivateRoomsSearch;
    EditText et_PrivateRoomsSearch;

    List<CreateMatchModel> matchModelList = new ArrayList<>();

    HomeAdapter homeAdapter;

    RecyclerView rv_homeList;

    LinearLayout lay_resultNotFound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_private_rooms, container, false);

        img_PrivateRoomsSearch = view.findViewById(R.id.img_PrivateRoomsSearch);
        et_PrivateRoomsSearch = view.findViewById(R.id.et_PrivateRoomsSearch);
        rv_homeList = view.findViewById(R.id.rv_homeList);
        lay_resultNotFound = view.findViewById(R.id.lay_resultNotFound);

        homeAdapter = new HomeAdapter(getActivity(), matchModelList, this);
        rv_homeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_homeList.setHasFixedSize(true);
        rv_homeList.setAdapter(homeAdapter);


        img_PrivateRoomsSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_PrivateRoomsSearch.getText().toString().trim())) {
                    loadRoom(Long.valueOf(et_PrivateRoomsSearch.getText().toString().trim()));
                } else {
                    Toast.makeText(getActivity(), "Please enter Room Id", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private void loadRoom(Long roomId) {

        matchModelList.clear();

        final CircularProgressBar circularProgressBar = new CircularProgressBar(getActivity());
        circularProgressBar.showProgressDialog();

        Query query = FirebaseFirestore.getInstance().collection(Constants.Matches)
                .whereEqualTo(Constants.JoinCode, roomId);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        CreateMatchModel createMatchModel = documentSnapshot.toObject(CreateMatchModel.class);
                        if (createMatchModel != null) {
                            matchModelList.add(createMatchModel);
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Match not found, Please check your join code once", Toast.LENGTH_LONG).show();
                    et_PrivateRoomsSearch.setText("");
                }
                if (matchModelList.size() <= 0) {
                    lay_resultNotFound.setVisibility(View.VISIBLE);
                } else {
                    lay_resultNotFound.setVisibility(View.GONE);
                }
                homeAdapter.notifyDataSetChanged();
                circularProgressBar.dismissProgressDialog();

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
