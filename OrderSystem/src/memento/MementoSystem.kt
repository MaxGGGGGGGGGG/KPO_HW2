package memento

class MementoSystem {
    val originator = Originator()
    val careTaker = CareTaker()

    fun AddState(state : State) {
        originator.setState(state)
        careTaker.add(originator.saveStateToMemento())
    }

    fun getState(index : Int) : State {
        return careTaker.get(index).getState()
    }
}