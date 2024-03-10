import facade.FacadeImpl
import kotlinx.coroutines.coroutineScope

suspend fun main() {
    val facade = FacadeImpl()
    facade.start()
}