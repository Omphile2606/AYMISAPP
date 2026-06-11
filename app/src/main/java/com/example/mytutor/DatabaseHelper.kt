package com.example.mytutor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MyTutor.db"
        private const val DATABASE_VERSION = 2

        // User table
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_ROLE = "role"
        const val COLUMN_BIO = "bio"

        // Booking table
        const val TABLE_BOOKINGS = "bookings"
        const val COLUMN_BOOKING_ID = "booking_id"
        const val COLUMN_DATE = "date"
        const val COLUMN_SUBJECT = "subject"
        const val COLUMN_TUTOR_NAME = "tutor_name"
        const val COLUMN_STUDENT_NAME = "student_name"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FULL_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT,"
                + COLUMN_BIO + " TEXT" + ")")
        
        val createBookingTable = ("CREATE TABLE " + TABLE_BOOKINGS + "("
                + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_SUBJECT + " TEXT,"
                + COLUMN_TUTOR_NAME + " TEXT,"
                + COLUMN_STUDENT_NAME + " TEXT" + ")")

        db?.execSQL(createUserTable)
        db?.execSQL(createBookingTable)

        preloadTutors(db)
    }

    private fun preloadTutors(db: SQLiteDatabase?) {
        val tutors = arrayOf(
            arrayOf("John Smith", "john@tutor.com", "password123", "Tutor", "Expert in Advanced Mathematics with 10 years of experience."),
            arrayOf("Sarah Jenkins", "sarah@tutor.com", "password123", "Tutor", "Specializes in Organic Chemistry and Biology."),
            arrayOf("Michael Chen", "michael@tutor.com", "password123", "Tutor", "Native English speaker focused on Literature and Grammar."),
            arrayOf("Emily Davis", "emily@tutor.com", "password123", "Tutor", "Physics PhD with a passion for teaching high school students.")
        )

        for (tutor in tutors) {
            val values = ContentValues()
            values.put(COLUMN_FULL_NAME, tutor[0])
            values.put(COLUMN_EMAIL, tutor[1])
            values.put(COLUMN_PASSWORD, tutor[2])
            values.put(COLUMN_ROLE, tutor[3])
            values.put(COLUMN_BIO, tutor[4])
            db?.insert(TABLE_USERS, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKINGS")
        onCreate(db)
    }

    fun addUser(fullName: String, email: String, password: String, role: String, bio: String = ""): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FULL_NAME, fullName)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        values.put(COLUMN_ROLE, role)
        values.put(COLUMN_BIO, bio)

        val success = db.insert(TABLE_USERS, null, values)
        db.close()
        return success
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_ID)
        val selection = "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(email, password)
        val cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

    fun getUser(email: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, null, "$COLUMN_EMAIL = ?", arrayOf(email), null, null, null)
        
        var user: User? = null
        if (cursor != null && cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME))
            val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
            user = User(name, userEmail, role)
        }
        cursor?.close()
        db.close()
        return user
    }

    data class User(val fullName: String, val email: String, val role: String)
    data class Booking(val date: String, val subject: String, val tutorName: String, val studentName: String)

    fun addBooking(date: String, subject: String, tutorName: String, studentName: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_SUBJECT, subject)
        values.put(COLUMN_TUTOR_NAME, tutorName)
        values.put(COLUMN_STUDENT_NAME, studentName)

        val success = db.insert(TABLE_BOOKINGS, null, values)
        db.close()
        return success
    }

    fun getBookingsForStudent(studentName: String): List<Booking> {
        val bookings = mutableListOf<Booking>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_BOOKINGS, null, "$COLUMN_STUDENT_NAME = ?", arrayOf(studentName), null, null, null)
        
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val subject = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT))
                val tutorName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TUTOR_NAME))
                bookings.add(Booking(date, subject, tutorName, studentName))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return bookings
    }

    fun getAllTutors(): List<String> {
        val tutorList = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_FULL_NAME FROM $TABLE_USERS WHERE $COLUMN_ROLE = 'Tutor'"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                tutorList.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return tutorList
    }

    fun getTutorBio(name: String): String {
        var bio = ""
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_BIO FROM $TABLE_USERS WHERE $COLUMN_FULL_NAME = ?"
        val cursor = db.rawQuery(query, arrayOf(name))
        if (cursor.moveToFirst()) {
            bio = cursor.getString(0) ?: ""
        }
        cursor.close()
        db.close()
        return bio
    }
}
