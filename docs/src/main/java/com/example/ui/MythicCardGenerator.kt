package com.example.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

// Struct to represent a completed Mythic Card
data class MythicCard(
    val id: String,
    val studentName: String,
    val birthDate: String,
    val cardType: String, // Prosperity, Zodiac, Festival, History
    val title: String,
    val proverb: String,
    val textContent: String,
    val zodiacSign: String = "",
    val companionEmoji: String = "☯️",
    val colorThemeHex: String = "#C8102E" // Chinese Red
)

object MythicCardGenerator {
    private const val TAG = "MythicCardGenerator"

    // Chinese Zodiac calculation
    fun getChineseZodiac(year: Int): Pair<String, String> {
        val signs = listOf(
            "Rato (鼠 - Shǔ)" to "🐁 Inteligente, adaptável e cheio de recursos.",
            "Boi (牛 - Niú)" to "🐂 Honesto, trabalhador e persistente.",
            "Tigre (虎 - Hǔ)" to "🐅 Corajoso, impulsivo e líder natural.",
            "Coelho (兔 - Tù)" to "🐇 Gentil, elegante e pacificador.",
            "Dragão (龙 - Lóng)" to "🐉 Nobre, poderoso, magnético e cheio de vitalidade.",
            "Serpente (蛇 - Shé)" to "🐍 Sábia, enigmática, intuitiva e calma.",
            "Cavalo (马 - Mǎ)" to "🐎 Ativo, enérgico, independente e livre.",
            "Cabra (羊 - Yáng)" to "🐑 Gentil, compassiva, artística e harmoniosa.",
            "Macaco (猴 - Hóu)" to "🐒 Curioso, inteligente, inovador e brincalhão.",
            "Galo (鸡 - Jī)" to "🐓 Observador, corajoso, pontual e orgulhoso.",
            "Cão (狗 - Gǒu)" to "🐕 Leal, protetor, honesto e sincero.",
            "Porco (猪 - Zhū)" to "🐖 Generoso, compassivo, diligente e pacífico."
        )
        if (year < 1900) return "Dragão" to "Espírito milenar de liderança e vigor espiritual."
        val index = (year - 1900) % 12
        return signs[index]
    }

    // Call Gemini REST API directly using OkHttp
    suspend fun generateWithGemini(
        studentName: String,
        birthDate: String,
        cardType: String,
        birthYear: Int
    ): MythicCard = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val (zodiac, traits) = getChineseZodiac(birthYear)

        // If no API key or default placeholder, use the smart offline generation
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "Empty or placeholder API key. Generating high-quality local card.")
            return@withContext getLocalFallback(studentName, birthDate, cardType, zodiac, traits)
        }

        val prompt = when (cardType) {
            "Prosperity" -> """
                Crie uma Carta Mítica de Prósperidade e Evolução Espiritual para o estudante brasileiro '$studentName' nascido em $birthDate.
                O foco da carta deve ser trazer mensagens profundas sobre paz, harmonia mental e evolução espiritual como caminho para uma vida feliz.
                Retorne uma resposta JSON com os seguintes campos:
                {
                  "title": "Um título poético em português remetendo à cultura e paisagem chinesa",
                  "proverb": "Um provérbio chinês real ou inspirado sobre harmonia e paz",
                  "textContent": "Uma mensagem de 3 a 5 linhas direcionada pessoalmente ao estudante sobre sua jornada espiritual e o cultivo da paz interna",
                  "companionEmoji": "Um emoji apropriado (ex: 🌿, 🌸, ⛰️, ☯️)",
                  "colorThemeHex": "Um código Hex de cor imperial (ex: #D4AF37 para ouro, #0F2818 para jade, #C8102E para carmim hindu)"
                }
                Retorne APENAS o JSON puro, sem trechos markdown (sem ```json ou semelhante).
            """.trimIndent()

            "Zodiac" -> """
                Crie uma Carta do Zodíaco Chinês personalizada para '$studentName', nascido em $birthDate (Ano do $zodiac).
                Descreva as características nobres e dons do seu signo '$zodiac' de acordo com o folclore e filosofia chinesa tradicional. Enfatize sabedoria, cooperação e equilíbrio.
                Retorne uma resposta JSON com os seguintes campos:
                {
                  "title": "Carta Guardiã do $zodiac",
                  "proverb": "Um provérbio tradicional sobre autoconhecimento e destino",
                  "textContent": "Mensagem descritiva de 3 a 5 linhas falando sobre seus dons de $zodiac, como usar sua energia terrestre de forma próspera e equilibrada e sua evolução espiritual",
                  "companionEmoji": "Emoji do animal do zodíaco ou elemento",
                  "colorThemeHex": "Código Hex representativo do signo"
                }
                Retorne APENAS o JSON puro, sem trechos markdown (sem ```json ou semelhante).
            """.trimIndent()

            "Festival" -> """
                Crie uma Carta de Conexão Cultural sobre 'Festivais Chineses no Brasil' para o estudante '$studentName'.
                Conecte a vibração alegre de festivais de lanternas, barcos dragão e celebrações da comunidade chinesa com votos de prosperidade, sorte, saúde e integração internacional.
                Retorne uma resposta JSON com os seguintes campos:
                {
                  "title": "Portal do Festival de Outono",
                  "proverb": "Um provérbio sobre reuniões, lanternas de luz ou comemorações",
                  "textContent": "Mensagem calorosa de 3 a 5 linhas sobre as lanternas no céu que iluminam os passos do estudante '$studentName', atraindo união familiar e prosperidade",
                  "companionEmoji": "🏮",
                  "colorThemeHex": "#E60000"
                }
                Retorne APENAS o JSON puro, sem trechos markdown (sem ```json ou semelhante).
            """.trimIndent()

            else -> """
                Crie uma Carta Histórica Militar/Científica para o estudante '$studentName'.
                Fale sobre a antiga Rota da Seda, invenções como a bússola, chá ou as dinastias antigas que moldaram o conhecimento mundial, inspirando o estudante a crescer academicamente.
                Retorne uma resposta JSON com os seguintes campos:
                {
                  "title": "Manuscrito Científico das Dinastias",
                  "proverb": "Provérbio antigo sobre dedicação aos estudos",
                  "textContent": "Mensagem empoderadora de 3 a 5 linhas ligando o nome do estudante às grandes mentes da dinastia de pensadores e astrônomos chineses",
                  "companionEmoji": "📜",
                  "colorThemeHex": "#8E5F38"
                }
                Retorne APENAS o JSON puro, sem trechos markdown (sem ```json ou semelhante).
            """.trimIndent()
        }

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply {
                        put("text", prompt)
                    }))
                }))
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini call failed: Code ${response.code}, Body: $responseBody")
                return@withContext getLocalFallback(studentName, birthDate, cardType, zodiac, traits)
            }

            val rootObj = JSONObject(responseBody)
            val candidates = rootObj.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val firstPart = parts?.optJSONObject(0)
            val jsonText = firstPart?.optString("text") ?: ""

            // Clean the json string if Gemini wrapped in codeblock markdown anyway
            var cleanJson = jsonText.trim()
            if (cleanJson.startsWith("```")) {
                cleanJson = cleanJson.substringAfter("```json").substringAfter("```").substringBeforeLast("```").trim()
            }

            val parsedCard = JSONObject(cleanJson)
            return@withContext MythicCard(
                id = "mythic_" + System.currentTimeMillis(),
                studentName = studentName,
                birthDate = birthDate,
                cardType = cardType,
                title = parsedCard.optString("title", "Senda de Luz Imperial"),
                proverb = parsedCard.optString("proverb", "“Um momento de paciência evita um grande desastre.”"),
                textContent = parsedCard.optString("textContent", "Que a paz interior guie seus estudos."),
                zodiacSign = zodiac,
                companionEmoji = parsedCard.optString("companionEmoji", "☯️"),
                colorThemeHex = parsedCard.optString("colorThemeHex", "#C8102E")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception calling Gemini", e)
            return@withContext getLocalFallback(studentName, birthDate, cardType, zodiac, traits)
        }
    }

    private fun getLocalFallback(
        studentName: String,
        birthDate: String,
        cardType: String,
        zodiac: String,
        zodiacTraits: String
    ): MythicCard {
        return when (cardType) {
            "Prosperity" -> MythicCard(
                id = "local_p_" + System.currentTimeMillis(),
                studentName = studentName,
                birthDate = birthDate,
                cardType = cardType,
                title = "Caminho Imperial do Rio de Jade",
                proverb = "“A paz no coração acalma as ondas do maior oceano.”",
                textContent = "Querido(a) $studentName, que esta carta encha seus caminhos acadêmicos de paz profunda. A força da evolução espiritual é a maior riqueza. Mantenha a mente límpida como as geleiras do Monte Huangshan para que a verdadeira prosperidade floresça na sua vida.",
                zodiacSign = zodiac,
                companionEmoji = "⛰️",
                colorThemeHex = "#0D5930" // Jade green
            )
            "Zodiac" -> MythicCard(
                id = "local_z_" + System.currentTimeMillis(),
                studentName = studentName,
                birthDate = birthDate,
                cardType = cardType,
                title = "Guardião Ancestral: $zodiac",
                proverb = "“Conhecer a si mesmo é o começo de toda sabedoria.”",
                textContent = "Parabéns, $studentName! Sob a regência do $zodiac, você traz as sementes da virtude ancestral: $zodiacTraits Use essa força interna para buscar harmonia escolar, paz de espírito e evolução cósmica rumo ao ano de 2026.",
                zodiacSign = zodiac,
                companionEmoji = "🐉",
                colorThemeHex = "#D4AF37" // Imperial Gold
            )
            "Festival" -> MythicCard(
                id = "local_f_" + System.currentTimeMillis(),
                studentName = studentName,
                birthDate = birthDate,
                cardType = cardType,
                title = "Festival das Lanternas da Sorte",
                proverb = "“Mesmo a menor das velas pode guiar um exército na escuridão.”",
                textContent = "$studentName, as lanternas vermelhas e douradas do Outono elevam o seu nome ao céu espiritual. Esta carta celebra a cooperação entre Brasil e China, enchendo sua vida de energia brilhante, amor familiar e evolução harmoniosa.",
                zodiacSign = zodiac,
                companionEmoji = "🏮",
                colorThemeHex = "#C8102E" // Vermillion Red
            )
            else -> MythicCard(
                id = "local_h_" + System.currentTimeMillis(),
                studentName = studentName,
                birthDate = birthDate,
                cardType = cardType,
                title = "Manuscrito dos Sábios da Dinastia",
                proverb = "“A aprendizagem é um tesouro que seguirá seu dono por toda parte.”",
                textContent = "Estimado(a) $studentName, a história registra que os grandes pensadores chineses uniram a botânica da fitoquímica tradicional às estrelas do céu. Que o seu espírito de cientista escolar continue brilhando na busca pelo saber cultural e humano.",
                zodiacSign = zodiac,
                companionEmoji = "📜",
                colorThemeHex = "#5C3D2E" // Ancient Paper Brown
            )
        }
    }

    // High fidelity drawing onto a 900x1350 pixel bitmap
    fun drawMythicCardBitmap(context: Context, card: MythicCard): Bitmap {
        val width = 900
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Parse theme color
        val primaryColor = try {
            Color.parseColor(card.colorThemeHex)
        } catch (e: Exception) {
            Color.parseColor("#C8102E") // Default Red
        }

        // Paints
        val bgPaint = Paint().apply {
            isAntiAlias = true
        }

        // Draw radial background gradient
        val gradient = RadialGradient(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (height / 1.1).toFloat(),
            intArrayOf(Color.parseColor("#1C1E24"), Color.parseColor("#08090C")),
            null,
            Shader.TileMode.CLAMP
        )
        bgPaint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Reset shader
        bgPaint.shader = null

        // Draw Chinese Watercolor Mountains Silhouette (Atmospheric Landscape)
        val landscapePaint = Paint().apply {
            isAntiAlias = true
            color = primaryColor
            alpha = 24
            style = Paint.Style.FILL
        }

        // Mountains path
        val path1 = Path().apply {
            moveTo(0f, height.toFloat())
            lineTo(0f, (height * 0.70).toFloat())
            quadTo((width * 0.25).toFloat(), (height * 0.62).toFloat(), (width * 0.52).toFloat(), (height * 0.75).toFloat())
            quadTo((width * 0.78).toFloat(), (height * 0.85).toFloat(), width.toFloat(), (height * 0.68).toFloat())
            lineTo(width.toFloat(), height.toFloat())
            close()
        }
        canvas.drawPath(path1, landscapePaint)

        val path2 = Path().apply {
            moveTo(0f, height.toFloat())
            lineTo(0f, (height * 0.82).toFloat())
            quadTo((width * 0.40).toFloat(), (height * 0.75).toFloat(), (width * 0.70).toFloat(), (height * 0.86).toFloat())
            quadTo((width * 0.85).toFloat(), (height * 0.90).toFloat(), width.toFloat(), (height * 0.80).toFloat())
            lineTo(width.toFloat(), height.toFloat())
            close()
        }
        landscapePaint.alpha = 40
        canvas.drawPath(path2, landscapePaint)

        // Draw glowing sun/moon wheel background
        val sunPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = Color.parseColor("#D4AF37")
            alpha = 30
            strokeWidth = 2f
        }
        canvas.drawCircle((width / 2).toFloat(), (height * 0.45).toFloat(), 200f, sunPaint)
        canvas.drawCircle((width / 2).toFloat(), (height * 0.45).toFloat(), 190f, sunPaint)

        // Draw traditional outer dual golden borders
        val borderPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#D4AF37") // Gold
            style = Paint.Style.STROKE
        }

        // Thick border
        borderPaint.strokeWidth = 10f
        canvas.drawRect(30f, 30f, (width - 30).toFloat(), (height - 30).toFloat(), borderPaint)

        // Inner thin border
        borderPaint.strokeWidth = 2f
        canvas.drawRect(45f, 45f, (width - 45).toFloat(), (height - 45).toFloat(), borderPaint)

        // Draw corner ornaments (classical Chinese patterns)
        val cornerPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#D4AF37")
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        // Top Left corner geometric step
        canvas.drawLine(45f, 85f, 85f, 85f, cornerPaint)
        canvas.drawLine(85f, 85f, 85f, 45f, cornerPaint)
        // Top Right
        canvas.drawLine((width - 45).toFloat(), 85f, (width - 85).toFloat(), 85f, cornerPaint)
        canvas.drawLine((width - 85).toFloat(), 85f, (width - 85).toFloat(), 45f, cornerPaint)
        // Bottom Left
        canvas.drawLine(45f, (height - 85).toFloat(), 85f, (height - 85).toFloat(), cornerPaint)
        canvas.drawLine(85f, (height - 85).toFloat(), 85f, (height - 45).toFloat(), cornerPaint)
        // Bottom Right
        canvas.drawLine((width - 45).toFloat(), (height - 85).toFloat(), (width - 85).toFloat(), (height - 85).toFloat(), cornerPaint)
        canvas.drawLine((width - 85).toFloat(), (height - 85).toFloat(), (width - 85).toFloat(), (height - 45).toFloat(), cornerPaint)

        // Text setup - Title, Subtitle, Proverbs, etc.
        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#D4AF37")
            textSize = 48f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val metaPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }

        val textPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#E0E0E0")
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        // Header school & fair details
        metaPaint.textSize = 22f
        metaPaint.color = Color.parseColor("#A0A0A0")
        canvas.drawText("EE CM Cremilda de Oliveira — Feira das Nações 2026", (width / 2).toFloat(), 95f, metaPaint)
        canvas.drawText("Turma: 7º Ano D — Ciências & Integração Cultural", (width / 2).toFloat(), 130f, metaPaint)

        // Divider
        borderPaint.strokeWidth = 2f
        canvas.drawLine(150f, 160f, (width - 150).toFloat(), 160f, borderPaint)

        // Card Category Type
        val catText = when (card.cardType) {
            "Prosperity" -> "CARTA MÍTICA DE PROSPERIDADE"
            "Zodiac" -> "CARTA DO ZODÍACO CHINÊS"
            "Festival" -> "CARTA DE CELEBRAÇÕES E FESTIVAIS"
            else -> "CARTA HISTÓRICA E INSTRUMENTAL"
        }
        metaPaint.textSize = 24f
        metaPaint.color = primaryColor
        canvas.drawText(catText, (width / 2).toFloat(), 215f, metaPaint)

        // Main Card Title
        titlePaint.textSize = 52f
        titlePaint.color = Color.parseColor("#D4AF37")
        canvas.drawText(card.title, (width / 2).toFloat(), 285f, titlePaint)

        // Companion Emoji Center drawing
        val emojiPaint = Paint().apply {
            isAntiAlias = true
            textSize = 120f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(card.companionEmoji, (width / 2).toFloat(), (height * 0.45).toFloat() + 40f, emojiPaint)

        // Holder Name
        metaPaint.textSize = 34f
        metaPaint.color = Color.WHITE
        canvas.drawText(card.studentName, (width / 2).toFloat(), (height * 0.65).toFloat(), metaPaint)

        // Birthdate & Sign details in subhead
        metaPaint.textSize = 24f
        metaPaint.color = Color.parseColor("#D4AF37")
        val infoLine = if (card.zodiacSign.isNotBlank()) {
            "Nascido em ${card.birthDate} • Signo: ${card.zodiacSign}"
        } else {
            "Nascido em ${card.birthDate}"
        }
        canvas.drawText(infoLine, (width / 2).toFloat(), (height * 0.69).toFloat(), metaPaint)

        // Traditional proverb box
        val proverbBgPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#121418")
            style = Paint.Style.FILL
        }
        val proverBorderPaint = Paint().apply {
            isAntiAlias = true
            color = primaryColor
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        val proverbRect = RectF(100f, (height * 0.74).toFloat(), (width - 100).toFloat(), (height * 0.82).toFloat())
        canvas.drawRoundRect(proverbRect, 12f, 12f, proverbBgPaint)
        canvas.drawRoundRect(proverbRect, 12f, 12f, proverBorderPaint)

        val provPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#FFD700") // Golden text
            textSize = 22f
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            textAlign = Paint.Align.CENTER
        }
        // Word wrap for proverb into 2 lines if needed
        val proverbWords = card.proverb.split(" ")
        var line1 = ""
        var line2 = ""
        for (w in proverbWords) {
            if (line1.length < 35) {
                line1 += "$w "
            } else {
                line2 += "$w "
            }
        }
        canvas.drawText(line1.trim(), (width / 2).toFloat(), (height * 0.78).toFloat(), provPaint)
        if (line2.isNotBlank()) {
            canvas.drawText(line2.trim(), (width / 2).toFloat(), (height * 0.81).toFloat(), provPaint)
        }

        // Card Description Core text wrap
        textPaint.textSize = 24f
        textPaint.color = Color.parseColor("#DDDDDD")
        val contentLines = wrapTextToList(card.textContent, 660, textPaint)

        var curY = (height * 0.88).toFloat()
        for (line in contentLines) {
            val bounds = Rect()
            textPaint.getTextBounds(line, 0, line.length, bounds)
            val lineW = bounds.width()
            canvas.drawText(line, ((width - lineW) / 2).toFloat(), curY, textPaint)
            curY += 34f
        }

        // Draw Chinese Imperial Seal Stamp (A red square with traditional look "福" or "吉")
        val sealPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#D01010") // China red seal
            style = Paint.Style.FILL
        }
        val sealRect = RectF((width - 150).toFloat(), (height - 150).toFloat(), (width - 80).toFloat(), (height - 80).toFloat())
        canvas.drawRoundRect(sealRect, 8f, 8f, sealPaint)

        val sealTextPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 34f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("吉", (width - 114).toFloat(), (height - 102).toFloat(), sealTextPaint)

        // Watermark bottom signature
        metaPaint.textSize = 18f
        metaPaint.color = Color.parseColor("#606060")
        canvas.drawText("Prêmio Especial Mítico Feira das Nações @ 2026 • Curadoria Prof. Johnny", (width / 2).toFloat(), (height - 45).toFloat(), metaPaint)

        return bitmap
    }

    private fun wrapTextToList(text: String, maxWidth: Int, paint: Paint): List<String> {
        val list = mutableListOf<String>()
        val words = text.split(" ")
        var currentLine = ""
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val width = paint.measureText(testLine)
            if (width < maxWidth) {
                currentLine = testLine
            } else {
                list.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) {
            list.add(currentLine)
        }
        return list
    }

    // Modern Android save bitmap utility compatible with scoped storage (API 29+)
    suspend fun saveCardToDeviceGallery(context: Context, card: MythicCard): Uri? = withContext(Dispatchers.IO) {
        val bitmap = drawMythicCardBitmap(context, card)
        val filename = "Carta_Mitica_${card.studentName.replace(" ", "_")}_${System.currentTimeMillis()}.png"
        var fos: OutputStream? = null
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/FeiraDasNacoes")
                    }
                    imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir = context.getExternalFilesDir(null) ?: context.cacheDir
                val file = File(imagesDir, filename)
                fos = FileOutputStream(file)
                // Use FileProvider on older APIs or directly share
                imageUri = try {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                } catch (e: Exception) {
                    Uri.fromFile(file)
                }
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                Log.d(TAG, "Mythic card saved successfully to Gallery.")
            }
            return@withContext imageUri
        } catch (e: Exception) {
            Log.e(TAG, "Error saving mythic card to gallery", e)
            return@withContext null
        }
    }

    // Trigger share intent
    fun shareMythicCard(context: Context, imageUri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartilhar Carta Mítica"))
    }
}
