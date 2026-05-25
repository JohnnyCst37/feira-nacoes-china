package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val idGrupo: Int,
    val tema: String,
    val pergunta: String,
    val opcoesRaw: String, // Split by "||"
    val respostaCorreta: String,
    val videoFeedbackUrl: String,
    val textoExplicativo: String
) {
    val opcoes: List<String>
        get() = if (opcoesRaw.isBlank()) emptyList() else opcoesRaw.split("||")
}

@Entity(tableName = "student_progress")
data class StudentProgressEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val score: Int,
    val xp: Int,
    val completed: Boolean,
    val stars: Int,
    val feedbackText: String,
    val badgesRaw: String, // Split by ","
    val escola: String,
    val turma: String,
    val avatarIndex: Int,
    val unlockedCardsRaw: String = "", // Split by ","
    val avatarCustomizationRaw: String = "0|0|0|0" // hair|face|clothing|color
) {
    val badges: List<String>
        get() = if (badgesRaw.isBlank()) emptyList() else badgesRaw.split(",")

    val unlockedCards: List<String>
        get() = if (unlockedCardsRaw.isBlank()) emptyList() else unlockedCardsRaw.split(",")
}

@Entity(tableName = "knowledge_cards")
data class KnowledgeCardEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "PROVERB", "HISTORY", "CURIOSITY", "INSTRUMENT_DANCE"
    val subtitle: String,
    val description: String,
    val imageUrlOrEmoji: String,
    val isCustomAdminAdded: Boolean = false,
    val isPlayerSpecific: Boolean = false,
    val playerName: String = "",
    val school: String = "",
    val className: String = "",
    val dateString: String = "",
    val teacherName: String = "",
    val fairName: String = "Feira das Nações"
)

@Entity(tableName = "reward_videos")
data class RewardVideoEntity(
    @PrimaryKey val id: Int,
    val videoUrl: String
)

@Entity(tableName = "station_configs")
data class StationConfigEntity(
    @PrimaryKey val stationId: Int, // 1 to 5
    val backgroundImageUrl: String = "",
    val backgroundVideoUrl: String = "",
    val melodyUrl: String = "",
    val customTitle: String = "",
    val customSubtitle: String = ""
)

