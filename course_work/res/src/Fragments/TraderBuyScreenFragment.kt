package com.example.tarkovcompanion.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tarkovcompanion.*
import kotlinx.android.synthetic.main.fragment_trader_buy_screen.view.*
import kotlinx.android.synthetic.main.fragment_trader_buy_screen.view.trader_level
import kotlinx.android.synthetic.main.fragment_trader_buy_screen.view.trader_name
import kotlinx.android.synthetic.main.fragment_trader_buy_screen.view.trader_relationship
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TraderBuyScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TraderBuyScreenFragment : Fragment() {
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
    private lateinit var mainView : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_trader_buy_screen, container, false)
        Resources.CURRENT_SELECTED_ITEMS_LIST.clear()

        mainView = view

        view.buy_screen_rubles_count.text = Resources.CURRENT_USER_RUBLES_COUNT.toString()
        view.buy_screen_dollars_count.text = Resources.CURRENT_USER_DOLLARS_COUNT.toString()
        view.buy_screen_euros_count.text = Resources.CURRENT_USER_EUROS_COUNT.toString()

        view.trader_name.text = Resources.CURRENT_TRADER_NAME
        view.trader_level.text = Resources.CURRENT_TRADER_LEVEL.toString()
        view.trader_relationship.text = Resources.CURRENT_TRADER_RELATIONSHIP.toString()

        if (Resources.LAST_VISITED_TRADER_NAME != Resources.CURRENT_TRADER_NAME) {
            Resources.IS_CURRENT_TRADER_ITEMS_LIST_UPDATED = true
            Resources.LAST_VISITED_TRADER_NAME = Resources.CURRENT_TRADER_NAME
        }

        view.buy_deal_button.setOnClickListener {
            if (Resources.CURRENT_SELECTED_ITEMS_LIST.isNotEmpty()) {

                var sum = 0
                for (item in Resources.CURRENT_SELECTED_ITEMS_LIST) sum += item.price

                if (sum < Resources.CURRENT_USER_RUBLES_COUNT) {
                    for (item in Resources.CURRENT_SELECTED_ITEMS_LIST) Database.buyItem(item)

                    Resources.CURRENT_SELECTED_ITEMS_LIST.clear()

                    view.buy_screen_rubles_count.text = Resources.CURRENT_USER_RUBLES_COUNT.toString()
                    view.buy_screen_dollars_count.text = Resources.CURRENT_USER_DOLLARS_COUNT.toString()
                    view.buy_screen_euros_count.text = Resources.CURRENT_USER_EUROS_COUNT.toString()

                    Toast.makeText(context, "Куплено!", Toast.LENGTH_SHORT).show()

                    Resources.IS_CURRENT_USER_ITEM_LIST_UPDATED = true

                    view.trader_buy_grid_layout.removeAllViews()
                    fillItemsGrid()
                }
                else Toast.makeText(context, "Не хватает денег", Toast.LENGTH_SHORT).show()

            }

        }

        fillItemsGrid()


        return view
    }

    private fun fillItemsGrid() {
        if (Resources.IS_CURRENT_TRADER_ITEMS_LIST_UPDATED) {

            giveItemsToTrader(Resources.CURRENT_TRADER_NAME)
            Resources.CURRENT_TRADER_ITEMS_LIST.clear()
            for (item in itemsList) {
                Resources.CURRENT_TRADER_ITEMS_LIST.add(item)
            }
            Resources.IS_CURRENT_TRADER_ITEMS_LIST_UPDATED = false

        }
        else {
            val beforeTime = System.nanoTime()

            giveItemsToTrader(Resources.CURRENT_TRADER_NAME)

            val afterTime = System.nanoTime()

            Log.d("TimeCheck", "${Resources.CURRENT_TRADER_NAME}-покупка | Время выполнения вытаскивания из кэша: ${(afterTime - beforeTime)/1000000} мс")
        }

    }

    private fun giveItemsToTrader(traderName : String) {
        if (Resources.IS_CURRENT_TRADER_ITEMS_LIST_UPDATED) {

            val beforeTime = System.nanoTime()

            when (traderName) {
                "Прапор" -> {
                    for (el in Types.Ammo.values()) giveItemsWithType(el.n)
                    for (el in Types.Weapon.values()) giveItemsWithType(el.n)
                }
                "Терапевт" -> {
                    for (el in Types.Meds.values()) giveItemsWithType(el.n)
                    for (el in Types.Keys.values()) giveItemsWithType(el.n)
                    for (el in Types.Maps.values()) giveItemsWithType(el.n)
                    for (el in Types.Provision.values()) giveItemsWithType(el.n)
                }
                "Скупщик" -> {
                    for (el in Types.SpecialEquipment.values()) giveItemsWithType(el.n)
                    for (el in Types.BarterItems.values()) giveItemsWithType(el.n)
                }
                "Лыжник" -> {
                    for (el in Types.Ammo.values()) giveItemsWithType(el.n)
                    for (el in Types.Weapon.values()) giveItemsWithType(el.n)
                }
                "Миротворец" -> {
                    for (el in Types.Ammo.values()) giveItemsWithType(el.n)
                    for (el in Types.Weapon.values()) giveItemsWithType(el.n)
                }
                "Механик" -> {
                    for (el in Types.SpecialEquipment.values()) giveItemsWithType(el.n)
                    for (el in Types.Ammo.values()) giveItemsWithType(el.n)
                    for (el in Types.Weapon.values()) giveItemsWithType(el.n)
                }
                "Барахольщик" -> {
                    for (el in Types.BarterItems.values()) giveItemsWithType(el.n)
                    for (el in Types.Equipment.values()) giveItemsWithType(el.n)
                }
                "Егерь" -> {
                    for (el in Types.Provision.values()) giveItemsWithType(el.n)
                    for (el in Types.InfoItems.values()) giveItemsWithType(el.n)
                    for (el in Types.Maps.values()) giveItemsWithType(el.n)
                }
            }

            Log.d("TimeCheck", "${Resources.CURRENT_TRADER_NAME} | Кол-во предметов: ${itemsList.size}")

            var afterTime = System.nanoTime()

            Log.d("TimeCheck", "${Resources.CURRENT_TRADER_NAME}-покупка | Время выполнения запроса: ${(afterTime - beforeTime)/1000000} мс")

            Resources.deleteFile(Resources.CURRENT_TRADER_ITEM_LIST_FILE_NAME)
            Resources.createTraderItemsListJSONFile()
            Resources.fillTraderItemsListJSONFile(itemsList)

            Resources.IS_CURRENT_TRADER_ITEMS_LIST_UPDATED = false

            afterTime = System.nanoTime()

            Log.d("TimeCheck", "${Resources.CURRENT_TRADER_NAME}-покупка | Время выполнения запроса + кэширования: ${(afterTime - beforeTime)/1000000} мс")
        }
        else {
            itemsList = Resources.createListWithJSONDataForTrader()

            val gridLayout = mainView.trader_buy_grid_layout
            for (item in itemsList) {
                Resources.addCell(item, requireContext(), activity, gridLayout)
            }
        }
    }

    private fun giveItemsWithType(type : String)  {
        for (item in Database.getItemsListWithType(type)) itemsList.add(item)

        val gridLayout = mainView.trader_buy_grid_layout
        gridLayout.removeAllViews()
        for (item in itemsList) {
            Resources.addCell(item, requireContext(), activity, gridLayout)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TraderBuyScreenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TraderBuyScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}