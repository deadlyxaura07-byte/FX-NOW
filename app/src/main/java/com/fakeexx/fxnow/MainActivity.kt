package com.fakeexx.fxnow
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.concurrent.thread

private val Bg = Color(0xFF050711)
private val Panel = Color(0xFF111827)
private val Cyan = Color(0xFF69E4FF)
private val Violet = Color(0xFF9B8CFF)
private val Soft = Color(0xFF9BA9BD)

class MainActivity : ComponentActivity() {
 private var tts: TextToSpeech? = null
 private val voiceResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
  val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
  if (!spoken.isNullOrBlank()) onVoiceText?.invoke(spoken)
 }
 private var onVoiceText: ((String) -> Unit)? = null
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  tts = TextToSpeech(this) { status -> if (status == TextToSpeech.SUCCESS) tts?.language = Locale.getDefault() }
  setContent { FxNowScreen(
   onListen = { callback -> onVoiceText = callback; try { voiceResult.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
    putExtra(RecognizerIntent.EXTRA_PROMPT, "FX NOW ko command dein")
   }) } catch (_: Exception) { callback("Voice recognition is not available on this device.") } },
   onSpeak = { tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, "fxnow-reply") },
   onStop = { tts?.stop() }
  ) }
 }
 override fun onDestroy() { tts?.shutdown(); super.onDestroy() }
}

@Composable
private fun FxNowScreen(onListen: ((String) -> Unit) -> Unit, onSpeak: (String) -> Unit, onStop: () -> Unit) {
 var input by remember { mutableStateOf("") }
 var baseUrl by remember { mutableStateOf("") }
 var token by remember { mutableStateOf("") }
 var provider by remember { mutableStateOf("auto") }
 var showSettings by remember { mutableStateOf(false) }
 var speakReplies by remember { mutableStateOf(true) }
 var busy by remember { mutableStateOf(false) }
 var status by remember { mutableStateOf("SYSTEM READY • CONNECT A BACKEND IN SETTINGS") }
 var messages by remember { mutableStateOf(listOf("FX NOW" to "Hey boss. I'm ready. Connect your secure AI backend in Settings to start real conversations.")) }
 val pulse = rememberInfiniteTransition(label = "orb")
 val glow by pulse.animateFloat(initialValue = 0.94f, targetValue = 1.06f, animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse), label = "glow")
 MaterialTheme(colorScheme = darkColorScheme(primary = Cyan, background = Bg, surface = Panel)) {
  Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0B1222), Bg, Color(0xFF090713)))).padding(horizontal = 16.dp, vertical = 12.dp)) {
   Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
    Column(Modifier.weight(1f)) {
     Text("FX  NOW", color = Cyan, fontSize = 21.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
     Text("PERSONAL INTELLIGENCE SYSTEM", color = Soft, fontSize = 9.sp, letterSpacing = 2.sp)
    }
    IconButton(onClick = { showSettings = true }) { Icon(Icons.Default.Settings, "Settings", tint = Cyan) }
   }
   Spacer(Modifier.height(8.dp))
   Box(Modifier.fillMaxWidth().weight(0.9f), contentAlignment = Alignment.Center) {
    Box(Modifier.size(176.dp).scale(glow).background(Brush.radialGradient(listOf(Color(0x5569E4FF), Color(0x229B8CFF), Color.Transparent)), CircleShape), contentAlignment = Alignment.Center) {
     Box(Modifier.size(112.dp).background(Brush.linearGradient(listOf(Color(0xFF1D4564), Color(0xFF3A2C68), Color(0xFF10192B))), CircleShape), contentAlignment = Alignment.Center) {
      Text("FX", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
     }
    }
    Column(Modifier.align(Alignment.BottomCenter), horizontalAlignment = Alignment.CenterHorizontally) {
     Text("J.A.R.V.I.S. MODE", color = Cyan, fontSize = 10.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
     Spacer(Modifier.height(5.dp))
     Text(status, color = Soft, fontSize = 9.sp)
    }
   }
   Text("CONVERSATION", color = Soft, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
   Spacer(Modifier.height(6.dp))
   LazyColumn(Modifier.weight(1.25f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items(messages) { msg ->
     Surface(color = if (msg.first == "You") Color(0xFF152C3B) else Panel, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
      Column(Modifier.padding(12.dp)) {
       Text(msg.first.uppercase(), color = if (msg.first == "You") Cyan else Violet, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
       Spacer(Modifier.height(4.dp))
       Text(msg.second, color = Color(0xFFE7EDF7), fontSize = 14.sp, lineHeight = 20.sp)
      }
     }
    }
   }
   Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    OutlinedTextField(input, { input = it }, modifier = Modifier.weight(1f), placeholder = { Text("Ask FX NOW anything…", fontSize = 13.sp) }, shape = RoundedCornerShape(18.dp), maxLines = 4)
    FilledIconButton(onClick = { onListen { heard -> input = heard } }, modifier = Modifier.size(48.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF192C42))) { Icon(Icons.Default.Mic, "Voice", tint = Cyan) }
    FilledIconButton(onClick = {
     val prompt = input.trim()
     if (prompt.isNotEmpty() && !busy) {
      messages = messages + ("You" to prompt)
      input = ""
      busy = true
      status = "THINKING…"
      val history = messages.filter { it.first == "You" || it.first == "FX NOW" }.map { (role, content) -> JSONObject().put("role", if (role == "You") "user" else "assistant").put("content", content) }
      thread {
       var reply: String
       try {
        if (baseUrl.isBlank()) throw IllegalStateException("Backend URL missing. Open Settings and enter your deployed HTTPS backend URL.")
        val conn = (URL(baseUrl.trimEnd('/') + "/api/chat").openConnection() as HttpURLConnection)
        conn.requestMethod = "POST"; conn.connectTimeout = 12000; conn.readTimeout = 45000; conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")
        if (token.isNotBlank()) conn.setRequestProperty("Authorization", "Bearer $token")
        val body = JSONObject().put("messages", JSONArray(history)).put("provider", if (provider == "auto") JSONObject.NULL else provider).toString()
        conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
        val responseCode = conn.responseCode
        val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
        val response = stream.bufferedReader().use { it.readText() }
        val json = JSONObject(response)
        if (responseCode !in 200..299) throw IllegalStateException(json.optString("error", "AI request failed (" + responseCode + ")"))
        reply = json.optString("reply", "No reply received.")
        conn.disconnect()
       } catch (e: Exception) { reply = "Setup needed: " + (e.message ?: "Could not reach AI backend.") }
       val answer = reply
       android.os.Handler(android.os.Looper.getMainLooper()).post {
        messages = messages + ("FX NOW" to answer)
        status = if (answer.startsWith("Setup needed:")) "BACKEND NEEDS SETUP" else "RESPONSE RECEIVED"
        busy = false
        if (speakReplies) onSpeak(answer)
       }
      }
     }
    }, enabled = !busy, modifier = Modifier.size(48.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Cyan)) {
     if (busy) CircularProgressIndicator(Modifier.size(20.dp), color = Bg, strokeWidth = 2.dp) else Icon(Icons.Default.Send, "Send", tint = Bg)
    }
   }
   Spacer(Modifier.height(8.dp))
   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
    Text(if (busy) "PROCESSING REQUEST" else "MULTI-PROVIDER READY", color = Soft, fontSize = 9.sp, letterSpacing = 1.sp)
    TextButton(onClick = onStop) { Icon(Icons.Default.Stop, null, tint = Violet, modifier = Modifier.size(14.dp)); Text(" STOP VOICE", color = Violet, fontSize = 9.sp) }
   }
  }
  if (showSettings) AlertDialog(
   onDismissRequest = { showSettings = false },
   title = { Text("FX NOW • CONFIGURATION", color = Cyan) },
   text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Text("Backend URL (HTTPS)", color = Soft, fontSize = 12.sp)
    OutlinedTextField(baseUrl, { baseUrl = it }, singleLine = true, placeholder = { Text("https://your-server.example") })
    Text("Optional server access token", color = Soft, fontSize = 12.sp)
    OutlinedTextField(token, { token = it }, singleLine = true, placeholder = { Text("Access token") })
    Text("AI provider", color = Soft, fontSize = 12.sp)
    var expanded by remember { mutableStateOf(false) }
    Box {
     OutlinedButton(onClick = { expanded = true }) { Text(when(provider) { "gemini" -> "Gemini • Free-tier priority"; "openai" -> "OpenAI-compatible"; "anthropic" -> "Claude"; else -> "Automatic provider" }, color = Cyan) }
     DropdownMenu(expanded, { expanded = false }) {
      listOf("auto" to "Automatic (Gemini priority)", "gemini" to "Gemini", "openai" to "OpenAI-compatible", "anthropic" to "Claude").forEach { (id, label) ->
       DropdownMenuItem(text = { Text(label) }, onClick = { provider = id; expanded = false })
      }
     }
    }
    Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(speakReplies, { speakReplies = it }); Text("Speak replies aloud", color = Soft, fontSize = 13.sp) }
    Text("Provider API keys must stay on your backend server; do not paste them here.", color = Soft, fontSize = 11.sp)
   } },
   confirmButton = { TextButton(onClick = { showSettings = false; status = if (baseUrl.isBlank()) "BACKEND URL REQUIRED" else "BACKEND URL SAVED FOR THIS SESSION" }) { Text("SAVE") } },
   dismissButton = { TextButton(onClick = { showSettings = false; onStop() }) { Text("CLOSE") } }
  )
 }
}
