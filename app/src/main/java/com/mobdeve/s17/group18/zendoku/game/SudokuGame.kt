package com.mobdeve.s17.group18.zendoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame { //acts as the backend of the sudoku board view

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()

    private var selectedRow = -1
    private var selectedCol = -1

    init{ //called whenever a sudoku game is created
        selectedCellLiveData.postValue(Pair(selectedRow,selectedCol)) //detects what the current coordinates are and sends it to the view for display
    }

    fun updateSelectedCell(row: Int, col:Int) { //updates the row and column values
        selectedRow = row
        selectedCol = col
        selectedCellLiveData.postValue(Pair(row,col))
    }

}