package com.example.mytutor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TutorRecyclerAdapter(
    private var tutors: List<DatabaseHelper.User>,
    private val onBookClicked: (DatabaseHelper.User) -> Unit
) : RecyclerView.Adapter<TutorRecyclerAdapter.TutorViewHolder>() {

    class TutorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.tutorName)
        val bioTv: TextView = view.findViewById(R.id.tutorBio)
        val locationTv: TextView = view.findViewById(R.id.tutorLocation)
        val bookBtn: Button = view.findViewById(R.id.bookButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor, parent, false)
        return TutorViewHolder(view)
    }

    override fun onBindViewHolder(holder: TutorViewHolder, position: Int) {
        val tutor = tutors[position]
        holder.nameTv.text = tutor.fullName
        holder.bioTv.text = tutor.bio
        holder.locationTv.text = "📍 ${tutor.location}"
        
        holder.bookBtn.setOnClickListener {
            onBookClicked(tutor)
        }
    }

    override fun getItemCount(): Int = tutors.size

    fun updateList(newList: List<DatabaseHelper.User>) {
        tutors = newList
        notifyDataSetChanged()
    }
}
