package it.niccolo.citytour

import android.content.Intent
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
    }

    fun showDetailsButton(toShow : Boolean, title : String?) {
        val bVisibility = if(toShow) View.VISIBLE else View.GONE
        val tVisibility = if(toShow) View.GONE else View.VISIBLE
        var text = ""

        if(title != null)
            text = if(title.length <= 20) "VISUALIZZA '$title'" else "VISUALIZZA '${title.take(17)}...'"

        btnDetails.text = text
        btnDetails.hint = title
        btnDetails.visibility = bVisibility
        txtInfo.visibility = tVisibility
    }

    fun goToInfoActivity(spotName : String) {
        startActivity(Intent(this, InfoActivity::class.java).putExtra("spotName", spotName))
        Log.d("dev-main", "Going to InfoActivity (passed '$spotName')")
    }

}