package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository(private val quizDao: QuizDao) {

    val allQuestions: Flow<List<QuestionEntity>> = quizDao.getAllQuestionsFlow()
    val allStudents: Flow<List<StudentProgressEntity>> = quizDao.getAllStudentProgressFlow()
    val rewardVideosHost: Flow<List<RewardVideoEntity>> = quizDao.getAllRewardVideosFlow()
    val allKnowledgeCards: Flow<List<KnowledgeCardEntity>> = quizDao.getAllKnowledgeCardsFlow()
    val allStationConfigs: Flow<List<StationConfigEntity>> = quizDao.getAllStationConfigsFlow()

    suspend fun saveQuestion(question: QuestionEntity) = withContext(Dispatchers.IO) {
        quizDao.insertQuestion(question)
    }

    suspend fun saveStationConfig(config: StationConfigEntity) = withContext(Dispatchers.IO) {
        quizDao.insertStationConfig(config)
    }

    suspend fun saveKnowledgeCard(card: KnowledgeCardEntity) = withContext(Dispatchers.IO) {
        quizDao.insertKnowledgeCard(card)
    }

    suspend fun saveStudentProgress(progress: StudentProgressEntity) = withContext(Dispatchers.IO) {
        quizDao.insertStudentProgress(progress)
    }

    suspend fun getStudentProgressByUid(uid: String): StudentProgressEntity? = withContext(Dispatchers.IO) {
        quizDao.getStudentProgressByUid(uid)
    }

    fun getStudentProgressByUidFlow(uid: String): Flow<StudentProgressEntity?> {
        return quizDao.getStudentProgressByUidFlow(uid)
    }

    suspend fun saveRewardVideos(urls: List<String>) = withContext(Dispatchers.IO) {
        val list = urls.mapIndexed { idx, url ->
            RewardVideoEntity(id = idx, videoUrl = url)
        }
        quizDao.insertRewardVideos(list)
    }

    suspend fun preseedIfNeeded() = withContext(Dispatchers.IO) {
        // Pre-seed default questions if empty
        val existingQuestions = quizDao.getAllQuestions()
        if (existingQuestions.isEmpty()) {
            val defaults = listOf(
                QuestionEntity(
                    id = "grupo_1",
                    idGrupo = 1,
                    tema = "Chá Imperial e Imigração",
                    pergunta = "Em qual local histórico do Rio de Janeiro a imigração chinesa iniciou o cultivo do chá no início do século XIX, sob incentivo de Dom João VI?",
                    opcoesRaw = "Jardim Botânico do Rio||Parque Lage||Pão de Açúcar||Floresta da Tijuca",
                    respostaCorreta = "Jardim Botânico do Rio",
                    videoFeedbackUrl = "https://www.youtube.com/embed/z12M1bI06iM",
                    textoExplicativo = "O chá da Índia e da China começou a ser plantado no Jardim Botânico e na Fazenda Real de Santa Cruz por colonos chineses contratados em 1812. Esse cultivo é considerado o marco inicial da presença chinesa oficial em solo brasileiro."
                ),
                QuestionEntity(
                    id = "grupo_2",
                    idGrupo = 2,
                    tema = "Festivais das Lanternas e Zodíaco",
                    pergunta = "Qual animal mitológico e elemento representam o ciclo de 2024 na cultura tradicional chinesa, simbolizando vitalidade e progresso científico?",
                    opcoesRaw = "Dragão de Madeira||Serpente de Fogo||Tigre de Terra||Cão de Metal",
                    respostaCorreta = "Dragão de Madeira",
                    videoFeedbackUrl = "https://www.youtube.com/embed/RkL6n327SIs",
                    textoExplicativo = "Os anos na cultura chinesa seguem o calendário solar-lunar e associam animais do zodíaco a cinco elementos e polaridades Yin/Yang. 2024 é consagrado ao Dragão de Madeira, associado a novos começos e força renovadora."
                ),
                QuestionEntity(
                    id = "grupo_3",
                    idGrupo = 3,
                    tema = "Instrumento Acústico Erhu",
                    pergunta = "Qual instrumento chinês de duas cordas tocado com arco possui uma caixa sonora de madeira tradicionalmente encapada com pele de serpente?",
                    opcoesRaw = "Erhu||Pipa||Guzheng||Dizi",
                    respostaCorreta = "Erhu",
                    videoFeedbackUrl = "https://www.youtube.com/embed/R6jYq2-z1c0",
                    textoExplicativo = "O Erhu é um instrumento melódico milenar. Seu timbre expressivo deve-se às duas cordas sintonizadas em intervalos de quinta que vibram a caixa acústica recoberta de pele de cobra, produzindo ondas sonoras marcantes."
                ),
                QuestionEntity(
                    id = "grupo_4",
                    idGrupo = 4,
                    tema = "Dança do Dragão na Feira",
                    pergunta = "Durante as comemorações de rua no Brasil, como o Ano Novo Chinês em SP, a Dança do Dragão é conduzida pelos dançarinos seguindo qual elemento orbital?",
                    opcoesRaw = "Uma pérola de sabedoria flutuante||O mastro imperial||O tambor da noite||A bandeira da dinastia",
                    respostaCorreta = "Uma pérola de sabedoria flutuante",
                    videoFeedbackUrl = "https://www.youtube.com/embed/nU2Wos-v680",
                    textoExplicativo = "Na tradicional Dança do Dragão, os intérpretes sustentam o dragão sobre varas e correm imitando movimentos ondulados e serpenteantes, engajados na perseguição dramática de uma Esfera/Pérola que representa sabedoria e luz espiritual."
                ),
                QuestionEntity(
                    id = "grupo_5",
                    idGrupo = 5,
                    tema = "Sabor das Feiras e Artes Marciais",
                    pergunta = "Qual o famoso salgado brasileiro, muito popular nas feiras, que se originou da adaptação de pratos tradicionais chineses como o rolinho primavera?",
                    opcoesRaw = "O Pastel de Feira||A Coxinha de Frango||O Pão de Queijo||O Rissole de Presunto",
                    respostaCorreta = "O Pastel de Feira",
                    videoFeedbackUrl = "https://www.youtube.com/embed/C5K-lWn_TjM",
                    textoExplicativo = "Na metade do século XX, imigrantes chineses no Brasil adaptaram a massa fina e crocante do rolinho primavera (Spring Roll) para atender ao paladar brasileiro, originando o clássico pastel de feira recheado de carne e queijo de forma frita!"
                )
            )
            quizDao.insertQuestions(defaults)
        }

        val existingVideos = quizDao.getAllRewardVideos()
        if (existingVideos.isEmpty()) {
            val defaultVideos = listOf(
                "https://www.youtube.com/embed/RkL6n327SIs",
                "https://www.youtube.com/embed/z12M1bI06iM",
                "https://www.youtube.com/embed/R6jYq2-z1c0",
                "https://www.youtube.com/embed/nU2Wos-v680"
            )
            val list = defaultVideos.mapIndexed { idx, url ->
                RewardVideoEntity(id = idx, videoUrl = url)
            }
            quizDao.insertRewardVideos(list)
        }

        val existingCards = quizDao.getAllKnowledgeCards()
        if (existingCards.isEmpty()) {
            val defaultCards = listOf(
                // Category: PROVERB
                KnowledgeCardEntity(
                    id = "card_proverb_1",
                    title = "Provérbio da Árvore de Ciências",
                    category = "PROVERB",
                    subtitle = "Saber Natural",
                    description = "Aquele que planta árvores hoje colherá frutos amanhã.\nReflete o princípio chinês de constância e respeito ao tempo natural das descobertas.",
                    imageUrlOrEmoji = "🌳"
                ),
                KnowledgeCardEntity(
                    id = "card_proverb_2",
                    title = "Provérbio da Água Fluida",
                    category = "PROVERB",
                    subtitle = "Resiliência",
                    description = "Água mole em pedra dura tanto bate até que fura.\nSimboliza a força silenciosa da constância e da repetição, ensinamento essencial para cientistas.",
                    imageUrlOrEmoji = "💧"
                ),
                KnowledgeCardEntity(
                    id = "card_proverb_3",
                    title = "Provérbio da Grande Jornada",
                    category = "PROVERB",
                    subtitle = "Primeiros Passos",
                    description = "A jornada de mil milhas começa com um único passo.\nLembra-nos que os maiores aprendizados e as descobertas cósmicas iniciam de forma humilde.",
                    imageUrlOrEmoji = "👣"
                ),
                KnowledgeCardEntity(
                    id = "card_proverb_4",
                    title = "Provérbio da Grande Montanha",
                    category = "PROVERB",
                    subtitle = "Determinação",
                    description = "Quem move montanhas começou carregando pequenas pedras.\nQualquer grande projeto científico ou cultural se baseia no esforço diário acumulado.",
                    imageUrlOrEmoji = "⛰️"
                ),
 
                 // Category: HISTORY
                KnowledgeCardEntity(
                    id = "card_history_1",
                    title = "A Rota do Chá no Rio",
                    category = "HISTORY",
                    subtitle = "Semente da Imigração (1812)",
                    description = "Sob incentivo de Dom João VI, agricultores chineses plantaram Camellia sinensis no Jardim Botânico carioca, deixando um marco científico e cultural no Brasil Império.",
                    imageUrlOrEmoji = "🍵"
                ),
                KnowledgeCardEntity(
                    id = "card_history_2",
                    title = "Ferrovias e Trilhos Científicos",
                    category = "HISTORY",
                    subtitle = "Trabalho Tecnológico",
                    description = "Muitos imigrantes chineses atuaram de forma corajosa na abertura de importantes malhas ferroviárias do Brasil Imperial, expandindo as comunicações nacionais.",
                    imageUrlOrEmoji = "🚂"
                ),
                KnowledgeCardEntity(
                    id = "card_history_3",
                    title = "Invenção do Pastel de Feira",
                    category = "HISTORY",
                    subtitle = "Alquimia Adaptada",
                    description = "Na década de 1940, imigrantes chineses no Brasil adaptaram a massa fina e crocante do Wonton asiático aos recheios brasileiros, criando o lanche nacional de feira.",
                    imageUrlOrEmoji = "🥟"
                ),
                KnowledgeCardEntity(
                    id = "card_history_4",
                    title = "O Tratado Diplomático de 1881",
                    category = "HISTORY",
                    subtitle = "Laços de Amizade",
                    description = "Tratado Imperial de Amizade, Comércio e Navegação entre o Império do Brasil e a Dinastia Qing, oficializando trocas científicas e culturais recíprocas.",
                    imageUrlOrEmoji = "📜"
                ),
 
                 // Category: CURIOSITY
                KnowledgeCardEntity(
                    id = "card_curio_1",
                    title = "A Descoberta Alquímica do Chá",
                    category = "CURIOSITY",
                    subtitle = "Mito Imperial",
                    description = "Diz-se que o imperador chinês Shennong descobriu a bebida em 2737 a.C., quando folhas de chá caíram em sua panela de água fervorosa enquanto descansava.",
                    imageUrlOrEmoji = "🍃"
                ),
                KnowledgeCardEntity(
                    id = "card_curio_2",
                    title = "Mistérios da Lanterna Vermelha",
                    category = "CURIOSITY",
                    subtitle = "Estética e Luz",
                    description = "Originalmente feitas de seda ou papel de arroz na Dinastia Han solar, representavam guia espiritual para monges e eram usadas como ferramenta de comunicação visual.",
                    imageUrlOrEmoji = "🏮"
                ),
                KnowledgeCardEntity(
                    id = "card_curio_3",
                    title = "Zodíaco e Órbita Planetária",
                    category = "CURIOSITY",
                    subtitle = "Astronomia Luni-Solar",
                    description = "O calendário tradicional chinês mescla as órbitas da Lua e do Sol. Cada um dos 12 anos é regido por um animal sob as energias cósmicas de madeira, fogo, terra, metal e água.",
                    imageUrlOrEmoji = "🐉"
                ),
                KnowledgeCardEntity(
                    id = "card_curio_4",
                    title = "Ciência Espacial do Feng Shui",
                    category = "CURIOSITY",
                    subtitle = "Harmonização de Forças",
                    description = "Esta arte antiga estuda a geomancia e a circulação de forças vitais (Chi) nos ambientes de vida de forma a atrair harmonia, prosperidade e saúde física.",
                    imageUrlOrEmoji = "☯️"
                ),
 
                 // Category: INSTRUMENT_DANCE
                KnowledgeCardEntity(
                    id = "card_music_1",
                    title = "Erhu: Vibração e Pele de Píton",
                    category = "INSTRUMENT_DANCE",
                    subtitle = "Acústica Milenar",
                    description = "O violino chinês possui apenas duas cordas. Sua melodia expressiva e ressonância singular devem-se a vibração das cordas sobre uma fina pele de serpente píton.",
                    imageUrlOrEmoji = "🎻"
                ),
                KnowledgeCardEntity(
                    id = "card_music_2",
                    title = "Dança Cósmica do Dragão",
                    category = "INSTRUMENT_DANCE",
                    subtitle = "Teatro Lúdico",
                    description = "Dançarinos impulsionam a criatura mítica perseguindo uma pérola flutuante, que representa a busca acadêmica contínua pela sabedoria e pureza mental.",
                    imageUrlOrEmoji = "🐲"
                ),
                KnowledgeCardEntity(
                    id = "card_music_3",
                    title = "Flauta de Bambu Dizi",
                    category = "INSTRUMENT_DANCE",
                    subtitle = "Física dos Sopros",
                    description = "Flauta transversal chinesa de bambu com um pequeno orifício extra forrado por uma membrana de cana, conferindo seu som nasal, brilhante e festivo.",
                    imageUrlOrEmoji = "🎋"
                )
            )
            quizDao.insertKnowledgeCards(defaultCards)
        }

        val existingConfigs = quizDao.getAllStationConfigs()
        if (existingConfigs.isEmpty()) {
            val defaultConfigs = listOf(
                StationConfigEntity(1, backgroundImageUrl = "", backgroundVideoUrl = "", melodyUrl = "", customTitle = "Jardim Imperial do Chá Realista 3D", customSubtitle = "Sinta o aroma do chá e a névoa das montanhas de Hangzhou"),
                StationConfigEntity(2, backgroundImageUrl = "", backgroundVideoUrl = "", melodyUrl = "", customTitle = "Céu Estrelado de Lanternas 3D", customSubtitle = "Veja os balões de luz aquecida subindo ao cosmo"),
                StationConfigEntity(3, backgroundImageUrl = "", backgroundVideoUrl = "", melodyUrl = "", customTitle = "Templo Acústico de Cordas", customSubtitle = "Vibração harmônica pentatônica de Guzheng & Erhu"),
                StationConfigEntity(4, backgroundImageUrl = "", backgroundVideoUrl = "", melodyUrl = "", customTitle = "Barraca Gastronômica Realista", customSubtitle = "Assando pasteis dourados e crocantes na brasa"),
                StationConfigEntity(5, backgroundImageUrl = "", backgroundVideoUrl = "", melodyUrl = "", customTitle = "Pátio Ancestral Shaolin", customSubtitle = "Pratique ensinamentos milenares com saudações ativas")
            )
            quizDao.insertStationConfigs(defaultConfigs)
        }
    }
}
