package com.mgdapps.play360.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mgdapps.play360.R
import com.mgdapps.play360.activities.MainActivity
import com.mgdapps.play360.activities.MatchDetailActivity
import com.mgdapps.play360.adapters.HomeAdapter
import com.mgdapps.play360.adapters.HomeViewPagerAdapter
import com.mgdapps.play360.callbacks.AllMatchesCallback
import com.mgdapps.play360.helper.CircularProgressBar
import com.mgdapps.play360.helper.Constants
import com.mgdapps.play360.models.AnnouncementsModel
import com.mgdapps.play360.models.CreateMatchModel
import kotlinx.coroutines.*
import java.util.*

class FragmentHome : Fragment(), AllMatchesCallback {
    var rv_homeList: RecyclerView? = null
    var lay_Announcements: FrameLayout? = null
    var vp_home_viewPager: ViewPager2? = null
    var pager_tab_layout: TabLayout? = null
    var firebaseUser: FirebaseUser? = null
    var firebaseAuth: FirebaseAuth? = null
    var firebaseFirestore: FirebaseFirestore? = null
    var circularProgressBar: CircularProgressBar? = null
    var mFrameLayout: RelativeLayout? = null
    var HomeSwipeToRefresh: SwipeRefreshLayout? = null
    var lay_resultNotFound: LinearLayout? = null
    var homeAdapter: HomeAdapter? = null
    var lastVisibleDocument: DocumentSnapshot? = null
    var isReachedEnd = false
    var mQuery: Query? = null
    var matchModelList: MutableList<CreateMatchModel> = ArrayList()
    var announcementsModelList: MutableList<AnnouncementsModel?> = ArrayList()
    var homeViewPagerAdapter: HomeViewPagerAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(view)
        circularProgressBar = CircularProgressBar(activity)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser
        firebaseFirestore = FirebaseFirestore.getInstance()
        homeAdapter = HomeAdapter(activity, matchModelList, this)
        rv_homeList!!.layoutManager = LinearLayoutManager(activity)
        rv_homeList!!.adapter = homeAdapter
        loadAnnouncements()
        loadTournaments()
        HomeSwipeToRefresh!!.setOnRefreshListener {
            lastVisibleDocument = null
            matchModelList.clear()
            loadTournaments()
        }
        homeViewPagerAdapter = HomeViewPagerAdapter(requireActivity(), announcementsModelList)
        vp_home_viewPager!!.adapter = homeViewPagerAdapter
        TabLayoutMediator(pager_tab_layout!!, vp_home_viewPager!!)
        { tab, position -> }.attach()
        for (i in 0 until pager_tab_layout!!.tabCount) {
            val tab = (pager_tab_layout!!.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as MarginLayoutParams
            p.setMargins(0, 0, 15, 0)
            tab.requestLayout()
        }

        vp_home_viewPager!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        runViewPagerChanger()
    }

    lateinit var pagerJob: Job
    fun runViewPagerChanger() {
        pagerJob = Job()
        CoroutineScope(Dispatchers.Main + pagerJob).launch {
            for (i in 0..1000) {
                delay(5000L)
                if (vp_home_viewPager!!.currentItem == announcementsModelList.size - 1) {
                    vp_home_viewPager!!.setCurrentItem(0, true)
                } else if (vp_home_viewPager!!.currentItem < announcementsModelList.size - 1) {
                    vp_home_viewPager!!.setCurrentItem(vp_home_viewPager!!.currentItem + 1, true)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pagerJob.cancel()
    }

    private fun initViews(view: View) {
        rv_homeList = view.findViewById(R.id.rv_homeRecyclerView)
        mFrameLayout = view.findViewById(R.id.lay_fragment_home)
        lay_Announcements = view.findViewById(R.id.lay_Announcements)
        HomeSwipeToRefresh = view.findViewById(R.id.HomeSwipeToRefresh)
        lay_resultNotFound = view.findViewById(R.id.lay_resultNotFound)
        pager_tab_layout = view.findViewById(R.id.pager_tab_layout)
        vp_home_viewPager = view.findViewById(R.id.vp_home_viewPager)
    }

    private fun loadAnnouncements() {
        announcementsModelList.clear()
        firebaseFirestore!!.collection(Constants.announcement).get().addOnSuccessListener { queryDocumentSnapshots ->
            if (queryDocumentSnapshots.isEmpty) {
                lay_Announcements!!.visibility = View.GONE
            } else {
                lay_Announcements!!.visibility = View.VISIBLE
            }
            for (document in queryDocumentSnapshots.documents) {
                val announcementsModel = document.toObject(AnnouncementsModel::class.java)
                if (announcementsModel != null) {
                    if (announcementsModel.title != null && !announcementsModel.title.equals("null", ignoreCase = true)
                            && !TextUtils.isEmpty(announcementsModel.title.trim { it <= ' ' })) {
                        announcementsModelList.add(announcementsModel)
                    }
                }
            }
            if (announcementsModelList.size > 0) {
                lay_Announcements!!.visibility = View.VISIBLE
            } else {
                lay_Announcements!!.visibility = View.GONE
            }
            homeViewPagerAdapter!!.notifyDataSetChanged()
        }.addOnFailureListener { lay_Announcements!!.visibility = View.GONE }
    }

    fun loadTournaments() {
        circularProgressBar!!.showProgressDialog()
        val searchLimit = 15
        mQuery = if (lastVisibleDocument == null) {
            FirebaseFirestore.getInstance().collection(Constants.Matches)
                    .whereEqualTo(Constants.MatchPrivate, false)
                    .orderBy(Constants.MatchTime, Query.Direction.DESCENDING)
                    .limit(searchLimit.toLong())
        } else {
            FirebaseFirestore.getInstance().collection(Constants.Matches)
                    .whereEqualTo(Constants.MatchPrivate, false)
                    .orderBy(Constants.MatchTime, Query.Direction.DESCENDING)
                    .startAfter(lastVisibleDocument!!)
                    .limit(searchLimit.toLong())
        }
        HomeSwipeToRefresh!!.isRefreshing = true
        mQuery!!.get().addOnSuccessListener { queryDocumentSnapshots ->
            if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0 && !isReachedEnd) {
                if (queryDocumentSnapshots.documents.size > 0) {
                    lastVisibleDocument = queryDocumentSnapshots.documents[queryDocumentSnapshots.documents.size - 1]
                    for (document in queryDocumentSnapshots.documents) {
                        val createMatchModel = document.toObject(CreateMatchModel::class.java)
                        if (createMatchModel != null) {
                            matchModelList.add(createMatchModel)
                        }
                    }
                } else {
                    isReachedEnd = true
                }
            } else {
                showToast("You Have Reached the end")
            }
            if (matchModelList.size <= 0) {
                lay_resultNotFound!!.visibility = View.VISIBLE
            } else {
                lay_resultNotFound!!.visibility = View.GONE
            }
            HomeSwipeToRefresh!!.isRefreshing = false
            homeAdapter!!.notifyDataSetChanged()
            circularProgressBar!!.dismissProgressDialog()

            if ((activity as MainActivity).gotoMatch != null && !TextUtils.isEmpty((activity as MainActivity).gotoMatch)) {
                var intent = Intent(activity, MatchDetailActivity::class.java)
                intent.putExtra(Constants.MatchId, (activity as MainActivity).gotoMatch)
                startActivity(intent)
                (activity as MainActivity).gotoMatch = ""
            }


        }.addOnFailureListener {
            showToast("There was an error in getting data")
            HomeSwipeToRefresh!!.isRefreshing = false
            if (matchModelList.size <= 0) {
                lay_resultNotFound!!.visibility = View.VISIBLE
            } else {
                lay_resultNotFound!!.visibility = View.GONE
            }
            circularProgressBar!!.dismissProgressDialog()
        }
    }

    private fun showToast(message: String) {
        if (activity != null) {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (circularProgressBar != null) {
            circularProgressBar!!.dismissProgressDialog()
        }
    }

    override fun clicked(position: Int) {
        var intent = Intent(activity, MatchDetailActivity::class.java)
        intent.putExtra("match", matchModelList.get(position))
        startActivity(intent)
    }
}