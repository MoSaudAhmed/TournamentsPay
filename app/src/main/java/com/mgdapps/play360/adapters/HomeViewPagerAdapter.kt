package com.mgdapps.play360.adapters

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mgdapps.play360.R
import com.mgdapps.play360.activities.MatchDetailActivity
import com.mgdapps.play360.helper.Constants
import com.mgdapps.play360.models.AnnouncementsModel
import com.squareup.picasso.Picasso
import java.util.*

class HomeViewPagerAdapter(var context: Context, offersList: List<AnnouncementsModel?>) : RecyclerView.Adapter<HomeViewPagerAdapter.MViewHolder>() {
    var offersList: List<AnnouncementsModel?> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.announcement_row_item, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.tv_homeViewpagerRow.text = offersList[position]!!.title
        Picasso.get().load(offersList[position]!!.imageURL).error(R.drawable.ic_launcher).into(holder.img_homeViewpagerRow)

        holder.itemView.setOnClickListener {
            if (offersList[position]!!.gotoMatch != null && !TextUtils.isEmpty(offersList[position]!!.gotoMatch)) {
                var intent = Intent(context, MatchDetailActivity::class.java)
                intent.putExtra(Constants.MatchId, offersList.get(position)!!.gotoMatch)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return offersList.size
    }

    inner class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_homeViewpagerRow: ImageView
        var tv_homeViewpagerRow: TextView

        init {
            tv_homeViewpagerRow = itemView.findViewById(R.id.tv_announcementRow)
            img_homeViewpagerRow =
                    itemView.findViewById(R.id.img_announcementRow)
        }
    }

    init {
        this.offersList = offersList
    }
}