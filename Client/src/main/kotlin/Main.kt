import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.Socket

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Login Form") {
        loginForm()
    }
}

@Composable
fun loginForm() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var serverResponse by remember { mutableStateOf<String?>(null) }
    var loginSuccessful by remember { mutableStateOf(false) }  // Флаг успешного входа

    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (loginSuccessful) {
                // Интерфейс после успешного входа
                Text("Вход выполнен успешно!", style = MaterialTheme.typography.h5)
                Text("Ответ сервера: $serverResponse", style = MaterialTheme.typography.body1)
            } else {
                // Интерфейс для входа
                Text(text = "Please log in", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    serverResponse = null
                    loginSuccessful = false
                    sendLoginData(username, password) { response ->
                        serverResponse = response
                        loginSuccessful = true
                    }
                }) {
                    Text("Login")
                }
            }
        }
    }
}


fun sendLoginData(username: String, password: String, onResult: (String) -> Unit) = CoroutineScope(Dispatchers.IO).launch {
    val hostname = "localhost"
    val port = 8090
    try {
        Socket(hostname, port).use { socket ->
            println("Соединение установлено")
            val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            writer.write("username=$username&password=$password\n")
            writer.flush()

            println("Данные отправлены, ожидание ответа")
            val response = reader.readLine()
            println("Ответ получен: $response")
            withContext(Dispatchers.Main) {
                onResult(response ?: "Нет ответа от сервера")
            }
        }
    } catch (e: Exception) {
        println("Ошибка при соединении или чтении данных: ${e.message}")
        withContext(Dispatchers.Main) {
            onResult("Ошибка: ${e.message}")
        }
    }
}


