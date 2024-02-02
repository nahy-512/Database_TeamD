package gachon.database.instagram.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gachon.database.instagram.R
import gachon.database.instagram.databinding.FragmentSignupUserNameBinding

class SignupUserNameFragment : Fragment() {

    lateinit var binding: FragmentSignupUserNameBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupUserNameBinding.inflate(inflater, container, false)

        initClickListener()

        return binding.root
    }

    private fun initClickListener() {
        // 개인정보 설정
        binding.signupUserNameNextBtn.setOnClickListener {
            val userName = binding.signupUserNameEt.text.toString()
            Log.d("SignupUserNameFrag", "userName: $userName")

            (context as SignupActivity).supportFragmentManager.beginTransaction()
                .add(R.id.signup_frm, SignupPWFragment().apply {
                    arguments = Bundle().apply {
                        putString("user_name", userName) // 사용자 이름
                    }
                })
                .addToBackStack(null) // 백 스택에 트랜잭션을 추가
                .commitAllowingStateLoss()
        }
    }
}