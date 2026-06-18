package com.example.mytutor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView

class TutorAdapter(context: Context, private val tutors: List<DatabaseHelper.User>) :
    ArrayAdapter<DatabaseHelper.User>(context, 0, tutors) {

    private var filteredTutors: List<DatabaseHelper.User> = tutors

    override fun getCount(): Int = filteredTutors.size

    override fun getItem(position: Int): DatabaseHelper.User? = filteredTutors[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.tutor_dropdown_item, parent, false
        )

        val tutor = getItem(position)
        val nameTv = view.findViewById<TextView>(R.id.tutorName)
        val bioTv = view.findViewById<TextView>(R.id.tutorBio)

        nameTv.text = tutor?.fullName
        bioTv.text = tutor?.bio

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val query = constraint?.toString()?.lowercase()

                filteredTutors = if (query.isNullOrEmpty()) {
                    tutors
                } else {
                    tutors.filter {
                        it.fullName.lowercase().contains(query) || it.bio.lowercase().contains(query)
                    }
                }

                results.values = filteredTutors
                results.count = filteredTutors.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null) {
                    filteredTutors = results.values as List<DatabaseHelper.User>
                }
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as? DatabaseHelper.User)?.fullName ?: ""
            }
        }
    }
}
