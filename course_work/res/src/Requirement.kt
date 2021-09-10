package com.example.tarkovcompanion

/** Класс требования для улучшения постройки
 *
 *      name - название предмета/навыка/торговца
 *      value - количество/уровень
 *      type - тип требования: 1 = Предмет, 2 = Навык, 3 = Торговец
 **/
class Requirement(
        val name : String,
        val value : Int,
        val type : Short,
        var isReady : Boolean
) {

    companion object {
        public val TYPE_ITEM: Short = 1
        public val TYPE_SKILL: Short = 2
        public val TYPE_TRADER: Short = 3
    }
}