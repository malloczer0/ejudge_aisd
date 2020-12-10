import java.io.File
import java.util.*
import kotlin.collections.HashSet
import kotlin.reflect.typeOf

class BinaryHeap {
    data class Element(val key: Long, var value: String)

    private infix fun Long.to(string: String) = Element(this, string)

    val elements = mutableListOf<Element>()
    private var keys = HashSet<Long>()

    private fun raise(index: Int) { // aka sift up
        var currentIndex = index
        while (currentIndex < elements.size) {
            if (elements[currentIndex].key >= elements[(currentIndex - 1) / 2].key) {
                break
            }
            with(elements[currentIndex]) {
                elements[currentIndex] = elements[(currentIndex - 1) / 2]
                elements[(currentIndex - 1) / 2] = this
            }
            currentIndex = (currentIndex-1)/2
        }
    }

    private fun descent(index: Int) { // aka sift down
        var currentIndex = index
        var bufferIndex = index
        val (left, right) = (fun(int: Int) = 2 * int + 1) to (fun(int: Int) = 2 * int + 2)
        while (true) {
            val leftEvaluated = left(currentIndex)
            if (leftEvaluated < elements.size) {
                if (elements[leftEvaluated].key < elements[bufferIndex].key) {
                    bufferIndex = leftEvaluated
                }
            }
            val rightEvaluated = right(currentIndex)
            if (rightEvaluated < elements.size) {
                if (elements[rightEvaluated].key < elements[bufferIndex].key) {
                    bufferIndex = rightEvaluated
                }
            }
            if (currentIndex == bufferIndex) {
                break
            }
            with(elements[currentIndex]) {
                elements[currentIndex] = elements[bufferIndex]
                elements[bufferIndex] = this
            }
            currentIndex = bufferIndex
        }
    }

    fun add(key: Long, value: String) = if (key in keys) false else run {
        elements.add(key to value)
        raise(elements.lastIndex)
        keys.add(key)
        true
    }

    fun set(key: Long, value: String) = if (key in keys) run {
            val index = elements.withIndex().find { it.value.key == key }?.index
            elements[index!!].value = value
            true
        } else false

    fun searchOrNull(key: Long) = if (key in keys) run {
        this.elements.withIndex().find { it.value.key == key }.run {
            this!!.index to this!!.value
        }
    } else null

    fun minOrNull() = if (elements.size > 0) elements[0] else null

    fun maxOrNull() = if (elements.size > 0)
        elements.map { it.key }
            .run {
                this.subList((this.size-2)/2,this.size).max().run {
                    elements.find {
                        it.key == this
                    }
                }
            } else null

    fun extract() = if (elements.size != 0) removeOrNull(elements[0].key) else null

    fun removeOrNull(key: Long) = run {
        val index = elements.withIndex().find { it.value.key == key }?.index ?: return@run null
        with(elements[index]) {
            elements[index] = elements[elements.lastIndex]
            elements[elements.lastIndex] = this
        }
        val deleted = elements.removeLast()
        raise(index)
        descent(index)
        keys.remove(key)
        deleted
    }

    fun display() {
        if (elements.size == 0) {
            println('_')
        }
        var levelLength = 1
        var counter = 1
        var str = ""
        for (element in elements.withIndex()) {
            if (element.index != 0) { // K V P
                str += "[${element.value.key} ${element.value.value} ${elements[(element.index -1)/2].key}] "
            } else {
                print("[${element.value.key} ${element.value.value}]")
            }
            if (counter == levelLength) {
                levelLength *= 2
                counter = 0
                print(str.dropLast(1))
                str = ""
                print('\n')
            }
            if ((element.index == elements.lastIndex) and (counter != 0)) {
                str += "_ ".repeat(levelLength-counter).dropLast(1)
                println(str)
                break
            }
            counter++
        }
    }
}

class UserInterfaceBinaryHeap {
    private val binaryHeap = BinaryHeap()

    init {
        val scanner = Scanner(System.`in`)

        while (scanner.hasNextLine()) {
            val str = scanner.nextLine().toString()
            when {
                Regex("""add [+-]?\d+ \S+""").matches(str) -> { // add K V
                    str.split(' ')
                        .let { (_, key, value) ->
                            if (!binaryHeap.add(key.toLong(),value)) {
                                println("error")
                            }
                        }
                }
                Regex("""set [+-]?\d+ \S+""").matches(str)  -> { // set K V
                    str.split(' ')
                        .let { (_, key, value) ->
                            if (!binaryHeap.set(key.toLong(),value)) {
                                println("error")
                            }
                        }
                }
                Regex("""search [+-]?\d+""").matches(str) -> {
                    str.split(' ')
                        .let { (_, key) ->
                            binaryHeap.searchOrNull(key.toLong()).let {
                                if (it == null) println('0') else {
                                    println("1 " +
                                            "${it.first} " +
                                            it.second.value
                                    )
                                }
                            }
                        }
                }
                Regex("""delete [+-]?\d+""").matches(str) -> {
                    str.split(' ')
                        .let { (_, key) ->
                            binaryHeap.removeOrNull(key.toLong()).let {
                                if (it == null) {
                                    println("error")
                                }
                            }
                        }
                }
                str == "min" -> {
                    binaryHeap.minOrNull().let {
                        println(if (it == null) "error" else "${it.key} 0 ${it.value}")
                    }
                }
                str == "max" -> {
                    binaryHeap.maxOrNull().let { max ->
                        println(if (max == null) "error" else "${max.key} " +
                                "${binaryHeap.elements.indexOf(binaryHeap.elements.find { it.key == max.key })} " +
                                max.value
                        )
                    }
                }
                str == "extract" -> {
                    val extracted = binaryHeap.extract()
                    println(if (extracted != null) "${extracted.key} ${extracted.value}" else "error")
                }
                str == "print" -> {
                    binaryHeap.display()
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
    UserInterfaceBinaryHeap()
}