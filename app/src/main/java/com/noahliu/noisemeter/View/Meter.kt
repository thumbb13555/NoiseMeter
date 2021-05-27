package com.noahliu.noisemeter.View

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import com.noahliu.noisemeter.R
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


class Meter : View {
    companion object{
        val TAG = Meter::class.java.simpleName+"My"
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    private val angle = 90
    private val startAngle = (90+angle/2).toFloat()
    private val sweepAngle = (360-angle).toFloat()
    private lateinit var mRecF:RectF
    private val scaleCount = 12
    private var volume = 0f
    private var currentVolume = 0f
    private var baseSize = 0



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mHeight = measureHeight(heightMeasureSpec)
        val mWidth = measureWidth(widthMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        baseSize = if (mHeight>mWidth) mWidth else mHeight
        Log.d(TAG, "onMeasure: $baseSize")
//        setMeasuredDimension(800,800)
    }

    private fun measureWidth(measureSpec:Int):Int{
        val specMode = MeasureSpec.getMode(measureSpec);
        val specSize = MeasureSpec.getSize(measureSpec);
        Log.d(TAG, "measureWidth: ${specSize}")
        var result = 1000
        if (specMode == MeasureSpec.AT_MOST) {
            result = 1000
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        }
        return result;
    }

    private fun measureHeight(measureSpec:Int):Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        var result = 1000
        Log.d(TAG, "measureHeight: ${specSize}")
        if (specMode == MeasureSpec.AT_MOST) {
            result = 1000
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        }
        return result
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        /**底盤黑色部分*/
        writeBaseCircular(canvas, paint)
        /**白色弧線*/
        //pathLength為白色弧形長度
        val pathLength = writeSector(canvas, paint)
        /**線上的白色刻度*/
        writeShape(pathLength, canvas, paint)
        /**畫上刻度*/
        writeMark(canvas)
        /**繪製指針*/
        writePoint(canvas)
        /**繪製中間圓點*/
        writeCenterPoint(canvas)
        /**繪製單位標籤*/
        writeDBWord(canvas)
        /**顯示目前量測數值*/
        writeCurrentValue(canvas)
        /**繪製紅色區域*/
//        val alarmLength = writeRedAlarmArea(canvas)
        /**繪製紅區域刻度*/
//        writeRedShape(alarmLength,canvas,paint)

    }
    /**畫底圓*/
    private fun writeBaseCircular(canvas: Canvas, paint: Paint){
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawCircle(
            width / 2.toFloat(),
            height / 2.toFloat(),
            (baseSize / 2)*0.8f,
            paint
        )
    }
    /**畫一個扇形*/
    private fun writeSector(canvas: Canvas, paint: Paint):Float{
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f
        val radius = baseSize/2.toFloat()*0.7f
        mRecF = RectF(
            width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius
        )
        canvas.drawArc(mRecF, startAngle, sweepAngle, false, paint)
        val path =Path()
        path.addArc(mRecF, startAngle, sweepAngle)
        val sectorPath = PathMeasure(path, false)
        return sectorPath.length
    }
    /**白色刻度*/
    private fun writeShape(length: Float, canvas: Canvas, paint: Paint){
        val v: Float = length - (resources.getDimension(R.dimen.dp_8))
        val shape = Path()
        shape.addRect(RectF(0f, 0f
            , resources.getDimension(R.dimen.dp_8)
            , resources.getDimension(R.dimen.dp_15)), Path.Direction.CW)
        val advance: Float = v / (scaleCount)
        val phase = 0f
        val mPathDashPathEffect =
            PathDashPathEffect(shape, advance, phase, PathDashPathEffect.Style.ROTATE)
        paint.pathEffect = mPathDashPathEffect
        canvas.drawArc(mRecF, startAngle, sweepAngle, false, paint)
        paint.pathEffect = null

    }
    /**刻度字*/
    private fun writeMark(canvas: Canvas){
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = resources.getDimension(R.dimen.dp_14)
        //length為標籤字離圓心的距離
        val length = baseSize / 2.toFloat() * 0.45
            var i = 0
            while (i <= 120) {
                val text = i.toString()
                val currentAngle = setMark(i.toFloat()).toInt()

                val font = paint.fontMetrics
                val yOffset = ((font.ascent + font.descent)/2).toInt()
                val stopX = (cos(Math.toRadians(currentAngle.toDouble())) *length)+width/2
                val stopY = (sin(Math.toRadians(currentAngle.toDouble())) *length)-yOffset+height/2
                //Log.d(TAG, "writeMark:value = $i, X = $stopX, Y = $stopY, currentAngle = $currentAngle")
                if ((i/10)%2 == 0){
                    canvas.drawText(text, stopX.toFloat(), stopY.toFloat(), paint)
                }
                i += 10
            }

    }
    /**刻度字-設置刻度字的位置*/
    private fun setMark(mark: Float):Float{
        return startAngle + 1.0f * sweepAngle / 120 * mark
    }
    /**設置指針*/
    private fun writePoint(canvas: Canvas){
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 16f
        //指針寬度
        val edge = 500
        //指向角度
        val currentAngle = setMark(volume).toInt()
        //指針長度
        val pointLength = (baseSize / 2) * 2/5
        val lX = (width/2)+ cos(Math.toRadians(currentAngle.toDouble()+edge))*40
        val ly = (height/2)+ sin(Math.toRadians(currentAngle.toDouble()+edge))*40
        val rX = (width/2)+ cos(Math.toRadians(currentAngle.toDouble()-edge))*40
        val rY = (height/2)+ sin(Math.toRadians(currentAngle.toDouble()-edge))*40
        val stopX = (cos(Math.toRadians(currentAngle.toDouble())) *pointLength)+width/2
        val stopY = (sin(Math.toRadians(currentAngle.toDouble())) *pointLength)+height/2
        val path = Path()
        path.moveTo(stopX.toFloat(),stopY.toFloat())
        path.lineTo(rX.toFloat(),rY.toFloat())
        path.lineTo(lX.toFloat(),ly.toFloat())
        path.close()
        canvas.drawPath(path,paint)


    }
    /**繪製中心點*/
    private fun writeCenterPoint(canvas: Canvas){
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.GRAY
        paint.style = Paint.Style.FILL
        canvas.drawCircle(
            width / 2.toFloat(),
            height / 2.toFloat(),
            baseSize / 2.toFloat() * 1/10,
            paint
        )
    }
    /**繪製"DB"字樣*/
    private fun writeDBWord(canvas: Canvas){
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = resources.getDimension(R.dimen.dp_18)

        canvas.drawText("db", (width/2).toFloat(), (height/2)+(baseSize/2*0.3).toFloat(), paint)
    }
    /**繪製及時數值之文字*/
    private fun writeCurrentValue(canvas: Canvas){
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = resources.getDimension(R.dimen.sp_20)
        canvas.drawText(currentVolume.roundToInt().toString(), (width/2).toFloat(), (height/2)+(baseSize/2*0.5).toFloat(), paint)

    }
    /**繪製紅色區域弧形*/
    private fun writeRedAlarmArea(canvas: Canvas):Float{
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f
        val radius = baseSize/2.toFloat()*0.7f
        mRecF = RectF(
            width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius
        )
        canvas.drawArc(mRecF, 0f, 50f, false, paint)
        val path =Path()
        path.addArc(mRecF, 0f, 50f)
        val sectorPath = PathMeasure(path, false)
        return sectorPath.length
    }
    /**繪製紅色區域刻度*/
    private fun writeRedShape(length: Float, canvas: Canvas, paint: Paint){
        paint.color = Color.RED
        val v: Float = length - (resources.getDimension(R.dimen.dp_10))
        val shape = Path()
        shape.addRect(RectF(0f, 0f
            , resources.getDimension(R.dimen.dp_10)
            , resources.getDimension(R.dimen.dp_20)), Path.Direction.CW)
        val advance: Float = v / 2
        val phase = 0f
        val mPathDashPathEffect =
            PathDashPathEffect(shape, advance, phase, PathDashPathEffect.Style.ROTATE)
        paint.pathEffect = mPathDashPathEffect
//        canvas.drawArc(mRecF, -4f, 60f, false, paint)
        paint.pathEffect = null

    }

    /**傳入數值*/
    fun setValue(db: Int){
        try {
            currentVolume = db.toFloat()
            val anim = ValueAnimator.ofInt(this.volume.toInt(),db)
            this.volume = db.toFloat()
            anim.addUpdateListener {
                this.volume = (it.animatedValue as Int).toFloat()
                postInvalidate()
            }
            anim.duration = 150
            anim.start()

        }catch (e:Exception){
            return
        }
    }


}