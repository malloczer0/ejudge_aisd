import java.io.File
import java.math.BigInteger

fun main(args: Array<String>) {
    val sum: MutableList<BigInteger> = mutableListOf()
    val inputFile = args[0]
    val outputFile = args[1]

    File(inputFile).forEachLine {
        it.trim().toBigIntegerOrNull()?.let {
                    it1 -> sum.add(it1)
            }
    }
    var res : BigInteger = BigInteger.ZERO
    sum.forEach { res += it }
    File(outputFile).appendText("${res.mod(256.toBigInteger())}")
}

