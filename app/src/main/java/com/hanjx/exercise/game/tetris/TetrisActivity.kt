package com.hanjx.exercise.game.tetris

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.hanjx.exercise.game.tetris.logic.TetrisViewModel

class TetrisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this).get(TetrisViewModel::class.java)
        setContent {
            GameScreen(Modifier)
        }
    }
}