package com.example.tarkovcompanion.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.tarkovcompanion.Building
import com.example.tarkovcompanion.Resources
import com.example.tarkovcompanion.Database
import com.example.tarkovcompanion.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SanctuaryAdapter(v : View) : RecyclerView.Adapter<SanctuaryAdapter.SanctuaryViewHolder>() {

    private var buildingsList = mutableListOf<Building>()
    private var i = 0
    private var numbElements = 0
    private val mainView = v

    init {
        buildingsList = Database.getCurrentUserBuildingsList()
        numbElements = buildingsList.size
    }

    class SanctuaryViewHolder(v : View, itemView: View) : RecyclerView.ViewHolder(itemView) {

        var buildingName : TextView = itemView.findViewById(R.id.building_name)
        var buildingLevelVal : TextView = itemView.findViewById(R.id.building_level_val)


        init {
            itemView.setOnClickListener {
                Navigation.findNavController(v).navigate(
                        R.id.action_sanctuaryScreenFragment_to_buildingScreenFragment
                )
                val curBuildingName : TextView = itemView.findViewById(R.id.building_name)
                val curBuildingLevelVal : TextView = itemView.findViewById(R.id.building_level_val)

                Resources.CURRENT_BUILDING_NAME = curBuildingName.text.toString()
                Resources.CURRENT_BUILDING_LEVEL = curBuildingLevelVal.text.toString().toInt()

            }
        }
        fun bind(buildingName : String, buildingLevelVal : Int) {
            this.buildingName.text = buildingName
            this.buildingLevelVal.text = buildingLevelVal.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SanctuaryViewHolder {
        val context : Context = parent.context
        val layoutId : Int = R.layout.rv_sanctuary_item

        val layoutInflater = LayoutInflater.from(context)
        val itemView = layoutInflater.inflate(layoutId, parent, false)

        return SanctuaryViewHolder(mainView, itemView)
    }

    override fun onBindViewHolder(holder: SanctuaryViewHolder, position: Int) {
        holder.bind(buildingsList[i].name, buildingsList[i].level)

        if (i < itemCount - 1) i++
        else i = 0
    }

    override fun getItemCount(): Int {
        return numbElements
    }

}