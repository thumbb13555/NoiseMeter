package com.noahliu.noisemeter.View

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.noahliu.noisemeter.R

class InfoBoard : View {
    companion object{
        val TAG = InfoBoard::class.java.simpleName+"My"
    }
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    private var value = "000db"

    var mTextPaint: TextPaint? = null
    var mStaticLayout: StaticLayout? = null
    init {
        mTextPaint = TextPaint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textSize = resources.getDimension(R.dimen.dp_18)
        mTextPaint!!.color = Color.BLACK

        val width = mTextPaint!!.measureText(value).toInt()
        mStaticLayout = StaticLayout(
            value, mTextPaint,
            width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, false
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width: Int
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthRequirement = MeasureSpec.getSize(widthMeasureSpec)
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthRequirement
        } else {
            width = mStaticLayout!!.width + paddingLeft + paddingRight
            if (widthMode == MeasureSpec.AT_MOST) {
                if (width > widthRequirement) {
                    width = widthRequirement

                    mStaticLayout = StaticLayout(
                        value,
                        mTextPaint,
                        width,
                        Layout.Alignment.ALIGN_NORMAL,
                        1.0f,
                        0f,
                        false
                    )
                }
            }
        }

        var height: Int
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightRequirement = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightRequirement
        } else {
            height = mStaticLayout!!.height + paddingTop + paddingBottom
            if (heightMode == MeasureSpec.AT_MOST) {
                height = height.coerceAtMost(heightRequirement)
            }
        }
        setMeasuredDimension(width, height)
    }

    
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null)return
        val tetwidth = mTextPaint!!.measureText(value).toInt()
        mStaticLayout = StaticLayout(
            value, mTextPaint,
            tetwidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, false
        )
        mStaticLayout!!.draw(canvas)
    }

    private fun drawString(text: String, x: Int, y: Int, paint: TextPaint, canvas: Canvas){
        if (text.contains("\n")){
            var my = y
            val ts = text.split("\n")
            for (i in ts.indices) {
                var offset = 0
                if (i == 0) {
                    paint.textSize = resources.getDimension(R.dimen.dp_12)
                    offset = 30
                } else{
                    paint.textSize = resources.getDimension(R.dimen.dp_24)
                    offset = 10
                }

                canvas.drawText(ts[i], x.toFloat(), my.toFloat(), paint)
                my += paint.textSize.toInt()+offset


            }

        }else canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
    }

    fun setValue(value: String){
        this.value = value
        invalidate()

    }
    fun getText():String{
        return value
    }

}