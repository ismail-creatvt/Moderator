package com.ismail.creatvt.moderator.models

data class QuestionObject(
    var id: String = "",
    var question: Question = Question(),
    var option1: String = "",
    var option2: String = "",
    var option3: String = "",
    var option4: String = "",
    var correct_option: String = ""
) {
    fun getOption(index: Int) = when (index) {
        0 -> option1
        1 -> option2
        2 -> option3
        else -> option4
    }
}

data class Question(var text: String = "")