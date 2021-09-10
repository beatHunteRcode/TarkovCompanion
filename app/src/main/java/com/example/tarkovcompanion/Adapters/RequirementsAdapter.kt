package com.example.tarkovcompanion.Adapters

import android.content.Context
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.tarkovcompanion.R
import com.example.tarkovcompanion.Requirement
import com.example.tarkovcompanion.Resources
import kotlinx.android.synthetic.main.rv_requirement_item.view.*

class RequirementsAdapter(v : View, requirementsList : MutableList<Requirement>) :
        RecyclerView.Adapter<RequirementsAdapter.RequirementViewHolder>() {

    private val mainView = v
    private var reqsList : MutableList<Requirement> = requirementsList
    private var i = 0

    class RequirementViewHolder(v : View, itemView: View) : RecyclerView.ViewHolder(itemView) {

        val checkIcon : ImageView = itemView.check_icon
        var requirementName : TextView = itemView.requirement_name
        var requirementValue : TextView = itemView.level_or_count_value
        var requirementText : TextView = itemView.level_or_count_text

        fun bind(requirement : Requirement) {
            this.requirementName.text = requirement.name
            this.requirementValue.text = requirement.value.toString()

            when (requirement.type) {
                Requirement.TYPE_ITEM -> {
                    requirementText.text = "Кол-во"
                    for (item in Resources.CURRENT_USER_ITEMS_LIST) {
                        if (item.name == requirement.name && item.count >= requirement.value) {
                            checkIcon.isVisible = true
                            requirement.isReady = true
                        }
                    }
                }
                Requirement.TYPE_SKILL -> {
                    requirementText.text = "Уровень"
                    for (skill in Resources.CURRENT_USER_SKILLS_LIST) {
                        if (skill.name == requirement.name && skill.level >= requirement.value) {
                            checkIcon.isVisible = true
                            requirement.isReady = true
                        }
                    }
                }
                Requirement.TYPE_TRADER -> {
                    requirementText.text = "Уровень"
                    for (trader in Resources.CURRENT_USER_TRADERS_LIST)
                        if (trader.name == requirement.name && trader.level >=  requirement.value) {
                            checkIcon.isVisible = true
                            requirement.isReady = true
                        }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequirementViewHolder {
        val context : Context = parent.context
        val layoutId : Int = R.layout.rv_requirement_item

        val layoutInflater = LayoutInflater.from(context)
        val itemView = layoutInflater.inflate(layoutId, parent, false)



        return RequirementViewHolder(mainView, itemView)
    }

    override fun onBindViewHolder(holder: RequirementViewHolder, position: Int) {
        holder.bind(reqsList[i])

        if (i < itemCount - 1) i++
        else i = 0
    }

    override fun getItemCount(): Int {
        return reqsList.size
    }
}