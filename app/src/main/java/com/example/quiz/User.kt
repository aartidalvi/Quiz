package com.example.quiz
import java.io.Serializable

class User(
    id: Int,
    firstname: String,
    lastname: String,
    nickname: String,
    age: Int,
    score: Int,
    isguest: Int
) : Serializable {
    var id = id
    var firstname = firstname
    var lastname = lastname
    var nickname = nickname
    var age = age
    var score = score
    var isguest = isguest
}
