package system

import authorization.AuthorizationSystem
import authorization.AuthorizationSystemImpl
import dish.Dish
import memento.MementoSystem
import order.*

import review.Review

interface OrderSystem {
    var revenue : Int
    var menu: MutableMap<String, Dish>
    val authorizationSystem: AuthorizationSystem
    val orderHandler : OrderHandler
    val mementoSystem : MementoSystem
    var reviews : MutableList<Review>
    fun makeOrder(id : Int, order : MutableList<String>, status : OrderUrgency)
    fun addToOrder(id : Int, subOrder: MutableList<String>)
    fun deleteOrder(orderId : Int, userId : Int)
    fun pay(orderId : Int)
    fun getStatistics()
    fun removeDish(name : String)
    fun changePrice(name : String, price : Int)
    fun changeTime(name : String, time : Long)
    fun changeAmount(name : String, amount : Int)
}

class OrderSystemImpl(cookAmount : Int) : OrderSystem {
    override var revenue = 0
    override var menu: MutableMap<String, Dish> = mutableMapOf()
    override val authorizationSystem: AuthorizationSystemImpl = AuthorizationSystemImpl()
    override val orderHandler: OrderHandlerImpl = OrderHandlerImpl(cookAmount)
    override val mementoSystem: MementoSystem = MementoSystem()
    override var reviews: MutableList<Review> = mutableListOf()

    override fun makeOrder(id: Int, order: MutableList<String>, status: OrderUrgency) {
        authorizationSystem.visitors[id]!!.orderId = orderHandler.orderId
        val dishes: MutableList<Dish?> = mutableListOf()
        val dishes1: MutableList<Dish?> = mutableListOf()
        for (dishName in order) {
            if (menu.containsKey(dishName) && menu[dishName]!!.amount > 0) {
                --menu[dishName]!!.amount
                order.remove(dishName)
            } else {
                dishes.add(menu[dishName])
            }
            dishes1.add(menu[dishName])
        }
        if (order.isEmpty()) {
            val order = OrderImpl(id, orderHandler.orderId++, dishes1, status)
            order.orderStatus = OrderStatus.READY
            orderHandler.readyOrders[order.orderId] = order
        } else {
            if (status == OrderUrgency.URGENT) {
                orderHandler.urgentOrders.add(OrderImpl(id, orderHandler.orderId++, dishes, status))

            } else {
                orderHandler.nonUrgentOrders.add(OrderImpl(id, orderHandler.orderId++, dishes, status))
            }
        }
    }

    override fun addToOrder(id: Int, subOrder: MutableList<String>) {
        if (orderHandler.readyOrders.containsKey(id)) {
            println("Нельзя дополнить обработанный заказ, создайте новый")
            return
        }
        val dishes: MutableList<Dish?> = mutableListOf()
        for (dishName in subOrder) {
            if (menu.containsKey(dishName) && menu[dishName]!!.amount > 0) {
                --menu[dishName]!!.amount
                subOrder.remove(dishName)
            } else {
                dishes.add(menu[dishName])
            }
        }
        if (subOrder.isEmpty()) {
            return
        } else {
            if (orderHandler.urgentOrders.indexOfFirst { order -> order.orderId == id } != -1) {
                orderHandler.urgentOrders[orderHandler.urgentOrders.indexOfFirst { order ->
                    order.orderId == id
                }].order.addAll(dishes)
            } else {
                orderHandler.nonUrgentOrders[orderHandler.urgentOrders.indexOfFirst { order ->
                    order.orderId == id
                }].order.addAll(dishes)
            }
        }
    }

    override fun deleteOrder(orderId: Int, userId: Int) {
        if (orderHandler.urgentOrders.find{order -> order.orderId == orderId} != null) {
            for (order in orderHandler.urgentOrders) {
                if (order.orderId == orderId && order.orderStatus == OrderStatus.PROCESS) {
                    order.orderStatus = OrderStatus.CANCELLED
                    return
                }
            }
        }
        if (orderHandler.nonUrgentOrders.find{order -> order.orderId == orderId} != null) {
            for (order in orderHandler.nonUrgentOrders) {
                if (order.orderId == orderId && order.orderStatus == OrderStatus.PROCESS) {
                    order.orderStatus = OrderStatus.CANCELLED
                    return
                }
            }
        }
        println("У вас нет активного заказа.")
    }

    override fun pay(orderId: Int) {
        if (!orderHandler.readyOrders.containsKey(orderId)) {
            println("У вас нет неоплаченных заказов")
            return
        }
        var sum = 0
        for (dish in orderHandler.readyOrders[orderId]!!.order) {
            sum += dish!!.price
        }
        revenue += if (orderHandler.readyOrders[orderId]!!.urgency == OrderUrgency.URGENT) {
            sum * 2
        } else {
            sum
        }
    }

    override fun getStatistics() {
        if (reviews.size > 0) {
            var sum = 0
            var count = 0
            for (review in reviews) {
                sum += review.grade
                ++count
            }
            println("Средняя оценка заказа: ${sum / count}")
        }
        println("Количество обработанных заказов с момента начала рабочего дня: ${orderHandler.readyOrders.size}")
    }

    override fun removeDish(name : String) {
        if (menu.containsKey(name)) menu.remove(name)
        else println("Этого блюда нет в меню")
    }

    override fun changePrice(name : String, price : Int) {
        if (menu.containsKey(name)) menu[name]?.price = price
        else println("Такого блюда в меню нет")
    }

    override fun changeTime(name: String, time: Long) {
        if (menu.containsKey(name)) menu[name]?.time = time
        else println("Такого блюда в меню нет")
    }

    override fun changeAmount(name: String, amount: Int) {
        if (menu.containsKey(name)) menu[name]?.amount = amount
        else println("Такого блюда в меню нет")
    }
}