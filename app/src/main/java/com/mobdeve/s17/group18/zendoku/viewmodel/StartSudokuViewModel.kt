package com.mobdeve.s17.group18.zendoku.viewmodel

import androidx.lifecycle.ViewModel
import com.mobdeve.s17.group18.zendoku.game.SudokuGame

class StartSudokuViewModel : ViewModel(){ //stores cell data
    val sudokuGame = SudokuGame()
}