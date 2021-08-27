package com.mobdeve.s17.group18.zendoku.views

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.mobdeve.s17.group18.zendoku.databinding.ActivityStartsudokuBinding
import com.mobdeve.s17.group18.zendoku.game.Cell
import com.mobdeve.s17.group18.zendoku.game.SudokuGame
import com.mobdeve.s17.group18.zendoku.util.StoragePreferences
import com.mobdeve.s17.group18.zendoku.viewmodel.StartSudokuViewModel
import kotlinx.android.synthetic.main.activity_startsudoku.*


class StartSudokuActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener{
    private lateinit var binding: ActivityStartsudokuBinding
    private lateinit var viewModel: StartSudokuViewModel
    private lateinit var sPref: StoragePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartsudokuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sPref = StoragePreferences(applicationContext)
        sudokuBoardView.registerListener(this)

        val viewModelFactory = SudokuViewModelFactory(applicationContext)
        ViewModelProvider(this, viewModelFactory).get(StartSudokuViewModel::class.java)
        viewModel = ViewModelProvider(this, viewModelFactory).get(StartSudokuViewModel::class.java) //sets the StartSudoku activity with the StartSudokuViewModel
        viewModel.sudokuGame.selectedCellLiveData.observe(this, {updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, {updateCells(it) })

        updateSkipText()

        val buttons = listOf(oneBtn, twoBtn, threeBtn, fourBtn, fiveBtn, sixBtn, sevenBtn, eightBtn, nineBtn)

        buttons.forEachIndexed{index, button ->
            button.setOnClickListener{viewModel.sudokuGame.handleInput(index + 1)}
        }
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuBoardView.updateCells(cells)
    }
    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let { // makes the sudokuBoardView update its UI from cell data processed in sudoku game
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    private fun updateSkipText() {
        var strSkip = viewModel.sudokuGame.getSkip().toString() + " SKIP"
        if (viewModel.sudokuGame.getSkip() != 1)
            strSkip += "S"
        strSkip += " LEFT"
        tvSkips.text = strSkip
    }

   override fun onCellTouched(row: Int, col: Int) { //accesses the sudoku cell update method to update a highlighted cell
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

    fun toHome(view: View) {
        finish()
    }

    fun skipBoard(view: View) {
        if(viewModel.sudokuGame.getSkip() > 0) {
            viewModel.sudokuGame.skipBoard()
            updateSkipText()
        }
        else {
            var nUntilSkip = 0
            when(viewModel.sudokuGame.getDiff()) {
                "easy" -> nUntilSkip = SudokuGame.EASY_MOD - viewModel.sudokuGame.getClear().mod(SudokuGame.EASY_MOD)
                "medium" -> nUntilSkip = SudokuGame.MED_MOD - viewModel.sudokuGame.getClear().mod(SudokuGame.MED_MOD)
                "hard" -> nUntilSkip = SudokuGame.HARD_MOD - viewModel.sudokuGame.getClear().mod(SudokuGame.HARD_MOD)
                "evil" -> nUntilSkip = SudokuGame.EVIL_MOD - viewModel.sudokuGame.getClear().mod(SudokuGame.EVIL_MOD)
            }
            var strMsg = "No skips available. $nUntilSkip clear"
            if(nUntilSkip != 1) {
                strMsg += "s"
            }
            strMsg += " left until next skip."
            Snackbar.make(findViewById(android.R.id.content),strMsg,Snackbar.LENGTH_SHORT).show()
        }
    }

    fun verifyBoard(view: View) {
        if(!viewModel.sudokuGame.getSudokuVals().contains("0")) {
            var strResult = ""
            when (viewModel.sudokuGame.verifyBoard() == SudokuGame.CLEAR_MSG) {
                true -> strResult = "Board cleared!"
                false -> strResult = "Board invalid. Maybe you missed something?"
            }
            Snackbar.make(findViewById(android.R.id.content), strResult, Snackbar.LENGTH_SHORT).show()
            updateSkipText()
        }
        else {
            Snackbar.make(findViewById(android.R.id.content), "Complete the board first before verifying.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun saveBoard() {
        sPref.saveStringPreferences("ZENDOKU_GRID", viewModel.sudokuGame.getSudokuVals())

        var gridDiff: String ?= null
        when (viewModel.sudokuGame.getDiff()) {
            "easy" -> gridDiff = "Easy"
            "medium" -> gridDiff = "Med"
            "hard" -> gridDiff = "Hard"
            "evil" -> gridDiff = "Evil"

        }
        sPref.saveStringPreferences("ZENDOKU_GRID_DIFF", gridDiff)
        sPref.saveIntPreferences("ZENDOKU_GRID_SKIP", viewModel.sudokuGame.getSkip())
        sPref.saveIntPreferences("ZENDOKU_GRID_CLEAR", viewModel.sudokuGame.getClear())
    }

    override fun onPause() {
        super.onPause()
        saveBoard()
    }
}

class SudokuViewModelFactory(val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Context::class.java).newInstance(context)
    }
}