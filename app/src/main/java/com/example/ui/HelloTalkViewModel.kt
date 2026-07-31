package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatMessageEntity
import com.example.data.local.MomentEntity
import com.example.data.local.UserEntity
import com.example.data.local.VocabularyEntity
import com.example.data.local.VoiceRoomEntity
import com.example.data.repository.HelloTalkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HelloTalkViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HelloTalkRepository(application)

    val partners: StateFlow<List<UserEntity>> = repository.allPartners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val voiceRooms: StateFlow<List<VoiceRoomEntity>> = repository.liveVoiceRooms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val moments: StateFlow<List<MomentEntity>> = repository.momentsFeed
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vocabulary: StateFlow<List<VocabularyEntity>> = repository.vocabularyList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Selected Chat
    private val _selectedChatId = MutableStateFlow("user_1")
    val selectedChatId: StateFlow<String> = _selectedChatId.asStateFlow()

    val currentChatMessages: StateFlow<List<ChatMessageEntity>> = _selectedChatId
        .flatMapLatest { chatId -> repository.getChatMessages(chatId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Live Voice Room
    private val _activeVoiceRoom = MutableStateFlow<VoiceRoomEntity?>(null)
    val activeVoiceRoom: StateFlow<VoiceRoomEntity?> = _activeVoiceRoom.asStateFlow()

    // Voice Room Stage State
    private val _isMicMuted = MutableStateFlow(true)
    val isMicMuted: StateFlow<Boolean> = _isMicMuted.asStateFlow()

    private val _isHandRaised = MutableStateFlow(false)
    val isHandRaised: StateFlow<Boolean> = _isHandRaised.asStateFlow()

    // Filter states
    private val _partnerSearchQuery = MutableStateFlow("")
    val partnerSearchQuery: StateFlow<String> = _partnerSearchQuery.asStateFlow()

    private val _selectedLanguageFilter = MutableStateFlow("All")
    val selectedLanguageFilter: StateFlow<String> = _selectedLanguageFilter.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeSeedData()
        }
    }

    fun selectChat(chatId: String) {
        _selectedChatId.value = chatId
    }

    fun setPartnerSearchQuery(query: String) {
        _partnerSearchQuery.value = query
    }

    fun setSelectedLanguageFilter(lang: String) {
        _selectedLanguageFilter.value = lang
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(chatId = _selectedChatId.value, text = text)
        }
    }

    fun translateMessage(message: ChatMessageEntity, targetLang: String = "English") {
        viewModelScope.launch {
            repository.translateChatMessage(message, targetLang)
        }
    }

    fun correctMessage(message: ChatMessageEntity, targetLang: String = "English") {
        viewModelScope.launch {
            repository.correctChatMessage(message, targetLang)
        }
    }

    fun joinVoiceRoom(room: VoiceRoomEntity) {
        _activeVoiceRoom.value = room
    }

    fun leaveVoiceRoom() {
        _activeVoiceRoom.value = null
        _isMicMuted.value = true
        _isHandRaised.value = false
    }

    fun toggleMic() {
        _isMicMuted.value = !_isMicMuted.value
    }

    fun toggleHandRaise() {
        _isHandRaised.value = !_isHandRaised.value
    }

    fun createMoment(text: String, category: String = "General") {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.createMoment(text = text, category = category, targetLang = "Japanese")
        }
    }

    fun toggleLikeMoment(moment: MomentEntity) {
        viewModelScope.launch {
            repository.toggleLikeMoment(moment)
        }
    }

    fun saveWordToVocabulary(word: String, phonetic: String, translation: String, example: String) {
        viewModelScope.launch {
            repository.saveVocabulary(
                word = word,
                phonetic = phonetic,
                translation = translation,
                example = example,
                sourceLang = "Japanese",
                targetLang = "English"
            )
        }
    }

    fun deleteVocabulary(id: Long) {
        viewModelScope.launch {
            repository.deleteVocabulary(id)
        }
    }
}
