package com.noahliu.noisemeter.View

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import com.noahliu.noisemeter.Model.Model.Utils.MyUtils
import com.noahliu.noisemeter.R
import kotlin.math.abs
import kotlin.math.roundToInt

class DraggingButton : androidx.appcompat.widget.AppCompatButton {

    var lastX = 0f
    var lastY = 0f
    private var beginX = 0f
    private var beginY = 0f
    private var screenWidth = 720f
    private var screenHeight = 1280f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        screenWidth = dm.widthPixels.toFloat()
        screenHeight = dm.heightPixels.toFloat()-resources.getDimension(R.dimen.dp_70)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){

            MotionEvent.ACTION_DOWN->{
                lastX = event.rawX
                lastY = event.rawY
                beginX = lastX
                beginY = lastY
            }

            MotionEvent.ACTION_MOVE->{
                val dx = event.rawX - lastX
                val dy = event.rawY - lastY
                var left = left + dx
                var top = top + dy
                var right = right + dx
                var bottom = bottom + dy
                if(left < 0){
                    left = 0f
                    right = left + width
                }
                if(right > screenWidth){
                    right = screenWidth
                    left = right - width
                }
                if(top < 0){
                    top = 0f
                    bottom = top + height
                }
                if(bottom>screenHeight){
                    bottom = screenHeight
                    top = bottom - height
                }
                layout(left.roundToInt(),top.roundToInt(),right.roundToInt(),bottom.roundToInt())
                lastY = event.rawY
                lastX = event.rawX
            }
            MotionEvent.ACTION_UP->{
                return if (abs(lastX - beginX) < 10 && abs(lastY - beginY) < 10){
                    super.onTouchEvent(event)
                }else{
                    isPressed = false
                    true
                }
            }
        }
        return super.onTouchEvent(event)
    }
}