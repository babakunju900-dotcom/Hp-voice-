package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.remote.CorrectionResult
import com.example.data.local.MomentEntity
import com.example.data.local.UserEntity
import com.example.data.local.VocabularyEntity
import com.example.data.local.VoiceRoomEntity
import com.example.data.remote.GeminiService
import com.example.data.remote.PhoneticBreakdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HelloTalkRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val chatDao = db.chatDao()
    private val voiceRoomDao = db.voiceRoomDao()
    private val momentDao = db.momentDao()
    private val vocabularyDao = db.vocabularyDao()

    val allPartners: Flow<List<UserEntity>> = userDao.getAllPartners()
    val liveVoiceRooms: Flow<List<VoiceRoomEntity>> = voiceRoomDao.getLiveVoiceRooms()
    val momentsFeed: Flow<List<MomentEntity>> = momentDao.getAllMoments()
    val vocabularyList: Flow<List<VocabularyEntity>> = vocabularyDao.getAllVocabulary()

    suspend fun initializeSeedData() = withContext(Dispatchers.IO) {
        // Seed Partners
        val existingPartners = userDao.getUserById("user_1")
        if (existingPartners == null) {
            val partners = listOf(
                UserEntity(
                    id = "ai_tutor",
                    name = "Gemini AI Language Tutor",
                    avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe",
                    nativeLanguage = "Global AI",
                    nativeFlag = "🤖",
                    targetLanguage = "All Languages",
                    targetFlag = "🌐",
                    targetLevel = "Master Tutor",
                    bio = "24/7 AI Language Exchange Partner. Practice conversation, ask grammar questions, and translate instantly!",
                    isOnline = true,
                    location = "Cloud AI",
                    matchScore = 100,
                    interests = "Grammar, Vocabulary, Pronunciation, Roleplay"
                ),
                UserEntity(
                    id = "user_1",
                    name = "Aoi Takahashi",
                    avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                    nativeLanguage = "Japanese",
                    nativeFlag = "🇯🇵",
                    targetLanguage = "English",
                    targetFlag = "🇺🇸",
                    targetLevel = "Intermediate",
                    bio = "Looking for English friends to practice daily! I love anime, cooking ramen, and traveling.",
                    isOnline = true,
                    location = "Tokyo, Japan",
                    matchScore = 98,
                    interests = "Anime, Cooking, J-Pop, Photography"
                ),
                UserEntity(
                    id = "user_2",
                    name = "Mateo Hernandez",
                    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                    nativeLanguage = "Spanish",
                    nativeFlag = "🇪🇸",
                    targetLanguage = "English",
                    targetFlag = "🇺🇸",
                    targetLevel = "Advanced",
                    bio = "Hola! Software developer learning English for work. Let's practice together!",
                    isOnline = true,
                    location = "Madrid, Spain",
                    matchScore = 95,
                    interests = "Tech, Football, Tapas, Guitar"
                ),
                UserEntity(
                    id = "user_3",
                    name = "Chen Wei",
                    avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                    nativeLanguage = "Mandarin",
                    nativeFlag = "🇨🇳",
                    targetLanguage = "English",
                    targetFlag = "🇺🇸",
                    targetLevel = "Beginner",
                    bio = "Hi all! I want to improve my spoken English and teach Chinese culture & Pinyin.",
                    isOnline = true,
                    location = "Shanghai, China",
                    matchScore = 92,
                    interests = "Tea, History, Calligraphy, Movies"
                ),
                UserEntity(
                    id = "user_4",
                    name = "Sophie Laurent",
                    avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                    nativeLanguage = "French",
                    nativeFlag = "🇫🇷",
                    targetLanguage = "English",
                    targetFlag = "🇺🇸",
                    targetLevel = "Intermediate",
                    bio = "Bonjour! Passionate about fashion, film, and cross-cultural friendship.",
                    isOnline = false,
                    location = "Paris, France",
                    matchScore = 90,
                    interests = "Cinema, Art, Bakery, Fashion"
                ),
                UserEntity(
                    id = "user_5",
                    name = "Min-jun Park",
                    avatarUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d",
                    nativeLanguage = "Korean",
                    nativeFlag = "🇰🇷",
                    targetLanguage = "English",
                    targetFlag = "🇺🇸",
                    targetLevel = "Intermediate",
                    bio = "K-pop & K-drama fan! Happy to help with Korean in exchange for English chat.",
                    isOnline = true,
                    location = "Seoul, South Korea",
                    matchScore = 96,
                    interests = "K-Pop, K-Drama, Travel, Gaming"
                )
            )
            userDao.insertUsers(partners)

            // Seed Live Voice Rooms
            val rooms = listOf(
                VoiceRoomEntity(
                    id = "room_1",
                    title = "🗣️ English-Japanese Friendly Chat & Slang",
                    topic = "Favorite Japanese Foods & Travel Spots",
                    hostId = "user_1",
                    hostName = "Aoi Takahashi",
                    hostAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                    nativeLang = "🇯🇵 Japanese",
                    targetLang = "🇺🇸 English",
                    speakersCount = 5,
                    listenersCount = 28,
                    isLive = true,
                    roomTags = "Slang, Travel, Anime"
                ),
                VoiceRoomEntity(
                    id = "room_2",
                    title = "🎉 Spanish Conversation & Pronunciation Practice",
                    topic = "Common Idioms in Daily Conversation",
                    hostId = "user_2",
                    hostName = "Mateo Hernandez",
                    hostAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                    nativeLang = "🇪🇸 Spanish",
                    targetLang = "🇺🇸 English",
                    speakersCount = 4,
                    listenersCount = 19,
                    isLive = true,
                    roomTags = "Idioms, Beginners Welcome"
                ),
                VoiceRoomEntity(
                    id = "room_3",
                    title = "🇨🇳 Chinese Pinyin & Tone Mastery Workshop",
                    topic = "Mastering the 4 Tones in Mandarin",
                    hostId = "user_3",
                    hostName = "Chen Wei",
                    hostAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                    nativeLang = "🇨🇳 Mandarin",
                    targetLang = "🇺🇸 English",
                    speakersCount = 3,
                    listenersCount = 42,
                    isLive = true,
                    roomTags = "Pinyin, Tones, Pronunciation"
                ),
                VoiceRoomEntity(
                    id = "room_4",
                    title = "☕ Paris Coffee Break: French & English Chit-Chat",
                    topic = "French Culture & Travel Tips",
                    hostId = "user_4",
                    hostName = "Sophie Laurent",
                    hostAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                    nativeLang = "🇫🇷 French",
                    targetLang = "🇺🇸 English",
                    speakersCount = 6,
                    listenersCount = 14,
                    isLive = true,
                    roomTags = "Culture, Casual, Paris"
                )
            )
            voiceRoomDao.insertVoiceRooms(rooms)

            // Seed Moments Feed
            val moments = listOf(
                MomentEntity(
                    id = "m_1",
                    authorId = "user_1",
                    authorName = "Aoi Takahashi",
                    authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                    nativeLang = "🇯🇵 Japanese",
                    targetLang = "🇺🇸 English",
                    contentText = "Konnichiwa everyone! How do you say 'I am really looking forward to the weekend' naturally in casual English? 🤔",
                    translatedText = "Hello everyone! How do you say 'I am really looking forward to the weekend' naturally in casual English?",
                    likesCount = 18,
                    commentsCount = 6,
                    category = "Question"
                ),
                MomentEntity(
                    id = "m_2",
                    authorId = "user_2",
                    authorName = "Mateo Hernandez",
                    authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                    nativeLang = "🇪🇸 Spanish",
                    targetLang = "🇺🇸 English",
                    contentText = "Today I visited a coffee shop and ordered completely in English! Small victory for my learning journey! ☕🇪🇸",
                    translatedText = "Today I visited a coffee shop and ordered completely in English! Small victory for my learning journey! ☕🇪🇸",
                    likesCount = 34,
                    commentsCount = 8,
                    category = "Daily Life"
                ),
                MomentEntity(
                    id = "m_3",
                    authorId = "user_5",
                    authorName = "Min-jun Park",
                    authorAvatar = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d",
                    nativeLang = "🇰🇷 Korean",
                    targetLang = "🇺🇸 English",
                    contentText = "Korean Phrase of the Day: '수고하셨습니다' (Sugohasyeossseumnida) means 'Great job today!'. Use it with coworkers or friends at the end of the day! 🇰🇷",
                    translatedText = "Korean Phrase of the Day: 'Great job today!'. Use it with coworkers or friends at the end of the day!",
                    likesCount = 52,
                    commentsCount = 12,
                    category = "Language Tip"
                )
            )
            momentDao.insertMoments(moments)

            // Seed Initial Chat Messages with Aoi
            chatDao.insertMessage(
                ChatMessageEntity(
                    chatId = "user_1",
                    senderId = "user_1",
                    senderName = "Aoi Takahashi",
                    text = "Konnichiwa! Nice to meet you! Are you learning Japanese?",
                    translatedText = "Hello! Nice to meet you! Are you learning Japanese?",
                    isFromMe = false
                )
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    chatId = "user_1",
                    senderId = "me",
                    senderName = "Me",
                    text = "Yes! I love Japanese culture. I can help you practice English too!",
                    isFromMe = true
                )
            )

            // Seed AI Tutor Chat
            chatDao.insertMessage(
                ChatMessageEntity(
                    chatId = "ai_tutor",
                    senderId = "ai_tutor",
                    senderName = "Gemini AI Tutor",
                    text = "Hello! I am your AI Language Partner powered by Gemini! 🤖 Ask me to translate, correct sentences, or chat in Japanese, Spanish, French, Mandarin, or Korean!",
                    isFromMe = false
                )
            )

            // Seed Vocabulary
            vocabularyDao.insertVocabulary(
                VocabularyEntity(
                    word = "수고하셨습니다",
                    phonetic = "Sugohasyeossseumnida",
                    translation = "Great job today / Thank you for your hard work",
                    sourceLang = "Korean",
                    targetLang = "English",
                    exampleSentence = "오늘도 수고하셨습니다!"
                )
            )
            vocabularyDao.insertVocabulary(
                VocabularyEntity(
                    word = "お疲れ様です (Otsukaresama desu)",
                    phonetic = "Otsukaresama desu",
                    translation = "Thank you for your hard work",
                    sourceLang = "Japanese",
                    targetLang = "English",
                    exampleSentence = "今日も一日お疲れ様でした。"
                )
            )
        }
    }

    // Chat Operations
    fun getChatMessages(chatId: String): Flow<List<ChatMessageEntity>> = chatDao.getMessagesForChat(chatId)

    suspend fun sendMessage(
        chatId: String,
        text: String,
        senderId: String = "me",
        senderName: String = "Me",
        isFromMe: Boolean = true
    ) {
        val userMsg = ChatMessageEntity(
            chatId = chatId,
            senderId = senderId,
            senderName = senderName,
            text = text,
            isFromMe = isFromMe
        )
        chatDao.insertMessage(userMsg)

        // If chatting with AI Tutor, trigger Gemini response
        if (chatId == "ai_tutor") {
            val aiResponseText = GeminiService.chatWithAiPartner(
                conversationHistory = emptyList(),
                userTargetLanguage = "Japanese",
                userMessage = text
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    chatId = "ai_tutor",
                    senderId = "ai_tutor",
                    senderName = "Gemini AI Tutor",
                    text = aiResponseText,
                    isFromMe = false
                )
            )
        }
    }

    suspend fun translateChatMessage(message: ChatMessageEntity, targetLang: String) {
        val translation = GeminiService.translate(message.text, targetLang)
        chatDao.updateMessage(message.copy(translatedText = translation, targetLang = targetLang))
    }

    suspend fun correctChatMessage(message: ChatMessageEntity, targetLang: String) {
        val correction = GeminiService.correctSentence(message.text, targetLang)
        chatDao.updateMessage(
            message.copy(
                correctionOriginal = message.text,
                correctionFixed = correction.correctedText,
                correctionNote = correction.explanation
            )
        )
    }

    // Moments Operations
    suspend fun createMoment(text: String, category: String, targetLang: String) {
        val newMoment = MomentEntity(
            id = "m_${System.currentTimeMillis()}",
            authorId = "me",
            authorName = "You (Learner)",
            authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
            nativeLang = "🇺🇸 English",
            targetLang = "🇯🇵 Japanese",
            contentText = text,
            category = category
        )
        momentDao.insertMoment(newMoment)
    }

    suspend fun toggleLikeMoment(moment: MomentEntity) {
        val updated = moment.copy(
            isLiked = !moment.isLiked,
            likesCount = if (!moment.isLiked) moment.likesCount + 1 else moment.likesCount - 1
        )
        momentDao.updateMoment(updated)
    }

    // Vocabulary Operations
    suspend fun saveVocabulary(word: String, phonetic: String, translation: String, example: String, sourceLang: String, targetLang: String) {
        vocabularyDao.insertVocabulary(
            VocabularyEntity(
                word = word,
                phonetic = phonetic,
                translation = translation,
                sourceLang = sourceLang,
                targetLang = targetLang,
                exampleSentence = example
            )
        )
    }

    suspend fun deleteVocabulary(id: Long) {
        vocabularyDao.deleteVocabulary(id)
    }

    suspend fun getUser(id: String): UserEntity? = userDao.getUserById(id)
}
