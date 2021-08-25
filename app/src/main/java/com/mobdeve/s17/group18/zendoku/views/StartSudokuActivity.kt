package com.mobdeve.s17.group18.zendoku.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mobdeve.s17.group18.zendoku.R
import com.mobdeve.s17.group18.zendoku.game.Cell
import com.mobdeve.s17.group18.zendoku.viewmodel.StartSudokuViewModel
import kotlinx.android.synthetic.main.activity_startsudoku.*

class StartSudokuActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener{

    private lateinit var viewModel:StartSudokuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startsudoku)

        sudokuBoardView.registerListener(this)

        viewModel = ViewModelProviders.of(this).get(StartSudokuViewModel::class.java) //sets the StartSudoku activity with the StartSudokuViewModel
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer{updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer{updateCells(it) })

        val buttons = listOf(oneBtn, twoBtn, threeBtn, fourBtn, fiveBtn, sixBtn, sevenBtn, eightBtn, nineBtn)

        buttons.forEachIndexed{index, button ->
            button.setOnClickListener{viewModel.sudokuGame.handleInput(index + 1)}}
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuBoardView.updateCells(cells)
    }
    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let { //makes the sudokuBoardView update its UI from cell data processed in sudoku game
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

   override fun onCellTouched(row: Int, col: Int) { //accesses the sudoku cell update method to update a highlighted cell
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

    fun toHome(view: View) {
        finish()
    }
}