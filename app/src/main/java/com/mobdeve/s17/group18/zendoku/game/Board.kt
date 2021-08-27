package com.mobdeve.s17.group18.zendoku.game

class Board(val size: Int, val cells: List<Cell>) {
    fun getCell(row: Int, col: Int) = cells[row * size + col]

    fun getCellVals(): String {
        var arrVals = Array(81) {0}
        cells.forEachIndexed { index, cell ->
            arrVals[index] = cell.value
        }
        return arrVals.contentToString();
    }

    fun setCellVals(arr: Array<Int>) {
        cells.forEachIndexed { index, cell ->
            cell.value = arr[index]
            if(cell.value != 0)
                cell.isStartingCell = true
        }
    }
}