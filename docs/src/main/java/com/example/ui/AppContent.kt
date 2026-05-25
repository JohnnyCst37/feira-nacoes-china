package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.QuestionEntity
import com.example.data.StudentProgressEntity
import com.example.data.KnowledgeCardEntity
import com.example.data.StationConfigEntity
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Path
import com.example.ui.components.VideoPlayer
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

val AVATARS = listOf(
    "👦 Mini-Imperador" to "👦",
    "👧 Princesa do Chá" to "👧",
    "🥋 Mestre Kung-Fu" to "🥋",
    "🐉 Guerreiro Dragão" to "🐉",
    "🐼 Filósofo Panda" to "🐼",
    "🎋 Sábio do Bambu" to "🎋"
)

val PROVERB_PASSWORDS = listOf(
    "A jornada de mil milhas começa com um único passo.",
    "O professor abre a porta, mas você deve entrar por si mesmo.",
    "A persistência realiza o impossível.",
    "Lembre-se de cavar o poço antes de sentir sede.",
    "O sábio aprende com os erros dos outros, o tolo com os seus próprios."
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppContent(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = viewModel()
) {
    val userSession by viewModel.currentUserSession.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ChineseDark)
    ) {
        if (userSession == null) {
            LoginFlowScreen(
                onLoginCompleted = { name, email, escola, turma, avatarIdx ->
                    viewModel.login(name, email, escola, turma, avatarIdx)
                }
            )
        } else {
            val session = userSession!!
            if (session.isAdmin) {
                AdminPanelScreen(
                    session = session,
                    viewModel = viewModel,
                    onLogout = { viewModel.logout() }
                )
            } else {
                StudentJourneyScreen(
                    session = session,
                    viewModel = viewModel,
                    onLogout = { viewModel.logout() }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 1: LOGIN FLOW & METAVERSE SPLASH
// -------------------------------------------------------------

@Composable
fun LoginFlowScreen(
    onLoginCompleted: (String, String, String, String, Int) -> Unit
) {
    var flowStage by remember { mutableStateOf("lanterns") } // "lanterns" -> "dragon" -> "form"
    
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var schoolInput by remember { mutableStateOf("Escola Estadual Prof. Johnny") }
    var classInput by remember { mutableStateOf("7º Ano D") }
    var selectedAvatarIndex by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "compass_spin"
    )

    LaunchedEffect(Unit) {
        delay(2000)
        flowStage = "dragon"
        delay(2000)
        flowStage = "form"
    }

    AnimatedContent(
        targetState = flowStage,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
        },
        modifier = Modifier.fillMaxSize()
    ) { stage ->
        when (stage) {
            "lanterns" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = DividerGoldColor,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(4f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Bússola",
                            tint = ChineseGold,
                            modifier = Modifier
                                .size(70.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Festival das Lanternas",
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = ChineseGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("festival_title")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sabedoria • Harmonia • Tradição",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ChineseRed,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            "dragon" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🐉",
                        fontSize = 80.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.animateContentSize()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ano Novo Chinês 2024",
                        fontSize = 30.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = ChineseRed,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "CICLO DO DRAGÃO DE MADEIRA",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ChineseGold,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            "form" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 500.dp)
                            .shadow(16.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = ChineseGray),
                        border = BorderStroke(1.dp, DividerGoldColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🏮 Feira das Nações 🏮",
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Serif,
                                color = ChineseGold,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Jornada Pedagógica de Ciências e Cultura",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 4.dp),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Escolha seu Avatar de Viagem:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChineseGold,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            var showOnboardingScanner by remember { mutableStateOf(false) }
                            var scannerHair by remember { mutableStateOf(0) }
                            var scannerClothes by remember { mutableStateOf(0) }

                            if (showOnboardingScanner) {
                                IAnanoScannerDialog(
                                    onDismissRequest = { showOnboardingScanner = false },
                                    onDigitized = { hair, clothes, face ->
                                        scannerHair = hair
                                        scannerClothes = clothes
                                        selectedAvatarIndex = 99 // Digitized selection code
                                        showOnboardingScanner = false
                                    }
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AVATARS.forEachIndexed { idx, pair ->
                                    val isSelected = selectedAvatarIndex == idx
                                    Column(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) ChineseRedDark else Color(0x11FFFFFF))
                                            .border(
                                                1.dp,
                                                if (isSelected) ChineseGold else Color.Transparent,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { selectedAvatarIndex = idx }
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = pair.second, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = pair.first.substringBefore(" "), fontSize = 11.sp, color = Color.White)
                                    }
                                }

                                if (selectedAvatarIndex == 99) {
                                    Column(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(ChineseRedDark)
                                            .border(
                                                1.dp,
                                                ChineseGold,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { selectedAvatarIndex = 99 }
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CustomizableAvatarVisual(
                                            hairIdx = scannerHair,
                                            faceIdx = 99,
                                            clothingIdx = scannerClothes,
                                            colorIdx = 1,
                                            size = 28.dp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Digitalizado", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }

                            Button(
                                onClick = { showOnboardingScanner = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp)
                             ) {
                                 Icon(imageVector = Icons.Default.Face, contentDescription = "Simular Foto", tint = Color.Black)
                                 Spacer(modifier = Modifier.width(8.dp))
                                 Text("📸 Tirar Foto & Digitalizar com IA Nano", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                             }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Nome Completo") },
                                textStyle = LocalTextStyle.current.copy(color = Color.White),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChineseGold,
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedLabelColor = ChineseGold,
                                    unfocusedLabelColor = Color.Gray
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("E-mail Escolar") },
                                placeholder = { Text("professor@escola.com para admin") },
                                textStyle = LocalTextStyle.current.copy(color = Color.White),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChineseGold,
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedLabelColor = ChineseGold,
                                    unfocusedLabelColor = Color.Gray
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = schoolInput,
                                onValueChange = { schoolInput = it },
                                label = { Text("Nome da Escola") },
                                textStyle = LocalTextStyle.current.copy(color = Color.White),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChineseGold,
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedLabelColor = ChineseGold,
                                    unfocusedLabelColor = Color.Gray
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = classInput,
                                onValueChange = { classInput = it },
                                label = { Text("Turma / Classe") },
                                textStyle = LocalTextStyle.current.copy(color = Color.White),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChineseGold,
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedLabelColor = ChineseGold,
                                    unfocusedLabelColor = Color.Gray
                                )
                            )

                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage!!,
                                    color = ChineseRed,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (nameInput.isBlank() || emailInput.isBlank()) {
                                        errorMessage = "Por favor, preencha o Nome e E-mail escolar."
                                    } else {
                                        onLoginCompleted(nameInput, emailInput, schoolInput, classInput, selectedAvatarIndex)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("submit_login_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = "Caminhar")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Entrar no Metaverso",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Dica: E-mail com 'professor' ou 'admin' abre o painel de mídia.",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 2: STUDENT PORTAL (METAVERSE & 5 WORLDS MAP)
// -------------------------------------------------------------

@Composable
fun StudentJourneyScreen(
    session: UserSession,
    viewModel: QuizViewModel,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allQuestions by viewModel.allQuestions.collectAsState()
    val rewardVideos by viewModel.rewardVideosHost.collectAsState()
    
    val dbProfileFlow = remember(session.uid) { viewModel.getCurrentStudentProgressFlow() }
    val dbProfile by dbProfileFlow.collectAsState(initial = null)

    val xp = dbProfile?.xp ?: 50
    val score = dbProfile?.score ?: 0
    val earnedBadgesRaw = dbProfile?.badgesRaw ?: ""
    val earnedBadges = earnedBadgesRaw.split(",").filter { it.isNotBlank() }
    val isOverallCompleted = dbProfile?.completed ?: false

    val allKnowledgeCards by viewModel.allKnowledgeCards.collectAsState()
    val unlockedCardIds = dbProfile?.unlockedCards ?: emptyList()
    val avatarCustomizationRaw = dbProfile?.avatarCustomizationRaw ?: "${session.avatarIndex}|0|0|0"

    // Navigation Tab state
    var activeTab by remember { mutableStateOf("explore") }

    var activeStationIndex by remember { mutableStateOf<Int?>(null) }
    var selectedPasswordOption by remember { mutableStateOf<String?>(null) }
    var passwordPortalUnlocked by remember { mutableStateOf(false) }
    var isWalkingSimRunning by remember { mutableStateOf(false) }
    var walkPercentage by remember { mutableStateOf(0f) }

    var selectedQuizOption by remember { mutableStateOf<String?>(null) }
    var showExplanationModal by remember { mutableStateOf(false) }
    var explanationVideoUrl by remember { mutableStateOf("") }
    var explanationTextByGroup by remember { mutableStateOf("") }

    var soundGameAnswer by remember { mutableStateOf<String?>(null) }
    var soundFeedbackText by remember { mutableStateOf("") }

    var mythIndex by remember { mutableStateOf(0) }
    var mythScore by remember { mutableStateOf(0) }
    var mythVotedTrueOrFalse by remember { mutableStateOf<Boolean?>(null) }
    var mythFeedbackText by remember { mutableStateOf("") }
    var isStation5CompletedState by remember { mutableStateOf(false) }

    var guideSpeech by remember { mutableStateOf("") }

    val startWalkSimulation = { stationNum: Int ->
        coroutineScope.launch {
            isWalkingSimRunning = true
            walkPercentage = 0f
            while (walkPercentage < 1.0f) {
                delay(60)
                walkPercentage += 0.05f
            }
            isWalkingSimRunning = false
            passwordPortalUnlocked = true
        }
    }

    LaunchedEffect(dbProfile, activeTab) {
        if (dbProfile != null) {
            val count = earnedBadges.size
            guideSpeech = when (activeTab) {
                "metaverse_grid" -> "Sinta o vento da Feira das Nações! Use as setas de comando ▲ ▼ ◄ ► para locomover-se e toque nas bancas ou personagens para receber desafios trívias!"
                "cards" -> "Espetacular! Aqui jaz o seu Livro Imperial de Selos de Conhecimento. Você desbloqueou ${unlockedCardIds.size} de ${allKnowledgeCards.size} cartas!"
                "avatar" -> "Sintonize os elementos estéticos de seu herói! Cada mudança se reflete na feira e no cabeçalho em tempo real."
                "mythic_cards" -> "Gere as fantásticas Cartas Míticas Imperiais com inteligência artificial! Insira seus dados para atrair prosperidade, harmonia e autoconhecimento."
                else -> {
                    when {
                        isOverallCompleted -> "Grandioso! Você se tornou uma Lenda Imperial! Explore suas Cartas de Conhecimento e mude seu visual."
                        count == 5 -> "Parabéns! Todas as 5 Estações foram concluídas. Vá para o final do pergaminho para cunhar sua Carta Chinesa de Ciências!"
                        count == 4 -> "Incrível! Falta apenas o desafio dos Combates e Alquimia Comestível. Visite o Sábio Chen."
                        count == 2 -> "Excelente! Continue caminhando com seu avatar pela feira. A música e os tambores te esperam à frente."
                        else -> "Saudações, ${session.name}! Para acessar as estações, use as Frases Passes que são provérbios de sabedoria. Toque na Estação 1 para começar!"
                    }
                }
            }
        } else {
            guideSpeech = "Preparando mapa... Que a sabedoria chinesa guie sua mente científica."
        }
    }

    Scaffold(
        topBar = {
            HeaderBar(
                name = session.name,
                escola = session.escola,
                turma = session.turma,
                xp = xp,
                badges = earnedBadges,
                avatarCustomizationRaw = avatarCustomizationRaw,
                onLogout = onLogout
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = ChineseGray,
                contentColor = ChineseGold,
                modifier = Modifier.border(1.dp, Color(0x33D4AF37), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                NavigationBarItem(
                    selected = activeTab == "explore",
                    onClick = { activeTab = "explore" },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Explorar") },
                    label = { Text("Mundos Map", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ChineseGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = ChineseGold
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "metaverse_grid",
                    onClick = { activeTab = "metaverse_grid" },
                    icon = { Icon(imageVector = Icons.Default.Place, contentDescription = "Metaverso") },
                    label = { Text("Explorar Feira", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ChineseGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = ChineseGold
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "cards",
                    onClick = { activeTab = "cards" },
                    icon = { Icon(imageVector = Icons.Default.Star, contentDescription = "Cards") },
                    label = { Text("Álbum Crachás", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ChineseGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = ChineseGold
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "avatar",
                    onClick = { activeTab = "avatar" },
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Avatar") },
                    label = { Text("Customizar", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ChineseGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = ChineseGold
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "mythic_cards",
                    onClick = { activeTab = "mythic_cards" },
                    icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "Prêmio Mítico") },
                    label = { Text("Prêmio Mítico", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ChineseGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = ChineseGold
                    )
                )
            }
        },
        containerColor = ChineseDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ChineseDark)
        ) {
            MestreGuideBubble(speech = guideSpeech, avatarEmoji = AVATARS[session.avatarIndex].second)

            if (activeTab == "metaverse_grid") {
                Box(modifier = Modifier.fillMaxSize()) {
                    MetaverseFairGridScreen(
                        unlockedCardsList = unlockedCardIds,
                        avatarCustomizationRaw = avatarCustomizationRaw,
                        onUnlockCard = { cardId ->
                            viewModel.unlockKnowledgeCard(cardId)
                        }
                    )
                }
            } else if (activeTab == "cards") {
                Box(modifier = Modifier.fillMaxSize()) {
                    KnowledgeCardAlbumScreen(
                        allCards = allKnowledgeCards,
                        unlockedCardIds = unlockedCardIds,
                        studentName = session.name,
                        studentEscola = session.escola,
                        studentTurma = session.turma
                    )
                }
            } else if (activeTab == "avatar") {
                Box(modifier = Modifier.fillMaxSize()) {
                    AvatarCustomizerPanel(
                        currentCustomization = avatarCustomizationRaw,
                        onSaveRequested = { hair, face, clothes, color ->
                            viewModel.updateAvatarCustomization(hair, face, clothes, color)
                        }
                    )
                }
            } else if (activeTab == "mythic_cards") {
                Box(modifier = Modifier.fillMaxSize()) {
                    MythicCardGeneratorScreen(
                        session = session,
                        currentXp = xp,
                        onAddXp = { newXp ->
                            viewModel.updateStudentProgress(
                                score = score,
                                xp = newXp,
                                badges = earnedBadges,
                                completed = isOverallCompleted,
                                feedback = ""
                            )
                        }
                    )
                }
            } else if (isOverallCompleted) {
                CompletedJourneyView(
                    session = session,
                    score = score,
                    xp = xp,
                    rewardVideos = rewardVideos.map { it.videoUrl },
                    onFinished = { rating, msg ->
                        viewModel.updateStudentProgress(
                            score = score,
                            xp = xp,
                            badges = earnedBadges,
                            completed = true,
                            stars = rating,
                            feedback = msg
                        )
                    }
                )
            } else if (activeStationIndex == null) {
                StudentMapDashboard(
                    earnedBadges = earnedBadges,
                    onStationClicked = { num ->
                        activeStationIndex = num
                        selectedPasswordOption = null
                        passwordPortalUnlocked = false
                        selectedQuizOption = null
                        showExplanationModal = false
                        
                        soundGameAnswer = null
                        soundFeedbackText = ""
                        mythIndex = 0
                        mythScore = 0
                        mythVotedTrueOrFalse = null
                        isStation5CompletedState = false
                    },
                    onGenerateImperialCard = {
                        viewModel.updateStudentProgress(
                            score = score,
                            xp = xp + 150,
                            badges = earnedBadges + "badge_final",
                            completed = true,
                            stars = 5,
                            feedback = "Concluiu no Metaverso!"
                        )
                    }
                )
            } else {
                val activeNum = activeStationIndex!!
                val currentQuestion = allQuestions.find { it.idGrupo == activeNum }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!passwordPortalUnlocked) {
                        PasswordPortalGate(
                            stationNum = activeNum,
                            onOptionSelected = { proverb ->
                                selectedPasswordOption = proverb
                                val indexExpected = (activeNum - 1).coerceIn(0, 4)
                                val expected = PROVERB_PASSWORDS[indexExpected]
                                if (proverb == expected) {
                                    startWalkSimulation(activeNum)
                                } else {
                                    guideSpeech = "Este provérbio não abre os selos da Estação $activeNum. Tente outra filosofia antiga!"
                                }
                            },
                            onBackToMap = { activeStationIndex = null }
                        )
                    } else if (isWalkingSimRunning) {
                        WalkSimulatorScreen(
                            avatarEmoji = AVATARS[session.avatarIndex].second,
                            progress = walkPercentage,
                            stationNum = activeNum
                        )
                    } else {
                        ImmersiveStationWorldContainer(
                            stationNum = activeNum,
                            viewModel = viewModel,
                            onBack = { activeStationIndex = null }
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = 600.dp)
                                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = ChineseGray),
                                border = BorderStroke(1.dp, DividerGoldColor)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Estação $activeNum",
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = ChineseRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                        IconButton(onClick = { activeStationIndex = null }) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = Color.Gray)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    CharacterIntroCard(stationNum = activeNum)

                                    Spacer(modifier = Modifier.height(16.dp))

                                    when (activeNum) {
                                        1, 2, 4 -> {
                                            if (currentQuestion == null) {
                                                Text(
                                                    text = "Aguardando o professor carregar as perguntas desta estação.",
                                                    color = Color.LightGray,
                                                    textAlign = TextAlign.Center
                                                )
                                            } else {
                                                QuizTaskView(
                                                    question = currentQuestion,
                                                    selectedOption = selectedQuizOption,
                                                    onOptionSelected = { op ->
                                                        selectedQuizOption = op
                                                        val isCorrect = op == currentQuestion.respostaCorreta
                                                        val xpBonus = if (isCorrect) 150 else 50
                                                        val badgeId = "badge_$activeNum"
                                                        val nextBadgesList = if (earnedBadges.contains(badgeId)) earnedBadges else (earnedBadges + badgeId)
                                                        
                                                        viewModel.updateStudentProgress(
                                                            score = score + (if (isCorrect) 1 else 0),
                                                            xp = xp + xpBonus,
                                                            badges = nextBadgesList
                                                        )

                                                        explanationVideoUrl = currentQuestion.videoFeedbackUrl
                                                        explanationTextByGroup = currentQuestion.textoExplicativo
                                                        showExplanationModal = true
                                                    }
                                                )
                                            }
                                        }
                                        3 -> {
                                            MusicTimbreGameView(
                                                selectedAnswer = soundGameAnswer,
                                                feedbackText = soundFeedbackText,
                                                onAnswerSubmit = { answer ->
                                                    soundGameAnswer = answer
                                                    val isCorrect = answer == "B"
                                                    val xpBonus = if (isCorrect) 150 else 50
                                                    val badgeId = "badge_3"
                                                    val nextBadgesList = if (earnedBadges.contains(badgeId)) earnedBadges else (earnedBadges + badgeId)
                                                    
                                                    viewModel.updateStudentProgress(
                                                        score = score + (if (isCorrect) 1 else 0),
                                                        xp = xp + xpBonus,
                                                        badges = nextBadgesList
                                                    )

                                                    soundFeedbackText = if (isCorrect) {
                                                        "Fantástico! Seu ouvido harmônico é excelente. O Erhu é sintonizado em quintas e ressoa comovente. Assista ao vídeo de física acústica do grupo 3!"
                                                    } else {
                                                        "Ops! O som A era a Guzheng (cítara dedilhada rápida). O Erhu era o som B, tocado com arco com rica modulação. Veja o vídeo explicativo!"
                                                    }

                                                    explanationVideoUrl = currentQuestion?.videoFeedbackUrl ?: "https://www.youtube.com/embed/R6jYq2-z1c0"
                                                    explanationTextByGroup = currentQuestion?.textoExplicativo ?: "O grupo de ciências explicou como a espessura das cordas e a elasticidade regulam a frequência das ondas acústicas residuais no Erhu."
                                                    showExplanationModal = true
                                                },
                                                onWatchExplained = {
                                                    showExplanationModal = true
                                                }
                                            )
                                        }
                                        5 -> {
                                            MythCarouselGame(
                                                index = mythIndex,
                                                votedTrueOrFalse = mythVotedTrueOrFalse,
                                                feedback = mythFeedbackText,
                                                onAnswer = { userVote ->
                                                    mythVotedTrueOrFalse = userVote
                                                    val currentMyth = MYTHS_LIST[mythIndex]
                                                    val isCorrect = userVote == currentMyth.isTrue
                                                    if (isCorrect) mythScore++

                                                    mythFeedbackText = if (isCorrect) {
                                                        "Esplêndido! ${currentMyth.explanation}"
                                                    } else {
                                                        "Incorreto! ${currentMyth.explanation}"
                                                    }
                                                },
                                                onNext = {
                                                    if (mythIndex + 1 < MYTHS_LIST.size) {
                                                        mythIndex++
                                                        mythVotedTrueOrFalse = null
                                                        mythFeedbackText = ""
                                                    } else {
                                                        isStation5CompletedState = true
                                                        val badgeId = "badge_5"
                                                        val nextBadgesList = if (earnedBadges.contains(badgeId)) earnedBadges else (earnedBadges + badgeId)
                                                        
                                                        viewModel.updateStudentProgress(
                                                            score = score + (if (mythScore >= 2) 1 else 0),
                                                            xp = xp + 150,
                                                            badges = nextBadgesList
                                                        )

                                                        explanationVideoUrl = currentQuestion?.videoFeedbackUrl ?: "https://www.youtube.com/embed/C5K-lWn_TjM"
                                                        explanationTextByGroup = currentQuestion?.textoExplicativo ?: "Nas artes marciais (Wushu/Kung Fu), a agilidade e contração muscular dependem da rápida oxigenação celular promovida pelo sistema cardiorrespiratório!"
                                                        showExplanationModal = true
                                                    }
                                                },
                                                finished = isStation5CompletedState
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showExplanationModal) {
        AlertDialog(
            onDismissRequest = {
                showExplanationModal = false
                activeStationIndex = null
            },
            title = {
                Text(
                    text = "📖 Feedback do Grupo de Ciências",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    color = ChineseGold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (explanationVideoUrl.isNotBlank()) {
                        Text(
                            text = "Assista à explicação gravada pelos alunos:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        VideoPlayer(
                            videoUrl = explanationVideoUrl,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Text(
                        text = "Explanação do Trabalho Editorial:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChineseGold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = explanationTextByGroup.ifBlank { "O grupo organizou este painel focado em documentar a imigração, as ciências culinárias antigas e a tecnologia instrumental dos chás e folclores integrados nos polos do Brasil." },
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExplanationModal = false
                        activeStationIndex = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChineseRed)
                ) {
                    Text("Continuar Jornada (+150 XP)", color = Color.White)
                }
            },
            containerColor = ChineseGray,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(8.dp)
        )
    }
}

// -------------------------------------------------------------
// MUSIC TIMBRE AUDIO SYNTH GAME
// -------------------------------------------------------------

@Composable
fun MusicTimbreGameView(
    selectedAnswer: String?,
    feedbackText: String,
    onAnswerSubmit: (String) -> Unit,
    onWatchExplained: () -> Unit
) {
    val coroutine = rememberCoroutineScope()
    var playingSound by remember { mutableStateOf<String?>(null) }

    Text(
        text = "Física Acústica & Timbre",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = ChineseGold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = "A ressonância por vibração de cordas varia em frequência e amplitude. Ouça sintetizadores das ondas reais obtidos de materiais acústicos da Ásia e diga: Qual é o som típico do Erhu (violino chinês de 2 cordas)?",
        fontSize = 13.sp,
        color = Color.LightGray,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color(0x0AFFFFFF), RoundedCornerShape(16.dp))
                .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Música A", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    playingSound = "A"
                    coroutine.launch {
                        SoundSynthesizer.playPipaSound()
                        playingSound = null
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (playingSound == "A") ChineseGold else ChineseRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Som Pipa")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tocar Guzheng", fontSize = 11.sp, color = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color(0x0AFFFFFF), RoundedCornerShape(16.dp))
                .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Música B", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    playingSound = "B"
                    coroutine.launch {
                        SoundSynthesizer.playErhuSound()
                        playingSound = null
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (playingSound == "B") ChineseGold else ChineseRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Som Erhu")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tocar Erhu", fontSize = 11.sp, color = Color.White)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (selectedAnswer == null) {
        Text("Dê seu veredito acústico:", fontSize = 12.sp, color = ChineseGold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onAnswerSubmit("A") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.weight(1f)
            ) {
                Text("A é Erhu", color = Color.White)
            }
            Button(
                onClick = { onAnswerSubmit("B") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.weight(1f)
            ) {
                Text("B é Erhu", color = Color.White)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x11FFFFFF), RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = feedbackText,
                fontSize = 13.sp,
                color = if (selectedAnswer == "B") ChineseJade else Color(0xFFFF6347),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onWatchExplained,
                colors = ButtonDefaults.buttonColors(containerColor = ChineseGold)
            ) {
                Text("Ver Vídeo de Física de Ondas", color = Color.Black)
            }
        }
    }
}

// -------------------------------------------------------------
// STUDENT MAP & MILESTONES
// -------------------------------------------------------------

@Composable
fun StudentMapDashboard(
    earnedBadges: List<String>,
    onStationClicked: (Int) -> Unit,
    onGenerateImperialCard: () -> Unit
) {
    val totalProgress = earnedBadges.size / 5f
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 700.dp)
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ChineseGray),
            shape = RoundedCornerShape(24.dp), // rounded-3xl
            border = BorderStroke(1.dp, Color(0x0DFFFFFF)) // border border-white/5
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "MUNDO ATUAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0x66FFFFFF), // text-white/40
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "1. Estações da Feira Chinesa",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = ChineseGold, // text-[#D4AF37]
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Text(
                        text = "0${earnedBadges.size} / 05",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0x99FFFFFF) // text-white/60
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = totalProgress,
                    color = ChineseGold, // text-[#D4AF37] progress
                    trackColor = Color(0x0DFFFFFF), // white/5
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                )
            }
        }

        Text(
            text = "Mapa do Metaverso - Feira Chinesa",
            fontSize = 18.sp,
            fontFamily = FontFamily.Serif,
            color = ChineseGold,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val stationTitles = listOf(
            "1: Imigração e Chá Imperial (Rio)" to "🗺️ Sr. Wang",
            "2: Festivais Luminosos e Zodíaco" to "🏮 Dona Mei",
            "3: Música Clássica e Acústica" to "🎵 Mestre Lee",
            "4: Dança folclórica do Leão/Dragão" to "🦁 Gabriela",
            "5: Combates Tradicionais e Alquimia" to "⚔️ Sábio Chen"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (i in 1..5) {
                val badgeRequired = "badge_$i"
                val isCompleted = earnedBadges.contains(badgeRequired)
                val isUnlocked = i == 1 || earnedBadges.contains("badge_${i - 1}")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isCompleted) Color(0x0F10B981) 
                            else if (isUnlocked) ChineseGray 
                            else Color(0x880D0E11)
                        )
                        .border(
                            1.dp,
                            if (isCompleted) Color(0x4410B981) 
                            else if (isUnlocked) Color(0x66D4AF37) // Golden highlight border-D4AF37/40
                            else Color(0x0DFFFFFF), // border white/5
                            RoundedCornerShape(16.dp)
                        )
                        .clickable(enabled = isUnlocked) { onStationClicked(i) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val badgeBrush = if (isCompleted) {
                        Brush.linearGradient(listOf(ChineseJade, Color(0xFF059669)))
                    } else if (isUnlocked) {
                        Brush.linearGradient(listOf(ChineseRed, ChineseRedDark))
                    } else {
                        Brush.linearGradient(listOf(Color(0xFF2A2A2A), Color(0xFF1E1E1E)))
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(badgeBrush),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Completo", tint = Color.Black)
                        } else if (!isUnlocked) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.Gray)
                        } else {
                            Text(text = "$i", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Estação " + stationTitles[i - 1].first,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color.White else Color.Gray
                        )
                        Text(
                            text = "Apresentação: " + stationTitles[i - 1].second,
                            fontSize = 12.sp,
                            color = if (isUnlocked) ChineseGold else Color.Gray
                        )
                    }

                    if (isUnlocked && !isCompleted) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Explorar", tint = ChineseGold)
                    }
                }
            }
        }

        if (earnedBadges.size >= 5) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onGenerateImperialCard,
                colors = ButtonDefaults.buttonColors(containerColor = ChineseGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 450.dp)
                    .height(56.dp)
                    .testTag("reward_card_badge_creation"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Star, contentDescription = "Certificado", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cunhar Carta Imperial Chinesa",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 3: COMPLETED JOURNEY VIEW (REWARDS CERTIFICATE GENERATOR)
// -------------------------------------------------------------

@Composable
fun CompletedJourneyView(
    session: UserSession,
    score: Int,
    xp: Int,
    rewardVideos: List<String>,
    onFinished: (Int, String) -> Unit
) {
    var currentRating by remember { mutableStateOf(5) }
    var userMessageText by remember { mutableStateOf("") }
    var selectedReportCardIndex by remember { mutableStateOf(0) }
    var generatedRewardVideo by remember { mutableStateOf("") }
    var savedSuccessfullyInSession by remember { mutableStateOf(false) }

    LaunchedEffect(rewardVideos) {
        val nonBlanks = rewardVideos.filter { it.isNotBlank() }
        if (nonBlanks.isNotEmpty()) {
            generatedRewardVideo = nonBlanks.random()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Parabéns, Lenda da Feira das Nações!",
            fontSize = 24.sp,
            fontFamily = FontFamily.Serif,
            color = ChineseGold,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Seus esforços científicos e de absorção cultural geraram uma premiação imperial.",
            fontSize = 13.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedReportCardIndex,
            containerColor = Color.Transparent,
            contentColor = ChineseGold,
            divider = { Divider(color = DividerGoldColor) }
        ) {
            Tab(
                selected = selectedReportCardIndex == 0,
                onClick = { selectedReportCardIndex = 0 },
                text = { Text("📜 Carta Oficial", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedReportCardIndex == 1,
                onClick = { selectedReportCardIndex = 1 },
                text = { Text("💡 Cartas de Conhecimento", fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedReportCardIndex == 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .border(3.dp, ChineseGold, RoundedCornerShape(24.dp))
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ChineseGray)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CARTEIRA DE RECONHECIMENTO",
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ChineseGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Feira das Nações — Culturas em Foco",
                        fontSize = 11.sp,
                        color = ChineseRed,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEECC))
                            .border(2.dp, ChineseGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = AVATARS[session.avatarIndex].second, fontSize = 50.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = session.name,
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Estudante do 7º Ano D",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x0AFFFFFF), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        ReportField(label = "Escola", value = session.escola)
                        ReportField(label = "Professor Orientador", value = "Johnny Fernandes (Ciências)")
                        ReportField(label = "Desafios do Quiz", value = "$score de 5 Corretos")
                        ReportField(label = "Pontos XP Ganhos", value = "$xp XP Imperial")
                        ReportField(label = "Chave Curricular", value = "Botânica e Física Médica Antiga")
                        
                        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                        ReportField(label = "Data da Integração", value = sdf.format(Date()))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    ) {
                        val length = size.width
                        drawLine(
                            color = ChineseGold,
                            start = Offset(0f, size.height / 2),
                            end = Offset(length, size.height / 2),
                            strokeWidth = 3f
                        )
                        drawCircle(
                            color = ChineseRed,
                            radius = 8f,
                            center = Offset(length / 2, size.height / 2)
                        )
                    }

                    Text(
                        text = "“A mente que compreende a história molda o progresso futuro.”",
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            KnowledgeCardsCarousel()
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (generatedRewardVideo.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .border(1.dp, DividerGoldColor, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = ChineseGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎁 Seu Vídeo Prêmio Especial:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChineseGold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    VideoPlayer(
                        videoUrl = generatedRewardVideo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "O professor Johnny liberou este documentário sobre as grandes dinastias folclóricas chinesas.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .padding(bottom = 24.dp)
                .border(1.dp, DividerGoldColor, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = ChineseGray)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Opinião do Estudante",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChineseGold
                )
                Text(
                    text = "Envie uma nota em estrelas e um comentário sobre a Feira de Ciências dos alunos.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    for (star in 1..5) {
                        val active = star <= currentRating
                        IconButton(onClick = { currentRating = star }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star Estrela",
                                tint = if (active) ChineseGold else Color.DarkGray,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = userMessageText,
                    onValueChange = { userMessageText = it },
                    label = { Text("Mensagem final para a turma") },
                    placeholder = { Text("Ex: Adorei o som do Erhu!") },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChineseGold,
                        unfocusedBorderColor = Color.DarkGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onFinished(currentRating, userMessageText)
                        savedSuccessfullyInSession = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (savedSuccessfullyInSession) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Salvo")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Avaliação Registrada!", color = Color.White)
                    } else {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Salvo")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enviar Review ao Professor", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportField(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

// -------------------------------------------------------------
// CAROUSEL OF EXTRA KNOWLEDGE CARDS
// -------------------------------------------------------------

@Composable
fun KnowledgeCardsCarousel() {
    var activeIdx by remember { mutableStateOf(0) }
    val current = KNOWLEDGE_FACTS[activeIdx]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 500.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, ChineseRed, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ChineseGray)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = current.characterEmoji,
                    fontSize = 54.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = current.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChineseGold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = current.subtitle.uppercase(),
                    fontSize = 10.sp,
                    color = ChineseRed,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = current.description,
                    fontSize = 13.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    activeIdx = if (activeIdx > 0) activeIdx - 1 else KNOWLEDGE_FACTS.size - 1
                },
                modifier = Modifier.background(ChineseGray, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Anterior", tint = ChineseGold)
            }

            Text(
                text = "${activeIdx + 1} de ${KNOWLEDGE_FACTS.size}",
                fontSize = 13.sp,
                color = Color.Gray,
                fontFamily = FontFamily.Monospace
            )

            IconButton(
                onClick = {
                    activeIdx = if (activeIdx + 1 < KNOWLEDGE_FACTS.size) activeIdx + 1 else 0
                },
                modifier = Modifier.background(ChineseGray, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Próximo", tint = ChineseGold)
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: CHARACTER PREVIEW AND MEETING CARD
// -------------------------------------------------------------

@Composable
fun CharacterIntroCard(stationNum: Int) {
    val characters = listOf(
        Pair("Sr. Wang", "🍵 \"Olá jovem! Sou o Sr. Wang, vendedor de chás imperiais na Feira das Nações. Você sabia que meus antepassados vieram plantar no Rio antigo?\""),
        Pair("Dona Mei", "🥟 \"Saudações, viajante! Dona Mei ao seu dispor. Ofereço pastéis crocantes e adoro cozinhar com especiarias científicas ancestrais!\""),
        Pair("Mestre Lee", "🎻 \"Bem-vindo! Sou o Mestre Lee. O Erhu possui o timbre dos espíritos da montanha. Consegue diferenciar sua ressonância das outras com seu ouvido?\""),
        Pair("Gabriela", "🦁 \"Oi! Sou a Gabriela. Eu e meus colegas ensaiamos a coreografia da Dança do Dragão para a grande festa local. Vamos treinar?\""),
        Pair("Sábio Chen", "⚔️ \"Olá combatente! Sou o Sábio Chen. Estudo o Tai Chi Chuan, Kung Fu e as artes alquímicas da pólvora. Vamos decifrar mitos?\"")
    )

    val current = characters[stationNum - 1]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DividerGoldColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0x33000000))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(ChineseRedDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (stationNum) {
                        1 -> "👨"
                        2 -> "👩"
                        3 -> "👴"
                        4 -> "👧"
                        else -> "👲"
                    },
                    fontSize = 28.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = current.first, fontSize = 15.sp, color = ChineseGold, fontWeight = FontWeight.Bold)
                Text(text = current.second, fontSize = 12.sp, color = Color.White, fontStyle = FontStyle.Italic, lineHeight = 16.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: WALK SIMULATOR ACROSS THE FAIR METAVERSE
// -------------------------------------------------------------

@Composable
fun WalkSimulatorScreen(
    avatarEmoji: String,
    progress: Float,
    stationNum: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 500.dp)
            .background(ChineseGray, RoundedCornerShape(24.dp))
            .border(1.dp, DividerGoldColor, RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Caminhando pela Feira...",
            fontSize = 16.sp,
            color = ChineseGold,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Viajando até a barra do seu correspondente!",
            fontSize = 11.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(6) { Text("🏮", fontSize = 14.sp) }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0.12f, 1f))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = avatarEmoji,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("🚪 Portal", fontSize = 10.sp, color = Color.Gray)
                Text("Banca $stationNum 🎪", fontSize = 10.sp, color = ChineseGold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = progress,
            color = ChineseGold,
            trackColor = Color.DarkGray,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
        )
    }
}

// -------------------------------------------------------------
// PORTAL PASSWORD DIALOG
// -------------------------------------------------------------

@Composable
fun PasswordPortalGate(
    stationNum: Int,
    onOptionSelected: (String) -> Unit,
    onBackToMap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 500.dp)
            .shadow(24.dp, RoundedCornerShape(24.dp))
            .background(ChineseGray, RoundedCornerShape(24.dp))
            .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(24.dp)) // border border-[#D4AF37]/20
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PORTAL DE JORNADA",
            fontSize = 10.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            color = ChineseGold, // text-[#D4AF37]
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "🚪 Selo do Portal de Ciências",
            fontSize = 18.sp,
            fontFamily = FontFamily.Serif,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Para acessar a Estação $stationNum, deves decifrar o provérbio de sabedoria correspondente:",
            fontSize = 13.sp,
            color = Color(0x99FFFFFF), // text-white/60
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PROVERB_PASSWORDS.forEach { proverb ->
                Button(
                    onClick = { onOptionSelected(proverb) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x66000000)), // bg-black/40
                    border = BorderStroke(1.dp, Color(0x1AFFFFFF)), // border-white/10
                    shape = RoundedCornerShape(12.dp), // rounded-xl
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = proverb,
                        fontSize = 13.sp,
                        color = TextLight,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToMap) {
            Text("Cancelar & Voltar ao Mapa", color = ChineseRed)
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: STANDARD QUIZ QUESTION CONTAINER
// -------------------------------------------------------------

@Composable
fun QuizTaskView(
    question: QuestionEntity,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Desafio do Grupo: " + question.tema,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ChineseGold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = BorderStroke(1.dp, DividerGoldColor)
        ) {
            Text(
                text = question.pergunta,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(16.dp),
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            question.opcoes.forEach { option ->
                val isAnswered = selectedOption != null
                val isCorrect = option == question.respostaCorreta
                val isSelected = option == selectedOption

                Button(
                    onClick = { onOptionSelected(option) },
                    enabled = !isAnswered,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAnswered) {
                            if (isCorrect) Color(0xFF1B4E26)
                            else if (isSelected) Color(0xFF5A1A22)
                            else Color.DarkGray
                        } else {
                            Color(0xFF242730)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = option,
                        fontSize = 13.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HEADER & ALIGN METADATA INFOBARS
// -------------------------------------------------------------

@Composable
fun HeaderBar(
    name: String,
    escola: String,
    turma: String,
    xp: Int,
    badges: List<String>,
    avatarCustomizationRaw: String = "0|0|0|0",
    onLogout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ChineseDark, // Elegant pure dark background #0F0F0F
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Typographical App Branding from Elegant Dark mockup
                Column {
                    Text(
                        text = "Metaverso Cultural",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = ChineseGold,
                        letterSpacing = 3.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = "Feira das Nações",
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        color = TextLight,
                        lineHeight = 28.sp
                    )
                }

                // Dual-ring Gradient Avatar Container
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0x33D4AF37), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val parts = avatarCustomizationRaw.split("|")
                    val hair = parts.getOrNull(0)?.toIntOrNull() ?: 0
                    val face = parts.getOrNull(1)?.toIntOrNull() ?: 0
                    val clothes = parts.getOrNull(2)?.toIntOrNull() ?: 0
                    val colorBg = parts.getOrNull(3)?.toIntOrNull() ?: 0

                    CustomizableAvatarVisual(
                        hairIdx = hair,
                        faceIdx = face,
                        clothingIdx = clothes,
                        colorIdx = colorBg,
                        size = 42.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-info bar showing student session and performance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ChineseGray)
                    .border(1.dp, Color(0x0DFFFFFF), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$turma • $escola",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0x1AD4AF37), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🏆 $xp XP",
                            fontSize = 12.sp,
                            color = ChineseGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sair",
                            tint = ChineseRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MestreGuideBubble(speech: String, avatarEmoji: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16181C)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, DividerGoldColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF332211)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = avatarEmoji, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Mestre da Trilha Pedagógica",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChineseGold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "\"$speech\"",
                    fontSize = 13.sp,
                    color = Color.White,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 4: TEACHER / ADMIN PANEL
// -------------------------------------------------------------

@Composable
fun AdminPanelScreen(
    session: UserSession,
    viewModel: QuizViewModel,
    onLogout: () -> Unit
) {
    val coroutine = rememberCoroutineScope()
    
    val allQuestions by viewModel.allQuestions.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val rewardVideosHost by viewModel.rewardVideosHost.collectAsState()

    var activeAdminTab by remember { mutableStateOf(0) }
    
    var selectedGroupIdx by remember { mutableStateOf(1) }
    var themeInput by remember { mutableStateOf("") }
    var questionTextInput by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctOptionInput by remember { mutableStateOf("") }
    var feedbackVideoUrl by remember { mutableStateOf("") }
    var textExplanationInput by remember { mutableStateOf("") }
    
    var showQuestionSaveSuccess by remember { mutableStateOf(false) }

    var rewardUrl0 by remember { mutableStateOf("") }
    var rewardUrl1 by remember { mutableStateOf("") }
    var rewardUrl2 by remember { mutableStateOf("") }
    var rewardUrl3 by remember { mutableStateOf("") }
    var showRewardSaveSuccess by remember { mutableStateOf(false) }

    // Station/World background personalization config states
    val allStationConfigs by viewModel.allStationConfigs.collectAsState()
    var editStationId by remember { mutableStateOf(1) }
    var stationBgImageInput by remember { mutableStateOf("") }
    var stationBgVideoInput by remember { mutableStateOf("") }
    var stationBgMelodyInput by remember { mutableStateOf("") }
    var stationTitleInput by remember { mutableStateOf("") }
    var stationSubtitleInput by remember { mutableStateOf("") }
    var showStationSaveSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(editStationId, allStationConfigs) {
        val config = allStationConfigs.find { it.stationId == editStationId }
        if (config != null) {
            stationBgImageInput = config.backgroundImageUrl
            stationBgVideoInput = config.backgroundVideoUrl
            stationBgMelodyInput = config.melodyUrl
            stationTitleInput = config.customTitle
            stationSubtitleInput = config.customSubtitle
        } else {
            stationBgImageInput = ""
            stationBgVideoInput = ""
            stationBgMelodyInput = ""
            stationTitleInput = ""
            stationSubtitleInput = ""
        }
    }

    LaunchedEffect(selectedGroupIdx, allQuestions) {
        val q = allQuestions.find { it.idGrupo == selectedGroupIdx }
        if (q != null) {
            themeInput = q.tema
            questionTextInput = q.pergunta
            val list = q.opcoes
            optionA = list.getOrNull(0) ?: ""
            optionB = list.getOrNull(1) ?: ""
            optionC = list.getOrNull(2) ?: ""
            optionD = list.getOrNull(3) ?: ""
            correctOptionInput = q.respostaCorreta
            feedbackVideoUrl = q.videoFeedbackUrl
            textExplanationInput = q.textoExplicativo
        } else {
            themeInput = ""
            questionTextInput = ""
            optionA = ""
            optionB = ""
            optionC = ""
            optionD = ""
            correctOptionInput = ""
            feedbackVideoUrl = ""
            textExplanationInput = ""
        }
    }

    LaunchedEffect(rewardVideosHost) {
        rewardUrl0 = rewardVideosHost.getOrNull(0)?.videoUrl ?: ""
        rewardUrl1 = rewardVideosHost.getOrNull(1)?.videoUrl ?: ""
        rewardUrl2 = rewardVideosHost.getOrNull(2)?.videoUrl ?: ""
        rewardUrl3 = rewardVideosHost.getOrNull(3)?.videoUrl ?: ""
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ChineseGray,
                border = BorderStroke(1.dp, DividerGoldColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Painel do Professor — Admin",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChineseGold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Orientador: Johnny Fernandes • Ciências",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Sair do Painel", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        },
        containerColor = ChineseDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Alunos",
                    value = "${allStudents.size}",
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Concluídos",
                    value = "${allStudents.filter { it.completed }.size}",
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f)
                )
                
                val avgStar = if (allStudents.isNotEmpty()) {
                    val floatList = allStudents.map { it.stars }.filter { it > 0 }
                    if (floatList.isEmpty()) "5.0" else "%.1f".format(floatList.average())
                } else "5.0"

                MetricCard(
                    title = "Avaliação Feira",
                    value = "$avgStar ⭐",
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1.5f)
                )
            }

            TabRow(
                selectedTabIndex = activeAdminTab,
                containerColor = ChineseGray,
                contentColor = ChineseGold,
                divider = { Divider(color = DividerGoldColor) }
            ) {
                Tab(
                    selected = activeAdminTab == 0,
                    onClick = { activeAdminTab = 0 },
                    text = { Text("📊 Alunos", fontSize = 12.sp) }
                )
                Tab(
                    selected = activeAdminTab == 1,
                    onClick = { activeAdminTab = 1 },
                    text = { Text("📝 Questões G1-G5", fontSize = 12.sp) }
                )
                Tab(
                    selected = activeAdminTab == 2,
                    onClick = { activeAdminTab = 2 },
                    text = { Text("🎁 Vídeos Prêmios", fontSize = 12.sp) }
                )
                Tab(
                    selected = activeAdminTab == 3,
                    onClick = { activeAdminTab = 3 },
                    text = { Text("🌌 Mundos", fontSize = 12.sp) }
                )
            }

            when (activeAdminTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                text = "Acompanhamento da Feira em Tempo Real:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        if (allStudents.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = ChineseGray)
                                ) {
                                    Text(
                                        text = "Nenhum estudante logado ou integrado no momento.",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            items(allStudents) { student ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = ChineseGray),
                                    border = BorderStroke(1.dp, Color.DarkGray)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(text = student.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Text(text = "${student.turma} • ${student.escola}", fontSize = 11.sp, color = Color.Gray)
                                                Text(text = student.email, fontSize = 10.sp, color = ChineseGold, fontFamily = FontFamily.Monospace)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .background(if (student.completed) Color(0x2210B981) else Color(0x22EF4444), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = if (student.completed) "Concluiu" else "Explorando",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (student.completed) ChineseJade else ChineseRed
                                                )
                                            }
                                        }

                                        Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "Pontuação: ${student.score}/5", fontSize = 12.sp, color = Color.LightGray)
                                            Text(text = "Experiência: ${student.xp} XP", fontSize = 12.sp, color = ChineseGold)
                                            Text(text = "Rating: ${student.stars} ⭐", fontSize = 12.sp, color = Color.LightGray)
                                        }

                                        if (student.feedbackText.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Mensagem: \"${student.feedbackText}\"",
                                                fontSize = 11.sp,
                                                color = Color.Gray,
                                                fontStyle = FontStyle.Italic
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Selecione o Grupo do Projeto (1 a 5):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChineseGold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (g in 1..5) {
                                Button(
                                    onClick = { selectedGroupIdx = g },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedGroupIdx == g) ChineseRed else ChineseGray
                                    ),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("G$g", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = themeInput,
                            onValueChange = { themeInput = it },
                            label = { Text("Tema do Trabalho Científico") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = questionTextInput,
                            onValueChange = { questionTextInput = it },
                            label = { Text("Pergunta para o Quiz") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Alternativas de Resposta:", fontSize = 12.sp, color = ChineseGold, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = optionA,
                            onValueChange = { optionA = it },
                            label = { Text("Opção A") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = optionB,
                            onValueChange = { optionB = it },
                            label = { Text("Opção B") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = optionC,
                            onValueChange = { optionC = it },
                            label = { Text("Opção C") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = optionD,
                            onValueChange = { optionD = it },
                            label = { Text("Opção D") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = correctOptionInput,
                            onValueChange = { correctOptionInput = it },
                            label = { Text("Resposta Correta") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = feedbackVideoUrl,
                            onValueChange = { feedbackVideoUrl = it },
                            label = { Text("Vídeo Explicativo") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = textExplanationInput,
                            onValueChange = { textExplanationInput = it },
                            label = { Text("Texto Complementar") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )

                        if (showQuestionSaveSuccess) {
                            Text(
                                text = "Salvo com sucesso!",
                                color = ChineseJade,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val list = listOf(optionA, optionB, optionC, optionD).filter { it.isNotBlank() }
                                if (list.size == 4) {
                                    viewModel.adminSaveQuestion(
                                        idGrupo = selectedGroupIdx,
                                        tema = themeInput,
                                        pergunta = questionTextInput,
                                        opcoes = list,
                                        respostaCorreta = correctOptionInput,
                                        videoUrl = feedbackVideoUrl,
                                        textoExplicativo = textExplanationInput
                                    )
                                    showQuestionSaveSuccess = true
                                    coroutine.launch {
                                        delay(3000)
                                        showQuestionSaveSuccess = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ChineseGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Salvar Atividade do Grupo $selectedGroupIdx", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                2 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Cadastre os Vídeos de Premiação:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChineseGold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Insira links do YouTube. Ao cruzar a linha de chegada o game fará um sorteio de recompensações culturais ao estudante.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = rewardUrl0,
                            onValueChange = { rewardUrl0 = it },
                            label = { Text("Opção de Vídeo Prêmio 1") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = rewardUrl1,
                            onValueChange = { rewardUrl1 = it },
                            label = { Text("Opção de Vídeo Prêmio 2") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = rewardUrl2,
                            onValueChange = { rewardUrl2 = it },
                            label = { Text("Opção de Vídeo Prêmio 3") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = rewardUrl3,
                            onValueChange = { rewardUrl3 = it },
                            label = { Text("Opção de Vídeo Prêmio 4") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )

                        if (showRewardSaveSuccess) {
                            Text(
                                text = "Salvo com sucesso!",
                                color = ChineseJade,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.adminSaveRewardVideos(
                                    listOf(rewardUrl0, rewardUrl1, rewardUrl2, rewardUrl3)
                                )
                                showRewardSaveSuccess = true
                                coroutine.launch {
                                    delay(3000)
                                    showRewardSaveSuccess = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ChineseGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Salvar 4 Vídeos Oficiais", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                3 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "🌌 Personalização dos Mundos (Estações)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChineseGold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Defina imagens e vídeos com melodias de fundo para cada mundo. Isso altera instantaneamente o visual e som que os alunos presenciam.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Station selector buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (s in 1..5) {
                                Button(
                                    onClick = { editStationId = s },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (editStationId == s) ChineseRed else ChineseGray
                                    ),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Mundo $s", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = stationTitleInput,
                            onValueChange = { stationTitleInput = it },
                            label = { Text("Título Personalizado do Mundo") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = stationSubtitleInput,
                            onValueChange = { stationSubtitleInput = it },
                            label = { Text("Subtítulo Personalizado do Mundo") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = stationBgImageInput,
                            onValueChange = { stationBgImageInput = it },
                            label = { Text("URL da Imagem de Fundo (JPG/PNG)") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold),
                            placeholder = { Text("Ex: https://dominio.com/fundo1.jpg") }
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = stationBgVideoInput,
                            onValueChange = { stationBgVideoInput = it },
                            label = { Text("URL do Vídeo de Fundo (YouTube)") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold),
                            placeholder = { Text("Ex: https://www.youtube.com/watch?v=...") }
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = stationBgMelodyInput,
                            onValueChange = { stationBgMelodyInput = it },
                            label = { Text("URL da Melodia de Fundo (YouTube/Link)") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChineseGold),
                            placeholder = { Text("Sons tradicionais ou música de fundo") }
                        )

                        if (showStationSaveSuccess) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Mundo $editStationId salvo com sucesso!",
                                color = ChineseJade,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.adminSaveStationConfig(
                                    StationConfigEntity(
                                        stationId = editStationId,
                                        backgroundImageUrl = stationBgImageInput,
                                        backgroundVideoUrl = stationBgVideoInput,
                                        melodyUrl = stationBgMelodyInput,
                                        customTitle = stationTitleInput,
                                        customSubtitle = stationSubtitleInput
                                    )
                                )
                                showStationSaveSuccess = true
                                coroutine.launch {
                                    delay(3000)
                                    showStationSaveSuccess = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ChineseGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Salvar Configuração do Mundo $editStationId", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(72.dp),
        colors = CardDefaults.cardColors(containerColor = ChineseGray),
        border = BorderStroke(1.dp, Color.DarkGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = ChineseGold, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontSize = 11.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                Text(text = value, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// MYTHS DATA & MINI GAME SCREEN
// -------------------------------------------------------------

data class Myth(
    val statement: String,
    val isTrue: Boolean,
    val explanation: String
)

val MYTHS_LIST = listOf(
    Myth(
        statement = "A pólvora na China antiga era usada exclusivamente em batalhas navais.",
        isTrue = false,
        explanation = "A pólvora começou na alquimia medicinal buscando o elixir da imortalidade, sendo usada depois em festivais espantando maus espíritos antes de qualquer guarnição de caravelas."
    ),
    Myth(
        statement = "O Tai Chi Chuan une respiração harmônica, equilíbrio físico e princípios da física de forças e alavancas.",
        isTrue = true,
        explanation = "Correto! A transferência de centro de gravidade no Tai Chi obedece perfeitamente às leis científicas de física biomecânica."
    ),
    Myth(
        statement = "A acupuntura é uma prática tradicional que estimula vias nervosas liberando endorfinas comprovadamente.",
        isTrue = true,
        explanation = "Exato! Pesquisas médicas ocidentais provam que a inserção de agulhas estimula o sistema nervoso central, liberando defesas analgésicas naturais."
    )
)

@Composable
fun MythCarouselGame(
    index: Int,
    votedTrueOrFalse: Boolean?,
    feedback: String,
    onAnswer: (Boolean) -> Unit,
    onNext: () -> Unit,
    finished: Boolean
) {
    if (finished) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉 Desafio de Mitos e Verdades Concluído!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ChineseJade,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = ChineseGold)
            ) {
                Text("Ir para Feedback da Estação", color = Color.Black)
            }
        }
    } else {
        val current = MYTHS_LIST[index]
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mito ou Verdade? (${index + 1} de ${MYTHS_LIST.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ChineseGold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(1.dp, DividerGoldColor)
            ) {
                Text(
                    text = current.statement,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (votedTrueOrFalse == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onAnswer(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = ChineseJade),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Verdade", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onAnswer(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mito", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x0AFFFFFF), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = feedback,
                        fontSize = 13.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(containerColor = ChineseGold)
                    ) {
                        Text(
                            text = if (index + 1 < MYTHS_LIST.size) "Próximo Mito" else "Concluir Estação",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// EXTRA KNOWLEDGE CARD MODELS & FACTS
// -------------------------------------------------------------

data class FactCard(
    val title: String,
    val subtitle: String,
    val description: String,
    val characterEmoji: String
)

val KNOWLEDGE_FACTS = listOf(
    FactCard(
        title = "A Rota do Chá no Rio",
        subtitle = "História do jardim botânico",
        description = "Sob incentivo direto de D. João VI em 1812, agricultores chineses trouxeram sementes de Camellia sinensis da Ásia para o Rio de Janeiro. Embora o projeto comercial tenha declinado, o chá transformou hábitos alimentares no Império brasileiro.",
        characterEmoji = "🍵"
    ),
    FactCard(
        title = "Acústica do Erhu",
        subtitle = "A ciência das duas cordas",
        description = "A caixa acústica do Erhu utiliza pele de cobra píton. A fricção do arco nas finas cordas gera timbres expressivos que cientistas acústicos estudam por emitirem frequências harmônicas únicas.",
        characterEmoji = "🎻"
    ),
    FactCard(
        title = "Sabor das Feiras",
        subtitle = "Do rolinho primavera ao pastel",
        description = "Durante a Segunda Guerra Mundial, imigrantes chineses abriram as primeiras pastelarias no Brasil. Eles adaptaram a massa fina e crocante do Wonton aos recheios e carne brasileiros, fundando o maior lanche de feira nacional.",
        characterEmoji = "🥟"
    )
)

// =============================================================
// METAVERSE AVATAR CUSTOMIZER AND CARD SYSTEM COMPOSABLES
// =============================================================

@Composable
fun CustomizableAvatarVisual(
    hairIdx: Int,
    faceIdx: Int,
    clothingIdx: Int,
    colorIdx: Int,
    size: androidx.compose.ui.unit.Dp = 80.dp
) {
    val bgColors = listOf(
        Brush.radialGradient(listOf(Color(0xFF2C1B1B), Color(0xFF0F0F0F))),
        Brush.radialGradient(listOf(Color(0xFF1B242C), Color(0xFF0F0F0F))),
        Brush.radialGradient(listOf(Color(0xFF1B2C24), Color(0xFF0F0F0F))),
        Brush.radialGradient(listOf(Color(0xFF2C2819), Color(0xFF0F0F0F)))
    )

    val hairEmoji = listOf("💇‍♂️", "🧑‍🦱", "👱", "🧑‍🦰", "👑")
    val faceExpression = listOf("😄", "😎", "🧐", "😇", "🧑‍💻")
    val clothingEmoji = listOf("🥋", "🧥", "👔", "👘", "🥻")

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColors.getOrElse(colorIdx) { bgColors[0] })
            .border(1.dp, if (faceIdx == 99) Color.Cyan else ChineseGold, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (faceIdx == 99) {
            // IA NANO DIGITALIZED PORTRAIT IN CHINESE GARMENTS
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Background matrix lines
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val headwear = when (hairIdx) {
                        0 -> "👑" // Adorno Imperial Golden Crown
                        1 -> "🧣" // Faixa Dojo
                        2 -> "🎩" // Chapéu de Erudito
                        3 -> "🎋" // Ramo de Feng Shui
                        else -> "👑"
                    }
                    Text(
                        text = headwear,
                        fontSize = (size.value * 0.32f).sp,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "🧑‍💻", // Face digitalizada
                        fontSize = (size.value * 0.42f).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = (size.value * 0.01f).dp)
                    )
                    
                    val garmentStyle = when (clothingIdx) {
                        0 -> "🥋" // Dojo uniform
                        1 -> "👘" // Traditional Hanfu dress
                        2 -> "🧥" // Emperor silk jacket
                        3 -> "👔" // Formal scholar suit
                        else -> "🥋"
                    }
                    Text(
                        text = garmentStyle,
                        fontSize = (size.value * 0.32f).sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Laser Scan Ring Indicator
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.5.dp, Color.Cyan.copy(alpha = 0.8f), CircleShape)
                )

                // Holographic Badge
                Text(
                    text = "IA Nano",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.12f).sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(Color.Cyan, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Hair Layer
                Text(
                    text = hairEmoji.getOrElse(hairIdx) { hairEmoji[0] },
                    fontSize = (size.value * 0.35f).sp,
                    textAlign = TextAlign.Center
                )
                // Face Layer
                Text(
                    text = faceExpression.getOrElse(faceIdx) { faceExpression[0] },
                    fontSize = (size.value * 0.4f).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = (size.value * 0.02f).dp)
                )
                // Clothing Layer
                Text(
                    text = clothingEmoji.getOrElse(clothingIdx) { clothingEmoji[0] },
                    fontSize = (size.value * 0.35f).sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun IAnanoScannerDialog(
    onDismissRequest: () -> Unit,
    onDigitized: (Int, Int, Int) -> Unit // returns hairIdx, clothingIdx, faceIdx=99
) {
    var stage by remember { mutableStateOf("ready") } // "ready" -> "flash" -> "scanning" -> "complete"
    var progress by remember { mutableStateOf(0f) }
    var userPostOption by remember { mutableStateOf(0) }
    var userGarmentOption by remember { mutableStateOf(0) }
    
    val postEmojis = listOf("🧑", "👧", "🧑‍🏫", "🧑‍🎓", "🧙")
    val postNames = listOf("Estudante", "Cientista", "Professor", "Pioneiro", "Sábio")

    val scope = rememberCoroutineScope()

    LaunchedEffect(stage) {
        if (stage == "flash") {
            SoundSynthesizer.playChineseGreetingSound() // Play feedback chime
            delay(300)
            stage = "scanning"
        } else if (stage == "scanning") {
            progress = 0f
            while (progress < 1f) {
                delay(80)
                progress += 0.04f
            }
            stage = "complete"
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(2.dp, Color.Cyan, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070C12))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📸 Câmara Facial IA NANO",
                    color = Color.Cyan,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Escaneie sua face e costure trajes tradicionais chineses",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (stage) {
                    "ready" -> {
                        // Viewfinder container
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF03060A))
                                .border(2.dp, Color.Cyan.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = postEmojis[userPostOption],
                                fontSize = 80.sp,
                                textAlign = TextAlign.Center
                            )
                            // Viewfinder grid overlay
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = Color.Cyan.copy(alpha = 0.2f),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(2f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Choose expression pose
                        Text("Selecione sua Postura Facial:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            postEmojis.forEachIndexed { index, emoji ->
                                val selected = userPostOption == index
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) Color.Cyan.copy(alpha = 0.3f) else Color(0x11FFFFFF))
                                        .clickable { userPostOption = index }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 24.sp)
                                }
                            }
                        }

                        // Garment style choice
                        Text("Estilo da Vestimenta Chinesa:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            listOf("🥋 Dojo", "👘 Hanfu", "🧥 Imperial").forEachIndexed { index, label ->
                                val selected = userGarmentOption == index
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) Color.Cyan.copy(alpha = 0.3f) else Color(0x11FFFFFF))
                                        .clickable { userGarmentOption = index }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(label, color = if (selected) Color.Cyan else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { stage = "flash" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Capturar", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Iniciar Escaneamento Imperial", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                    "flash" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📸 CHISPA!", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        }
                    }
                    "scanning" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color(0xFF03060A), RoundedCornerShape(16.dp))
                                .border(1.dp, Color.Cyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(postEmojis[userPostOption], fontSize = 60.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = when {
                                        progress < 0.25f -> "[IA-NANO v4] Tracing proporções faciais..."
                                        progress < 0.50f -> "[SÍNTESE] Sintonizando volumetria com Dinastia Han..."
                                        progress < 0.75f -> "[TECELAGEM] Tecendo túnica tradicional de seda..."
                                        else -> "[SINTONIA] Aplicando polimento holográfico nano..."
                                    },
                                    color = Color.Cyan,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                LinearProgressIndicator(
                                    progress = progress,
                                    color = Color.Cyan,
                                    trackColor = Color(0x2200FFFF),
                                    modifier = Modifier.width(180.dp).height(8.dp).clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${(progress * 100).toInt()}% completo", color = Color.Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            // Moving scan-laser sweeping effect drawn over box
                            val laserHeight = rememberInfiniteTransition().animateFloat(
                                initialValue = 0f,
                                targetValue = 220f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1500, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .offset(y = (laserHeight.value - 110).dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color.Transparent, Color.Cyan, Color.Transparent)
                                        )
                                    )
                            )
                        }
                    }
                    "complete" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.DarkGray)
                                    .border(2.dp, Color.Cyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                CustomizableAvatarVisual(
                                    hairIdx = userPostOption % 4,
                                    faceIdx = 99,
                                    clothingIdx = userGarmentOption,
                                    colorIdx = 1,
                                    size = 90.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "✓ SUCESSO COGNITIVO!",
                                color = Color.Cyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "Seu avatar digitalizado pela IA Nano foi paramentado com trajes tradicionais chineses. Seus trajes representam a união milenar entre estética e engenharia têxtil de seda natural (+50 XP adicionais!",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    onDigitized(userPostOption % 4, userGarmentOption, 99)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Equipar Avatar Digitalizado", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onDismissRequest) {
                    Text("Cancelar Escaneamento", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AvatarCustomizerPanel(
    currentCustomization: String,
    onSaveRequested: (Int, Int, Int, Int) -> Unit
) {
    val parts = currentCustomization.split("|")
    var hairIdx by remember { mutableStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 0) }
    var faceIdx by remember { mutableStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 0) }
    var clothingIdx by remember { mutableStateOf(parts.getOrNull(2)?.toIntOrNull() ?: 0) }
    var colorIdx by remember { mutableStateOf(parts.getOrNull(3)?.toIntOrNull() ?: 0) }

    var saveBadgeTriggered by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = ChineseGray),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Corte Imperial & Vestimentas do Metaverso",
                color = ChineseGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "Customize sua identidade visual para caminhar na feira chinesa",
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Animated Avatar Preview Box
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F0F0F))
                    .border(2.dp, ChineseGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CustomizableAvatarVisual(
                    hairIdx = hairIdx,
                    faceIdx = faceIdx,
                    clothingIdx = clothingIdx,
                    colorIdx = colorIdx,
                    size = 110.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            var showScanDialog by remember { mutableStateOf(false) }

            if (showScanDialog) {
                IAnanoScannerDialog(
                    onDismissRequest = { showScanDialog = false },
                    onDigitized = { hair, clothing, face ->
                        hairIdx = hair
                        clothingIdx = clothing
                        faceIdx = face // sets to 99
                        showScanDialog = false
                    }
                )
            }

            Button(
                onClick = { showScanDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                modifier = Modifier.padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Face, contentDescription = "Scan", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("📸 Digitalizar Nova Face (IA Nano)", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // HAIR SELECTION
            Text("Estilo de Penteado", color = ChineseGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Liso Clássico", "Cacheado", "Franja", "Ruivo").forEachIndexed { idx, name ->
                    FilterChip(
                        selected = hairIdx == idx,
                        onClick = { hairIdx = idx },
                        label = { Text(name, fontSize = 11.sp, color = if (hairIdx == idx) Color.Black else Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChineseGold,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            // FACE SELECTION
            Text("Expressões & Fatos Históricos", color = ChineseGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Alegre", "Estiloso", "Estudioso", "Tranquilo").forEachIndexed { idx, name ->
                    FilterChip(
                        selected = faceIdx == idx,
                        onClick = { faceIdx = idx },
                        label = { Text(name, fontSize = 11.sp, color = if (faceIdx == idx) Color.Black else Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChineseGold,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            // CLOTHING SELECTION
            Text("Vestimentas Tradicionais", color = ChineseGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Dojo Tradicional", "Casaco Longo", "Terno Moderno", "Hanfu Imperial").forEachIndexed { idx, name ->
                    FilterChip(
                        selected = clothingIdx == idx,
                        onClick = { clothingIdx = idx },
                        label = { Text(name, fontSize = 11.sp, color = if (clothingIdx == idx) Color.Black else Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChineseGold,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            // COLOR PRESET
            Text("Aura Espiritual do Selo", color = ChineseGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Vermelho", "Azul", "Verde", "Dourado").forEachIndexed { idx, name ->
                    FilterChip(
                        selected = colorIdx == idx,
                        onClick = { colorIdx = idx },
                        label = { Text(name, fontSize = 11.sp, color = if (colorIdx == idx) Color.Black else Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChineseGold,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onSaveRequested(hairIdx, faceIdx, clothingIdx, colorIdx)
                    saveBadgeTriggered = true
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Gravar Customização de Avatar", color = Color.White, fontWeight = FontWeight.Bold)
            }

            if (saveBadgeTriggered) {
                Text(
                    text = "✓ Avatar sintonizado! Identidade enviada à nuvem nacional do metaverso.",
                    color = Color.Green,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
fun MetaverseFairGridScreen(
    unlockedCardsList: List<String>,
    avatarCustomizationRaw: String,
    onUnlockCard: (String) -> Unit
) {
    // 10 stalls of interest on our legendary virtual landscape
    val stalls = listOf(
        MetaverseStall("1", "Tenda de Sobrevivência do Chá Imperial", "D. João VI & Camellia sinensis", 2, 2, "prover_01", "Chá Imperial"),
        MetaverseStall("2", "Templo Secreto das Duas Cordas (Erhu)", "Física Acústica & Vibração de Píton", 3, 4, "desc_02", "Erhu de Píton"),
        MetaverseStall("3", "Banca do Saber Gastronômico", "Imigração & O Pastel adaptado", 1, 6, "curio_01", "Pastel de Vento"),
        MetaverseStall("4", "Grupo Científico da Caligrafia", "Tinta Guache & Pincel de Bambu", 5, 1, "desc_04", "Tinta Bambu"),
        MetaverseStall("5", "Mestre de Acupuntura & Nervos", "Sinalização Elétrica do Cérebro", 6, 3, "prover_02", "Linha de Agulha"),
        MetaverseStall("6", "Teatro Folclórico das Danças Orientais", "Leão do Sul & Ritmo Cordial", 4, 5, "curio_03", "Cabeça de Dragão"),
        MetaverseStall("7", "Pavilhão do Papel Alquímico (Origami)", "Polímero de Celulose Tridimensional", 2, 7, "prover_03", "Origami Guache"),
        MetaverseStall("8", "Escola do Kung Fu / Wushu Cinético", "Gasto calórico & Adenosina mitocôndria", 5, 5, "desc_05", "Energia Chi Qi"),
        MetaverseStall("9", "Cantinho do Ábaco (Cálculo Sólido)", "Matemática Antiga e Operações Rápidas", 1, 3, "desc_03", "Grânulo Real"),
        MetaverseStall("10", "Altar das Lanternas Térmicas", "Convecção dos gases do ar aquecido", 4, 1, "curio_05", "Ar Quente")
    )

    // Avatar virtual coordinates
    var posX by remember { mutableStateOf(4) }
    var posY by remember { mutableStateOf(4) }

    var selectedStallToQuiz by remember { mutableStateOf<MetaverseStall?>(null) }
    var selectedAnswerOption by remember { mutableStateOf<String?>(null) }
    var answerSubmittedResult by remember { mutableStateOf<String?>(null) }

    val pointsOfPlace = remember(posX, posY) {
        stalls.find { it.gridX == posX && it.gridY == posY }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(24.dp))
            .background(ChineseGray, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Metaverso Feira das Nações",
                color = ChineseGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "Pilote seu herói pelas bancas. Descubra segredos de história e ciências!",
                color = Color.LightGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Canvas Grid Rendering
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .border(1.5.dp, ChineseGold, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF090909))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (y in 0..7) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (x in 0..7) {
                                val stallFound = stalls.find { it.gridX == x && it.gridY == y }
                                val withAvatar = (posX == x && posY == y)

                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when {
                                                withAvatar -> Color.Transparent
                                                stallFound != null -> {
                                                    val earned = unlockedCardsList.contains(stallFound.cardRewardId)
                                                    if (earned) Color(0xFF1B2C24) else Color(0xFF2C1B1B)
                                                }
                                                else -> Color(0x0F000000)
                                            }
                                        )
                                        .border(
                                            width = 0.5.dp,
                                            color = when {
                                                withAvatar -> Color.Transparent
                                                stallFound != null -> ChineseGold
                                                else -> Color(0x1AFFFFFF)
                                            }
                                        )
                                        .clickable {
                                            posX = x
                                            posY = y
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (withAvatar) {
                                        val parts = avatarCustomizationRaw.split("|")
                                        val hair = parts.getOrNull(0)?.toIntOrNull() ?: 0
                                        val face = parts.getOrNull(1)?.toIntOrNull() ?: 0
                                        val clothes = parts.getOrNull(2)?.toIntOrNull() ?: 0
                                        val colorBg = parts.getOrNull(3)?.toIntOrNull() ?: 0

                                        CustomizableAvatarVisual(
                                            hairIdx = hair,
                                            faceIdx = face,
                                            clothingIdx = clothes,
                                            colorIdx = colorBg,
                                            size = 24.dp
                                        )
                                    } else if (stallFound != null) {
                                        Text(stallFound.id, color = ChineseGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Controller D-Pad Buttons
            Card(
                modifier = Modifier
                    .width(180.dp)
                    .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { if (posY > 0) posY-- },
                        modifier = Modifier.size(32.dp).background(ChineseGray, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = ChineseGold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { if (posX > 0) posX-- },
                            modifier = Modifier.size(32.dp).background(ChineseGray, CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = ChineseGold)
                        }
                        Spacer(modifier = Modifier.width(28.dp))
                        IconButton(
                            onClick = { if (posX < 7) posX++ },
                            modifier = Modifier.size(32.dp).background(ChineseGray, CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = ChineseGold)
                        }
                    }

                    IconButton(
                        onClick = { if (posY < 7) posY++ },
                        modifier = Modifier.size(32.dp).background(ChineseGray, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = ChineseGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Area info & dialog
            if (pointsOfPlace != null) {
                val hasCard = unlockedCardsList.contains(pointsOfPlace.cardRewardId)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ChineseGold, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1515))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📍 ${pointsOfPlace.name}", color = ChineseGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Badge(
                                containerColor = if (hasCard) Color(0xFF1B2C24) else Color(0xFF2C1B1B)
                            ) {
                                Text(
                                    text = if (hasCard) "Cartão Ganho" else "Pendente",
                                    color = if (hasCard) Color.Green else Color.Red,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(pointsOfPlace.themeDesc, color = Color.White, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                selectedStallToQuiz = pointsOfPlace
                                selectedAnswerOption = null
                                answerSubmittedResult = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ChineseRed)
                        ) {
                            Text(if (hasCard) "Rever Desafio" else "Conversar & Responder Desafio", color = Color.White)
                        }
                    }
                }
            } else {
                Text(
                    text = "Use as setas ▲ ▼ ◄ ► ou toque para mover o avatar até uma das 10 barracas históricas (números amarelos). Ao encontrá-los uma conversa é iniciada!",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    // Modal Quiz Challenge dialog
    if (selectedStallToQuiz != null) {
        val active = selectedStallToQuiz!!
        val isCardAlreadyEarned = unlockedCardsList.contains(active.cardRewardId)

        AlertDialog(
            onDismissRequest = { selectedStallToQuiz = null },
            title = { Text(text = "Desafio: ${active.name}", color = ChineseGold, fontFamily = FontFamily.Serif, fontSize = 15.sp) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Os feirantes da banca de ${active.name} desafiam você a responder cientificamente:",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Static scientific questions depending on index
                    val quizQuestion = when (active.id) {
                        "1" -> "Como a espécie Camellia sinensis se adaptou ao clima do Rio de Janeiro em 1812?"
                        "2" -> "Cientificamente, por que a vibração da pele de píton dá ao Erhu seu som inconfundível?"
                        "3" -> "Qual foi a adaptação física no preparo do pastel de vento pelos imigrantes?"
                        "4" -> "Qual componente químico do bambu confere resistência ao cabo de pincéis orientais?"
                        "5" -> "Como as linhas de acupuntura atuam na propagação dos sinais químicos nas sinapses neuronais?"
                        "6" -> "Por que a dança folclórica do dragão sincroniza a frequência cardíaca dos alunos?"
                        "7" -> "Qual a base geométrica da dobradura de polímeros orgânicos de papel?"
                        "8" -> "Qual ciclo celular libera maior energia Chi Qi mitocondrial no Kung Fu?"
                        "9" -> "Cientificamente, como o ábaco permite aritmética visual veloz de neurotransmissores no lobo frontal?"
                        else -> "Como a convecção térmica mantém aquecido e suspenso o ar dentro de lanternas decorativas?"
                    }

                    val options = when (active.id) {
                        "1" -> listOf("A) Solo argiloso e umidade subtropical favorável", "B) Neve artificial", "C) Irrigação de água mineral imperial", "D) Plantio em estufas de policarbonato")
                        "2" -> listOf("A) Devido ao amortecimento de ondas sonoras de média frequência", "B) Ressonância eletrostática", "C) Magnetismo de metais pesados", "D) Não gera diferença científica")
                        "3" -> listOf("A) Massa com álcool (cachaça) que infla ao vaporizar calor", "B) Fermento químico pesado", "C) Cozimento sob vácuo", "D) Adição de cal virgem")
                        else -> listOf("A) Celulose e lignina que dão flexibilidade e rigidez estrutural", "B) Plástico encapsulado", "C) Nitrogênio gasoso", "D) Borracha nitrílica")
                    }

                    Text(text = quizQuestion, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(bottom = 12.dp))

                    options.forEach { op ->
                        val isSelected = selectedAnswerOption == op
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedAnswerOption = op }
                                .border(1.5.dp, if (isSelected) ChineseGold else Color.Transparent, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) ChineseDark else ChineseGray)
                        ) {
                            Text(text = op, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(10.dp))
                        }
                    }

                    if (answerSubmittedResult != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = answerSubmittedResult!!,
                            color = if (answerSubmittedResult!!.contains("PARABÉNS")) Color.Green else Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val isCorrect = selectedAnswerOption?.startsWith("A") ?: false
                        if (isCorrect) {
                            answerSubmittedResult = "✓ PARABÉNS! Resposta correta do metaverso! Cartão Colecionável Desbloqueado com sucesso!"
                            onUnlockCard(active.cardRewardId)
                        } else {
                            answerSubmittedResult = "✗ Sabedoria incompleta. Releia a alternativa A que concentra o princípio científico imperial!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChineseRed)
                ) {
                    Text("Validar Resposta Imperial", color = Color.White, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedStallToQuiz = null }) {
                    Text("Sair", color = Color.Gray)
                }
            },
            containerColor = ChineseGray,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

data class MetaverseStall(
    val id: String,
    val name: String,
    val themeDesc: String,
    val gridX: Int,
    val gridY: Int,
    val cardRewardId: String,
    val shortWord: String
)

@Composable
fun KnowledgeCardAlbumScreen(
    allCards: List<KnowledgeCardEntity>,
    unlockedCardIds: List<String>,
    studentName: String,
    studentEscola: String,
    studentTurma: String
) {
    var selectedDetailedCard by remember { mutableStateOf<KnowledgeCardEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏅 Álbum Imperial de Cartas de Conhecimento",
            color = ChineseGold,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = "Toque em um cartão desbloqueado (verde) para emitir sua Certidão Científica da Feira das Nações!",
            color = Color.LightGray,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Suas Cartas Desbloqueadas:",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Badge {
                Text(
                    text = "${unlockedCardIds.size} / ${allCards.size}",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        if (allCards.isEmpty()) {
            Text(
                text = "Nenhuma carta imperial cadastrada na base de dados.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 30.dp)
            )
        } else {
            // Lazy grid logic mapped into Rows to keep lightweight layout and prevent infinite scroll compose loops
            val chunks = allCards.chunked(2)
            chunks.forEach { rowCards ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowCards.forEach { card ->
                        val isUnlocked = unlockedCardIds.contains(card.id)

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(6.dp)
                                .border(
                                    1.dp,
                                    if (isUnlocked) ChineseGold else Color(0x33FFFFFF),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    if (isUnlocked) {
                                        selectedDetailedCard = card
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUnlocked) Color(0xFF161E1A) else Color(0xFF1B1B1B)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(if (isUnlocked) Color(0xFF1B2C24) else Color(0xFF2C1B1B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = card.imageUrlOrEmoji,
                                        fontSize = 24.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = card.title,
                                    color = if (isUnlocked) ChineseGold else Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Categoria: ${card.category}",
                                    color = Color.Gray,
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (isUnlocked) "🔓 Ver Crachá" else "🔒 Bloqueado",
                                    color = if (isUnlocked) Color.Green else Color.Red,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    if (rowCards.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    // Modal Certificate Dialog
    if (selectedDetailedCard != null) {
        val cert = selectedDetailedCard!!
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        Dialog(onDismissRequest = { selectedDetailedCard = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .border(2.dp, ChineseGold, RoundedCornerShape(24.dp)),
                color = Color(0xFF0F0F0F),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title Header
                    Text(
                        text = "📜 CERTIDÃO DE CONHECIMENTO IMPERIAL",
                        color = ChineseGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Card Info Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ChineseGray)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(cert.imageUrlOrEmoji, fontSize = 32.sp, modifier = Modifier.padding(end = 10.dp))
                        Column {
                            Text(cert.title, color = ChineseGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Categoria: ${cert.category}", color = Color.Gray, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = cert.description,
                        color = Color.White,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Divider(color = Color(0x33D4AF37), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mandatory certificate fields for player award
                    Text("DADOS DO PATRONO CIENTÍFICO", color = ChineseGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF141414))
                            .border(0.5.dp, Color(0x1AD4AF37), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        CertRow("Nome do Jogador:", studentName)
                        CertRow("Nome da Feira:", "Feira das Nações")
                        CertRow("Escola / Colégio:", studentEscola)
                        CertRow("Ano / Turma:", studentTurma)
                        CertRow("Data Emissão:", dateStr)
                        CertRow("Orientador(a):", "Prof. Alquimista Orientador")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { selectedDetailedCard = null },
                        colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Fechar Selo Real", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CertRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ImmersiveStationWorldContainer(
    stationNum: Int,
    viewModel: QuizViewModel,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background_drift")
    val driftX by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift_x"
    )
    val driftY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift_y"
    )

    val allConfigs by viewModel.allStationConfigs.collectAsState()
    val config = allConfigs.find { it.stationId == stationNum }

    val bgGradient = when (stationNum) {
        1 -> Brush.verticalGradient(listOf(Color(0xFF0F2818), Color(0xFF070B0E)))
        2 -> Brush.verticalGradient(listOf(Color(0xFF2C1304), Color(0xFF0A050D)))
        3 -> Brush.verticalGradient(listOf(Color(0xFF0A1C2C), Color(0xFF04060C)))
        4 -> Brush.verticalGradient(listOf(Color(0xFF1F1C0D), Color(0xFF09060B)))
        else -> Brush.verticalGradient(listOf(Color(0xFF1E0E1E), Color(0xFF07020A)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // 1. Dynamic background image
        if (config != null && !config.backgroundImageUrl.isNullOrBlank()) {
            coil.compose.AsyncImage(
                model = config.backgroundImageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                alpha = 0.35f
            )
        }

        // 2. Dynamic background video
        if (config != null && !config.backgroundVideoUrl.isNullOrBlank()) {
            com.example.ui.components.VideoPlayer(
                videoUrl = config.backgroundVideoUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = 0.40f)
            )
        }

        // 3. Dynamic background audio melody (loaded as a tiny 1dp autoplay video player behind themes)
        if (config != null && !config.melodyUrl.isNullOrBlank()) {
            com.example.ui.components.VideoPlayer(
                videoUrl = config.melodyUrl,
                modifier = Modifier
                    .size(1.dp)
                    .graphicsLayer(alpha = 0f)
            )
        }

        // Realistic 3D Background Silhouette Canvas Layers:
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (driftX * 0.5f).dp, y = (driftY * 0.5f).dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Background star lanterns or mountain curves
                when (stationNum) {
                    1 -> {
                        // Imperial Green Hill silhouettes
                        val p = Path().apply {
                            moveTo(0f, h * 0.8f)
                            quadraticBezierTo(w * 0.3f, h * 0.65f, w * 0.6f, h * 0.85f)
                            quadraticBezierTo(w * 0.85f, h * 0.7f, w, h * 0.9f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        drawPath(p, Color(0xFF0B1F13), alpha = 0.5f)
                    }
                    2 -> {
                        // Lantern night sky stars
                        for (i in 0..12) {
                            val cx = (0.08f + i * 0.07f) * w
                            val cy = (0.1f + (i % 3) * 0.15f) * h
                            drawCircle(Color(0xFFFFA53D).copy(alpha = 0.3f), radius = 10f, center = Offset(cx, cy))
                        }
                    }
                    3 -> {
                        // Jade curtains background stripes
                        for (i in 0..6) {
                            val lx = (i * 0.15f) * w
                            drawLine(
                                color = Color(0xFF00FFD8).copy(alpha = 0.08f),
                                start = Offset(lx, 0f),
                                end = Offset(lx + w * 0.05f, h),
                                strokeWidth = 14f
                            )
                        }
                    }
                    4 -> {
                        // Paper umbrellas shapes
                        for (i in 0..3) {
                            val ux = (0.2f + i * 0.25f) * w
                            val uy = 0.3f * h
                            drawCircle(Color(0xFFFF5722).copy(alpha = 0.08f), radius = 60f, center = Offset(ux, uy))
                        }
                    }
                    else -> {
                        // Mystical training platform stripes
                        drawLine(
                            color = Color(0xFFFFCC00).copy(alpha = 0.06f),
                            start = Offset(0f, h * 0.4f),
                            end = Offset(w, h * 0.42f),
                            strokeWidth = 6f
                        )
                    }
                }
            }
        }

        // Middle Layer: Realistic architectural element / Giant symbol floating
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (driftX * 1.1f).dp, y = (driftY * 1.1f).dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val headerIcon = when (stationNum) {
                    1 -> "🍵"
                    2 -> "🏮"
                    3 -> "🎻"
                    4 -> "🥟"
                    else -> "🥋"
                }

                val headerTitle = if (config != null && !config.customTitle.isNullOrBlank()) {
                    config.customTitle
                } else when (stationNum) {
                    1 -> "Jardim Imperial do Chá Realista 3D"
                    2 -> "Céu Estrelado de Lanternas 3D"
                    3 -> "Templo Acústico de Cordas"
                    4 -> "Barraca Gastronômica Realista"
                    else -> "Pátio Ancestral Shaolin"
                }

                val headerSubtitle = if (config != null && !config.customSubtitle.isNullOrBlank()) {
                    config.customSubtitle
                } else when (stationNum) {
                    1 -> "Sinta o aroma do chá e a névoa das montanhas de Hangzhou"
                    2 -> "Veja os balões de luz aquecida subindo ao cosmo"
                    3 -> "Vibração harmônica pentatônica de Guzheng & Erhu"
                    4 -> "Assando pasteis dourados e crocantes na brasa"
                    else -> "Pratique ensinamentos milenares com saudações ativas"
                }

                Text(headerIcon, fontSize = 52.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = headerTitle,
                    color = ChineseGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = headerSubtitle,
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Foreground Layer: Main task content alongside the sensory rewards scroll bar!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Render the main interactive card / quiz contents
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                content()
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Immersive sensory prizes playground
            ImmersiveSensoryRecompensesWidget(stationNum = stationNum)

            Spacer(modifier = Modifier.height(30.dp))

            // Exit button to clear active station
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = ChineseRedDark),
                border = BorderStroke(1.dp, ChineseGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retornar à Navegação do Mapa", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ImmersiveSensoryRecompensesWidget(stationNum: Int) {
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(2.dp, ChineseGold, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = ChineseGray.copy(alpha = 0.95f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "🎁 Saguão de Recompensas de Imersão Literomusical",
                color = ChineseGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Text(
                "Ganhe XP realizando rituais tradicionais interativos de recompensa!",
                color = Color.LightGray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (stationNum) {
                1 -> {
                    // TEA RITUAL
                    var isPouring by remember { mutableStateOf(false) }
                    var currentCupCapacity by remember { mutableStateOf(0f) }
                    var cupsDrank by remember { mutableStateOf(0) }

                    LaunchedEffect(isPouring) {
                        if (isPouring) {
                            SoundSynthesizer.playTeaPourSound()
                            delay(1200)
                            currentCupCapacity = 1.0f
                            isPouring = false
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (currentCupCapacity > 0f) "🍵 Xícara de Porcelana Cheia!" else "🫙 Bule Imperial Pronto de Chá",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { isPouring = true },
                                enabled = !isPouring && currentCupCapacity < 1f,
                                colors = ButtonDefaults.buttonColors(containerColor = ChineseGold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (isPouring) "Servindo..." else "Servir Chá da Dinastia", color = Color.Black)
                            }

                            Button(
                                onClick = {
                                    scope.launch { SoundSynthesizer.playChineseGreetingSound() } // play gentle chime
                                    currentCupCapacity = 0f
                                    cupsDrank++
                                },
                                enabled = currentCupCapacity >= 1.0f,
                                colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Degustar & Beber 🍵", color = Color.White)
                            }
                        }

                        if (cupsDrank > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Você bebeu $cupsDrank chá da longevidade! Sabedoria adquirida: *\"Quem tem paciência colhe o melhor aroma folhear.\"* (+25 XP)",
                                color = Color.Green,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
                2 -> {
                    // LANTERN LAUNCHING
                    var wishInput by remember { mutableStateOf("") }
                    val wishesList = remember { mutableStateListOf<String>() }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏮 Escreva seu Desejo Científico e Lançe ao Céu:", color = Color.White, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = wishInput,
                                onValueChange = { wishInput = it },
                                label = { Text("Seu Desejo Imperial") },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 12.sp),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    if (wishInput.isNotBlank()) {
                                        wishesList.add(wishInput)
                                        scope.launch { SoundSynthesizer.playChineseGreetingSound() } // gong chime!
                                        wishInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ChineseGold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Soltar 🚀", color = Color.Black)
                            }
                        }

                        if (wishesList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Suas lanternas flutuando no espaço sideral:", color = ChineseGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Column(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                wishesList.forEach { wish ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("🏮", fontSize = 14.sp)
                                        Text("\"$wish\" subindo suavemente ao festival das estrelas...", color = Color.Yellow, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // GUZHENG MINI STRING PLAYGROUND
                    val scaleStrings = listOf("宮 (Gōng) - Dó", "商 (Shāng) - Ré", "角 (Jué) - Mi", "徵 (Zhǐ) - Sol", "羽 (Yǔ) - Lá")
                    var playedNotesCount by remember { mutableStateOf(0) }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎵 Dedilhe as Cordas Sagradas do Templo:", color = Color.White, fontSize = 12.sp)
                        Text("Pressione as cordas na escala pentatônica tradicional chinesa", color = Color.LightGray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 8.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            scaleStrings.forEachIndexed { index, stringName ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(30.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF161A22))
                                        .border(1.dp, Color(0xFF00FFD8).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            scope.launch { SoundSynthesizer.playGuzhengMelody() }
                                            playedNotesCount++
                                        }
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawLine(
                                            color = Color(0xFF00FFD8).copy(alpha = 0.8f),
                                            start = Offset(0f, size.height / 2),
                                            end = Offset(size.width, size.height / 2),
                                            strokeWidth = 3f
                                        )
                                    }

                                    Text(
                                        text = stringName,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.background(ChineseGray).padding(horizontal = 6.dp)
                                    )
                                }
                            }
                        }

                        if (playedNotesCount > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Você emitiu $playedNotesCount notas de Guzheng e Erhu com acústica harmônica! (+25 XP)", color = Color.Green, fontSize = 11.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
                4 -> {
                    // CRUNCHY PASTEL EATING MINI GAME
                    var bitesCount by remember { mutableStateOf(0) }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🥟 Seu Pastel de Vento Adaptado:", color = Color.White, fontSize = 12.sp)

                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clickable(enabled = bitesCount < 4) {
                                    scope.launch { SoundSynthesizer.playPastelCrunchSound() }
                                    bitesCount++
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val pastelScale = when (bitesCount) {
                                0 -> 1.0f
                                1 -> 0.8f
                                2 -> 0.6f
                                3 -> 0.4f
                                else -> 0f
                            }

                            if (pastelScale > 0f) {
                                Text(
                                    "🥟",
                                    fontSize = (72 * pastelScale).sp,
                                    modifier = Modifier.graphicsLayer {
                                        scaleX = pastelScale
                                        scaleY = pastelScale
                                    }
                                )
                            } else {
                                Text("✨ Prato Vazio! Devorado com Sucesso!", color = Color.Yellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = when (bitesCount) {
                                0 -> "Clique no pastel acima para dar a primeira mordida crocante!"
                                in 1..3 -> "Mordidas: $bitesCount! Nhoc! Crocante! Óleo de soja e amido sob expansão térmica de gases!"
                                else -> "Pastel consumido integralmente! Você aprendeu as transformações físicas do vapor d'água no cozimento! ( +25 XP )"
                            },
                            color = if (bitesCount >= 4) Color.Green else Color.LightGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        if (bitesCount >= 4) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { bitesCount = 0 },
                                colors = ButtonDefaults.buttonColors(containerColor = ChineseGold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Assar outro Pastel 🥟", color = Color.Black)
                            }
                        }
                    }
                }
                else -> {
                    // CHINESE GREETINGS AND GONG PRONUNCIATION INSTRUCTOR
                    val greetings = listOf(
                        Triple("你好 (Nǐ hǎo)", "Olá!", "Saudação universal de cordialidade e bem-estar"),
                        Triple("谢谢 (Xièxie)", "Obrigado!", "Expressão de gratidão profunda literomilenar"),
                        Triple("再见 (Zàijiàn)", "Até logo!", "Que significa literalmente 'voltar a ver'"),
                        Triple("恭喜发财 (Gōngxǐ fācái)", "Feliz Ano Novo!", "Desejo de prosperidade e boas fortunas no festival")
                    )

                    var lastPronouncedByTeach by remember { mutableStateOf("") }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗣️ Estúdio Falado de Saudações Chinesas:", color = Color.White, fontSize = 12.sp)
                        Text("Clique em qualquer saudação para sintonizar a melodia e ouvir os tons falados do mandarim!", color = Color.LightGray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 12.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            greetings.forEach { phrase ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF161A22))
                                        .border(1.dp, ChineseGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            scope.launch { SoundSynthesizer.playChineseGreetingSound() }
                                            lastPronouncedByTeach = phrase.first
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(phrase.first, color = ChineseGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(phrase.third, color = Color.LightGray, fontSize = 10.sp)
                                    }
                                    Text(phrase.second, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                                }
                            }
                        }

                        if (lastPronouncedByTeach.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Você ouviu a lição de **$lastPronouncedByTeach** tocada pelo sino gongo de bronze sônico! Pronúncia correta e respeito às tradições (+25 XP)",
                                color = Color.Green,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MythicCardGeneratorScreen(
    session: UserSession,
    currentXp: Int,
    onAddXp: (Int) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var nameInput by remember { mutableStateOf(session.name) }
    var dobInput by remember { mutableStateOf("15/08/2012") }
    var selectedTheme by remember { mutableStateOf("Prosperity") } // Prosperity, Zodiac, Festival, History
    
    var isLoading by remember { mutableStateOf(false) }
    var loadingStatusText by remember { mutableStateOf("") }
    var generatedCard by remember { mutableStateOf<MythicCard?>(null) }
    var generatedUri by remember { mutableStateOf<android.net.Uri?>(null) }
    
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(isLoading) {
        if (isLoading) {
            val steps = listOf(
                "Consultando o Oráculo Imperial...",
                "Equilibrando as energias cósmicas do Yin e Yang...",
                "Traçando o mapa estelar do ano de nascimento...",
                "Analisando dons tradicionais através do Nano...",
                "Tecendo caligrafia milenar com fios dourados...",
                "Cunhando Carta Mítica de Alta Vibração..."
            )
            var idx = 0
            while (isLoading) {
                loadingStatusText = steps[idx % steps.size]
                kotlinx.coroutines.delay(1500)
                idx++
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔮 Gerador de Cartas Míticas Imperiais",
            fontSize = 22.sp,
            fontFamily = FontFamily.Serif,
            color = ChineseGold,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Integração I.A. • Sorte, Harmonia & Evolução Espiritual",
            fontSize = 12.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .border(1.dp, DividerGoldColor, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = ChineseGray),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Dados Clássicos da Carta",
                    color = ChineseGold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x0AFFFFFF), RoundedCornerShape(10.dp))
                        .border(0.5.dp, Color(0x1AD4AF37), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text("• Escola: EE CM Cremilda de Oliveira", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("• Evento: Feira das Nações 2026", color = Color.White, fontSize = 12.sp)
                    Text("• Turma: 7º Ano D", color = Color.White, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Nome do Aluno") },
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChineseGold,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = ChineseGold,
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dobInput,
                    onValueChange = { dobInput = it },
                    label = { Text("Data de Nascimento (DD/MM/AAAA)") },
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChineseGold,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedLabelColor = ChineseGold,
                        unfocusedLabelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Text("Selecione o Propósito Sagrado da Carta:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                
                val themes = listOf(
                    "Prosperity" to "🌌 Prosperidade",
                    "Zodiac" to "🐉 Zodíaco Chinês",
                    "Festival" to "🏮 Feiras & Festivais",
                    "History" to "📜 Sabedoria Histórica"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    themes.forEach { (type, label) ->
                        val isSelected = selectedTheme == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTheme = type },
                            label = { Text(label, fontSize = 11.sp, color = if (isSelected) Color.Black else Color.White) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ChineseGold,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Button(
                    onClick = {
                        isLoading = true
                        generatedCard = null
                        generatedUri = null
                        val birthYear = try {
                            dobInput.split("/").last().trim().toInt()
                        } catch (e: Exception) {
                            2012
                        }
                        scope.launch {
                            try {
                                val card = MythicCardGenerator.generateWithGemini(
                                    studentName = nameInput,
                                    birthDate = dobInput,
                                    cardType = selectedTheme,
                                    birthYear = birthYear
                                )
                                generatedCard = card
                                onAddXp(150) // Ganhos de XP por ritual de ia mítica
                                SoundSynthesizer.playChineseGreetingSound()
                            } catch (e: Exception) {
                                android.util.Log.e("MythicUI", "Error generating", e)
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChineseGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Invocar Carta Imperial (+150 XP)", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = ChineseGold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = loadingStatusText,
                        color = ChineseGold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        generatedCard?.let { card ->
            val themeColor = try { Color(android.graphics.Color.parseColor(card.colorThemeHex)) } catch(e: Exception) { ChineseRed }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .border(3.dp, ChineseGold, RoundedCornerShape(24.dp))
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("EE CM CREMILDA DE OLIVEIRA", fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                    Text("Feira das Nações 2026 • 7º Ano D", fontSize = 9.sp, color = ChineseGold)

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .border(1.5.dp, themeColor, CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(1.dp, ChineseGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(card.companionEmoji, fontSize = 70.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = card.title,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif,
                        color = ChineseGold,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = card.studentName,
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    val metaLabel = if (card.zodiacSign.isNotBlank()) {
                        "Nascimento: ${card.birthDate} • Zodíaco: ${card.zodiacSign}"
                    } else {
                        "Nascimento: ${card.birthDate}"
                    }
                    Text(
                        text = metaLabel,
                        fontSize = 11.sp,
                        color = ChineseGold,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, themeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0C0C)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = card.proverb,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD4AF37),
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = card.textContent,
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Divider(color = DividerGoldColor)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    val uri = MythicCardGenerator.saveCardToDeviceGallery(context, card)
                                    generatedUri = uri
                                    if (uri != null) {
                                        android.widget.Toast.makeText(context, "Carta Salva com Sucesso na Galeria! 📸", android.widget.Toast.LENGTH_LONG).show()
                                    } else {
                                        android.widget.Toast.makeText(context, "Falha ao salvar carta de prosperidade.", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ChineseGold),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("📥 Baixar Carta", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    val uri = generatedUri ?: MythicCardGenerator.saveCardToDeviceGallery(context, card)
                                    if (uri != null) {
                                        MythicCardGenerator.shareMythicCard(context, uri)
                                    } else {
                                        android.widget.Toast.makeText(context, "Gere e guarde a imagem primeiro para compartilhar.", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ChineseRed),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Compartilhar", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Compartilhar", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "A imagem exportada conterá carimbo especial e moldura dourada imperial antiga.", fontSize = 9.sp, color = Color.Gray)
                }
            }
        }
    }
}

