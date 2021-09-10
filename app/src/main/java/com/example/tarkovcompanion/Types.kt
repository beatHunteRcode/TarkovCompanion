package com.example.tarkovcompanion

class Types {

    enum class BarterItems(val n : String) {
        OTHERS("другие"),
        COMBUSTIBLE_LUBRICANTS("горюче-смазочные материалы"),
        INSTRUMENTS("инструменты"),
        MED_MATERIALS("медматериалы"),
        BUILD_MATERIALS("стройматериалы"),
        HOUSEHOLD_GOODS("хозтовары"),
        GOODS("ценности"),
        ELECTRONIC("электроника"),
        BATTERIES("элементы питания")
    }

    enum class Equipment(val n : String) {
        ARMOR("бронежилеты"),
        GLASSES("визоры и очки"),
        HELMETS("головные уборы и шлемы"),
        SECURE_CONTAINERS("защищенные контейнеры"),
        EQUIPMENT_COMPONENTS("компоненты снаряжения"),
        CONTAINERS_AND_CASES("контейнеры и кейсы"),
        MASKS("маски и балаклавы"),
        HEADPHONES("наушники"),
        TACTICAL_RIGS("разгрузки"),
        BACKPACKS("рюкзаки")
    }

    enum class Weapon(val n : String) {
        BOLT_RIFLES("болтовые винтовки"),
        SHOTGUNS("дробовики"),
        MARKSMAN_RIFLES("марксманские винтовки"),
        THROWING_WEAPON("метательное оружие"),
        PISTOLS("пистолеты"),
        SUBMACHINE_GUNS("пистолеты-пулёметы"),
        MACHINE_GUNS("пулемёты"),
        MELEE_WEAPON("холодное оружие"),
        ASSAULT_RIFLES("штурмовые винтовки"),
        ASSAULT_CARABINES("штурмовые карабины")
    }

    enum class Ammo(val n : String) {
        SHELLS("патроны"),
        SHELLS_BOXES("пачки патронов")
    }

    enum class Provision(val n : String) {
        FOOD("еда"),
        DRINKS("напитки")
    }

    enum class Meds(val n : String) {
        MEDKITS("аптечки"),
        INJECTORS("инжекторы"),
        ACCESSORY("обработка травм и ранений"),
        PILLS("таблетки")
    }

    enum class Keys(val n : String) {
        MECHANICAL_KEYS("механические ключи"),
        ELECTRONIC_KEYS("электронные ключи")
    }

    enum class InfoItems(val n : String) {
        INFO_ITEMS("инфо предметы")
    }

    enum class SpecialEquipment(val n : String) {
        SPECIAL_EQUIPMENT("спецоборудование")
    }

    enum class Maps(val n : String) {
        MAPS("карты")
    }

}
