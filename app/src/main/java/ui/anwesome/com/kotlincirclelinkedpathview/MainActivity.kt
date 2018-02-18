package ui.anwesome.com.kotlincirclelinkedpathview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.circlelinkedpathview.CircleLinkedPathView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = CircleLinkedPathView.create(this)
    }
}
