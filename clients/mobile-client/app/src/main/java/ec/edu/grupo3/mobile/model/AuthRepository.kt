package ec.edu.grupo3.mobile.model

object AuthRepository {
    private const val VALID_USER = "MONSTER"
    private const val VALID_PASS = "MONSTER9"

    fun authenticate(username: String, password: String): Boolean {
        return VALID_USER == username.trim() && VALID_PASS == password
    }
}