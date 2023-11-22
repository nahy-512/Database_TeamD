package gachon.third.umc.android

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gachon.fourth.umc.android.ProfileEditActivity
import gachon.third.umc.android.databinding.FragmentProfileBinding

class ProfileFragment: Fragment() {

    lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // 클릭 이벤트 정의
        setClickListeners()

        return binding.root
    }

    private fun setClickListeners() {
        binding.profileEditBtn.setOnClickListener {
            // 프로필 편집 화면으로 이동
            moveToEdit()
        }
    }

    private fun moveToEdit() {
        // 화면 이동
        startActivity(Intent(activity, ProfileEditActivity::class.java))
    }

}