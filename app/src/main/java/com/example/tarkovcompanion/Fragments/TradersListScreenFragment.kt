package com.example.tarkovcompanion.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tarkovcompanion.Adapters.SanctuaryAdapter
import com.example.tarkovcompanion.Adapters.TradersAdapter
import com.example.tarkovcompanion.R
import kotlinx.android.synthetic.main.fragment_sanctuary_screen.view.*
import kotlinx.android.synthetic.main.fragment_traders_list_screen.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TradersListScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TradersListScreenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var tradersRV : RecyclerView
    private lateinit var tradersAdapter: TradersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.title = "Торговцы"
        val view = inflater.inflate(R.layout.fragment_traders_list_screen, container, false)

        tradersRV = view.traders_rv
        val layoutManager = LinearLayoutManager(context)
        tradersRV.layoutManager = layoutManager
        tradersAdapter = TradersAdapter(view, this)
        tradersRV.adapter = tradersAdapter

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TradersListScreenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TradersListScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}