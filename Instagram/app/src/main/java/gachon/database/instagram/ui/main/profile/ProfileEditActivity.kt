package gachon.database.instagram.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import gachon.database.instagram.R
import gachon.database.instagram.data.LoginUser
import gachon.database.instagram.databinding.ActivityProfileEditBinding
import gachon.database.instagram.ui.signin.LoginActivity
import kotlin.math.log

class ProfileEditActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileEditBinding

    private var gson: Gson = Gson()
    private var userName = ""
//    private var user: LoginUser = LoginUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initClickListener()
        setInit()
    }

    private fun initClickListener() {
        // 닫기 버튼 클릭
        binding.profileEditBackIv.setOnClickListener {
            finish() // 뒤로가기
        }

        // 저장 버튼 클릭
        binding.profileEditSaveIv.setOnClickListener {
            //TODO: 프로필 편집 장태 저장
            finish()
        }

        // 개인정보 설정
        binding.profileEditSetPersonalInformationTv.setOnClickListener {
            val intent = Intent(this, EditPasswordActivity::class.java)
            intent.putExtra("user_name", userName)
            // 화면 이동
            startActivity(intent)
        }

        // 로그아웃
        binding.profileEditLogoutTv.setOnClickListener {
            logout()
        }
    }

    private fun setInit() {
        // intent가 넘어왔는지 확인
        intent.getStringExtra("user")?.let { userJson ->

            // 넘어왔다면 song 인스턴스에 gson 형태로 받아온 데이터를 넣어줌
            val user = gson.fromJson(userJson, LoginUser::class.java)
            Log.e("user", user.toString()) // 로그 확인

            userName = user.userName

            // 값 넣어주기
            with(binding) {
                profileEditNameEt.setText(user.name)
                profileEditUserNameEt.setText(userName)
                if (user.profileImage.isNotEmpty()) {
                    Glide.with(baseContext).load(user.profileImage).error(R.drawable.ic_profile_default).into(profileEditImgIv)
                }
            }
        }
    }

    private fun logout() {
        // 토큰 비우기 (user_id)
        getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply()
        // 로그아웃 토스트 출력
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        // 로그인 화면으로 이동하기
        startActivity(Intent(this, LoginActivity::class.java))
        // 스택에 쌓여있는 액티비티들을 모두 종료 (메인, 프로필 편집)
        finishAffinity()
    }
}