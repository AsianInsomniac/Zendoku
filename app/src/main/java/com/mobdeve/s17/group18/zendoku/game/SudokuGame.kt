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
    private var nClear: Int ?= null
    private var strOrig: String ?= null
    private var nOrig: Array<Int> = Array(81) {0}
    private lateinit var sudokuArray: Array<Int>

    private var selectedRow = -1
    private var selectedCol = -1

    private val board: Board

    companion object {
        const val EASY_MOD: Int = 10
        const val MED_MOD: Int = 5
        const val HARD_MOD: Int = 3
        const val EVIL_MOD: Int = 1
        const val MAX_SKIP: Int = 5
        const val CLEAR_MSG: String = "Result is valid."
    }

    init { // Called whenever a sudoku game is created
        val cells = List(9*9) {i -> Cell(i / 9, i % 9, 0  )} //initializes a list of of Cell class
        board = Board(9, cells) //board class is instantiated with a cell size of 9

        this.context = context
        sPref = StoragePreferences(context)
        val prevBoard = sPref!!.getStringPreferences("ZENDOKU_GRID") // Gets previously-saved grid from sharedPreferences
        val prevDiff = sPref!!.getStringPreferences("ZENDOKU_GRID_DIFF")
        val currDiff = sPref!!.getStringPreferences("ZENDOKU_DIFF")

        if(prevBoard != null && prevDiff == currDiff)
            setPrevBoard(prevBoard)
        else
            genBoard(sPref!!.getStringPreferences("ZENDOKU_DIFF").toString())

        updateBoard()
    }

    private fun updateBoard() {
        board.setCellVals(sudokuArray)

        board.cells.forEachIndexed { index, cell ->
            cell.isStartingCell = nOrig[index] == sudokuArray[index] && cell.value != 0
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

    private fun genNewBoard() {
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
                val createResponseResult = gson.fromJson(responseBody, CreateResponseResult::class.java)
                val responseRaw = createResponseResult.output.raw_data
                strOrig = responseRaw
                var rawArray = Array(81) {0}

                responseRaw.forEachIndexed { index, c ->
                    rawArray[index] = c.code - 48 // 48 is 0 in ASCII; Char.code gets ASCII value of Char
                }

                strOrig!!.forEachIndexed { index, c ->
                    nOrig[index] = c.code - 48
                }

                sudokuArray = rawArray
                sPref?.saveStringPreferences("ZENDOKU_ORIGINAL_GRID", responseRaw)
                countDownLatch.countDown() // Starts countdown
            }
        })
        countDownLatch.await() // Waits until countdown is finished before proceeding
    }

    private fun genBoard(currDiff: String) {
        setDiff(currDiff)

        // Initialize number of skips
        nSkip = sPref!!.getIntPreferences("ZENDOKU_SKIP")

        // Initialize number of board clears
        nClear = 0

        // Generate new board and update view
        genNewBoard()
        updateBoard()
    }

    fun skipBoard() {
        genNewBoard()
        updateBoard()
        nSkip = nSkip?.minus(1)
    }

    fun verifyBoard(): String {
        val client = OkHttpClient()
        val arrVal = getSudokuVals()

        val request = Request.Builder()
            .url("https://sudoku-all-purpose-pro.p.rapidapi.com/sudoku?verify=$arrVal")
            .get()
            .addHeader("x-rapidapi-host", "sudoku-all-purpose-pro.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "7f61c6b2ccmsh269974c4aca773bp1e8bd0jsnb2adfc80f46e")
            .build()

        val countDownLatch = CountDownLatch(1) // Initialize countdown that waits for enqueue to finish before proceeding

        var strResult = ""

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                strResult = "API was not responsive."
                Log.i("verifyBoard", strResult)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                val gson = GsonBuilder().create()
                val verifyResponseResult = gson.fromJson(responseBody, VerifyResponseResult::class.java)

                when(verifyResponseResult.status.notice != ""){
                    true -> {
                        strResult = verifyResponseResult.status.notice
                        clearBoard()
                    }
                    false -> strResult = verifyResponseResult.status.error
                }

                countDownLatch.countDown()
            }
        })

        countDownLatch.await()
        Log.i ("verifyBoard", strResult)
        return strResult
    }

    private fun clearBoard() {
        nClear = nClear?.inc()

        if(nSkip!! < MAX_SKIP) {
            when(strDiff){
                "easy" -> {
                    if(nClear!!.mod(EASY_MOD) == 0) {
                        nSkip = nSkip!!.inc()
                    }
                }
                "medium" -> {
                    if(nClear!!.mod(MED_MOD) == 0) {
                        nSkip = nSkip!!.inc()
                    }
                }
                "hard" -> {
                    if(nClear!!.mod(HARD_MOD) == 0) {
                        nSkip = nSkip!!.inc()
                    }
                }
                "evil" -> {
                    if(nClear!!.mod(EVIL_MOD) == 0) {
                        nSkip = nSkip!!.inc()
                    }
                }
            }
        }

        genNewBoard()
        updateBoard()
    }

    private fun setPrevBoard(prevBoard: String) {
        setDiff(sPref!!.getStringPreferences("ZENDOKU_GRID_DIFF").toString())

        nSkip = sPref!!.getIntPreferences("ZENDOKU_GRID_SKIP")
        nClear = sPref!!.getIntPreferences("ZENDOKU_GRID_CLEAR")
        strOrig = sPref!!.getStringPreferences("ZENDOKU_ORIGINAL_GRID")!!

        nOrig = Array(81) {0}
        strOrig!!.forEachIndexed { index, c ->
            nOrig[index] = c.code - 48
        }

        sudokuArray = Array(81) {0}
        prevBoard.forEachIndexed { index, c ->
            sudokuArray[index] = c.code - 48
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

    fun getSudokuVals(): String {
        val strRemove = "[], "
        return board.getCellVals().filterNot { strRemove.indexOf(it) > -1 }
    }

    fun getDiff(): String {
        return strDiff.toString()
    }

    fun getSkip(): Int {
        return nSkip!!.toInt()
    }

    fun getClear(): Int {
        return nClear!!.toInt()
    }
}

class CreateResponseResult(val output: Output)

class VerifyResponseResult(val status: Status)

class Output(val raw_data: String)

class Status(val notice: String, val error: String)