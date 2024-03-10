package memento

class Originator {
    private lateinit var state : State
    fun setState(state : State) {
        this.state = state
    }

    fun getState() : State {
        return state
    }

    fun saveStateToMemento() : Memento {
        return Memento(state)
    }

    fun getStateFromMemento(memento : Memento) {
        state = memento.getState()
    }
}