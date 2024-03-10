package user

interface Visitor {
    val id : Int
    var orderId : Int
}

class VisitorImpl(override val id : Int) : Visitor {
    override var orderId : Int = 0
}