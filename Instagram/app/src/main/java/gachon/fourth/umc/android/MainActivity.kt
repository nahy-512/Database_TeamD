package gachon.fourth.umc.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import gachon.third.umc.android.ProfileFragment
import gachon.third.umc.android.R
import gachon.third.umc.android.ReelsFragment
import gachon.third.umc.android.ShopFragment
import gachon.third.umc.android.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 가장 처음에 표시할 Fragment 설정 -> HomeFragment
        supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment()).commitAllowingStateLoss()
        // 바텀네비 아이템 클릭 이벤트 정의
        setBottomNavi()
    }

    private fun setBottomNavi() {
        // 각 아이템을 클릭했을 때 나타나는 이벤트 정의
        binding.mainBottomNavi.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    setFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.search -> {
                    setFragment(SearchFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.reels -> {
                    setFragment(ReelsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.shop -> {
                    setFragment(ShopFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    setFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        // 이동할 Fragment 지정
        supportFragmentManager.beginTransaction().replace(R.id.main_frm, fragment).commit()
    }
}