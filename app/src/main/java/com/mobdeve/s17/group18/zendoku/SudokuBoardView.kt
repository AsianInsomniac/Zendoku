package com.mobdeve.s17.group18.zendoku

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SudokuBoardView (context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var sqrtSize = 3
    private var size = 9

    private var cellSizePixels = 0F

    private var selectedRow = -1
    private var selectedCol = -1

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

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#E1C699")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) { //determines the view's size
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec) //gets the minimum width and height of the board
        setMeasuredDimension(sizePixels, sizePixels) //sets the minimum width and height as the dimension of the board
    }

    override fun onDraw(canvas: Canvas) { // draws the board outer lines
        cellSizePixels = (width / size).toFloat() //takes the size of the board and divides it by the number of cells indicated, determines the size of the cells
        fillCells(canvas)
        drawLines(canvas)
    }

    private fun drawLines(canvas: Canvas){
        canvas.drawRect(0F, 0F, width.toFloat(),height.toFloat(), thickLinePaint) //draws the outside border of the board

        for (i in 1 until size) { //determines what line to use when
            val paintToUse = when (i % sqrtSize) {
                0 -> thickLinePaint
                else -> thinLinePaint
            }

            canvas.drawLine(
                i * cellSizePixels,
                0F,
                i * cellSizePixels,
                height.toFloat(),
                paintToUse
            )

            canvas.drawLine(
                0F,
                i * cellSizePixels,
                width.toFloat(),
                i * cellSizePixels,
                paintToUse
            )
        }
    }

    private fun fillCells(canvas: Canvas) {
        for(row in 0..size){
            for(col in 0..size){

            }
        }
    }

}