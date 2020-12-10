import java.io.File
import java.util.*

// v1.1-SNAPSHOT
class  SplayTree {

    data class Vertex(var value: String,
                      var parentKey: Long? = null,
                      var leftChildKey: Long? = null,
                      var rightChildKey: Long? = null)

    private var rootKey: Long? = null
    val vertexes = mutableMapOf<Long, Vertex>()

    private fun parentByKey(key: Long?) = vertexes[key]?.parentKey
    private fun leftChildByKey(key: Long?) = vertexes[key]?.leftChildKey
    private fun rightChildByKey(key: Long?) = vertexes[key]?.rightChildKey

    private tailrec fun splay(key: Long) {

        fun clockWiseRotate(key: Long) {
            val leftChild = leftChildByKey(key)
            vertexes[leftChild]?.parentKey = parentByKey(key)
            vertexes[key]?.parentKey = leftChild
            vertexes[key]?.leftChildKey = vertexes[leftChild]?.rightChildKey
            vertexes[leftChild]?.rightChildKey = key
        }

        fun counterClockWiseRotate(key: Long) {
            val rightChild = rightChildByKey(key)
            vertexes[rightChild]?.parentKey = parentByKey(key)
            vertexes[key]?.parentKey = rightChild
            vertexes[key]?.rightChildKey = vertexes[rightChild]?.leftChildKey
            vertexes[rightChild]?.leftChildKey = key
        }

        fun zig(key: Long) {
            val parent = parentByKey(key)!!
            when (key) {
                leftChildByKey(parent) -> {
                    clockWiseRotate(parent)
                    vertexes[leftChildByKey(parent)]?.parentKey = parent
                }
                rightChildByKey(parent) -> {
                    counterClockWiseRotate(parent)
                    vertexes[rightChildByKey(parent)]?.parentKey = parent
                }
            }
        }

        fun zigZig(key: Long) = run {
            val parent = parentByKey(key)!!
            val grandpa = parentByKey(parent)!!
            val elderKey = parentByKey(grandpa)
            if (parentByKey(parent) != null) {
                when (parent) {
                    leftChildByKey(grandpa) -> {
                        clockWiseRotate(grandpa)
                        clockWiseRotate(parent)
                        vertexes[leftChildByKey(parent)]?.parentKey = parent
                        vertexes[leftChildByKey(grandpa)]?.parentKey = grandpa
                    }
                    rightChildByKey(grandpa) -> {
                        counterClockWiseRotate(grandpa)
                        counterClockWiseRotate(parent)
                        vertexes[rightChildByKey(parent)]?.parentKey = parent
                        vertexes[rightChildByKey(grandpa)]?.parentKey = grandpa
                    }
                }
            }
            if (elderKey != null) {
                when(grandpa) {
                    leftChildByKey(elderKey) -> {
                        vertexes[elderKey]?.leftChildKey = key
                    }
                    rightChildByKey(elderKey) -> {
                        vertexes[elderKey]?.rightChildKey = key
                    }
                }
            }
        }

        fun zigZag(key: Long) = run {
            val parent = parentByKey(key)!!
            val grandpa = parentByKey(parent)!!
            val elderKey = parentByKey(grandpa)
            if (parentByKey(parent) != null) {
                when (parent) {
                    leftChildByKey(grandpa) -> {
                        vertexes[parent]?.rightChildKey = leftChildByKey(key)
                        vertexes[grandpa]?.leftChildKey = rightChildByKey(key)
                        vertexes[key]?.leftChildKey = parent
                        vertexes[key]?.rightChildKey = grandpa
                        vertexes[rightChildByKey(parent)]?.parentKey = parent
                        vertexes[leftChildByKey(grandpa)]?.parentKey = grandpa
                    }
                    rightChildByKey(grandpa) -> {
                        vertexes[parent]?.leftChildKey = rightChildByKey(key)
                        vertexes[grandpa]?.rightChildKey = leftChildByKey(key)
                        vertexes[key]?.leftChildKey = grandpa
                        vertexes[key]?.rightChildKey = parent
                        vertexes[leftChildByKey(parent)]?.parentKey = parent
                        vertexes[rightChildByKey(grandpa)]?.parentKey = grandpa
                    }
                }
                vertexes[parent]?.parentKey = key
                vertexes[key]?.parentKey = parentByKey(grandpa)
                vertexes[grandpa]?.parentKey = key
                if (elderKey != null) {
                    when(grandpa) {
                        leftChildByKey(elderKey) -> {
                            vertexes[elderKey]?.leftChildKey = key
                        }
                        rightChildByKey(elderKey) -> {
                            vertexes[elderKey]?.rightChildKey = key
                        }
                    }
                }
            }
        }

        val parent = parentByKey(key)
        when {
            parent == null -> {
                return
            }
            parent == rootKey -> {
                zig(key)
            }
            (leftChildByKey(parentByKey(parent)) == parent) and (leftChildByKey(parent) == key)-> {
                zigZig(key)
            }
            (rightChildByKey(parentByKey(parent)) == parent) and (rightChildByKey(parent) == key)-> {
                zigZig(key)
            }
            (leftChildByKey(parentByKey(parent)) == parent) and (rightChildByKey(parent) == key)-> {
                zigZag(key)
            }
            (rightChildByKey(parentByKey(parent)) == parent) and (leftChildByKey(parent) == key)-> {
                zigZag(key)
            }
        }
        rootKey = vertexes.entries.find { it.value.parentKey == null }?.key
        splay(key)
    }

    fun add(key: Long, value: String) = if (key in vertexes.keys) {
        searchOrNull(key)
        false
    } else run {
        vertexes[key] = Vertex(value)
        if (rootKey == null) {
            rootKey = key
        }
        var vertexKey: Long = rootKey!!
        while (true) {
            when {
                key < vertexKey -> {
                    if (vertexes[vertexKey]?.leftChildKey == null) {
                        vertexes[vertexKey]?.leftChildKey = key
                        vertexes[vertexes[vertexKey]?.leftChildKey!!]?.parentKey = vertexKey
                        break
                    } else {
                        vertexKey = vertexes[vertexKey]?.leftChildKey!!
                    }
                }
                key > vertexKey -> {
                    if (vertexes[vertexKey]?.rightChildKey == null) {
                        vertexes[vertexKey]?.rightChildKey = key
                        vertexes[vertexes[vertexKey]?.rightChildKey!!]?.parentKey = vertexKey
                        break
                    } else {
                        vertexKey = vertexes[vertexKey]?.rightChildKey!!
                    }
                }
                else -> return@run true
            }
        }
        splay(key)
        true
    }

    fun set(key: Long, value: String) = if (key in vertexes.keys) run {
        searchOrNull(key)
        vertexes[key]!!.value = value
        true
    } else {
        searchOrNull(key)
        false
    }

    fun searchOrNull(key: Long) = run {
        if (rootKey != null) {
            var currentKey = rootKey
            while (true) {
                when {
                    key < currentKey!! -> {
                        if (leftChildByKey(currentKey) != null) {
                            currentKey = leftChildByKey(currentKey)
                        } else {
                            splay(currentKey)
                            return@run null
                        }
                    }
                    key > currentKey -> {
                        if (rightChildByKey(currentKey) != null) {
                            currentKey = rightChildByKey(currentKey)
                        } else {
                            splay(currentKey)
                            return@run null
                        }
                    }
                    key == currentKey -> {
                        splay(currentKey)
                        return@run rootKey
                    }
                }
            }
        } else null
    }

    fun delete(key: Long) = if(key in vertexes.keys) run {
        searchOrNull(key)
        val (left, right) = leftChildByKey(key) to rightChildByKey(key)
        when {
            (left != null) and (right != null) -> {
                vertexes.remove(key)
                rootKey = left
                vertexes[left]!!.parentKey = null
                val max = maxKey(left)
                vertexes[max]!!.rightChildKey = right
                vertexes[right]!!.parentKey = max
            }
            right != null -> {
                vertexes.remove(key)
                vertexes[right]!!.parentKey = null
                rootKey = right
            }
            left != null -> {
                vertexes.remove(key)
                vertexes[left]!!.parentKey = null
                rootKey = left
            }
            else -> {
                vertexes.remove(key)
                rootKey = null
            }
        }
        true
    } else {
        searchOrNull(key)
        false
    }

    // no need to make a search from subtree
    fun minKey() = if (rootKey != null) run {
        val min = vertexes.keys.minOrNull()
        splay(min!!)
        min
    } else null

    tailrec fun maxKey(subTreeRootKey: Long? = rootKey): Long? {
        return if (rootKey != null) {
            if (rightChildByKey(subTreeRootKey) != null) {
                maxKey(rightChildByKey(subTreeRootKey!!))
            } else run {
                splay(subTreeRootKey!!)
                subTreeRootKey
            }
        } else null
    }

    fun display() {
        if (rootKey == null) {
            println('_')
        } else {
            val bufferedOutputStream = System.out.buffered()
            val newQueue: Queue<Pair<Int, Long>> = LinkedList()

            newQueue.add(0 to rootKey!!)

            fun buildLevel(currentQueue: Queue<Pair<Int, Long>> = LinkedList(), levelLength: Int = 1) {
                var level = ""
                var hasNext = false
                val newQueue: Queue<Pair<Int, Long>> = LinkedList() //Int is a pos of an element on the current level
                var lastValuablePoint = -1

                while (currentQueue.isNotEmpty()) {
                    currentQueue.remove().let {
                        level += "_ ".repeat(it.first-lastValuablePoint-1) +
                                "[${it.second} ${vertexes[it.second]?.value}${
                                    if (vertexes[it.second]?.parentKey != null) { 
                                        " ${vertexes[it.second]!!.parentKey}" 
                                    } else ""
                                }] "
                        lastValuablePoint = it.first
                        val (left, right) = leftChildByKey(it.second) to rightChildByKey(it.second)
                        if (left != null) {
                            newQueue.add(lastValuablePoint*2 to left)
                            hasNext = true
                        }
                        if (right != null) {
                            newQueue.add(lastValuablePoint*2+1 to right)
                            hasNext = true
                        }
                    }
                }

                level += "_ ".repeat(levelLength-lastValuablePoint-1)
                bufferedOutputStream.write((level.dropLast(1) + '\n').toByteArray(Charsets.UTF_8))

                if (hasNext) buildLevel(newQueue, levelLength*2)
            }

            buildLevel(newQueue)
            bufferedOutputStream.flush()
        }
    }
}

class UserInterfaceSplayTree {
    private val splayTree = SplayTree()

    init {
        val scanner = Scanner(System.`in`)

        while (scanner.hasNextLine()) {
            val str = scanner.nextLine().toString()
            // K - Long, V - String
            when {
                Regex("""add [+-]?\d+ [^\n]+""").matches(str) -> { // K V
                    str.split(' ').let { (_, key, value) ->
                        if (!splayTree.add(key.toLong(), value)) {
                            println("error")
                        }
                    }
                }
                Regex("""set [+-]?\d+ [^\n]+""").matches(str) -> { // K V
                    str.split(' ').let { (_, key, value) ->
                       if (!splayTree.set(key.toLong(), value)) {
                           println("error")
                       }
                    }
                }
                Regex("""search [+-]?\d+""").matches(str) -> { // K
                    str.split(' ').let { (_, key) ->
                        val res = splayTree.searchOrNull(key.toLong())
                        println(if (res != null) "1 ${splayTree.vertexes[res]!!.value}" else '0')
                    }
                }
                Regex("""delete [+-]?\d+""").matches(str) -> { // K
                    str.split(' ').let { (_, key) ->
                        if (!splayTree.delete(key.toLong())) {
                            println("error")
                        }
                    }
                }
                str == "min" -> {
                    splayTree.minKey().let {
                        println(if (it != null) "$it ${splayTree.vertexes[it]!!.value}" else "error")
                    }
                }
                str == "max" -> {
                    splayTree.maxKey().let {
                        println(if (it != null) "$it ${splayTree.vertexes[it]?.value}" else "error")
                    }
                }
                str == "print" -> {
                    splayTree.display()
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
    UserInterfaceSplayTree()
}