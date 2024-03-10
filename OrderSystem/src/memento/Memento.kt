package memento

class Memento(private var state : State) {
    fun getState() : State {
        return state
    }
}