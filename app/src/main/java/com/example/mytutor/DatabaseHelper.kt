package com.example.mytutor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MyTutor.db"
        private const val DATABASE_VERSION = 10 // Incremented to 10 to ensure location column is added for all users

        // User table
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_ROLE = "role"
        const val COLUMN_BIO = "bio"
        const val COLUMN_STATUS = "status"
        const val COLUMN_LOCATION = "location"

        // Booking table
        const val TABLE_BOOKINGS = "bookings"
        const val COLUMN_BOOKING_ID = "booking_id"
        const val COLUMN_DATE = "date"
        const val COLUMN_SUBJECT = "subject"
        const val COLUMN_TUTOR_NAME = "tutor_name"
        const val COLUMN_STUDENT_NAME = "student_name"
        const val COLUMN_GRADE = "grade"
        const val COLUMN_ATTENDANCE = "attendance"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FULL_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT,"
                + COLUMN_BIO + " TEXT,"
                + COLUMN_STATUS + " TEXT,"
                + COLUMN_LOCATION + " TEXT" + ")")
        
        val createBookingTable = ("CREATE TABLE " + TABLE_BOOKINGS + "("
                + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_SUBJECT + " TEXT,"
                + COLUMN_TUTOR_NAME + " TEXT,"
                + COLUMN_STUDENT_NAME + " TEXT,"
                + COLUMN_GRADE + " TEXT,"
                + COLUMN_ATTENDANCE + " TEXT" + ")")

        db?.execSQL(createUserTable)
        db?.execSQL(createBookingTable)

        preloadAdmin(db)
        preloadTutors(db)
        preloadStudents(db)
    }

    private fun preloadAdmin(db: SQLiteDatabase?) {
        val values = ContentValues()
        values.put(COLUMN_FULL_NAME, "Admin User")
        values.put(COLUMN_EMAIL, "admin@mytutor.com")
        values.put(COLUMN_PASSWORD, "admin123")
        values.put(COLUMN_ROLE, "Admin")
        values.put(COLUMN_STATUS, "Approved")
        db?.insert(TABLE_USERS, null, values)
    }

    private fun preloadTutors(db: SQLiteDatabase?) {
        val tutors = arrayOf(
            arrayOf("John Smith", "john@tutor.com", "password123", "Tutor", "Expert in Advanced Mathematics with 10 years of experience.", "Cape Town"),
            arrayOf("Sarah Jenkins", "sarah@tutor.com", "password123", "Tutor", "Specializes in Organic Chemistry and Biology.", "Johannesburg"),
            arrayOf("Michael Chen", "michael@tutor.com", "password123", "Tutor", "Native English speaker focused on Literature and Grammar.", "Durban"),
            arrayOf("Emily Davis", "emily@tutor.com", "password123", "Tutor", "Physics PhD with a passion for teaching high school students.", "Online")
        )

        for (tutor in tutors) {
            val values = ContentValues()
            values.put(COLUMN_FULL_NAME, tutor[0])
            values.put(COLUMN_EMAIL, tutor[1])
            values.put(COLUMN_PASSWORD, tutor[2])
            values.put(COLUMN_ROLE, tutor[3])
            values.put(COLUMN_BIO, tutor[4])
            values.put(COLUMN_LOCATION, tutor[5])
            values.put(COLUMN_STATUS, "Approved")
            db?.insert(TABLE_USERS, null, values)
        }
    }

    private fun preloadStudents(db: SQLiteDatabase?) {
        val students = arrayOf(
            arrayOf("Alex Johnson", "alex@student.com", "password123", "Student"),
            arrayOf("Emma Wilson", "emma@student.com", "password123", "Student")
        )
        for (student in students) {
            val values = ContentValues()
            values.put(COLUMN_FULL_NAME, student[0])
            values.put(COLUMN_EMAIL, student[1])
            values.put(COLUMN_PASSWORD, student[2])
            values.put(COLUMN_ROLE, student[3])
            values.put(COLUMN_STATUS, "Approved")
            db?.insert(TABLE_USERS, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 5) {
            try {
                db?.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_STATUS TEXT DEFAULT 'Approved'")
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error adding status column: ${e.message}")
            }
        }
        if (oldVersion < 7) {
            try {
                addColumnIfNotExists(db, TABLE_BOOKINGS, COLUMN_GRADE, "TEXT")
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error adding grade column: ${e.message}")
            }
        }
        if (oldVersion < 8) {
            try {
                addColumnIfNotExists(db, TABLE_BOOKINGS, COLUMN_ATTENDANCE, "TEXT")
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error adding attendance column: ${e.message}")
            }
        }
        if (oldVersion < 10) {
            try {
                addColumnIfNotExists(db, TABLE_USERS, COLUMN_LOCATION, "TEXT")
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error adding location column: ${e.message}")
            }
        }
    }

    private fun addColumnIfNotExists(db: SQLiteDatabase?, tableName: String, columnName: String, columnType: String) {
        val cursor = db?.rawQuery("PRAGMA table_info($tableName)", null)
        var exists = false
        if (cursor != null) {
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (nameIndex != -1 && cursor.getString(nameIndex) == columnName) {
                    exists = true
                    break
                }
            }
            cursor.close()
        }
        if (!exists) {
            db?.execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $columnType")
        }
    }

    fun addUser(fullName: String, email: String, password: String, role: String, bio: String = "", status: String = "Approved", location: String = ""): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FULL_NAME, fullName)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        values.put(COLUMN_ROLE, role)
        values.put(COLUMN_BIO, bio)
        values.put(COLUMN_STATUS, status)
        values.put(COLUMN_LOCATION, location)
        
        return try {
            val result = db.insert(TABLE_USERS, null, values)
            if (result == -1L) {
                Log.e("DatabaseHelper", "Failed to insert user: $email")
            }
            result
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting user: ${e.message}")
            -1L
        }
    }

    fun approveUser(email: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_STATUS, "Approved")
        return db.update(TABLE_USERS, values, "$COLUMN_EMAIL = ?", arrayOf(email))
    }

    fun getPendingUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, null, "$COLUMN_STATUS = ?", arrayOf("Pending"), null, null, null)
        if (cursor.moveToFirst()) {
            do {
                users.add(User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIO)) ?: "",
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)) ?: ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }

    fun getAllSessions(): List<Booking> {
        val bookings = mutableListOf<Booking>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_BOOKINGS, null, null, null, null, null, "$COLUMN_BOOKING_ID DESC")
        if (cursor.moveToFirst()) {
            do {
                bookings.add(Booking(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TUTOR_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTENDANCE))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return bookings
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_ID), "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?", arrayOf(email, password), null, null, null)
        val count = cursor.count
        cursor.close()
        return count > 0
    }

    fun getUser(email: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, null, "$COLUMN_EMAIL = ?", arrayOf(email), null, null, null)
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIO)) ?: "",
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)) ?: "Approved",
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)) ?: ""
            )
        }
        cursor.close()
        return user
    }

    fun isEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_ID), "$COLUMN_EMAIL = ?", arrayOf(email), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    data class User(val fullName: String, val email: String, val role: String, val bio: String = "", val status: String = "Approved", val location: String = "")
    data class Booking(val id: Int, val date: String, val subject: String, val tutorName: String, val studentName: String, val grade: String?, val attendance: String?)

    fun addBooking(date: String, subject: String, tutorName: String, studentName: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_SUBJECT, subject)
        values.put(COLUMN_TUTOR_NAME, tutorName)
        values.put(COLUMN_STUDENT_NAME, studentName)
        return db.insert(TABLE_BOOKINGS, null, values)
    }

    fun getBookingsForStudent(studentName: String): List<Booking> {
        val bookings = mutableListOf<Booking>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_BOOKINGS, null, "$COLUMN_STUDENT_NAME = ?", arrayOf(studentName), null, null, "$COLUMN_BOOKING_ID DESC")
        if (cursor.moveToFirst()) {
            do {
                bookings.add(Booking(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TUTOR_NAME)),
                    studentName,
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTENDANCE))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return bookings
    }

    fun getBookingsForTutor(tutorName: String): List<Booking> {
        val bookings = mutableListOf<Booking>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_BOOKINGS, null, "$COLUMN_TUTOR_NAME = ?", arrayOf(tutorName), null, null, "$COLUMN_BOOKING_ID DESC")
        if (cursor.moveToFirst()) {
            do {
                bookings.add(Booking(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                    tutorName,
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTENDANCE))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return bookings
    }

    fun updateGrade(bookingId: Int, grade: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_GRADE, grade)
        return db.update(TABLE_BOOKINGS, values, "$COLUMN_BOOKING_ID = ?", arrayOf(bookingId.toString()))
    }

    fun updateAttendance(bookingId: Int, attendance: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ATTENDANCE, attendance)
        return db.update(TABLE_BOOKINGS, values, "$COLUMN_BOOKING_ID = ?", arrayOf(bookingId.toString()))
    }

    fun updateUser(email: String, fullName: String, bio: String, password: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FULL_NAME, fullName)
        values.put(COLUMN_BIO, bio)
        values.put(COLUMN_PASSWORD, password)
        return db.update(TABLE_USERS, values, "$COLUMN_EMAIL = ?", arrayOf(email))
    }

    fun getAllTutorsWithBio(): List<User> {
        val tutorList = mutableListOf<User>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_FULL_NAME, $COLUMN_EMAIL, $COLUMN_ROLE, $COLUMN_BIO, $COLUMN_STATUS, $COLUMN_LOCATION FROM $TABLE_USERS WHERE $COLUMN_ROLE = 'Tutor' AND $COLUMN_STATUS = 'Approved'"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                tutorList.add(User(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3) ?: "",
                    cursor.getString(4) ?: "Approved",
                    cursor.getString(5) ?: ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tutorList
    }

    fun searchTutors(subject: String?, location: String?): List<User> {
        val tutorList = mutableListOf<User>()
        val db = this.readableDatabase
        
        var selection = "$COLUMN_ROLE = 'Tutor' AND $COLUMN_STATUS = 'Approved'"
        val selectionArgs = mutableListOf<String>()

        if (!subject.isNullOrEmpty() && subject != "Select Subject") {
            selection += " AND $COLUMN_BIO LIKE ?"
            selectionArgs.add("%$subject%")
        }

        if (!location.isNullOrEmpty() && location != "Select Location") {
            selection += " AND $COLUMN_LOCATION = ?"
            selectionArgs.add(location)
        }

        val cursor = db.query(TABLE_USERS, null, selection, selectionArgs.toTypedArray(), null, null, null)
        
        if (cursor.moveToFirst()) {
            do {
                tutorList.add(User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIO)) ?: "",
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)) ?: "Approved",
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)) ?: ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tutorList
    }

    fun getAllStudents(): List<String> {
        val studentList = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_FULL_NAME FROM $TABLE_USERS WHERE $COLUMN_ROLE = 'Student' AND $COLUMN_STATUS = 'Approved'"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                studentList.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return studentList
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
        return bio
    }

    /**
     * Explicitly opens and returns a writable database connection.
     */
    fun openDatabase(): SQLiteDatabase {
        return this.writableDatabase
    }

    /**
     * Explicitly closes the database connection.
     */
    fun closeDatabase() {
        this.close()
    }
}
