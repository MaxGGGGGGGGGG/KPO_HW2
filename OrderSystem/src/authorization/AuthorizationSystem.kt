package authorization

import order.OrderStatus
import user.Admin
import user.AdminImpl
import user.Visitor
import user.VisitorImpl

interface AuthorizationSystem {
    var userId : Int
    var visitors: MutableMap<Int, Visitor>
    var admins: MutableMap<Int, Admin>
    fun authorize(id : Int) : Boolean
    fun registerUser(str : String) : Int
    fun exit(id : Int)
}

class AuthorizationSystemImpl : AuthorizationSystem {
    override var userId: Int = 1
    override var visitors: MutableMap<Int, Visitor> = mutableMapOf()
    override var admins: MutableMap<Int, Admin> = mutableMapOf()
    override fun authorize(id : Int) : Boolean {
        return !(!visitors.containsKey(id) && !admins.containsKey(id))
    }

    override fun registerUser(str : String) : Int {
        if (str == "Visitor") {
            visitors[userId] = VisitorImpl(userId++)
        } else {
            admins[userId] = AdminImpl(userId++)
        }
        return userId - 1
    }

    override fun exit(id : Int) {
        if (visitors.containsKey(id)) {
            visitors.remove(id)
            return
        }
        if(admins.containsKey(id)) {
            admins.remove(id)
            return
        }
        println("Нет такого пользователя")
    }

}