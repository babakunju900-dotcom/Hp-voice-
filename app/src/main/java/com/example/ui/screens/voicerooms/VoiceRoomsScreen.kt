package com.example.ui.screens.voicerooms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.VoiceRoomEntity
import com.example.ui.HelloTalkViewModel
import com.example.ui.components.AnimatedAudioWaveform
import com.example.ui.components.VoiceStageAvatar

@Composable
fun VoiceRoomsScreen(
    viewModel: HelloTalkViewModel,
    modifier: Modifier = Modifier
) {
    val rooms by viewModel.voiceRooms.collectAsState()
    val activeRoom by viewModel.activeVoiceRoom.collectAsState()

    if (activeRoom != null) {
        // Active Live Audio Exchange Room View
        LiveVoiceRoomView(viewModel = viewModel, room = activeRoom!!)
    } else {
        // Room Explorer List
        VoiceRoomExplorerView(
            rooms = rooms,
            onJoinRoom = { viewModel.joinVoiceRoom(it) }
        )
    }
}

@Composable
fun VoiceRoomExplorerView(
    rooms: List<VoiceRoomEntity>,
    onJoinRoom: (VoiceRoomEntity) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Demo room creation */ },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = Color.White
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Create Voice Room")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start Room", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Bar Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Live Voice Rooms",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFF3D00))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LIVE NOW",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Join active audio spaces to practice speaking with native speakers in real-time.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rooms List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rooms) { room ->
                    VoiceRoomCard(room = room, onJoinRoom = { onJoinRoom(room) })
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceRoomCard(
    room: VoiceRoomEntity,
    onJoinRoom: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onJoinRoom() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title & Language Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )

                AnimatedAudioWaveform(isPlaying = true, barColor = MaterialTheme.colorScheme.tertiary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Topic Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "💬 Topic: ${room.topic}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Host Info & Counters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = room.hostAvatar,
                        contentDescription = room.hostName,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Host: ${room.hostName}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${room.nativeLang} ➔ ${room.targetLang}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Speakers",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${room.speakersCount}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 2.dp, end = 8.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "Listeners",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${room.listenersCount}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Join Button
            Button(
                onClick = onJoinRoom,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Join Voice Room", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LiveVoiceRoomView(
    viewModel: HelloTalkViewModel,
    room: VoiceRoomEntity
) {
    val isMicMuted by viewModel.isMicMuted.collectAsState()
    val isHandRaised by viewModel.isHandRaised.collectAsState()
    var roomChatMessage by remember { mutableStateOf("") }
    var roomChatStream by remember {
        mutableStateOf(
            listOf(
                "Aoi Takahashi: Welcome everyone! Let's talk about Japanese food! 🍜",
                "Mateo Hernandez: Hello from Spain! 🇪🇸",
                "System: Gemini AI Tutor joined as Audio Assistant"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF3D00))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE AUDIO STAGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFFF3D00),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            IconButton(
                onClick = { viewModel.leaveVoiceRoom() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFC62828))
            ) {
                Icon(Icons.Default.CallEnd, contentDescription = "Leave Room", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Topic Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Topic: ${room.topic}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "AI Suggested Question: What is your absolute favorite comfort meal?",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stage Speakers Grid
        Text(
            text = "STAGE SPEAKERS (${room.speakersCount})",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.height(180.dp)
        ) {
            item {
                VoiceStageAvatar(
                    name = room.hostName,
                    avatarUrl = room.hostAvatar,
                    isSpeaking = true,
                    isHost = true,
                    isMuted = false
                )
            }
            item {
                VoiceStageAvatar(
                    name = "Mateo",
                    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                    isSpeaking = false,
                    isMuted = true
                )
            }
            item {
                VoiceStageAvatar(
                    name = "Chen",
                    avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                    isSpeaking = false,
                    isMuted = false
                )
            }
            item {
                VoiceStageAvatar(
                    name = "You (Learner)",
                    avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
                    isSpeaking = !isMicMuted,
                    isMuted = isMicMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Room Chat Stream
        Text(
            text = "LIVE ROOM CHAT",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E1E1E))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(roomChatStream) { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Room Controls Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mic Toggle
            IconButton(
                onClick = { viewModel.toggleMic() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isMicMuted) Color(0xFFC62828) else MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mic Toggle",
                    tint = Color.White
                )
            }

            // Raise Hand Toggle
            IconButton(
                onClick = { viewModel.toggleHandRaise() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isHandRaised) Color(0xFFFF9800) else Color(0xFF2C2C2C))
            ) {
                Icon(
                    imageVector = Icons.Default.PanTool,
                    contentDescription = "Raise Hand",
                    tint = Color.White
                )
            }

            // Send Chat Input
            OutlinedTextField(
                value = roomChatMessage,
                onValueChange = { roomChatMessage = it },
                placeholder = { Text("Chat in room...", color = Color.Gray, fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(20.dp),
                trailingIcon = {
                    IconButton(onClick = {
                        if (roomChatMessage.isNotBlank()) {
                            roomChatStream = roomChatStream + "You: $roomChatMessage"
                            roomChatMessage = ""
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                singleLine = true
            )
        }
    }
}
