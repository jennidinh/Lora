package com.jetbrains.handson.mpp.ny

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(toolbar)

        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)

        val viewP = findViewById<ViewPager>(R.id.viewpager)
        viewP.adapter = fragmentAdapter

        tabLayout.setupWithViewPager(viewP)
    }

}





