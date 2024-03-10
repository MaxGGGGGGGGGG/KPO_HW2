package auxiliary

class AuxiliaryMethods {
    fun readPositiveInt() : Int {
        while (true) {
            var number : Int
            try {
                number = readln().toInt()
            } catch (ex : NumberFormatException) {
                println("Вы ввели не число, попробуйте ещё раз:")
                continue
            }
            if (number > 0) {
                return number
            } else {
                println ("Вы ввели неположительное число, попробуйте ещё раз:")
            }
        }
    }

    fun readNonNegativeInt() : Int {
        while (true) {
            var number : Int
            try {
                number = readln().toInt()
            } catch (ex : NumberFormatException) {
                println("Вы ввели не число, попробуйте ещё раз:")
                continue
            }
            if (number >= 0) {
                return number
            } else {
                println ("Вы ввели неположительное число, попробуйте ещё раз:")
            }
        }
    }

    fun readPositiveLong() : Long {
        while (true) {
            var number : Long
            try {
                number = readln().toLong()
            } catch (ex : NumberFormatException) {
                println("Вы ввели не число, попробуйте ещё раз:")
                continue
            }
            if (number > 0) {
                return number
            } else {
                println ("Вы ввели неположительное число, попробуйте ещё раз:")
            }
        }
    }
}