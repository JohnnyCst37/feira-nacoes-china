package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UserSession(
    val uid: String,
    val name: String,
    val email: String,
    val isAdmin: Boolean,
    val escola: String,
    val turma: String,
    val avatarIndex: Int
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuizRepository

    private val _currentUserSession = MutableStateFlow<UserSession?>(null)
    val currentUserSession: StateFlow<UserSession?> = _currentUserSession.asStateFlow()

    val allQuestions: StateFlow<List<QuestionEntity>>
    val allStudents: StateFlow<List<StudentProgressEntity>>
    val rewardVideosHost: StateFlow<List<RewardVideoEntity>>
    val allKnowledgeCards: StateFlow<List<KnowledgeCardEntity>>
    val allStationConfigs: StateFlow<List<StationConfigEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = QuizRepository(database.quizDao())

        // Initial setup
        viewModelScope.launch {
            repository.preseedIfNeeded()
        }

        allQuestions = repository.allQuestions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allStudents = repository.allStudents
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        rewardVideosHost = repository.rewardVideosHost
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allKnowledgeCards = repository.allKnowledgeCards
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allStationConfigs = repository.allStationConfigs
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // Try logging in based on entered form
    fun login(name: String, email: String, escola: String, turma: String, avatarIndex: Int) {
        viewModelScope.launch {
            val normalizedEmail = email.trim().lowercase()
            val isAdmin = normalizedEmail.contains("admin") || normalizedEmail.contains("professor")
            
            val uid = normalizedEmail.hashCode().toString()
            val session = UserSession(
                uid = uid,
                name = name.trim(),
                email = normalizedEmail,
                isAdmin = isAdmin,
                escola = escola.trim().ifBlank { "Escola Estadual" },
                turma = turma.trim().ifBlank { "7º Ano D" },
                avatarIndex = avatarIndex
            )
            
            _currentUserSession.value = session

            // If not admin, register student record in Room DB if not present
            if (!isAdmin) {
                val existing = repository.getStudentProgressByUid(uid)
                if (existing == null) {
                    val initialProgress = StudentProgressEntity(
                        uid = uid,
                        name = session.name,
                        email = session.email,
                        score = 0,
                        xp = 50, // Initial onboarding bonus
                        completed = false,
                        stars = 0,
                        feedbackText = "",
                        badgesRaw = "",
                        escola = session.escola,
                        turma = session.turma,
                        avatarIndex = avatarIndex,
                        unlockedCardsRaw = "",
                        avatarCustomizationRaw = "$avatarIndex|0|0|0" // Default initialized
                    )
                    repository.saveStudentProgress(initialProgress)
                }
            }
        }
    }

    fun logout() {
        _currentUserSession.value = null
    }

    // Save student progress helper
    fun updateStudentProgress(score: Int, xp: Int, badges: List<String>, completed: Boolean = false, stars: Int = 0, feedback: String = "") {
        val session = _currentUserSession.value ?: return
        if (session.isAdmin) return
        
        viewModelScope.launch {
            val existing = repository.getStudentProgressByUid(session.uid)
            val unlockedCardsRaw = existing?.unlockedCardsRaw ?: ""
            val avatarCustomizationRaw = existing?.avatarCustomizationRaw ?: "${session.avatarIndex}|0|0|0"

            val progress = StudentProgressEntity(
                uid = session.uid,
                name = session.name,
                email = session.email,
                score = score,
                xp = xp,
                completed = completed,
                stars = stars,
                feedbackText = feedback,
                badgesRaw = badges.joinToString(","),
                escola = session.escola,
                turma = session.turma,
                avatarIndex = session.avatarIndex,
                unlockedCardsRaw = unlockedCardsRaw,
                avatarCustomizationRaw = avatarCustomizationRaw
            )
            repository.saveStudentProgress(progress)
        }
    }

    // Update avatar customization string directly
    fun updateAvatarCustomization(hairIdx: Int, faceIdx: Int, clothingIdx: Int, colorPaletteIdx: Int) {
        val session = _currentUserSession.value ?: return
        if (session.isAdmin) return

        viewModelScope.launch {
            val existing = repository.getStudentProgressByUid(session.uid) ?: return@launch
            val progress = existing.copy(
                avatarCustomizationRaw = "$hairIdx|$faceIdx|$clothingIdx|$colorPaletteIdx"
            )
            repository.saveStudentProgress(progress)
        }
    }

    // Unlocks a new knowledge card in the student's album
    fun unlockKnowledgeCard(cardId: String) {
        val session = _currentUserSession.value ?: return
        if (session.isAdmin) return

        viewModelScope.launch {
            val existing = repository.getStudentProgressByUid(session.uid) ?: return@launch
            val currentList = existing.unlockedCards.toMutableList()
            if (!currentList.contains(cardId)) {
                currentList.add(cardId)
                val updatedProgress = existing.copy(
                    unlockedCardsRaw = currentList.joinToString(","),
                    xp = existing.xp + 15 // Bonus XP for discovering card
                )
                repository.saveStudentProgress(updatedProgress)
            }
        }
    }

    // Save or update knowledge card
    fun adminSaveKnowledgeCard(card: KnowledgeCardEntity) {
        viewModelScope.launch {
            repository.saveKnowledgeCard(card)
        }
    }

    // Admin commands
    fun adminSaveQuestion(
        idGrupo: Int,
        tema: String,
        pergunta: String,
        opcoes: List<String>,
        respostaCorreta: String,
        videoUrl: String,
        textoExplicativo: String
    ) {
        viewModelScope.launch {
            val q = QuestionEntity(
                id = "grupo_$idGrupo",
                idGrupo = idGrupo,
                tema = tema,
                pergunta = pergunta,
                opcoesRaw = opcoes.joinToString("||"),
                respostaCorreta = respostaCorreta,
                videoFeedbackUrl = videoUrl,
                textoExplicativo = textoExplicativo
            )
            repository.saveQuestion(q)
        }
    }

    fun adminSaveRewardVideos(videos: List<String>) {
        viewModelScope.launch {
            repository.saveRewardVideos(videos)
        }
    }

    fun adminSaveStationConfig(config: StationConfigEntity) {
        viewModelScope.launch {
            repository.saveStationConfig(config)
        }
    }

    // Direct helper to find current student's active live flow from Database
    fun getCurrentStudentProgressFlow(): Flow<StudentProgressEntity?> {
        val session = _currentUserSession.value ?: return flowOf(null)
        if (session.isAdmin) return flowOf(null)
        return repository.getStudentProgressByUidFlow(session.uid)
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return QuizViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
