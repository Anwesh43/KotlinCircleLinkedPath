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
}