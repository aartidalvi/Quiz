package com.example.quiz

import java.io.Serializable

class Question(
    id: Int,
    question: String,
    optiona: String,
    optionb: String,
    optionc: String,
    optiond: String,
    answer: Int
) : Serializable {
    var id = id
    var question = question
    var optiona = optiona
    var optionb = optionb
    var optionc = optionc
    var optiond = optiond
    var answer = answer
}
