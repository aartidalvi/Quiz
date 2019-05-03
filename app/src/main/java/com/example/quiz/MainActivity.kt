package com.example.quiz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.result_layout.*

class MainActivity : AppCompatActivity() {
    lateinit var dbHelper: DatabaseManager
    lateinit var quizSQLHelper: QuizSQLHelper
    private var user: User? = null
    private var guest: User? = null
    private val REQ_CODE = 123
    private var isUserPresent = false
    private var scoreVal: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        quizSQLHelper = QuizSQLHelper(this)
        dbHelper = DatabaseManager(quizSQLHelper)
        uploadQuiz()
    }

    private fun checkUserExists(isGuest: Boolean): Boolean {
        return if (isGuest) {
            guest = dbHelper.getUserData(1)
            guest != null
        } else {
            user = dbHelper.getUserData(0)
            user != null
        }
    }

    private fun uploadQuiz() {
        var questionSet = FileParser().parseQuestions(assets.open("QuestionBank.json"))
        dbHelper.saveQuestionSet(questionSet)
    }

    private fun updateView(user: User?) {
        FirstNameInput.setText(user?.firstname.toString())
        LastNameInput.setText(user?.lastname.toString())
        NickNameInput.setText(user?.nickname.toString())
        AgeInput.setText(user?.age.toString())
        ScoreValue.setText(user?.score.toString())
    }

    override fun onStart() {
        super.onStart()
        isUserPresent = checkUserExists(false)
        if (isUserPresent) {
            updateView(user)
        }
        SignUp.setOnClickListener { startQuiz() }
    }

    override fun onPause() {
        super.onPause()
        if (!(FirstNameInput.text.isNullOrBlank() ||
                    LastNameInput.text.isNullOrBlank() ||
                    NickNameInput.text.isNullOrBlank() ||
                    AgeInput.text.isNullOrBlank())
        ) {
            guest = User(
                -1,
                FirstNameInput.text.toString(),
                LastNameInput.text.toString(),
                NickNameInput.text.toString(),
                AgeInput.text.toString().toInt(),
                scoreVal,
                1
            )

            dbHelper.deleteGuest()
            dbHelper.saveUserData(
                null,
                FirstNameInput.text.toString(),
                LastNameInput.text.toString(),
                NickNameInput.text.toString(),
                AgeInput.text.toString().toInt(),
                scoreVal,
                1
            )
        }

    }

    private fun startQuiz() {
        val goIntent = Intent(this, QuizActivity::class.java)

        if (FirstNameInput.text.isNullOrBlank() ||
            LastNameInput.text.isNullOrBlank() ||
            NickNameInput.text.isNullOrBlank() ||
            AgeInput.text.isNullOrBlank()
        )
            Toast.makeText(applicationContext, "Have you filled all the information?", Toast.LENGTH_SHORT).show()
        else {
            dbHelper.saveUserData(
                user?.id?.toInt(),
                FirstNameInput.text.toString(),
                LastNameInput.text.toString(),
                NickNameInput.text.toString(),
                AgeInput.text.toString().toInt(),
                ScoreValue.text.toString().toInt(),
                0
            )
            startActivityForResult(goIntent, REQ_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkUserExists(true)) {
            updateView(guest)
            guest = null
            dbHelper.deleteGuest()
        } else {
            isUserPresent = checkUserExists(false)
            if (isUserPresent) {
                updateView(user)
            }
        }
    }


    private fun convertNullableToInt(value: Int?): Int {
        if (value == null)
            return 0
        return value.toInt()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            scoreVal = convertNullableToInt(data?.getIntExtra(getString(R.string.result), -1))

            if (scoreVal != -1) {
                var resultText =
                    "Firstname:" + FirstNameInput.text.toString() +
                            "\nLastname:" + LastNameInput.text.toString() +
                            "\nNickName:" + NickNameInput.text.toString() +
                            "\nAge:" + AgeInput.text.toString() +
                            "\nCurrent Result:"

                setContentView(R.layout.result_layout)
                resultTextView.text = resultText + scoreVal
                Done.setOnClickListener { finish() }

                dbHelper.saveUserData(
                    user?.id?.toInt(),
                    FirstNameInput.text.toString(),
                    LastNameInput.text.toString(),
                    NickNameInput.text.toString(),
                    AgeInput.text.toString().toInt(),
                    scoreVal,
                    0
                )

                if (user != null) user?.score = scoreVal
            }
        }
    }
}