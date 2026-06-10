package com.example.mytutor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MyTutor.db"
        private const val DATABASE_VERSION = 1

        // User table
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_ROLE = "role"

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
                + COLUMN_ROLE + " TEXT" + ")")
        
        val createBookingTable = ("CREATE TABLE " + TABLE_BOOKINGS + "("
                + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_SUBJECT + " TEXT,"
                + COLUMN_TUTOR_NAME + " TEXT,"
                + COLUMN_STUDENT_NAME + " TEXT" + ")")

        db?.execSQL(createUserTable)
        db?.execSQL(createBookingTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKINGS")
        onCreate(db)
    }

    fun addUser(fullName: String, email: String, password: String, role: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FULL_NAME, fullName)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        values.put(COLUMN_ROLE, role)

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
}
