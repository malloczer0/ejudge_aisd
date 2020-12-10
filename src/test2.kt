import java.util.*

fun main(args: Array<String>) {
    val sum: MutableList<Long> = mutableListOf()
    val scanner = Scanner(System.`in`)

    while (scanner.hasNextLine()) {
        Regex("""[-+]?\d+""")
            .findAll(scanner.nextLine())
            .forEach {
                it.groupValues[0].toLongOrNull()?.let { it1 -> sum.add(it1) }
            }
    }

    print(sum.sum())
}