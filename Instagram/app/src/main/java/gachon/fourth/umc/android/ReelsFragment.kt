package gachon.third.umc.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gachon.third.umc.android.databinding.FragmentReelsBinding

class ReelsFragment: Fragment() {

    lateinit var binding: FragmentReelsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReelsBinding.inflate(inflater, container, false)
        return binding.root
    }

}