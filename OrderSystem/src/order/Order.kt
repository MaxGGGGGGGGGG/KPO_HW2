package order

import dish.Dish

interface Order {
    val userId : Int
    val orderId : Int
    var order: MutableList<Dish?>
    val urgency: OrderUrgency
    var orderStatus : OrderStatus
}

class OrderImpl(override val userId : Int, override val orderId : Int, override var order: MutableList<Dish?>,
                override val urgency: OrderUrgency) : Order {
    override var orderStatus : OrderStatus = OrderStatus.PROCESS
}