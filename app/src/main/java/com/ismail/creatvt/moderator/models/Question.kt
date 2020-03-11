package com.ismail.creatvt.moderator.models

data class Question(val question:String, val options:List<String>, val correctOption:String, val categoryId:String, val difficultyId:String)