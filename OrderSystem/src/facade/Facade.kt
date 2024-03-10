package facade

import auxiliary.AuxiliaryMethods
import dish.DishImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import memento.State
import order.OrderUrgency
import review.ReviewImpl
import system.OrderSystem
import system.OrderSystemImpl
import java.lang.IndexOutOfBoundsException

const val COOK_AMOUNT = 5

interface Facade {
    val system : OrderSystem
    suspend fun start()
}

class FacadeImpl : Facade {
    override val system: OrderSystem = OrderSystemImpl(COOK_AMOUNT)
    private var auxiliary = AuxiliaryMethods()
    private var stopFlag = true
    private var first = true
    private val cooks : MutableList<Job> = mutableListOf()
    override suspend fun start() = coroutineScope {
        if (first) {
            repeat(system.orderHandler.cookAmount) {
                cooks.add(launch {
                    system.orderHandler.cooker()
                })
            }
            first = false
        }
        var currUserId: Int
        var currUserStatus: String
        while (stopFlag) {
            println(
                "Если хотите зарегистрироваться, введите 1, " +
                        "если авторизоваться - любую другю последовательность символов:"
            )
            val str = readln()
            if (str == "1") {
                println(
                    "Если вы регистрируетесь как гость, введите 1, " +
                            "если как администратор - любую другую последовательность символов:"
                )
                val str1 = readln()
                if (str1 == "1") {
                    currUserId = system.authorizationSystem.registerUser("Visitor")
                    currUserStatus = "Visitor"
                } else {
                    currUserId = system.authorizationSystem.registerUser("Admin")
                    currUserStatus = "Admin"
                }
            } else {
                println("Введите ваш id:")
                val userId = auxiliary.readPositiveInt()
                if (!system.authorizationSystem.authorize(userId)) {
                    println("Пользователя с введённым id нет в системе")
                    continue
                }
                currUserId = userId
                currUserStatus = if (system.authorizationSystem.visitors.containsKey(userId)) {
                    "Visitor"
                } else {
                    "Admin"
                }
            }
            if (currUserStatus == "Visitor") {
                visitorInteract(currUserId)
            } else {
                adminInteract()
            }
        }
    }

    private fun visitorInteract(currUserId: Int) {
        var flag = true
        while (flag) {
            println("Если хотите сделать заказ, введите 1, если хотите дополнить заказ, введите 2, если хотите " +
                    "удалить заказ, введите 3, если хотите заплатить, введите 4, если хотите оставить отзыв, " +
                    "введите 5, если хотите преостановить работу в системе, введите 6, если хотите выйти - введите " +
                    "любую другую последовательность символов:")
            val str = readln()
            when (str) {
                "1" -> {
                    println("В меню имеются следующие блюда:")
                    var names = ""
                    for (name in system.menu.keys) names += name
                    println(names)
                    println("Введите сначала количество заказываемых блюд, потом с новой строки перечислите " +
                            "названия всех блюд (в заказ будут добавлены только блюда, которые есть в меню):")
                    val amount = auxiliary.readPositiveInt()
                    val order: MutableList<String> = readOrder(amount)
                    println("Если заказ нужно сделать срочно (за двойную цену), введите 1, " +
                            "иначе введите любую другую последовательность символов:")
                    val str1 = readln()
                    if (str1 == "1") system.makeOrder(currUserId, order, OrderUrgency.URGENT)
                    else system.makeOrder(currUserId, order, OrderUrgency.NON_URGENT)
                }

                "2" -> {
                    println("Введите сначала количество заказываемых блюд, потом с новой строки перечислите " +
                            "названия всех блюд в заказ будут добавлены только блюда, которые есть в меню):")
                    val amount = auxiliary.readPositiveInt()
                    val subOrder: MutableList<String> = readOrder(amount)
                    system.addToOrder(system.authorizationSystem.visitors[currUserId]!!.orderId, subOrder)
                }

                "3" -> {
                    system.deleteOrder(system.authorizationSystem.visitors[currUserId]!!.orderId, currUserId)
                }

                "4" -> {
                    system.pay(system.authorizationSystem.visitors[currUserId]!!.orderId)
                }

                "5" -> {
                    println("Введите оценку - целое число от 1 до 5")
                    val grade = auxiliary.readPositiveInt()
                    val text = readln()
                    system.reviews.add(ReviewImpl(system.authorizationSystem.visitors[currUserId]!!.orderId,
                        grade, text))
                }

                "6" -> {flag = false}
                else -> {
                    system.authorizationSystem.exit(currUserId)
                    flag = false
                }
            }
        }
    }

    private suspend fun adminInteract() {
        var flag = true
        while(flag) {
            println(
                "Если хотите добавить блюдо, введите 1, если хотите удалить блюдо, введите 2, " +
                        "если хотите поменять цену, введите 3, если хотите поменять время приготовления, введите 4, " +
                        "если хотите поменять количество, введите 5, если хотите получить статистику, введите 6, " +
                        "если хотите приостановить работу в системе, введите 7, если хотите сохранить состояние" +
                        "программы, состояние программы, введите 8, если хотите посмотреть состояние программы, " +
                        "введите 9, если хотите выйти закрыть ресторан, введите любую другую последовательность" +
                        " символов:"
            )
            val str = readln()
            when (str) {
                "1" -> {
                    println("Введите имя блюда:")
                    val name = readln()
                    println("Введите время приготовления:")
                    val time = auxiliary.readPositiveLong()
                    println("Введите цену:")
                    val price = auxiliary.readPositiveInt()
                    println("Введите количество блюда:")
                    val amount = auxiliary.readNonNegativeInt()
                    system.menu[name] = DishImpl(name, time, price, amount)
                }

                "2" -> {
                    println("Введите имя блюда:")
                    val name = readln()
                    system.removeDish(name)
                }

                "3" -> {
                    println("Введите имя блюда:")
                    val name = readln()
                    println("Введите цену:")
                    val price = auxiliary.readPositiveInt()
                    system.changePrice(name, price)
                }

                "4" -> {
                    println("Введите имя блюда:")
                    val name = readln()
                    println("Введите время:")
                    val time = auxiliary.readPositiveLong()
                    system.changeTime(name, time)
                }

                "5" -> {
                    println("Введите имя блюда:")
                    val name = readln()
                    println("Введите количество:")
                    val amount = auxiliary.readNonNegativeInt()
                    system.changeAmount(name, amount)
                }

                "6" -> { system.getStatistics() }

                "7" -> { flag = false }

                "8" -> {system.mementoSystem.AddState(State(system.menu, system.revenue,
                    system.authorizationSystem.visitors, system.authorizationSystem.admins))}

                "9" -> {
                    println("Введите индекс состояния:")
                    val index = auxiliary.readNonNegativeInt()
                    try {
                        println(system.mementoSystem.getState(index))
                    } catch (ex: IndexOutOfBoundsException) {
                        println("Нет состояния с таким индексом")
                    }
                }
                else -> {
                    flag = false
                    stopFlag = false
                    for (cook in cooks) {
                        cook.cancel()
                    }
                }
            }
        }
    }

    private fun readOrder(n : Int) : MutableList<String> {
        val list : MutableList<String> = mutableListOf()
        repeat(n) {
            val name = readln()
            if (system.menu.containsKey(name)) list.add(name)
        }
        return list
    }
}
