package ui.anwesome.com.circlelinkedpathview

/**
 * Created by anweshmishra on 18/02/18.
 */
import android.app.Activity
import android.content.*
import android.view.*
import android.graphics.*
import android.util.Log

class CircleLinkedPathView(ctx:Context, var n:Int = 6):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = Renderer(this)
    override fun onDraw(canvas:Canvas) {
        renderer.render(canvas, paint)
    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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
                view.invalidate()
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
        fun update(stopcb : () -> Unit) {
            scales[j] += dir * 0.1f
            if(Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += jDir
                if(j == scales.size) {
                    j = 0
                    dir = 0f
                    stopcb()
                }
            }
        }
        fun initScales() {
            scales = arrayOf(0f,0f)
        }
        fun startUpdating(startcb : () -> Unit) {
            if(dir == 0f) {
                initScales()
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
        fun initState() {
            state.initScales()
        }
        fun draw(canvas:Canvas, paint:Paint, curr:Boolean) {
            val scale1 = state.scales[0]
            val scale2 = state.scales[1]
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(x, y, r/10, paint)
            if(curr) {
                paint.style = Paint.Style.FILL
                canvas.drawCircle(x, y, (r / 10) * (1 - scale1), paint)
                val x1 = neighbor?.x ?: 0f
                val y1 = neighbor?.y ?: 0f
                val updatePoints: (Float) -> PointF = { scale -> PointF(x + (x1 - x)*scale, y +(y1 - y)*scale) }
                val point1 = updatePoints(scale1)
                val point2 = updatePoints(scale2)
               // canvas.drawLine(point2.x , point2.y ,point1.x, point1.y, paint)
                var neighborIndex = neighbor?.i?:0
                if(neighborIndex == 0) {
                    neighborIndex = 6
                }
                val updateDeg:(Float) -> Float = { scale -> i * deg + (neighborIndex * deg - i * deg) * scale }
                val deg1 = updateDeg(scale1)
                val deg2 = updateDeg(scale2)
                paint.style = Paint.Style.STROKE
                val path = Path()
                for(j in (deg2).toInt()..(deg1).toInt()) {
                    val px = r * Math.cos(j * Math.PI/180).toFloat()
                    val py = r * Math.sin(j * Math.PI/180).toFloat()
                    if(j == deg2.toInt()) {
                        path.moveTo(px, py)
                    }
                    else {
                        path.lineTo(px, py)
                    }
                }
                canvas.drawPath(path, paint)
                paint.style = Paint.Style.FILL
                canvas.drawCircle(x1, y1, (r/10) * scale2, paint)
            }
        }
        fun update(stopcb: (Int) -> Unit) {
            state.update({
                stopcb(i)
            })
        }
        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }
    data class LinkedCirclePath(var w:Float, var h:Float,var n:Int) {
        val deg = 360f/n
        val r = Math.min(w,h)/3
        val root: CircleNode = CircleNode(0, deg , Math.min(w,h)/3)
        var curr:CircleNode? = root
        init {
            var i = 1
            var node:CircleNode? = curr
            while(i != n) {
                node?.addNeighbor(CircleNode(i, deg, r))
                node = node?.neighbor
                i++
            }
            node?.neighbor = curr
        }
        fun draw(canvas:Canvas, paint:Paint) {
            var node:CircleNode? = root
            canvas.save()
            canvas.translate(w/2, h/2)
            while(true) {
                node?.draw(canvas, paint, node?.i?:0 == curr?.i?:0)
                node = node?.neighbor
                if(node?.i?:0 == 0) {
                    break
                }
            }
            canvas.restore()
        }
        fun update(stopcb: () -> Unit, listenerCb: (Int) -> Unit) {
            curr?.update { it ->
                curr = curr?.neighbor
                listenerCb(it)
                if(curr?.i?:0 == 0) {
                    curr = root
                    curr?.initState()
                    stopcb()
                }
                else {
                    startUpdating {

                    }
                }
            }
        }
        fun startUpdating(startcb: () -> Unit) {
            curr?.startUpdating {
                if(curr?.i?:0 == 0) {
                    startcb()
                }
            }
        }
    }
    class Renderer(var view: CircleLinkedPathView, var time: Int = 0) {
        val animator:Animator = Animator(view)
        var circleLinkedPath: LinkedCirclePath ?= null
        fun render(canvas: Canvas, paint: Paint) {
            if(time == 0) {
                val w = canvas.width.toFloat()
                val h = canvas.height.toFloat()
                if(view.n > 1) {
                    circleLinkedPath = LinkedCirclePath(w, h, view.n)
                }
                paint.color = Color.parseColor("#3498db")
                paint.strokeWidth = Math.min(w,h)/50
                paint.strokeCap = Paint.Cap.ROUND
            }
            canvas.drawColor(Color.parseColor("#212121"))
            circleLinkedPath?.draw(canvas, paint)
            time++
            animator.animate {
                circleLinkedPath?.update({
                    animator.stop()
                }) { it ->
                    Log.d("from","$it to $it+1")
                }
            }
        }
        fun handleTap() {
            circleLinkedPath?.startUpdating {
                animator.start()
            }
        }
    }
    companion object {
        fun create(activity: Activity):CircleLinkedPathView {
            val view = CircleLinkedPathView(activity)
            activity.setContentView(view)
            return view
        }
    }
}