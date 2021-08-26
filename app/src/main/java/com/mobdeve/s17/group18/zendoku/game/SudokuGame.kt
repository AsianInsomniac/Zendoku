package com.mobdeve.s17.group18.zendoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame { //acts as the backend of the sudoku board view

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()

    private var selectedRow = -1
    private var selectedCol = -1

    private val board: Board

    init{ //called whenever a sudoku game is created
        val cells = List(9*9) {i -> Cell(i / 9, i % 9, i %9  )} //initializes a list of of Cell class
        board = Board(9, cells) //board class is instantiated with a cell size of 9

        selectedCellLiveData.postValue(Pair(selectedRow,selectedCol)) //detects what the current coordinates are and sends it to the view for display
        cellsLiveData.postValue(board.cells) //posts the current board data
    }

    fun handleInput(number: Int) { //accepts user input value and displays it to the respective cell
        if(selectedRow == -1 || selectedCol == -1) return

        board.getCell(selectedRow, selectedCol).value = number
        cellsLiveData.postValue(board.cells)
    }

    fun updateSelectedCell(row: Int, col:Int) { //updates the row and column values
        selectedRow = row
        selectedCol = col
        selectedCellLiveData.postValue(Pair(row,col))
    }

}