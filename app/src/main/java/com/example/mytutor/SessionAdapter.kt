package com.example.mytutor

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast

class SessionAdapter(
    private val context: Context, 
    private val sessions: List<DatabaseHelper.Booking>,
    private val isTutor: Boolean = false,
    private val onSessionUpdated: () -> Unit = {}
) : BaseAdapter() {

    private val databaseHelper = DatabaseHelper(context)

    override fun getCount(): Int = sessions.size

    override fun getItem(position: Int): Any = sessions[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.sessioninfor, parent, false)
        
        val session = getItem(position) as DatabaseHelper.Booking
        
        val subjectTv = view.findViewById<TextView>(R.id.subject)
        val nameTv = view.findViewById<TextView>(R.id.topic)
        val dateTv = view.findViewById<TextView>(R.id.Classdate)
        val attendanceValue = view.findViewById<TextView>(R.id.attendanceValue)
        val gradeValue = view.findViewById<TextView>(R.id.gradeValue)
        
        subjectTv.text = session.subject
        nameTv.text = if (isTutor) "Student: ${session.studentName}" else "Tutor: ${session.tutorName}"
        dateTv.text = session.date

        if (!session.grade.isNullOrEmpty()) {
            gradeValue.visibility = View.VISIBLE
            gradeValue.text = session.grade
        } else {
            gradeValue.visibility = View.GONE
        }

        if (!session.attendance.isNullOrEmpty()) {
            attendanceValue.visibility = View.VISIBLE
            attendanceValue.text = session.attendance
            // Change color based on status
            if (session.attendance == "PRESENT") {
                attendanceValue.setTextColor(context.getColor(android.R.color.holo_green_dark))
            } else {
                attendanceValue.setTextColor(context.getColor(android.R.color.holo_red_dark))
            }
        } else {
            attendanceValue.visibility = View.GONE
        }

        if (isTutor) {
            view.setOnClickListener {
                showTutorActionsDialog(session)
            }
        }
        
        return view
    }

    private fun showTutorActionsDialog(session: DatabaseHelper.Booking) {
        val options = arrayOf("Mark Attendance", "Assign Grade")
        AlertDialog.Builder(context)
            .setTitle("Session Actions")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showAttendanceDialog(session)
                    1 -> showGradeDialog(session)
                }
            }
            .show()
    }

    private fun showAttendanceDialog(session: DatabaseHelper.Booking) {
        val options = arrayOf("PRESENT", "ABSENT")
        AlertDialog.Builder(context)
            .setTitle("Mark Attendance")
            .setItems(options) { _, which ->
                val status = options[which]
                databaseHelper.updateAttendance(session.id, status)
                Toast.makeText(context, "Attendance marked as $status", Toast.LENGTH_SHORT).show()
                onSessionUpdated()
            }
            .show()
    }

    private fun showGradeDialog(session: DatabaseHelper.Booking) {
        val grades = arrayOf("A+", "A", "B+", "B", "C", "D", "E", "F")
        AlertDialog.Builder(context)
            .setTitle("Assign Grade")
            .setItems(grades) { _, which ->
                val grade = grades[which]
                databaseHelper.updateGrade(session.id, grade)
                Toast.makeText(context, "Grade $grade assigned", Toast.LENGTH_SHORT).show()
                onSessionUpdated()
            }
            .show()
    }
}
