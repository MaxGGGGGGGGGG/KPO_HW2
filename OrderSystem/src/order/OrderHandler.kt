package order

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface OrderHandler {
    var orderId : Int
    var urgentOrders : MutableList<Order>
    var nonUrgentOrders : MutableList<Order>
    var readyOrders : MutableMap<Int, Order>
    var cookAmount : Int
    var indicator : Boolean
    suspend fun cooker()
}

class OrderHandlerImpl(override var cookAmount: Int) : OrderHandler {
    override var orderId = 1
    override var urgentOrders : MutableList<Order> = mutableListOf()
    override var nonUrgentOrders : MutableList<Order> = mutableListOf()
    override var readyOrders : MutableMap<Int, Order> = mutableMapOf()
    override var indicator = false

    override suspend fun cooker() {
        while (true) {
            if (indicator) break
            if (urgentOrders.isNotEmpty()) {
                val order = urgentOrders[0]
                urgentOrders.remove(urgentOrders[0])
                for (dish in order.order) {
                    delay(dish!!.time)
                }
                readyOrders[order.orderId] = order
            } else {
                if (nonUrgentOrders.isEmpty()) {
                    continue
                }
                val order = nonUrgentOrders[0]
                nonUrgentOrders.remove(nonUrgentOrders[0])
                for (dish in order.order) {
                    delay(dish!!.time)
                }
                readyOrders[order.orderId] = order
            }
        }
    }
}