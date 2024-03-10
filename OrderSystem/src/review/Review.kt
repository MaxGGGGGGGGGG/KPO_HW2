package review

interface Review {
    val orderId : Int
    val grade:  Int
    val text : String
}

class ReviewImpl (override val orderId : Int, override val grade:  Int, override val text : String) : Review