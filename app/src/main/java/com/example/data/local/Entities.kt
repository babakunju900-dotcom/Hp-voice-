package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarUrl: String,
    val nativeLanguage: String,
    val nativeFlag: String,
    val targetLanguage: String,
    val targetFlag: String,
    val targetLevel: String, // "Native", "Beginner", "Intermediate", "Advanced"
    val bio: String,
    val isOnline: Boolean,
    val location: String,
    val matchScore: Int,
    val interests: String,
    val audioIntroUrl: String? = null
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val translatedText: String? = null,
    val originalLang: String? = null,
    val targetLang: String? = null,
    val correctionOriginal: String? = null,
    val correctionFixed: String? = null,
    val correctionNote: String? = null,
    val isVoiceNote: Boolean = false,
    val voiceDurationSec: Int = 0,
    val isFromMe: Boolean = false
)

@Entity(tableName = "voice_rooms")
data class VoiceRoomEntity(
    @PrimaryKey val id: String,
    val title: String,
    val topic: String,
    val hostId: String,
    val hostName: String,
    val hostAvatar: String,
    val nativeLang: String,
    val targetLang: String,
    val speakersCount: Int,
    val listenersCount: Int,
    val isLive: Boolean = true,
    val roomTags: String
)

@Entity(tableName = "moments")
data class MomentEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String,
    val nativeLang: String,
    val targetLang: String,
    val contentText: String,
    val translatedText: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLiked: Boolean = false,
    val category: String = "General"
)

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val phonetic: String,
    val translation: String,
    val sourceLang: String,
    val targetLang: String,
    val exampleSentence: String,
    val reviewCount: Int = 0,
    val isMastered: Boolean = false,
    val addedTimestamp: Long = System.currentTimeMillis()
)
