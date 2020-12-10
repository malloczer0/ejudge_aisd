import java.util.*
import kotlin.math.*

// Bloom filter

open class BloomFilter (n: Long, P: Double, val k: Int) {
    val m = (-n*log2(P)/ln(2.0)).roundToInt()    // bitset size
    val bitSet: BitSet = BitSet(m)                  // actually bitset

    open fun hash(x: ULong): BitSet {
        val mersen31 = (2.0F.pow(31).roundToLong()-1).toULong()
        val hashesForX = BitSet(m)

        fun Int.isPrime(): Boolean {
            if (this in 1..2) {
                return true
            } else {
                for (i in 2..this/2) {
                    if (this % i == 0) {
                        return false
                    }
                }
            }
            return true
        }

        var p = 2
        for (i in 1..k) {
            while (!p.isPrime()) {
                p++
            }
            hashesForX[((((i.toULong()% mersen31)*(x%mersen31) + p.toULong()% mersen31) % mersen31).toLong() % m).toInt()] = true
            p++
        }
        return hashesForX
    }

    fun add(x: ULong) {
        bitSet.or(hash(x))
    }

    fun find(x: ULong): Boolean {
        val temp1 = hash(x)
        val temp2 = hash(x)
        temp1.and(bitSet)
        return temp1 == temp2
    }
}

class UserInterface {
    private lateinit var bloomFilter: BloomFilter

    init {
        val scanner = Scanner(System.`in`)
        var instance = false

        fun showBitSet() {
            //println(bloomFilter.bitSet)
            for (i in 0 until bloomFilter.m) {
                print(if(bloomFilter.bitSet[i]) 1 else 0)
            }
            println()
        }

        while (scanner.hasNextLine()) {
            val str = scanner.nextLine().toString()
            when {
                Regex("""set [+]?\d+ [+]?0.\d+""").matches(str) and !instance -> { // set n P
                    str.split(' ')
                        .let {
                            val P = it[2].toDouble()
                            if ((it[1].toLong() <= 0)
                                or (P <= 0.0)
                                or (P >= 1.0)) {
                                println("error")
                            } else {
                                val k = -log2(P).roundToInt()
                                if (k < 1) {
                                    println("error")
                                } else {
                                    bloomFilter = BloomFilter(it[1].toLong(), P, k)
                                    println("${bloomFilter.m} ${bloomFilter.k}")
                                    instance = true
                                }
                            }
                        }
                }
                Regex("""add [+]?\d+""").matches(str) and instance -> {
                    str.split(' ').let {
                        bloomFilter.add(it[1].toULong())
                    }
                }
                Regex("""search [+]?\d+""").matches(str) and instance -> {
                    println(if (bloomFilter.find(str.split(' ')[1].toULong())) 1 else 0)
                }
                Regex("print").matches(str) and instance -> {
                    showBitSet()
                }
                str == "" -> {
                    continue
                }
                else -> println("error")
            }
        }
    }
}

fun main() {
    UserInterface()
}