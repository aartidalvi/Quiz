package com.example.quiz

import android.app.Activity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.transition.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.question_layout.*
import kotlinx.android.synthetic.main.question_layout.view.*

class QuestionFragment : Fragment() {

    lateinit var currentView : View
    var question: Question? = null
    var answer: Int = -1


    override fun onStart() {
        super.onStart()

        activity?.next?.visibility = View.INVISIBLE

        currentView.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = radioGroup.findViewById<View>(i)
            answer = radioGroup.indexOfChild(radioButton)
            activity?.next?.visibility = View.VISIBLE
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        currentView = inflater.inflate(R.layout.question_layout,container,false);

        question = this.arguments?.getSerializable("Question") as Question?

        currentView.QuestionView.text = question?.question ?: ""
        currentView.radioA.text = question?.optiona ?: ""
        currentView.radioB.text = question?.optionb ?: ""
        currentView.radioC.text = question?.optionc ?: ""
        currentView.radioD.text = question?.optiond ?: ""

        return currentView
    }
}