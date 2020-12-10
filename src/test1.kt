import java.util.*

fun main(args: Array<String>) {
    val sum: MutableList<Int> = mutableListOf()
    val scanner = Scanner(System.`in`)

    while (scanner.hasNextLine()) {
        scanner.nextLine().trim().toIntOrNull()?.let { sum.add(it) }
    }

    print(sum.sum())
}