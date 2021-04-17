package it.niccolo.citytour

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_places.*


class PlacesFragment : Fragment() {

    private lateinit var db : DatabaseHandler
    private lateinit var spots : MutableList<Spot>
    private lateinit var adapter : SpotAdapter
    private lateinit var appContext : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHandler(appContext)
        spots = db.getSpots()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = SpotAdapter(appContext, spots as ArrayList<Spot>)
        listPlaces.adapter = adapter
        listPlaces.setOnItemClickListener { _, _, pos, _ ->
            (appContext as MainActivity).goToInfoActivity(adapter.getItemName(pos))
        }
    }

}