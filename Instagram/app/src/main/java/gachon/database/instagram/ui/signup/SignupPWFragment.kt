package gachon.database.instagram.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gachon.database.instagram.R
import gachon.database.instagram.databinding.FragmentSignupPwBinding

class SignupPWFragment : Fragment() {

    lateinit var binding: FragmentSignupPwBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupPwBinding.inflate(inflater, container, false)

        initClickListener()

        return binding.root
    }

    private fun initClickListener() {
        // 개인정보 설정
        binding.signupPwNextBtn.setOnClickListener {
            val userName = arguments?.getString("user_name")
            val password = binding.signupPwEt.text.toString()
            Log.d("SignupPWFrag", "userName: $userName")

            (context as SignupActivity).supportFragmentManager.beginTransaction()
                .add(R.id.signup_frm, SignupFinishFragment().apply {
                    arguments = Bundle().apply {
                        putString("user_name", userName) // 사용자 이름
                        putString("password", password) // 비밀번호
                    }
                })
                .addToBackStack(null) // 백 스택에 트랜잭션을 추가
                .commitAllowingStateLoss()
        }
    }
}