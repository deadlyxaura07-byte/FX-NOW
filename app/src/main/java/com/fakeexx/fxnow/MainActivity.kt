package com.fakeexx.fxnow

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.json.JSONArray
import org.json.JSONObject

private val Ink = Color(0xFF08090D)
private val Panel = Color(0xFF151720)
private val Gold = Color(0xFFE6C987)
private val Muted = Color(0xFF9A9EAA)

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent { FxNow(this) }
 }
}

@Composable
private fun FxNow(ctx: Context) {
 var page by remember { mutableStateOf("Home") }
 var query by remember { mutableStateOf("") }
 var command by remember { mutableStateOf("") }
 var status by remember { mutableStateOf("Ready when you are, boss.") }
 var tasks by remember { mutableStateOf(loadTasks(ctx)) }
 val apps = remember {
  val pm = ctx.packageManager
  val i = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
  @Suppress("DEPRECATION")
  pm.queryIntentActivities(i, PackageManager.MATCH_ALL).map { it.loadLabel(pm).toString() to it.activityInfo.packageName }.distinctBy { it.second }.sortedBy { it.first.lowercase() }
 }
 MaterialTheme(colorScheme = darkColorScheme(primary = Gold, background = Ink, surface = Panel)) {
  Column(Modifier.fillMaxSize().background(Ink).padding(18.dp)) {
   Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    Column(Modifier.weight(1f)) {
     Text("FX  /  NOW", color = Gold, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
     Text("Your phone. Your command.", color = Muted, fontSize = 12.sp)
    }
    Text("● SYSTEM READY", color = Gold, fontSize = 10.sp)
   }
   Spacer(Modifier.height(22.dp))
   when(page) {
    "Home" -> {
     Surface(Modifier.fillMaxWidth(), color = Panel, shape = RoundedCornerShape(26.dp), border = BorderStroke(1.dp, Color(0xFF33313A))) {
      Column(Modifier.padding(20.dp)) {
       Text("FX INTELLIGENCE", color = Muted, fontSize = 11.sp, letterSpacing = 2.sp)
       Spacer(Modifier.height(12.dp))
       Text("Yo, boss.", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.SemiBold)
       Text("What are we getting done today?", color = Muted)
       Spacer(Modifier.height(18.dp))
       OutlinedTextField(command, { command = it }, Modifier.fillMaxWidth(), placeholder = { Text("Try: show apps / new task idea") }, singleLine = true)
       Spacer(Modifier.height(8.dp))
       Button(onClick = {
        val c = command.trim()
        when {
         c.equals("help", true) -> status = "Commands: show apps, show tasks, status, new task <title>, settings"
         c.equals("show apps", true) || c.equals("open apps", true) -> page = "Apps"
         c.equals("show tasks", true) || c.equals("open tasks", true) -> page = "Tasks"
         c.equals("settings", true) || c.equals("open settings", true) -> page = "Settings"
         c.equals("status", true) -> status = "FX NOW active • " + apps.size + " apps • " + tasks.count { !it.second } + " pending tasks"
         c.startsWith("new task ", true) -> { tasks = tasks + (c.substringAfter("new task ").trim() to false); saveTasks(ctx, tasks); status = "Task added." }
         else -> status = "Type help to see supported commands."
        }
        command = ""
       }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("RUN COMMAND  ↗") }
       Spacer(Modifier.height(10.dp))
       Text(status, color = Gold, fontSize = 12.sp)
      }
     }
     Spacer(Modifier.height(16.dp))
     Text("QUICK CONTROL", color = Muted, fontSize = 11.sp, letterSpacing = 2.sp)
     Spacer(Modifier.height(10.dp))
     Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      QuickCard("Apps", apps.size.toString() + " detected", Modifier.weight(1f)) { page = "Apps" }
      QuickCard("Tasks", tasks.count { !it.second }.toString() + " pending", Modifier.weight(1f)) { page = "Tasks" }
     }
     Spacer(Modifier.height(10.dp))
     QuickCard("FX AI", "AI provider integration foundation", Modifier.fillMaxWidth()) { page = "FX AI" }
    }
    "Apps" -> {
     Text("APP VAULT", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
     OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), placeholder = { Text("Search installed apps") }, singleLine = true)
     LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(apps.filter { it.first.contains(query, true) }) { app ->
       Surface(Modifier.fillMaxWidth().clickable {
        ctx.packageManager.getLaunchIntentForPackage(app.second)?.let { ctx.startActivity(it) }
       }, color = Panel, shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
         Text(app.first, color = Color.White, modifier = Modifier.weight(1f))
         Icon(Icons.Default.ArrowOutward, null, tint = Gold)
        }
       }
      }
     }
    }
    "Tasks" -> {
     var newTask by remember { mutableStateOf("") }
     Text("MISSION BOARD", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
     Row(verticalAlignment = Alignment.CenterVertically) {
      OutlinedTextField(newTask, { newTask = it }, Modifier.weight(1f), placeholder = { Text("Add a task") }, singleLine = true)
      IconButton(onClick = { if (newTask.isNotBlank()) { tasks = tasks + (newTask.trim() to false); saveTasks(ctx, tasks); newTask = "" } }) { Icon(Icons.Default.Add, "Add", tint = Gold) }
     }
     LazyColumn {
      items(tasks.toList()) { task ->
       Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(task.second, { done -> tasks = tasks.map { if (it == task) it.first to done else it }; saveTasks(ctx, tasks) })
        Text(task.first, color = if (task.second) Muted else Color.White, modifier = Modifier.weight(1f))
        IconButton(onClick = { tasks = tasks.filterNot { it == task }; saveTasks(ctx, tasks) }) { Icon(Icons.Default.DeleteOutline, "Delete", tint = Muted) }
       }
      }
     }
    }
    "FX AI" -> {
     Text("FX AI CORE", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
     InfoCard("AI is not connected yet", "Online research, autonomous coding and voice features need a configured AI provider, internet access and explicit permissions.")
     InfoCard("Built-in commands", "Use help, show apps, show tasks, status, or new task <title> from Home.")
     InfoCard("Android safety", "FX NOW uses supported Android intents and permissions. It cannot bypass system security or control every app without authorization.")
    }
    else -> {
     Text("CONTROL SETTINGS", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
     InfoCard("Set as Home app", "Choose FX NOW in Android Settings → Apps → Default apps → Home app.")
     Button(onClick = { ctx.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }, Modifier.fillMaxWidth()) { Text("OPEN ANDROID SETTINGS") }
     InfoCard("Local privacy", "Tasks are saved on this device. No cloud sync or account is enabled in this MVP.")
    }
   }
   Spacer(Modifier.weight(1f))
   NavigationBar(containerColor = Panel) {
    listOf("Home", "Apps", "Tasks", "FX AI", "Settings").forEach { item ->
     NavigationBarItem(selected = page == item, onClick = { page = item }, icon = {
      Icon(when(item) { "Home" -> Icons.Default.Home; "Apps" -> Icons.Default.GridView; "Tasks" -> Icons.Default.CheckCircle; "FX AI" -> Icons.Default.AutoAwesome; else -> Icons.Default.Settings }, null)
     }, label = { Text(item, fontSize = 9.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Gold, selectedTextColor = Gold, indicatorColor = Color(0xFF34302A), unselectedIconColor = Muted, unselectedTextColor = Muted))
    }
   }
  }
 }
}

@Composable
private fun QuickCard(title: String, detail: String, modifier: Modifier = Modifier, click: () -> Unit) {
 Surface(modifier.clickable { click() }, color = Panel, shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Color(0xFF292B33))) {
  Column(Modifier.padding(16.dp)) {
   Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
   Spacer(Modifier.height(5.dp))
   Text(detail, color = Muted, fontSize = 11.sp)
  }
 }
}
@Composable
private fun InfoCard(title: String, detail: String) {
 Surface(Modifier.fillMaxWidth().padding(bottom = 10.dp), color = Panel, shape = RoundedCornerShape(18.dp)) {
  Column(Modifier.padding(16.dp)) {
   Text(title, color = Gold, fontWeight = FontWeight.SemiBold)
   Spacer(Modifier.height(6.dp))
   Text(detail, color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
  }
 }
}
private fun loadTasks(ctx: Context): List<Pair<String, Boolean>> = try {
 val a = JSONArray(ctx.getSharedPreferences("fxnow", Context.MODE_PRIVATE).getString("tasks", "[]"))
 (0 until a.length()).map { val o = a.getJSONObject(it); o.getString("title") to o.getBoolean("done") }
} catch (_: Exception) { emptyList() }
private fun saveTasks(ctx: Context, tasks: List<Pair<String, Boolean>>) {
 val a = JSONArray()
 tasks.forEach { (title, done) -> a.put(JSONObject().put("title", title).put("done", done)) }
 ctx.getSharedPreferences("fxnow", Context.MODE_PRIVATE).edit().putString("tasks", a.toString()).apply()
}