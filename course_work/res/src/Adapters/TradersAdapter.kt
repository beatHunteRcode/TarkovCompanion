package com.example.tarkovcompanion.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.tarkovcompanion.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class TradersAdapter(v : View, fragment : Fragment) : RecyclerView.Adapter<TradersAdapter.TradersViewHolder>() {

    private var tradersList = mutableListOf<Trader>()
    private var i = 0
    private var numbElements = 0
    private val mainView = v
    private val frag = fragment

    init {
        tradersList = Database.getCurrentUserTradersList()
        numbElements = tradersList.size
    }

    class TradersViewHolder(v : View, itemView: View, fragment: Fragment) : RecyclerView.ViewHolder(itemView) {
        var traderName : TextView = itemView.findViewById(R.id.trader_name)
        var traderLevel : TextView = itemView.findViewById(R.id.trader_level)
        var traderRelationship : TextView = itemView.findViewById(R.id.trader_relationship)

        init {
            itemView.setOnClickListener {

                val intent: Intent = Intent(fragment.context, TradersActivity::class.java)
                fragment.startActivity(intent)
//                Navigation.findNavController(v).navigate(
//                        R.id.action_tradersListScreenFragment_to_traderScreenFragment
//                )

                val curTraderName : TextView = itemView.findViewById(R.id.trader_name)
                val curTraderLevel : TextView = itemView.findViewById(R.id.trader_level)
                val curTraderRelationship : TextView = itemView.findViewById(R.id.trader_relationship)

                Resources.CURRENT_TRADER_NAME = curTraderName.text.toString()
                Resources.CURRENT_TRADER_LEVEL = curTraderLevel.text.toString().toInt()
                Resources.CURRENT_TRADER_RELATIONSHIP = curTraderRelationship.text.toString().toDouble()
            }
        }

        fun bind(traderName : String, traderLevel : Int, traderRelationship : Double) {
            this.traderName.text = traderName
            this.traderLevel.text = traderLevel.toString()
            val bigDecimal = BigDecimal(traderRelationship).setScale(2, RoundingMode.HALF_EVEN)
            this.traderRelationship.text = bigDecimal.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradersViewHolder {
        val context : Context = parent.context
        val layoutId : Int = R.layout.rv_traders_item

        val layoutInflater = LayoutInflater.from(context)
        val itemView = layoutInflater.inflate(layoutId, parent, false)

        return TradersViewHolder(mainView, itemView, frag)
    }

    override fun onBindViewHolder(holder: TradersViewHolder, position: Int) {
        holder.bind(tradersList[i].name, tradersList[i].level, tradersList[i].relationshipValue)

        if (i < itemCount - 1) i++
        else i = 0
    }

    override fun getItemCount(): Int {
        return numbElements
    }

}