package it.niccolo.citytour

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item.view.*
import kotlin.collections.ArrayList

class SpotAdapter(private val context: Context,
                  private val dataSource: ArrayList<Spot>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount() : Int {
        return dataSource.size
    }

    override fun getItem(p0 : Int) : Any {
        return dataSource[p0]
    }

    override fun getItemId(p0 : Int) : Long {
        return p0.toLong()
    }

    fun getItemName(p0 : Int) : String {
        return dataSource[p0].name
    }

    override fun getView(p0 : Int, p1 : View?, p2 : ViewGroup?) : View {
        val rowView = inflater.inflate(R.layout.list_item, p2, false)

        val title = rowView.txtTitle as TextView
        val snippet = rowView.txtSnippet as TextView
        val lat = rowView.txtLat as TextView
        val lgt = rowView.txtLgt as TextView

        val spot = getItem(p0) as Spot
        title.text = spot.name
        snippet.text = spot.description
        lat.text = spot.lat.toString()
        lgt.text = spot.lgt.toString()

        return rowView
    }

}