package gachon.database.instagram.data

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

data class LoginUser(
    val id: Int,
    val userName: String,
    val name: String?,
    val followerCnt: Int,
    val followingCnt: Int
)

data class Follow (
    val userId: Int,
    val userName: String,
    val name: String
)