package com.noahliu.noisemeter.View

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.roundToInt


class VolumeBar : View {
    companion object{
        val TAG = VolumeBar::class.java.simpleName+"My"
    }

    val barWeight = 65
    var volume = height


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(barWeight, measureHeight(heightMeasureSpec))
        this.volume = height
    }
    private fun measureHeight(measureSpec:Int):Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return if (specMode == MeasureSpec.AT_MOST) 500 else specSize
    }

    @SuppressLint("DrawAllocation", "CanvasSize")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val colorArray = intArrayOf(Color.GREEN,Color.YELLOW,Color.RED)

        val paint = Paint()
        paint.color = Color.BLUE;
        val linearGradient: LinearGradient = LinearGradient(
            canvas!!.width.toFloat(), canvas.height.toFloat(), 100f, 0f,colorArray, floatArrayOf(0f,0.6f,0.9f), Shader.TileMode.CLAMP
        )
        paint.shader = linearGradient
        val rect = Rect(0, volume, barWeight, canvas.height)
        canvas.drawRect(rect, paint)
    }
    fun setVolumeBar(db:Int){
        val per = 100-(((db.toDouble()/120)*100).roundToInt())
        try {
            val value = ((height.toDouble()*per)/100).roundToInt()
            val anim = ValueAnimator.ofInt(this.volume,value)
            this.volume = value
            anim.addUpdateListener {
                this.volume = it.animatedValue as Int
                postInvalidate()
            }
            anim.duration = 100
            anim.start()

        }catch (e:Exception){
            return
        }
//        Log.d(TAG, "setVolumeBar: db=$db, per=${per},vol=${this.volume} ")
    }
}