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
}