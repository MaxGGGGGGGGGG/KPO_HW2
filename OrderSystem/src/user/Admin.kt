package user

interface Admin {
    val id: Int
}

class AdminImpl(override val id: Int) : Admin