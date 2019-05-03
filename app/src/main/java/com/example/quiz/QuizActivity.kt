package com.example.quiz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.view.View
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    var currentQuestionIndex = -1
    lateinit var dbHelper: DatabaseManager
    lateinit var quizSQLHelper: QuizSQLHelper
    lateinit var questionSet: ArrayList<Question>
    var fragmentSet = arrayListOf<QuestionFragment>()
    var nextQuestions = arrayListOf<QuestionFragment?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        back.setOnClickListener { loadPrevQuestion() }
        next.setOnClickListener { loadNextQuestion() }

        quizSQLHelper = QuizSQLHelper(this)
        dbHelper = DatabaseManager(quizSQLHelper)
        questionSet = dbHelper.getQuestionSet()
        loadNextQuestion()
    }

    private fun loadPrevQuestion() {
        fragmentManager.executePendingTransactions()
        val currentFragment = fragmentManager.fragments.last() as QuestionFragment
        if (currentFragment != null) {
            fragmentManager.popBackStackImmediate()
            nextQuestions.add(currentFragment)

            if (currentQuestionIndex > 0) {
                currentQuestionIndex -= 1
                val prevQuestion = fragmentManager.fragments.last()
                if (prevQuestion != null) {
                    next.visibility = View.INVISIBLE
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.frameLayout, prevQuestion)
                    fragmentTransaction.commit()
                }
            } else {
                finish()
                currentQuestionIndex = -1
            }
        }
    }

    private fun loadNextQuestion() {
        currentQuestionIndex += 1

        var questionFragment = QuestionFragment()

        when {
            currentQuestionIndex == questionSet.size -> { //if all questions are done
                val toPassBack = intent
                toPassBack.putExtra(getString(R.string.result), countResult())
                setResult(RESULT_OK, toPassBack)
                finish()
                return
            }
            nextQuestions.isNotEmpty() -> { //if user has come here after prev navigation, retrieve current answers
                questionFragment = nextQuestions.last() as QuestionFragment
                nextQuestions.removeAt(nextQuestions.lastIndex)
            }
            else -> { //if user is loading question for the first time
                val args = Bundle()
                args.putSerializable(getString(R.string.question), questionSet[currentQuestionIndex])
                questionFragment.arguments = args
            }
        }
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, questionFragment)
        fragmentTransaction.addToBackStack(currentQuestionIndex.toString())
        fragmentTransaction.commit()

        fragmentSet.add(currentQuestionIndex, questionFragment)
    }

    private fun countResult(): Int {
        fragmentManager.executePendingTransactions()
        var result = 0

        for (i in 0 until questionSet.size) {
            val userAnswer = fragmentSet[i].answer//fragmentQuestion.answer
            val correctAnswer = questionSet[i].answer

            if (userAnswer == correctAnswer)
                result += 1
        }
        return result
    }
}
