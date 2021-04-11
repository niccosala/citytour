package it.niccolo.citytour

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_location.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pagerAdapter = PagerAdapter(supportFragmentManager, tabLayout.tabCount)
        pager.adapter = pagerAdapter

        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) { }
            override fun onTabReselected(tab: TabLayout.Tab) { }
        })

        val db = DatabaseHandler(this)
        db.dropDatabase()
    }

    fun showDetailsButton(toShow : Boolean, title : String?) {
        val bVisibility = if(toShow) View.VISIBLE else View.GONE
        val tVisibility = if(toShow) View.GONE else View.VISIBLE
        lateinit var text : String

        if(title != null)
            text = if(title.length <= 20 ) "VISUALIZZA '$title'" else "VISUALIZZA '${title.take(20)}'"

        btnDetails.text = text
        btnDetails.visibility = bVisibility
        txtInfo.visibility = tVisibility
    }

}