package com.mobdeve.s17.group18.zendoku.game

class Board(val size: Int, val cells: List<Cell>) {

    fun getCell(row: Int, col: Int) = cells[row * size + col]

}