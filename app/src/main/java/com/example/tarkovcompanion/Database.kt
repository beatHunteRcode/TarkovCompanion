package com.example.tarkovcompanion

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tarkovcompanion.Adapters.RequirementsAdapter
import kotlinx.android.synthetic.main.fragment_building_screen.view.*
import kotlinx.coroutines.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*

class Database {
    companion object {

        val dbHOST = "192.168.1.112"
        val dbPORT = "5432"
        val dbUSER = "postgres"
        val dbPASSWORD = "sherepa2000spb"

        lateinit var connection : Connection

        public fun connect() {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://${dbHOST}:${dbPORT}/Tarkov_DB",
                    dbUSER,
                    dbPASSWORD
            )
        }

        public fun getTableSize(tableName : String) : Int {
            val result = connection.createStatement().executeQuery(
                    "SELECT count(*) FROM $tableName"
            )
            result.next()
            result.close()
            return result.getInt(1)
        }


        /**
         * Inserts random item from tables.items into table for one given user
         *
         * If the user already has an item,
         * a new generated item count is added to the existing item count
         * of the existing item
         *
         * */
        public fun buyItem(item : Item)  = runBlocking {

            CoroutineScope(Dispatchers.IO).launch {

                //Does item exist in use's inventory?
                var res = connection.createStatement().executeQuery(
                        "select exists(" +
                                "select * from items_in_inventories_of_all_users WHERE " +
                                "user_id = ${Resources.CURRENT_USER_ID} AND " +
                                "item_id = ${item.id}" +
                                ")"
                )
                res.next()

                val isExist = res.getBoolean(1)
                if (isExist) {
                    //if item exists -> increase current count of item in database
                    res = connection.createStatement().executeQuery(
                            "SELECT item_count FROM items_in_inventories_of_all_users where " +
                                    "user_id = ${Resources.CURRENT_USER_ID} and " +
                                    "item_id = ${item.id}"
                    )
                    res.next()
                    val curItemCount = res.getInt(1)
                    val newItemCount = curItemCount + item.count
                    connection.createStatement().execute(
                            "" +
                                    "UPDATE items_in_inventories_of_all_users " +
                                    "SET item_count = $newItemCount WHERE " +
                                    "user_id =  ${Resources.CURRENT_USER_ID}  AND " +
                                    "item_id = ${item.id}"
                    )
                }
                else {
                    //if item doesn't exist -> insert item in user's inventory
                    connection.createStatement().execute(
                            "INSERT INTO items_in_inventories_of_all_users (user_id, item_id, item_count)\n" +
                                    "VALUES (${Resources.CURRENT_USER_ID},\n" +
                                    "        ${item.id},\n" +
                                    "        ${item.count}" +
                                    ")"
                    )
                }

                Resources.CURRENT_USER_RUBLES_COUNT -= item.price

                connection.createStatement().execute(
                        "update items_in_inventories_of_all_users\n" +
                                "set item_count = ${Resources.CURRENT_USER_RUBLES_COUNT}" +
                                "where item_id = ${Resources.ITEM_RUBLES_ID}"
                )

                res.close()
            }.join()

        }

        public fun sellItem(item: Item) = runBlocking {

            CoroutineScope(Dispatchers.IO).launch {
                val result = connection.createStatement().executeQuery(
                        "select item_count from items_in_inventories_of_all_users\n" +
                                "where user_id = ${Resources.CURRENT_USER_ID} and " +
                                "item_id = ${item.id};"
                )

                var currentItemCount = -1
                if (result.next()) currentItemCount = result.getInt(1)



                if (currentItemCount > 1) {
                    connection.createStatement().execute(
                            "update items_in_inventories_of_all_users\n" +
                                    "set item_count = ${Resources.CURRENT_USER_RUBLES_COUNT}" +
                                    "where user_id = ${Resources.CURRENT_USER_ID} and " +
                                    "item_id = ${Resources.ITEM_RUBLES_ID}"
                    )

                    currentItemCount--
                    connection.createStatement().execute(
                            "update items_in_inventories_of_all_users\n" +
                                    "set item_count = $currentItemCount" +
                                    "where user_id = ${Resources.CURRENT_USER_ID} and " +
                                    "item_id = ${item.id}"
                    )
                } else {
                    connection.createStatement().execute(
                            "delete from items_in_inventories_of_all_users\n" +
                                    "where user_id = ${Resources.CURRENT_USER_ID} and " +
                                    "item_id = ${item.id}"
                    )
                }

                Resources.CURRENT_USER_RUBLES_COUNT += item.price

                connection.createStatement().execute(
                        "update items_in_inventories_of_all_users\n" +
                                "set item_count = ${Resources.CURRENT_USER_RUBLES_COUNT}" +
                                "where item_id = ${Resources.ITEM_RUBLES_ID}"
                )

                result.close()
            }.join()

        }

        public fun userAuthorize(nickname : String, password : String, view : View) = runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                var result = Database.connection.createStatement().executeQuery(
                        "SELECT id, password from users WHERE nickname = '$nickname'")
                if (result == null)
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(Resources.CURRENT_ACTIVITY, "Нет cоединения с БД.", Toast.LENGTH_SHORT).show()
                    }

                if (result.next()) {

                    val passwordHash = result.getString(2)
                    if (Resources.isPasswordValid(password, passwordHash)) {

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                Resources.CURRENT_ACTIVITY,
                                "Пользователь найден!",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                        }
                        result = Database.connection.createStatement().executeQuery(
                            "SELECT * from users WHERE nickname = '$nickname'"
                        )
                        result.next()

                        //getting user's info from database (from table 'users')
                        Resources.CURRENT_USER_ID = result.getInt(1)
                        Resources.CURRENT_USER_NICKNAME = result.getString(2)
                        Resources.CURRENT_USER_LEVEL = result.getInt(3)
                        Resources.CURRENT_USER_INVENTORY_SIZE = result.getInt(4)

                        result = Database.connection.createStatement().executeQuery(
                            "SELECT items.name, item_count FROM items_in_inventories_of_all_users\n" +
                                    "INNER JOIN items  on items.id = items_in_inventories_of_all_users.item_id\n" +
                                    "WHERE user_id = ${Resources.CURRENT_USER_ID} and type = 'деньги'"
                        )

                        while (result.next()) {
                            when (result.getString(1)) {
                                "Рубли" -> Resources.CURRENT_USER_RUBLES_COUNT = result.getInt(2)
                                "Доллары" -> Resources.CURRENT_USER_DOLLARS_COUNT = result.getInt(2)
                                "Евро" -> Resources.CURRENT_USER_EUROS_COUNT = result.getInt(2)
                            }
                        }

                        Navigation.findNavController(view).navigate(
                            R.id.action_authorizeScreenFragment_to_mainMenuFragment
                        )
                    }
                    else {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(Resources.CURRENT_ACTIVITY, "Неверный пароль.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(Resources.CURRENT_ACTIVITY, "Пользователь не найден.", Toast.LENGTH_SHORT).show()
                    }
                }

                result.close()
            }.join()
        }

        public fun getInfoAboutSelectedBuilding(
                requirementsList: MutableList<Requirement>,
                curBonuses: String,
                reqId: Int,
                view: View) {
            var currentBonuses = curBonuses
            var requirementId = reqId

            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {

                    var result = Database.connection.createStatement().executeQuery(
                            "SELECT buildings.id, buildings.name, buildings.level, improvements.future_bonuses, improvements.requirement_id\n" +
                                    "FROM buildings\n" +
                                    "    INNER JOIN improvements ON improvements.id = buildings.improvement_id\n" +
                                    "WHERE name = '${Resources.CURRENT_BUILDING_NAME}' " +
                                    "AND level = ${Resources.CURRENT_BUILDING_LEVEL}"
                    )

                    if (result.next()) {
                        Resources.CURRENT_BUILDING_ID = result.getInt(1)
                        currentBonuses = result.getString(4)
                        requirementId = result.getInt(5)
                    }
                    if (currentBonuses == "null") currentBonuses = "Нет"

                    result = Database.connection.createStatement().executeQuery(
                            "SELECT requirements.id, items.name, item_count, skills.name, skill_level, traders.name, trader_level FROM requirements\n" +
                                    "    LEFT JOIN items ON items.id = requirements.item_id\n" +
                                    "    LEFT JOIN skills ON skills.id = requirements.skill_id\n" +
                                    "    LEFT JOIN traders ON traders.id = requirements.trader_id\n" +
                                    "WHERE requirements.id = $requirementId"
                    )

                    if (result.next()) {
                        //если в требовании есть предмет
                        if (result.getString(2) != "null") {
                            requirementsList.add(
                                    Requirement(result.getString(2), result.getInt(3), 1, false)
                            )
                        }
                        //если в требовании есть навык
                        if (result.getString(4) != "null") {
                            requirementsList.add(
                                    Requirement(result.getString(4), result.getInt(5), 2, false)
                            )
                        }
                        //если в требовании есть торговец
                        if (result.getString(6) != "null") {
                            requirementsList.add(
                                    Requirement(result.getString(6), result.getInt(7), 3, false)
                            )
                        }
                    }


                    //filling user's items list
                    result = Database.connection.createStatement().executeQuery(
                            "SELECT id, items.name, items.type, price, item_count FROM items_in_inventories_of_all_users\n" +
                                    "INNER JOIN items  on items.id = items_in_inventories_of_all_users.item_id\n" +
                                    "WHERE user_id = ${Resources.CURRENT_USER_ID}\n"
                    )

                    while (result.next()) {
                        val id = result.getInt(1)
                        val name = result.getString(2)
                        val type = result.getString(3)
                        val price = result.getInt(4)
                        val count = result.getInt(5)
                        Resources.CURRENT_USER_ITEMS_LIST.add(
                                Item(id, name, type, price, count, false)
                        )
                    }

                    //filling user's traders list
                    result = Database.connection.createStatement().executeQuery(
                            "SELECT trader_id, traders.name, trader_level, trader_relationship_value\n" +
                                    "FROM traders_of_all_users\n" +
                                    "         INNER JOIN traders on traders.id = traders_of_all_users.trader_id\n" +
                                    "WHERE user_id = ${Resources.CURRENT_USER_ID};"
                    )

                    while (result.next()) {
                        val id = result.getInt(1)
                        val name = result.getString(2)
                        val level = result.getInt(3)
                        val relationshipValue = result.getDouble(4)
                        Resources.CURRENT_USER_TRADERS_LIST.add(
                                Trader(id, name, level, relationshipValue)
                        )
                    }

                    //filling user's skills list
                    result = Database.connection.createStatement().executeQuery(
                            "SELECT skill_id, skills.name, skill_level, skill_value\n" +
                                    "FROM skills_of_all_users\n" +
                                    "         INNER JOIN skills on skills.id = skills_of_all_users.skill_id\n" +
                                    "WHERE user_id = ${Resources.CURRENT_USER_ID};"
                    )

                    while (result.next()) {
                        val id = result.getInt(1)
                        val name = result.getString(2)
                        val level = result.getInt(3)
                        val value = result.getDouble(4)
                        Resources.CURRENT_USER_SKILLS_LIST.add(
                                Skill(id, name, level, value)
                        )
                    }

                    result.close()
                }.join()

                view.current_bonuses_text.text = currentBonuses
            }

        }

        public fun improveBuilding(
                requirementsList : MutableList<Requirement>,
                curBonuses : String,
                reqId : Int,
                view : View) {

            var currentBonuses = curBonuses
            var requirementId = reqId

            var isAllRequirementsReady = true
            for (requirement in requirementsList) {
                if (!requirement.isReady) isAllRequirementsReady = false
            }
            if (isAllRequirementsReady) {
                runBlocking {
                    CoroutineScope(Dispatchers.IO).launch {

                        var result: ResultSet

                        for (requirement in requirementsList) {
                            if (requirement.type == Requirement.TYPE_ITEM) {
                                val itemName = requirement.name
                                result = Database.connection.createStatement().executeQuery(
                                        "SELECT id, items.name, items.type, price, item_count FROM items_in_inventories_of_all_users\n" +
                                                "INNER JOIN items  on items.id = items_in_inventories_of_all_users.item_id\n" +
                                                "WHERE user_id = ${Resources.CURRENT_USER_ID} and\n" +
                                                "        items.name = '$itemName'"
                                )
                                result.next()
                                val id = result.getInt(1)
                                val name = result.getString(2)
                                val type = result.getString(3)
                                val price = result.getInt(4)
                                val count = result.getInt(5)
                                val curItem = Item(id, name, type, price, count, false)
                                if (curItem.count != -1) {
                                    if (curItem.count - requirement.value >= 1) {
                                        val newItemCount = curItem.count - requirement.value
                                        Database.connection.createStatement().execute(
                                                "UPDATE items_in_inventories_of_all_users " +
                                                        "SET item_count = $newItemCount WHERE " +
                                                        "user_id = ${Resources.CURRENT_USER_ID}  AND " +
                                                        "item_id = ${curItem.id}"
                                        )
                                    } else {
                                        Database.connection.createStatement().execute(
                                                "delete from items_in_inventories_of_all_users\n" +
                                                        "where user_id = ${Resources.CURRENT_USER_ID} and " +
                                                        "item_id = ${curItem.id}"
                                        )
                                    }
                                }

                            }
                        }
                        Database.connection.createStatement().execute(
                                "delete from buildings_in_all_users_sanctuaries\n" +
                                        "    where user_id = ${Resources.CURRENT_USER_ID} and " +
                                        "building_id = ${Resources.CURRENT_BUILDING_ID}"
                        )

                        result = Database.connection.createStatement().executeQuery(
                                "select id from buildings\n" +
                                        "where name = '${Resources.CURRENT_BUILDING_NAME}' and\n" +
                                        "      level = ${Resources.CURRENT_BUILDING_LEVEL + 1}"
                        )
                        result.next()
                        val nextBuildingId = result.getInt(1)
                        Database.connection.createStatement().execute(
                                "insert into buildings_in_all_users_sanctuaries (user_id, building_id) \n" +
                                        "values (${Resources.CURRENT_USER_ID}, $nextBuildingId)"
                        )

                        Resources.CURRENT_BUILDING_ID = nextBuildingId
                        Resources.CURRENT_BUILDING_LEVEL++

                        result.close()
                    }.join()

                    requirementsList.clear()

                    view.building_name.text = Resources.CURRENT_BUILDING_NAME
                    view.building_level_val.text = Resources.CURRENT_BUILDING_LEVEL.toString()

                    runBlocking {
                        CoroutineScope(Dispatchers.IO).launch {

                            var result = Database.connection.createStatement().executeQuery(
                                    "SELECT buildings.id, buildings.name, buildings.level, improvements.future_bonuses, improvements.requirement_id\n" +
                                            "FROM buildings\n" +
                                            "    INNER JOIN improvements ON improvements.id = buildings.improvement_id\n" +
                                            "WHERE name = '${Resources.CURRENT_BUILDING_NAME}' " +
                                            "AND level = ${Resources.CURRENT_BUILDING_LEVEL}"
                            )

                            if (result.next()) {
                                Resources.CURRENT_BUILDING_ID = result.getInt(1)
                                currentBonuses = result.getString(4)
                                requirementId = result.getInt(5)
                            }
                            if (currentBonuses == "null") currentBonuses = "Нет"

                            result = Database.connection.createStatement().executeQuery(
                                    "SELECT requirements.id, items.name, item_count, skills.name, skill_level, traders.name, trader_level FROM requirements\n" +
                                            "    LEFT JOIN items ON items.id = requirements.item_id\n" +
                                            "    LEFT JOIN skills ON skills.id = requirements.skill_id\n" +
                                            "    LEFT JOIN traders ON traders.id = requirements.trader_id\n" +
                                            "WHERE requirements.id = $requirementId"
                            )

                            if (result.next()) {
                                //если в требовании есть предмет
                                if (result.getString(2) != "null") {
                                    requirementsList.add(
                                            Requirement(result.getString(2), result.getInt(3), 1, false)
                                    )
                                }
                                //если в требовании есть навык
                                if (result.getString(4) != "null") {
                                    requirementsList.add(
                                            Requirement(result.getString(4), result.getInt(5), 2, false)
                                    )
                                }
                                //если в требовании есть торговец
                                if (result.getString(6) != "null") {
                                    requirementsList.add(
                                            Requirement(result.getString(6), result.getInt(7), 3, false)
                                    )
                                }
                            }

                            Resources.CURRENT_USER_ITEMS_LIST.clear()
                            //filling user's items list
                            result = Database.connection.createStatement().executeQuery(
                                    "SELECT id, items.name, items.type, price, item_count FROM items_in_inventories_of_all_users\n" +
                                            "INNER JOIN items  on items.id = items_in_inventories_of_all_users.item_id\n" +
                                            "WHERE user_id = ${Resources.CURRENT_USER_ID}\n"
                            )

                            while (result.next()) {
                                val id = result.getInt(1)
                                val name = result.getString(2)
                                val type = result.getString(3)
                                val price = result.getInt(4)
                                val count = result.getInt(5)
                                Resources.CURRENT_USER_ITEMS_LIST.add(
                                        Item(id, name, type, price, count, false)
                                )
                            }

                            result.close()
                        }.join()
                    }


                    view.current_bonuses_text.text = currentBonuses
                    val requirementsRV = view.requirements_rv
                    requirementsRV.layoutManager = LinearLayoutManager(Resources.CURRENT_ACTIVITY)
                    requirementsRV.adapter = RequirementsAdapter(view, requirementsList)
                }
                Toast.makeText(Resources.CURRENT_ACTIVITY, "Улучшено!", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(Resources.CURRENT_ACTIVITY, "Не все требования выполнены!", Toast.LENGTH_SHORT).show()
            }
        }

        public fun getCurrentUserItemList() : MutableList<Item> {
            val itemsList = mutableListOf<Item>()

            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = Database.connection.createStatement().executeQuery(
                            "SELECT id, items.name, items.type, item_count, price FROM items_in_inventories_of_all_users\n" +
                                    "INNER JOIN items  on items.id = items_in_inventories_of_all_users.item_id\n" +
                                    "WHERE user_id = ${Resources.CURRENT_USER_ID}"
                    )
                    while (result.next()) {
                        val id: Int = result.getInt(1)
                        val name: String = result.getString(2)
                        val type: String = result.getString(3)
                        val count: Int = result.getInt(4)
                        val price: Int = result.getInt(5)

                        itemsList.add(Item(id, name, type, price, count, false))
                    }

                    result.close()
                }.join()
            }

            return itemsList
        }

        public fun getItemsListWithType(type : String) : MutableList<Item> {
            val itemsList = mutableListOf<Item>()
            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = Database.connection.createStatement().executeQuery(
                            "select * from items where type = '$type'"
                    )
                    while (result.next()) {
                        val id = result.getInt(1)
                        val name = result.getString(2)
                        val itemType = result.getString(3)
                        val count = 1
                        val price = result.getInt(4)

                        val item = Item(id, name, itemType, price, count, false)
                        itemsList.add(item)
                    }
                    result.close()
                } .join()
            }
            return itemsList
        }

        public fun getCurrentUserItemsList() : MutableList<Item> {
            val itemsList = mutableListOf<Item>()

            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = Database.connection.createStatement().executeQuery(
                            "SELECT id, items.name, items.type, item_count, price FROM items_in_inventories_of_all_users\n" +
                                    "INNER JOIN items  on items.id = items_in_inventories_of_all_users.item_id\n" +
                                    "WHERE user_id = ${Resources.CURRENT_USER_ID}"
                    )

                    while (result.next()) {
                        val id: Int = result.getInt(1)
                        val name: String = result.getString(2)
                        val type: String = result.getString(3)
                        val count: Int = result.getInt(4)
                        val price: Int = result.getInt(5)

                        itemsList.add(Item(id, name, type, price, count, false))
                    }

                    result.close()
                }.join()
            }

            return itemsList
        }

        public fun getCurrentUserBuildingsList() : MutableList<Building> {

            val buildingsList = mutableListOf<Building>()

            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = Database.connection.createStatement().executeQuery(
                            "SELECT buildings.name, buildings.level\n" +
                                    "FROM buildings_in_all_users_sanctuaries\n" +
                                    "         INNER JOIN buildings on buildings.id = buildings_in_all_users_sanctuaries.building_id\n" +
                                    "WHERE user_id = ${Resources.CURRENT_USER_ID}\n" +
                                    "ORDER BY name"
                    )

                    while (result.next()) {

                        val buildingName = result.getString(1)
                        val buildingLevel = result.getInt(2)

                        buildingsList.add(Building(buildingName, buildingLevel))
                    }

                    result.close()

                }.join()
            }

            return buildingsList
        }

        public fun getCurrentUserTradersList() : MutableList<Trader> {
            val tradersList = mutableListOf<Trader>()

            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = Database.connection.createStatement().executeQuery(
                            "SELECT id, traders.name, trader_level, trader_relationship_value\n" +
                                    "FROM traders_of_all_users\n" +
                                    "         INNER JOIN traders on traders.id = traders_of_all_users.trader_id\n" +
                                    "WHERE user_id = ${Resources.CURRENT_USER_ID}"
                    )

                    while (result.next()) {

                        val id = result.getInt(1)
                        val name = result.getString(2)
                        val level = result.getInt(3)
                        val relationshipValue = result.getDouble(4)

                        tradersList.add(Trader(id, name, level, relationshipValue))
                    }

                    result.close()

                }.join()
            }
            return tradersList
        }

        public fun getRublesID() : Int {
            val result = connection.createStatement().executeQuery(
                    "select id from items where name = 'Рубли';"
            )
            result.next()
            val id = result.getInt(1)
            result.close()
            return id
        }

        public fun getDollarsID() : Int {
            val result = connection.createStatement().executeQuery(
                    "select id from items where name = 'Доллары';"
            )
            result.next()
            val id = result.getInt(1)
            result.close()
            return id
        }

        public fun getEurosID() : Int {
            val result = connection.createStatement().executeQuery(
                    "select id from items where name = 'Евро';"
            )
            result.next()
            val id = result.getInt(1)
            result.close()
            return id
        }

    }
}