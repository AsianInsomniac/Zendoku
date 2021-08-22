package com.mobdeve.s17.group18.zendoku

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SudokuBoardView (context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var sqrtSize = 3
    private var size = 9

    private var cellSizePixels = 0F

    private var selectedRow = 5
    private var selectedCol = 5

    private val thickLinePaint = Paint().apply {       // board perimeter lines and grid separator lines
        style= Paint.Style.STROKE
        color = Color.BLACK          // adjust these to change grid line characteristics
        strokeWidth = 4F
    }

    private val thinLinePaint = Paint().apply {       // board cell group divider
        style= Paint.Style.STROKE
        color = Color.BLACK          // adjust these to change grid line characteristics
        strokeWidth = 2F
    }

    private val selectedCellPaint = Paint().apply {  //sets the color and style of a selected cell
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#c4aa78")
    }

    private val conflictingCellPaint = Paint().apply { //sets the color and style of a conflicting cell
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#b4b8b5")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) { //determines the view's size
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec) //gets the minimum width and height of the board
        setMeasuredDimension(sizePixels, sizePixels) //sets the minimum width and height as the dimension of the board
    }

    override fun onDraw(canvas: Canvas) { // draws the board outer lines
        cellSizePixels = (width / size).toFloat() //takes the size of the board and divides it by the number of cells indicated, determines the size of the cells
        fillCells(canvas) //highlights the cell selected
        drawLines(canvas) //draws board lines
    }

    private fun fillCells(canvas: Canvas) { //responsible for locating the cell selected in the board
        if(selectedRow == -1 || selectedRow == -1) return //checks if the cell selected is valid

        for(row in 0..size){
            for(col in 0..size){
                if(row == selectedRow && col == selectedCol) { //fills the selected cell with the selectedCellPaint when matched with the cell selected
                    fillCell(canvas, row, col, selectedCellPaint)
                } else if (row == selectedRow || col == selectedCol) { //fills the whole row and column the selected cell is in with conflictingCellPaint
                    fillCell(canvas, row, col, conflictingCellPaint)
                } else if ( row / sqrtSize == selectedRow / sqrtSize && col / sqrtSize == selectedCol / sqrtSize) { //fills the grid the selected cell is in with conflictingCellPaint
                    fillCell(canvas, row, col, conflictingCellPaint)
                }
            }
        }
    }

    private fun fillCell(canvas: Canvas, row: Int, col: Int, paint: Paint) { //function for filling a cell with a specific color given coordinates and paint
        canvas.drawRect(col * cellSizePixels, row * cellSizePixels, (col + 1) * cellSizePixels, (row + 1) *cellSizePixels, paint)
    }
    private fun drawLines(canvas: Canvas){
        canvas.drawRect(0F, 0F, width.toFloat(),height.toFloat(), thickLinePaint) //draws the outside border of the board

        for (i in 1 until size) { //determines what line to use, either thick or thin
            val paintToUse = when (i % sqrtSize) {
                0 -> thickLinePaint //border lines and grid separators
                else -> thinLinePaint //makes up the grid and each cell of the board
            }

            canvas.drawLine( //draws the horizontal lines of the sudoku board
                i * cellSizePixels,
                0F,
                i * cellSizePixels,
                height.toFloat(),
                paintToUse
            )

            canvas.drawLine( //draws the vertical lines of the sudoku board
                0F,
                i * cellSizePixels,
                width.toFloat(),
                i * cellSizePixels,
                paintToUse
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    private fun handleTouchEvent(x: Float, y: Float){
        selectedRow = (y / cellSizePixels).toInt()
        selectedCol = (x / cellSizePixels).toInt()
        invalidate()
    }
}