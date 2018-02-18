package ui.anwesome.com.circlelinkedpathview

/**
 * Created by anweshmishra on 18/02/18.
 */
import android.content.*
import android.view.*
import android.graphics.*
class CircleLinkedPathView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas:Canvas) {

    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
    data class Animator(var view: View, var animated: Boolean = false) {
        fun start() {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
        fun animate(updatecb: () -> Unit) {
            if(animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex: Exception) {

                }
            }
        }
    }
    data class State(var prevScale : Float = 0f,var j : Int = 0, var jDir : Int = 1, var dir : Float = 0f) {
        var scales : Array<Float> = arrayOf(0f, 0f)
        fun update(stopcb : (Float) -> Unit) {
            scales[j] += dir * 0.1f
            if(Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                if(j == scales.size) {
                    j = 0
                    dir = 0f
                }
            }
        }
        fun startUpdating(startcb : () -> Unit) {
            if(dir == 0f) {
                scales = arrayOf(0f,0f)
                dir = 1f
                startcb()
            }
        }
    }
}