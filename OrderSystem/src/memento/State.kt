package memento

import dish.Dish
import user.Admin
import user.Visitor

class State(
    private val menu: MutableMap<String, Dish>, private val revenue : Int, private val visitors: MutableMap<Int, Visitor>,
    private val admins : MutableMap<Int, Admin>) {
    override fun toString(): String {
        println("Меню:")
        println(menu.values)
        println("Выручка:")
        println(revenue)
        println("Id покупателей:")
        println(visitors.keys)
        println("Id администраторов:")
        println(admins.keys)
        return ""
    }
}