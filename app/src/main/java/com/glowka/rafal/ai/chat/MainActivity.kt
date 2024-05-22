package com.glowka.rafal.ai.chat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glowka.rafal.ai.chat.ui.chat.ChatScreen
import com.glowka.rafal.ai.chat.ui.chat.ChatViewModel
import com.glowka.rafal.ai.chat.ui.theme.GeminiAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {

    private val _hasAudioPermission = MutableStateFlow(false)
    val hasAudioPermission: StateFlow<Boolean> = _hasAudioPermission.asStateFlow()

    private val chatViewModel by lazy { ChatViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val hasPermissions by hasAudioPermission.collectAsState()

            GeminiAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    if (!hasPermissions) {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = stringResource(id = R.string.missing_permission)
                        )
                    } else {
                        ChatScreen(chatViewModel)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val privilageRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            Log.d("Activity", "Permission results: $result")
            _hasAudioPermission.value = result[android.Manifest.permission.RECORD_AUDIO] == true
        }
        val privileges = arrayOf(
            android.Manifest.permission.RECORD_AUDIO
        )
        privilageRequest.launch(privileges)
    }
}