package gachon.database.instagram.ui.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gachon.database.instagram.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}