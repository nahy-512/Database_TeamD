package gachon.database.instagram

data class User (
    val userId: Int,
    val userName: String,
    val password: String,
    val name: String,
    val email: String,
//    val birthday: LocalDateTime,
//    val gender: String,
//    val introduction: String
)