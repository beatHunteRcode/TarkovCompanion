package com.example.tarkovcompanion.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tarkovcompanion.*
import com.example.tarkovcompanion.Adapters.RequirementsAdapter
import kotlinx.android.synthetic.main.fragment_building_screen.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.ResultSet

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BuildingScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BuildingScreenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    /** Список требований для улучшения текущей постройки
     *  Содержит элементы класса Triple
     *      first - название предмета/навыка/торговца
     *      second - количество/уровень
     *      third - тип требования: 1 = Предмет, 2 = Навык/Торговец
    **/
    private var requirementsList = mutableListOf<Requirement>()

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
        (activity as AppCompatActivity).supportActionBar!!.title = "Убежище"
        val view = inflater.inflate(R.layout.fragment_building_screen, container, false)

        view.building_name.text = Resources.CURRENT_BUILDING_NAME
        view.building_level_val.text = Resources.CURRENT_BUILDING_LEVEL.toString()

        val currentBonuses = "Error"
        val requirementId = -1
        Database.getInfoAboutSelectedBuilding(requirementsList, currentBonuses, requirementId, view)

        view.improve_button.setOnClickListener {
            Database.improveBuilding(requirementsList, currentBonuses, requirementId, view)

        }

        val requirementsRV =  view.requirements_rv
        requirementsRV.layoutManager = LinearLayoutManager(context)
        requirementsRV.adapter = RequirementsAdapter(view, requirementsList)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BuildingScreenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BuildingScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Resources.CURRENT_USER_ITEMS_LIST.clear()
    }
}