package com.example.tarkovcompanion.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tarkovcompanion.Database
import com.example.tarkovcompanion.Item
import com.example.tarkovcompanion.R
import com.example.tarkovcompanion.Resources
import kotlinx.android.synthetic.main.fragment_trader_buy_screen.view.*
import kotlinx.android.synthetic.main.fragment_trader_sell_screen.view.*
import kotlinx.android.synthetic.main.fragment_trader_sell_screen.view.trader_level
import kotlinx.android.synthetic.main.fragment_trader_sell_screen.view.trader_name
import kotlinx.android.synthetic.main.fragment_trader_sell_screen.view.trader_relationship
import kotlinx.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TraderSellScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TraderSellScreenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    private var itemsList : MutableList<Item> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_trader_sell_screen, container, false)
        Resources.CURRENT_SELECTED_ITEMS_LIST.clear()

        view.sell_screen_rubles_count.text = Resources.CURRENT_USER_RUBLES_COUNT.toString()
        view.sell_screen_dollars_count.text = Resources.CURRENT_USER_DOLLARS_COUNT.toString()
        view.sell_screen_euros_count.text = Resources.CURRENT_USER_EUROS_COUNT.toString()

        view.trader_name.text = Resources.CURRENT_TRADER_NAME
        view.trader_level.text = Resources.CURRENT_TRADER_LEVEL.toString()
        view.trader_relationship.text = Resources.CURRENT_TRADER_RELATIONSHIP.toString()

        view.sell_deal_button.setOnClickListener {
            if (Resources.CURRENT_SELECTED_ITEMS_LIST.isNotEmpty()) {

                for (item in Resources.CURRENT_SELECTED_ITEMS_LIST) {
                    for (i in 1..item.count) Database.sellItem(item)
                }
                Resources.CURRENT_SELECTED_ITEMS_LIST.clear()

                view.sell_screen_rubles_count.text = Resources.CURRENT_USER_RUBLES_COUNT.toString()
                view.sell_screen_dollars_count.text = Resources.CURRENT_USER_DOLLARS_COUNT.toString()
                view.sell_screen_euros_count.text = Resources.CURRENT_USER_EUROS_COUNT.toString()

                Toast.makeText(context, "Продано!", Toast.LENGTH_SHORT).show()
                Resources.IS_CURRENT_USER_ITEM_LIST_UPDATED = true

                view.user_sell_grid_layout.removeAllViews()
                fillItemsGridFromView(view)
            }

        }

        fillItemsGridFromView(view)

        return view
    }

    private fun giveItemsToUser(v : View) {
        if (Resources.IS_CURRENT_USER_ITEM_LIST_UPDATED) {

            val beforeTime = System.nanoTime()

            itemsList = Database.getCurrentUserItemsList()

            var afterTime = System.nanoTime()

            Log.d("TimeCheck", "${Resources.CURRENT_TRADER_NAME}-продажа | Время выполнения запроса: ${(afterTime - beforeTime)/1000000} мс")

            Resources.deleteFile(Resources.CURRENT_USER_ITEM_LIST_FILE_NAME)
            Resources.createUserItemsListJSONFile()
            Resources.fillUserItemsListJSONFile(itemsList)

            Resources.IS_CURRENT_USER_ITEM_LIST_UPDATED = false

            afterTime = System.nanoTime()

            Log.d("TimeCheck", "${Resources.CURRENT_TRADER_NAME}-продажа | Время выполнения запроса + кэширования: ${(afterTime - beforeTime)/1000000} мс")
        }
        else itemsList = Resources.createListWithJSONDataForUser()

        var cellNumb = 0
        val gridLayout = v.user_sell_grid_layout

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
    }

    private fun fillItemsGridFromView(view : View) {
        if (Resources.IS_CURRENT_USER_ITEM_LIST_UPDATED) {

            giveItemsToUser(view)
            Resources.CURRENT_USER_ITEMS_LIST.clear()
            for (item in itemsList) {
                Resources.CURRENT_USER_ITEMS_LIST.add(item)
            }
            Resources.IS_CURRENT_USER_ITEM_LIST_UPDATED = false

        }
        else {

            val beforeTime = System.nanoTime()

            giveItemsToUser(view)

            val afterTime = System.nanoTime()

            Log.d("TimeCheck", "Торговец-продажа | Время выполнения вытаскивания из кэша: ${(afterTime - beforeTime)/1000000} мс")
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TraderSellScreenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TraderSellScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}