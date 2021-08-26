package com.mobdeve.s17.group18.zendoku.game

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.mobdeve.s17.group18.zendoku.util.StoragePreferences
import okhttp3.*
import java.io.IOException

class SudokuGame(context: Context) { //acts as the backend of the sudoku board view
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    var strDiff: String ?= null
    var sPref: StoragePreferences?= null
    var context: Context ?= null
    var sudokuArray: IntArray ?= null

    private var selectedRow = -1
    private var selectedCol = -1

    private val board: Board

    init { //called whenever a sudoku game is created
        val cells = List(9*9) {i -> Cell(i / 9, i % 9, 0  )} //initializes a list of of Cell class
        board = Board(9, cells) //board class is instantiated with a cell size of 9

        this.context = context
        sudokuArray = IntArray(81) {0}
        genBoard()

        Log.i("genBoard", sudokuArray.contentToString())

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

    private fun genBoard() {
        // Get difficulty setting from SharedPreferences
        sPref = StoragePreferences(context!!)
        when(sPref!!.getStringPreferences("ZENDOKU_DIFF")) {
            "Easy" -> strDiff = "easy"
            "Med" -> strDiff = "medium"
            "Hard" -> strDiff = "hard"
            "Evil" -> strDiff = "evil"
        }

        // Creates and runs a query to generate a grid of the specified difficulty to the Sudoku API
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://sudoku-all-purpose-pro.p.rapidapi.com/sudoku?create=$strDiff&output=raw")
            .get()
            .addHeader("x-rapidapi-host", "sudoku-all-purpose-pro.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "7f61c6b2ccmsh269974c4aca773bp1e8bd0jsnb2adfc80f46e")
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("genBoard", "API was not responsive.")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                val gson = GsonBuilder().create()
                val responseResult = gson.fromJson(responseBody, ResponseResult::class.java)
                val responseRaw = responseResult.output.raw_data

                for(i in 0 until sudokuArray!!.size){
                    sudokuArray!![i] = responseRaw[i].code - 48 // 48 is 0 in ASCII; Char.code gets ASCII value of Char
                }
            }
        })
    }
}

class ResponseResult(val output: Output)

class Output(val raw_data: String)