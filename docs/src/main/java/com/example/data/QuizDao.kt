package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

    @Query("SELECT * FROM questions ORDER BY idGrupo ASC")
    fun getAllQuestionsFlow(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions ORDER BY idGrupo ASC")
    suspend fun getAllQuestions(): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT * FROM student_progress")
    fun getAllStudentProgressFlow(): Flow<List<StudentProgressEntity>>

    @Query("SELECT * FROM student_progress")
    suspend fun getAllStudentProgress(): List<StudentProgressEntity>

    @Query("SELECT * FROM student_progress WHERE uid = :uid LIMIT 1")
    suspend fun getStudentProgressByUid(uid: String): StudentProgressEntity?

    @Query("SELECT * FROM student_progress WHERE uid = :uid LIMIT 1")
    fun getStudentProgressByUidFlow(uid: String): Flow<StudentProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentProgress(progress: StudentProgressEntity)

    @Query("SELECT * FROM reward_videos ORDER BY id ASC")
    fun getAllRewardVideosFlow(): Flow<List<RewardVideoEntity>>

    @Query("SELECT * FROM reward_videos ORDER BY id ASC")
    suspend fun getAllRewardVideos(): List<RewardVideoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewardVideos(videos: List<RewardVideoEntity>)

    @Query("SELECT * FROM knowledge_cards ORDER BY id ASC")
    fun getAllKnowledgeCardsFlow(): Flow<List<KnowledgeCardEntity>>

    @Query("SELECT * FROM knowledge_cards ORDER BY id ASC")
    suspend fun getAllKnowledgeCards(): List<KnowledgeCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledgeCard(card: KnowledgeCardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledgeCards(cards: List<KnowledgeCardEntity>)

    @Query("SELECT * FROM station_configs ORDER BY stationId ASC")
    fun getAllStationConfigsFlow(): Flow<List<StationConfigEntity>>

    @Query("SELECT * FROM station_configs ORDER BY stationId ASC")
    suspend fun getAllStationConfigs(): List<StationConfigEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStationConfig(config: StationConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStationConfigs(configs: List<StationConfigEntity>)
}
