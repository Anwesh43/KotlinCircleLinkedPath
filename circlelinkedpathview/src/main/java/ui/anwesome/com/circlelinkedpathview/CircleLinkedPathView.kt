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
    data class CircleNode(var i:Int, var deg:Float, var r:Float) {
        val x:Float = r*Math.cos(i*Math.PI/180*deg).toFloat()
        val y:Float = r*Math.sin(i*Math.PI/180*deg).toFloat()
        var neighbor:CircleNode ?= null
        val state = State()
        fun addNeighbor(circleNode: CircleNode) {
            neighbor = circleNode
        }
        fun draw(canvas:Canvas, paint:Paint, var curr:Boolean) {
            val scale1 = state.scales[0]
            val scale2 = state.scales[1]
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(x, y, r/15, paint)
            if(curr) {
                paint.style = Paint.Style.FILL
                canvas.drawCircle(x, y, (r / 15) * (1 - scale1), paint)
                val x1 = neighbor?.x ?: 0f
                val y1 = neighbor?.x ?: 0f
                val updatePoints: (Float) -> PointF = { scale -> PointF(x + (x1 - x)*scale, y +(y1 - y)*scale) }
                val point1 = updatePoints(scale1)
                val point2 = updatePoints(scale2)
                canvas.drawLine(point2.x , point2.y ,point1.x, point1.y, paint)
                canvas.drawCircle(x1, y1, r/15*scale2, paint)
            }
        }
        fun update(stopcb: (Float) -> Unit) {
            state.update(stopcb)
        }
        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }
}