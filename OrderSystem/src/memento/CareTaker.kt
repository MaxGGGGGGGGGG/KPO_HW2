package memento

class CareTaker {
    private var mementoList : MutableList<Memento> = mutableListOf()

    fun add(state : Memento) {
        mementoList.add(state)
    }

    fun get(index : Int) : Memento {
        return mementoList[index]
    }
}