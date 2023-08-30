package com.example.permissionaccesskotlin

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.permissionaccesskotlin.databinding.ActivityInputTokenBinding

class InputTokenActivity : AppCompatActivity() {

    private lateinit var binding : ActivityInputTokenBinding

    private val token = "ABPEXAM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputTokenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAction.setOnClickListener {
            if (binding.etToken.text.toString() == token){
                startActivity(Intent(this, ExamActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "Token Anda Salah", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        AlertDialog.Builder(this)
            .setTitle("Perhatian !")
            .setMessage("Appakah Anda Akan Keluar Dari Aplikasi ?")
            .setPositiveButton("Ya"){ dialogInterface: DialogInterface, i: Int ->
                finishAffinity()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Tidak"){ dialogInterface: DialogInterface, i: Int ->
                finishAffinity()
                dialogInterface.dismiss()
            }
            .show()
        finishAffinity()
    }
}