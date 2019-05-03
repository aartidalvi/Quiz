package com.example.quiz

import android.content.ContentValues
import android.provider.BaseColumns
import android.util.Log
import org.json.JSONArray
import java.io.Serializable

class DatabaseManager(quizSQLHelper: QuizSQLHelper) : Serializable {
    // Table contents are grouped together in an anonymous object.
    val db = quizSQLHelper.writableDatabase

    object UserData : BaseColumns {
        const val TABLE_NAME = "userinfo"
        const val COLUMN_NAME_FIRSTNAME = "firstname"
        const val COLUMN_NAME_LASTNAME = "lastname"
        const val COLUMN_NAME_NICKNAME = "nickname"
        const val COLUMN_NAME_AGE = "age"
        const val COLUMN_NAME_SCORE = "score"
        const val COLUMN_NAME_ISGUEST = "isguest"
    }

    private val CREATE_USERTABLE =
        "CREATE TABLE IF NOT EXISTS ${UserData.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${UserData.COLUMN_NAME_FIRSTNAME} TEXT," +
                "${UserData.COLUMN_NAME_LASTNAME} TEXT," +
                "${UserData.COLUMN_NAME_NICKNAME} TEXT," +
                "${UserData.COLUMN_NAME_AGE} INTEGER," +
                "${UserData.COLUMN_NAME_SCORE} INTEGER," +
                "${UserData.COLUMN_NAME_ISGUEST} INTEGER)"

    private val DELETE_USERTABLE = "DROP TABLE IF EXISTS ${UserData.TABLE_NAME}"

    object QUESTIONSET : BaseColumns {
        const val TABLE_NAME = "questionset"
        const val COLUMN_NAME_QUESTION = "question"
        const val COLUMN_NAME_OPTIONA = "optionA"
        const val COLUMN_NAME_OPTIONB = "optionB"
        const val COLUMN_NAME_OPTIONC = "optionC"
        const val COLUMN_NAME_OPTIOND = "optionD"
        const val COLUMN_NAME_ANSWER = "answer"
    }

    private val CREATE_QUESTIONSET =
        "CREATE TABLE IF NOT EXISTS ${QUESTIONSET.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${QUESTIONSET.COLUMN_NAME_QUESTION} TEXT," +
                "${QUESTIONSET.COLUMN_NAME_OPTIONA} TEXT," +
                "${QUESTIONSET.COLUMN_NAME_OPTIONB} TEXT," +
                "${QUESTIONSET.COLUMN_NAME_OPTIONC} TEXT," +
                "${QUESTIONSET.COLUMN_NAME_OPTIOND} TEXT," +
                "${QUESTIONSET.COLUMN_NAME_ANSWER} INTEGER)"

    private val DELETE_QUESTIONSET = "DROP TABLE IF EXISTS ${QUESTIONSET.TABLE_NAME}"

    fun getUserData(isguest: Int): User? {
        val projection = arrayOf(
            BaseColumns._ID,
            UserData.COLUMN_NAME_FIRSTNAME,
            UserData.COLUMN_NAME_LASTNAME,
            UserData.COLUMN_NAME_NICKNAME,
            UserData.COLUMN_NAME_AGE,
            UserData.COLUMN_NAME_SCORE,
            UserData.COLUMN_NAME_ISGUEST
        )

        // How you want the results sorted in the resulting Cursor
        val sortOrder = "${BaseColumns._ID} ASC"
        val selection = "${UserData.COLUMN_NAME_ISGUEST} = ?"
        val selectionArgs = arrayOf(isguest.toString())

        val cursor = db.query(
            UserData.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(BaseColumns._ID))
                val firstname = getString(getColumnIndexOrThrow(UserData.COLUMN_NAME_FIRSTNAME))
                val lastname = getString(getColumnIndexOrThrow(UserData.COLUMN_NAME_LASTNAME))
                val nickname = getString(getColumnIndexOrThrow(UserData.COLUMN_NAME_NICKNAME))
                val age = getInt(getColumnIndexOrThrow(UserData.COLUMN_NAME_AGE))
                val score = getInt(getColumnIndexOrThrow(UserData.COLUMN_NAME_SCORE))
                val isguest = getInt(getColumnIndexOrThrow(UserData.COLUMN_NAME_ISGUEST))

                return User(id, firstname, lastname, nickname, age, score,isguest)
            }
        }
        return null
    }

    fun deleteGuest() {
        val selection = "${UserData.COLUMN_NAME_ISGUEST} LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf("1")
        // Issue SQL statement.
        val deletedRows = db.delete(UserData.TABLE_NAME, selection, selectionArgs)
        Log.i("Quiz","Deleted:" + deletedRows)
    }

    fun saveUserData(id: Int?, firstname: String, lastname: String, nickname: String, age: Int, score: Int?, isguest: Int) {

        val values = ContentValues().apply {
            put(UserData.COLUMN_NAME_FIRSTNAME, firstname)
            put(UserData.COLUMN_NAME_LASTNAME, lastname)
            put(UserData.COLUMN_NAME_NICKNAME, nickname)
            put(UserData.COLUMN_NAME_AGE, age)
            put(UserData.COLUMN_NAME_SCORE, score)
            put(UserData.COLUMN_NAME_ISGUEST,isguest)
        }

        if(id == null)
            db?.insert(UserData.TABLE_NAME, null, values)
        else {
            val selection = "${BaseColumns._ID} LIKE ?"
            val selectionArgs = arrayOf(id.toString())

            db?.update(UserData.TABLE_NAME, values, selection, selectionArgs)
        }
    }

    fun saveQuestionSet(questionSet: JSONArray) {
        //db.execSQL(DELETE_USERTABLE)
        db.execSQL(CREATE_USERTABLE) //create user table
        db.execSQL(DELETE_QUESTIONSET) //delete previous quiz question set
        db.execSQL(CREATE_QUESTIONSET) //create quiz question set

        for (i in 0 until questionSet.length()) {
            var jsonObject = questionSet.getJSONObject(i)

            val values = ContentValues().apply {
                put(QUESTIONSET.COLUMN_NAME_QUESTION, jsonObject.getString("Question"))
                put(QUESTIONSET.COLUMN_NAME_OPTIONA, jsonObject.getString("OptionA"))
                put(QUESTIONSET.COLUMN_NAME_OPTIONB, jsonObject.getString("OptionB"))
                put(QUESTIONSET.COLUMN_NAME_OPTIONC, jsonObject.getString("OptionC"))
                put(QUESTIONSET.COLUMN_NAME_OPTIOND, jsonObject.getString("OptionD"))
                put(QUESTIONSET.COLUMN_NAME_ANSWER, jsonObject.getInt("Answer"))
            }

            val newRowId = db?.insert(QUESTIONSET.TABLE_NAME, null, values)
        }
    }

    public fun getQuestionSet(): ArrayList<Question> {
        val projection = arrayOf(
            BaseColumns._ID,
            QUESTIONSET.COLUMN_NAME_QUESTION,
            QUESTIONSET.COLUMN_NAME_OPTIONA,
            QUESTIONSET.COLUMN_NAME_OPTIONB,
            QUESTIONSET.COLUMN_NAME_OPTIONC,
            QUESTIONSET.COLUMN_NAME_OPTIOND,
            QUESTIONSET.COLUMN_NAME_ANSWER
        )

        // How you want the results sorted in the resulting Cursor
        val sortOrder = "${BaseColumns._ID} ASC"

        val cursor = db.query(
            QUESTIONSET.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,//selection,              // The columns for the WHERE clause
            null,//selectionArgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        val questionSet = ArrayList<Question>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(BaseColumns._ID))
                val question = getString(getColumnIndexOrThrow(QUESTIONSET.COLUMN_NAME_QUESTION))
                val optiona = getString(getColumnIndexOrThrow(QUESTIONSET.COLUMN_NAME_OPTIONA))
                val optionb = getString(getColumnIndexOrThrow(QUESTIONSET.COLUMN_NAME_OPTIONB))
                val optionc = getString(getColumnIndexOrThrow(QUESTIONSET.COLUMN_NAME_OPTIONC))
                val optiond = getString(getColumnIndexOrThrow(QUESTIONSET.COLUMN_NAME_OPTIOND))
                val answer = getInt(getColumnIndexOrThrow(QUESTIONSET.COLUMN_NAME_ANSWER))
                questionSet.add(Question(id, question, optiona, optionb, optionc, optiond, answer))
            }
        }
        return questionSet
    }

}