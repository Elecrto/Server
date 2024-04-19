import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class Server(val port: Int = 8090) {
    private val serverSocket = ServerSocket(port)

    fun start() {
        println("Сервер запущен и ожидает подключений...")

        while (true) {
            val clientSocket = serverSocket.accept()
            println("Клиент подключен: ${clientSocket.inetAddress.hostAddress}")

            val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val writer = PrintWriter(clientSocket.getOutputStream(), true)

            writer.println(1)
            try {
                val username = reader.readLine()
                val password = reader.readLine()
                val db = DataBase()
                val value = db.checkUser(username, password)
                if (value != null) {
                    writer.println(value)  // Отправляем значение value клиенту
                    println("Значение отправлено клиенту: $value")
                } else {
                    writer.println("Пользователь не найден или ошибка данных.")
                }
            } catch (e: Exception) {
                println("Ошибка при обработке запроса: ${e.message}")
                writer.println("Ошибка на сервере.")
            } finally {
                clientSocket.close()
            }
        }
    }

    private fun checkCredentials(username: String, password: String): Boolean {
        val db = DataBase()
        val userValue = db.checkUser(username, password)
        println("Значение для пользователя: $userValue")
        return userValue != null  // Возвращает true, если значение поля value получено, иначе false
    }

}