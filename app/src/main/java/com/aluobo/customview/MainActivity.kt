package com.aluobo.customview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aluobo.customview.views.MaterialEditText

class MainActivity : AppCompatActivity() {
    lateinit var mEt: MaterialEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mEt = findViewById(R.id.material_et)

    }
}