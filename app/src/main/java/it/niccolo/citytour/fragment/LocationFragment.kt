package it.niccolo.citytour.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import it.niccolo.citytour.R
import it.niccolo.citytour.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_location.*

class LocationFragment : Fragment() {

    private lateinit var appContext : Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnDetails.setOnClickListener {
            (appContext as MainActivity).goToInfoActivity((it as Button).hint.toString())
        }
    }

}