package com.example.tarkovcompanion

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.android.synthetic.main.fragment_inventory_screen.view.*
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class Resources {

    companion object {
        public lateinit var CURRENT_ACTIVITY : Activity

        public var CURRENT_USER_ID : Int = 0
        public var CURRENT_USER_NICKNAME : String = ""
        public var CURRENT_USER_LEVEL : Int = 0
        public var CURRENT_USER_INVENTORY_SIZE : Int = 0

        public var CURRENT_USER_RUBLES_COUNT = 0
        public var CURRENT_USER_DOLLARS_COUNT = 0
        public var CURRENT_USER_EUROS_COUNT = 0

        public var CURRENT_BUILDING_ID = 0
        public var CURRENT_BUILDING_NAME = ""
        public var CURRENT_BUILDING_LEVEL = 0

        public var CURRENT_TRADER_NAME = ""
        public var CURRENT_TRADER_LEVEL = 0
        public var CURRENT_TRADER_RELATIONSHIP = 0.0

        public var LAST_VISITED_TRADER_NAME = ""

        public var CURRENT_USER_ITEMS_LIST = mutableListOf<Item>()
        public var CURRENT_TRADER_ITEMS_LIST = mutableListOf<Item>()
        public var CURRENT_USER_TRADERS_LIST = mutableListOf<Trader>()
        public var CURRENT_USER_SKILLS_LIST = mutableListOf<Skill>()

        public var CURRENT_SELECTED_ITEMS_LIST = mutableListOf<Item>()

        public var ITEM_RUBLES_ID = -1
        public var ITEM_DOLLARS_ID = -1
        public var ITEM_EUROS_ID = -1

        public var CURRENT_USER_ITEM_LIST_FILE_NAME = "current_user_items.json"
        public var CURRENT_USER_ITEM_LIST_FILE_PATH = ""
        public var CURRENT_TRADER_ITEM_LIST_FILE_NAME = "current_trader_items.json"
        public var CURRENT_TRADER_ITEM_LIST_FILE_PATH = ""

        public var IS_CURRENT_USER_ITEM_LIST_UPDATED = true
        public var IS_CURRENT_TRADER_ITEMS_LIST_UPDATED = true

        public fun addCell(
                item : Item,
                context : Context?,
                activity : FragmentActivity?,
                gridLayout: GridLayout?
        ) {
            val newItem = TextView(activity)
            val scale = context!!.resources.displayMetrics.density
            val width = (85 * scale + 0.5f).toInt() // width = 85 dp
            val height = (80 * scale + 0.5f).toInt() // height = 80 dp
            val params = LinearLayout.LayoutParams(width, height)
            params.setMargins(5, 5, 5, 5)
            newItem.layoutParams = params
            newItem.text = item.name
            newItem.gravity = Gravity.FILL
            newItem.textAlignment = View.TEXT_ALIGNMENT_CENTER
            newItem.setTextColor(context.resources.getColor(R.color.button_color))

            newItem.background = AppCompatResources.getDrawable(context, R.drawable.item_container_icon)
            val selectedItem = Item(item.id, item.name, item.type, item.price, 1, true)
            newItem.setOnClickListener {
                if (!item.isSelected && item.name != "") {
                    item.isSelected = true
                    CURRENT_SELECTED_ITEMS_LIST.add(selectedItem)
                    it.background = AppCompatResources.getDrawable(context, R.drawable.item_selected_container_icon)
                }
                else {
                    item.isSelected = false
                    CURRENT_SELECTED_ITEMS_LIST.remove(selectedItem)
                    it.background = AppCompatResources.getDrawable(context, R.drawable.item_container_icon)
                }
            }
            gridLayout?.addView(newItem)
        }

        public fun unSelectCells() {

        }

        public fun fillUserItemsListJSONFile(itemsList : MutableList<Item>) {
            val mapper = ObjectMapper()
            val file = File(CURRENT_USER_ITEM_LIST_FILE_PATH)
            val node = mapper.readValue(
                    file, JsonNode::class.java
            )

            val itemsArrayNode: ArrayNode = node.findValue("current_user_items") as ArrayNode
            for (item in itemsList) {
                val addedNode: ObjectNode = itemsArrayNode.addObject()
                addedNode
                        .put("id", item.id)
                        .put("name", item.name)
                        .put("type", item.type)
                        .put("price", item.count)
                        .put("count", item.count)
                mapper.writeValue(file, node)
            }

            Resources.IS_CURRENT_USER_ITEM_LIST_UPDATED = false
        }

        public fun fillTraderItemsListJSONFile(itemsList : MutableList<Item>) {
            val mapper = ObjectMapper()
            val file = File(CURRENT_TRADER_ITEM_LIST_FILE_PATH)
            val node = mapper.readValue(
                    file, JsonNode::class.java
            )

            val itemsArrayNode: ArrayNode = node.findValue("current_trader_items") as ArrayNode
            for (item in itemsList) {
                val addedNode: ObjectNode = itemsArrayNode.addObject()
                addedNode
                        .put("id", item.id)
                        .put("name", item.name)
                        .put("type", item.type)
                        .put("price", item.price)
                        .put("count", item.count)
                mapper.writeValue(file, node)
            }

            Resources.IS_CURRENT_TRADER_ITEMS_LIST_UPDATED = false
        }

        //Создаёт JSON-файл, который будет хранить список предметов текущего пользователя
        public fun createUserItemsListJSONFile() {
            var file = File(CURRENT_ACTIVITY.filesDir, Resources.CURRENT_USER_ITEM_LIST_FILE_NAME)
            var fileExists = file.exists()

//            if (!fileExists) {
                val fos: FileOutputStream = CURRENT_ACTIVITY.openFileOutput(Resources.CURRENT_USER_ITEM_LIST_FILE_NAME, Context.MODE_PRIVATE)
                val osw = OutputStreamWriter(fos)
                val text = "{\n" +
                        "    \"current_user_items\" : [\n" +
                        "    ]\n" +
                        "}"
                osw.write(text);
                osw.flush();
                osw.close();
                fos.close()
                Log.d("DByyy","Saved to ${CURRENT_ACTIVITY.filesDir}/${Resources.CURRENT_USER_ITEM_LIST_FILE_NAME}")
                Resources.CURRENT_USER_ITEM_LIST_FILE_PATH = "${CURRENT_ACTIVITY.filesDir}/${Resources.CURRENT_USER_ITEM_LIST_FILE_NAME}"

//            sPref = getPreferences(Context.MODE_PRIVATE)
//            val editor = sPref.edit()
//            editor.putString(CURRENT_USER_ITEM_LIST_FILE_PATH, Resources.CURRENT_USER_ITEM_LIST_FILE_PATH)
//            editor.commit()
//            }
//        else {
//            sPref = getPreferences(Context.MODE_PRIVATE)
//            val jsonFilePath : String? = sPref.getString(CURRENT_USER_ITEM_LIST_FILE_PATH, "")
//            Resources.CURRENT_USER_ITEM_LIST_FILE_PATH = jsonFilePath.toString()
//        }
        }

        //Создаёт JSON-файл, который будет хранить список предметов последнего открытого торговца
        public fun createTraderItemsListJSONFile() {
            var file = File(CURRENT_ACTIVITY.filesDir, Resources.CURRENT_TRADER_ITEM_LIST_FILE_NAME)
            var fileExists = file.exists()

//            if (!fileExists) {
                val fos: FileOutputStream = CURRENT_ACTIVITY.openFileOutput(Resources.CURRENT_TRADER_ITEM_LIST_FILE_NAME, Context.MODE_PRIVATE)
                val osw = OutputStreamWriter(fos)
                val text = "{\n" +
                        "    \"current_trader_items\" : [\n" +
                        "    ]\n" +
                        "}"
                osw.write(text);
                osw.flush();
                osw.close();
                fos.close()
                Log.d("DByyy","Saved to ${CURRENT_ACTIVITY.filesDir}/${Resources.CURRENT_TRADER_ITEM_LIST_FILE_NAME}")
                Resources.CURRENT_TRADER_ITEM_LIST_FILE_PATH = "${CURRENT_ACTIVITY.filesDir}/${Resources.CURRENT_TRADER_ITEM_LIST_FILE_NAME}"

//            sPref = getPreferences(Context.MODE_PRIVATE)
//            val editor = sPref.edit()
//            editor.putString(CURRENT_TRADER_ITEM_LIST_FILE_PATH, Resources.CURRENT_TRADER_ITEM_LIST_FILE_PATH)
//            editor.commit()
//            }
//        else {
//            sPref = getPreferences(Context.MODE_PRIVATE)
//            val jsonFilePath : String? = sPref.getString(CURRENT_TRADER_ITEM_LIST_FILE_PATH, "")
//            Resources.CURRENT_TRADER_ITEM_LIST_FILE_PATH = jsonFilePath.toString()
//        }
        }


        public fun deleteFile(fileName : String) {
            val file = File(CURRENT_ACTIVITY.filesDir, fileName)
            file.delete()
        }

        public fun createListWithJSONDataForUser() : MutableList<Item> {
            val itemsList = mutableListOf<Item>()

            //инициализация JSON-парсера
            val mapper = ObjectMapper()
            val node = mapper.readValue(
                    File(Resources.CURRENT_USER_ITEM_LIST_FILE_PATH), JsonNode::class.java
            )

            //вычисляем количество вещей в JSON-файле
            val itemArrayNode : ArrayNode = node.findValue("current_user_items") as ArrayNode

            //вытаскиваем инфу из JSON-файла
            for (item in itemArrayNode) {
                val id = item.findValue("id").asText().toInt()
                val name = item.findValue("name").asText()
                val type = item.findValue("type").asText()
                val price = item.findValue("price").asText().toInt()
                val count = item.findValue("count").asText().toInt()

                itemsList.add(Item(id, name, type, price, count, false))
            }

            return itemsList
        }


        public fun createListWithJSONDataForTrader() : MutableList<Item> {
            val itemsList = mutableListOf<Item>()

            //инициализация JSON-парсера
            val mapper = ObjectMapper()
            val node = mapper.readValue(
                    File(Resources.CURRENT_TRADER_ITEM_LIST_FILE_PATH), JsonNode::class.java
            )

            //вычисляем количество вещей в JSON-файле
            val itemArrayNode : ArrayNode = node.findValue("current_trader_items") as ArrayNode

            //вытаскиваем инфу из JSON-файла
            for (item in itemArrayNode) {
                val id = item.findValue("id").asText().toInt()
                val name = item.findValue("name").asText()
                val type = item.findValue("type").asText()
                val price = item.findValue("price").asText().toInt()
                val count = item.findValue("count").asText().toInt()

                itemsList.add(Item(id, name, type, price, count, false))
            }

            return itemsList
        }

        public fun isPasswordValid(password: String, hash : String) : Boolean {
            return BCrypt.checkpw(password, hash)
        }

        public fun clearAllFields() {
            CURRENT_USER_ID = 0
            CURRENT_USER_NICKNAME = ""
            CURRENT_USER_LEVEL = 0
            CURRENT_USER_INVENTORY_SIZE = 0

            CURRENT_USER_RUBLES_COUNT = 0
            CURRENT_USER_DOLLARS_COUNT = 0
            CURRENT_USER_EUROS_COUNT = 0

            CURRENT_BUILDING_ID = 0
            CURRENT_BUILDING_NAME = ""
            CURRENT_BUILDING_LEVEL = 0

            CURRENT_TRADER_NAME = ""
            CURRENT_TRADER_LEVEL = 0
            CURRENT_TRADER_RELATIONSHIP = 0.0

            LAST_VISITED_TRADER_NAME = ""

            CURRENT_USER_ITEMS_LIST.clear()
            CURRENT_TRADER_ITEMS_LIST.clear()
            CURRENT_USER_TRADERS_LIST.clear()
            CURRENT_USER_SKILLS_LIST.clear()

            CURRENT_SELECTED_ITEMS_LIST.clear()

            ITEM_RUBLES_ID = -1
            ITEM_DOLLARS_ID = -1
            ITEM_EUROS_ID = -1

            CURRENT_USER_ITEM_LIST_FILE_NAME = "current_user_items.json"
            CURRENT_USER_ITEM_LIST_FILE_PATH = ""
            CURRENT_TRADER_ITEM_LIST_FILE_NAME = "current_trader_items.json"
            CURRENT_TRADER_ITEM_LIST_FILE_PATH = ""

            IS_CURRENT_USER_ITEM_LIST_UPDATED = true
            IS_CURRENT_TRADER_ITEMS_LIST_UPDATED = true
        }
    }




}