package com.capstone.gometry.utils

object Constants {
    // Extra data key for passing to other screen
    const val EXTRA_GEOMETRY_ID = "extra_geometry_id"
    const val EXTRA_DETAIL = "extra_detail"
    const val EXTRA_SCORE = "extra_score"
    const val EXTRA_HAVE_PASSED_BEFORE = "extra_have_passed_before"
    const val EXTRA_ACHIEVEMENT_ID = "extra_achievement"
    const val EXTRA_USER = "extra_user"
    const val EXTRA_EXAM_ID = "extra_exam_id"
    const val EXTRA_EXAM_POINT = "extra_exam_point"

    // Reference key for realtime database
    const val REF_QUESTIONS = "questions"
    const val REF_USERS = "users"

    // Reference child path for realtime database
    const val CHILD_GEOMETRY_ID = "geometryId"
    const val CHILD_LEVEL = "level"

    // Level key name
    const val LEVEL_KEY_BEGINNER = "beginner"
    const val LEVEL_KEY_SKILLED = "skilled"
    const val LEVEL_KEY_PROFICIENT = "proficient"

    // Max question to showing
    const val MAX_QUESTION = 5
}