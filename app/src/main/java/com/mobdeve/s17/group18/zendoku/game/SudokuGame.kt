package com.mobdeve.s17.group18.zendoku.game

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.mobdeve.s17.group18.zendoku.util.StoragePreferences
import okhttp3.*
import java.io.IOException
import java.util.concurrent.CountDownLatch


class SudokuGame(context: Context) { //acts as the backend of the sudoku board view
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    var context: Context ?= null
    private var strDiff: String ?= null
    private var sPref: StoragePreferences?= null
    private var nSkip: Int ?= null
    private lateinit var sudokuArray: Array<Int>

    private var selectedRow = -1
    private var selectedCol = -1

    private val board: Board

    init { // Called whenever a sudoku game is created
        val cells = List(9*9) {i -> Cell(i / 9, i % 9, 0  )} //initializes a list of of Cell class
        board = Board(9, cells) //board class is instantiated with a cell size of 9

        this.context = context
        sPref = StoragePreferences(context)
        val prevGrid = sPref!!.getStringPreferences("ZENDOKU_GRID") // Gets previously-saved grid from sharedPreferences
        val prevDiff = sPref!!.getStringPreferences("ZENDOKU_GRID_DIFF")
        val currDiff = sPref!!.getStringPreferences("ZENDOKU_DIFF")

        if(prevGrid != null && prevDiff == currDiff)
            setPrevGrid(prevGrid)
        else
            genBoard(sPref!!.getStringPreferences("ZENDOKU_DIFF").toString())

        cells.forEachIndexed { index, cell ->
            cell.value = sudokuArray!![index]
            if(cell.value != 0)
                cell.isStartingCell = true
        }

        selectedCellLiveData.postValue(Pair(selectedRow,selectedCol)) //detects what the current coordinates are and sends it to the view for display
        cellsLiveData.postValue(board.cells) //posts the current board data
    }

    fun handleInput(number: Int) { //accepts user input value and displays it to the respective cell
        if(selectedRow == -1 || selectedCol == -1) return
        if(board.getCell(selectedRow, selectedCol).isStartingCell) return

        board.getCell(selectedRow, selectedCol).value = number
        cellsLiveData.postValue(board.cells)
    }

    fun updateSelectedCell(row: Int, col:Int) { //updates the row and column values
        if (!board.getCell(row, col).isStartingCell) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(Pair(row,col))
        }
    }

    private fun genBoard(currDiff: String) {
        // Get difficulty setting from sharedPreferences
        setDiff(currDiff)

        // Initialize number of skips
        nSkip = sPref!!.getIntPreferences("ZENDOKU_SKIP")

        // Creates and runs a query to generate a grid of the specified difficulty to the Sudoku API
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://sudoku-all-purpose-pro.p.rapidapi.com/sudoku?create=$strDiff&output=raw")
            .get()
            .addHeader("x-rapidapi-host", "sudoku-all-purpose-pro.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "7f61c6b2ccmsh269974c4aca773bp1e8bd0jsnb2adfc80f46e")
            .build()

        val countDownLatch = CountDownLatch(1) // Initialize countdown that waits for enqueue to finish before proceeding

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("genBoard", "API was not responsive.")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                val gson = GsonBuilder().create()
                val responseResult = gson.fromJson(responseBody, ResponseResult::class.java)
                val responseRaw = responseResult.output.raw_data
                val rawArray = Array(81) {0}

                responseRaw.forEachIndexed { index, c ->
                    rawArray[index] = c.code - 48 // 48 is 0 in ASCII; Char.code gets ASCII value of Char
                }

                sudokuArray = rawArray
                countDownLatch.countDown() // Starts countdown
            }
        })

        countDownLatch.await() // Waits until countdown is finished before proceeding
    }

    private fun setPrevGrid(prevGrid: String) {
        setDiff(sPref!!.getStringPreferences("ZENDOKU_GRID_DIFF").toString())
        nSkip = sPref!!.getIntPreferences("ZENDOKU_GRID_SKIP")
        sudokuArray = Array<Int>(81) {0}
        prevGrid.forEachIndexed { index, c ->
            sudokuArray[index] = c.code - 48 // 48 is 0 in ASCII; Char.code gets ASCII value of Char
        }
    }

    private fun setDiff(strDiff: String) {
        when(strDiff) {
            "Easy" -> this.strDiff = "easy"
            "Med" -> this.strDiff = "medium"
            "Hard" -> this.strDiff = "hard"
            "Evil" -> this.strDiff = "evil"
        }
    }

    fun setSkip(nSkip: Int) {
        this.nSkip = nSkip
    }

    fun getSudokuVals(): String {
        val strRemove = "[], "
        return sudokuArray.contentToString().filterNot { strRemove.indexOf(it) > -1 }
    }

    fun getDiff(): String {
        return strDiff.toString()
    }

    fun getSkip(): Int {
        return nSkip!!.toInt()
    }
}

class ResponseResult(val output: Output)

class Output(val raw_data: String)