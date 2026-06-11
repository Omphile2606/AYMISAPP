package com.example.mytutor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class SessionAdapter(private val context: Context, private val sessions: List<DatabaseHelper.Booking>) : BaseAdapter() {

    override fun getCount(): Int = sessions.size

    override fun getItem(position: Int): Any = sessions[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.sessioninfor, parent, false)
        
        val session = getItem(position) as DatabaseHelper.Booking
        
        val subjectTv = view.findViewById<TextView>(R.id.subject)
        val tutorTv = view.findViewById<TextView>(R.id.topic)
        val dateTv = view.findViewById<TextView>(R.id.Classdate)
        
        subjectTv.text = session.subject
        tutorTv.text = session.tutorName
        dateTv.text = session.date
        
        return view
    }
}
