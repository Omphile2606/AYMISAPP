package com.example.mytutor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PendingUserAdapter(
    private var users: List<DatabaseHelper.User>,
    private val onApproveClick: (DatabaseHelper.User) -> Unit
) : RecyclerView.Adapter<PendingUserAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.userName)
        val infoTv: TextView = view.findViewById(R.id.userRoleEmail)
        val bioTv: TextView = view.findViewById(R.id.userBio)
        val approveBtn: Button = view.findViewById(R.id.btnApprove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.nameTv.text = user.fullName
        holder.infoTv.text = "${user.role} | ${user.email}"
        
        if (user.bio.isNotEmpty()) {
            holder.bioTv.text = user.bio
            holder.bioTv.visibility = View.VISIBLE
        } else {
            holder.bioTv.visibility = View.GONE
        }

        holder.approveBtn.setOnClickListener {
            onApproveClick(user)
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateData(newUsers: List<DatabaseHelper.User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
