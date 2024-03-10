package dish

interface Dish {
    var name: String
    var time: Long
    var price : Int
    var amount : Int
}

class DishImpl(override var name: String, override var time: Long, override var price : Int, override var amount : Int) : Dish {
    override fun toString(): String {
        return "Имя: $name время: $time цена: $price количество: $amount"
    }
}