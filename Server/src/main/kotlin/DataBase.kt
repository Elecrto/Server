import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

class DataBase {
    private val url = "jdbc:mysql://localhost:3306/users"
    private val username = "root"
    private val password = "root"
    private var connection: Connection? = null

    init{
        try {
            connection = DriverManager.getConnection(url, username, password)
            println("Соединение с базой данных успешно установлено!")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkUser(username: String, password: String): String? {
        try {
            val statement = connection?.prepareStatement("SELECT value FROM users WHERE username = ? AND password = ?")
            statement?.setString(1, username)
            statement?.setString(2, password)
            val resultSet = statement?.executeQuery()
            if (resultSet != null && resultSet.next()) {
                return resultSet.getString("value")  // Возвращаем значение поля value
            }
        } catch (e: Exception) {
            println("Ошибка при проверке пользователя: ${e.message}")
        } finally {
            connection?.close()  // Закрыть соединение после завершения запроса
        }
        return null  // Возвращаем null, если пользователь не найден или произошла ошибка
    }
}
