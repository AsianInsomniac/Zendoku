package com.mobdeve.s17.group18.zendoku.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.mobdeve.s17.group18.zendoku.game.SudokuGame

class StartSudokuViewModel(context: Context) : ViewModel(){ //stores cell data
    val sudokuGame = SudokuGame(context)
}