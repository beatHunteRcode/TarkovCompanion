package com.example.tarkovcompanion.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tarkovcompanion.Resources
import com.example.tarkovcompanion.Database
import com.example.tarkovcompanion.Item
import com.example.tarkovcompanion.R
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.android.synthetic.main.fragment_inventory_screen.view.*
import kotlinx.coroutines.*
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InventoryScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InventoryScreenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private var itemsList : MutableList<Item> = mutableListOf()


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
        (activity as AppCompatActivity).supportActionBar!!.title = "Схрон"
        val view = inflater.inflate(R.layout.fragment_inventory_screen, container, false)
        Resources.CURRENT_SELECTED_ITEMS_LIST.clear()
        Log.d("DByyy", Resources.CURRENT_SELECTED_ITEMS_LIST.size.toString())

        view.rubles_count.text = Resources.CURRENT_USER_RUBLES_COUNT.toString()
        view.dollars_count.text = Resources.CURRENT_USER_DOLLARS_COUNT.toString()
        view.euros_count.text = Resources.CURRENT_USER_EUROS_COUNT.toString()

        if (Resources.IS_CURRENT_USER_ITEM_LIST_UPDATED) {

            val beforeTime = System.nanoTime()

            itemsList = Database.getCurrentUserItemList()

            Log.d("TimeCheck", "Схрон | Кол-во предметов: ${itemsList.size}")

            var afterTime = System.nanoTime()

            Log.d("TimeCheck", "Схрон | Время выполнения запроса: ${(afterTime - beforeTime)/1000000} мс")

            Resources.deleteFile(Resources.CURRENT_USER_ITEM_LIST_FILE_NAME)
            Resources.createUserItemsListJSONFile()
            Resources.fillUserItemsListJSONFile(itemsList)

            Resources.IS_CURRENT_USER_ITEM_LIST_UPDATED = false
            Resources.CURRENT_USER_ITEMS_LIST.clear()
            for (item in itemsList) {
                Resources.CURRENT_USER_ITEMS_LIST.add(item)
            }

            afterTime = System.nanoTime()

            Log.d("TimeCheck", "Схрон | Время выполнения запроса + кэширования: ${(afterTime - beforeTime)/1000000} мс")
        }
        else {
            val beforeTime = System.nanoTime()

            itemsList = Resources.createListWithJSONDataForUser()

            val afterTime = System.nanoTime()

            Log.d("TimeCheck", "Схрон | Время выполнения вытаскивания из кэша: ${(afterTime - beforeTime)/1000000} мс")
        }

        var cellNumb = 0
        val gridLayout = view.items_grid_layout

        for (item in itemsList) {
            if (cellNumb >= Resources.CURRENT_USER_INVENTORY_SIZE) break
            if (item.type != "деньги") {
                for (i in 1..item.count) {
                    if (cellNumb >= Resources.CURRENT_USER_INVENTORY_SIZE) break
                    Resources.addCell(item, requireContext(), activity, gridLayout)
                    cellNumb++
                }
            }
        }

        for (i in 0..Resources.CURRENT_USER_INVENTORY_SIZE - cellNumb) {
            Resources.addCell(
                    Item(-1, "", "", 0, 0, false),
                    requireContext(),
                    activity,
                    gridLayout
            )
        }

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InventoryScreenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InventoryScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}