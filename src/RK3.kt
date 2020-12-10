import java.util.*

class Sorter(
    private val array: Array<Int>
) {
    fun countSort() = run {
        array.toMutableList().let { list ->
            var (zeros, ones) = list.count { it == 0 } to list.count { it == 1 }
            // 'ones' is useless, can be useful with (0,1,*) instead of (0,1)
            list.withIndex().forEach {
                list[it.index] = when (it.index) {
                    in 0 until zeros -> 0//.also(::println)
                    else -> 1//.also(::println)
                }
            }
            list
        }
    }
}

class SorterUI {
    init {
        val scanner = Scanner(System.`in`)
        scanner.nextLine().toString().toInt() // array size, useless actually
        val array = scanner.nextLine().toString()
        Sorter(array.map {
            if (it == '0') 0 else 1
        }.toTypedArray())
            .countSort()
            .forEach(::print)
    }
}

/* Можно ли однопроходным?
 Нет, если мы хотим оставить его устойчивым.
 Потому что тогда придется делать swap-ы, которые убъют устойчивость. Или нарушить условие по памяти

 Сложности :
 Память : O(1) - потому что ничего кроме двух переменных под число нулей и единиц тут не нужно,
 и число единиц даже не используется, но я все равно его посчитал чтобы показать логику работы.
 Оставить только нули логичнее с точки зрения эффективности.

 Время: O(n) - 2 прохода для подсчета нулей/единиц, умещается и в один, но такой код читается хуже. 1 проход на перезапись.
 2n -> O(n)

 Эффективность:
 Для диапазона чисел (0,1) найти что-то более оптимальное будет трудно. дополнительные затраты памяти минимальны.
 Если бы чисел-значений было больше, алгоритм логично показал бы себя хуже.
 В ситуации когда все значения уникальны алгоритм неэффективен.

 Распараллеливание: в теории возможно, можно например считать в разных потоках число 0 и 1.
 Реализовывать это тут я нужным не посчитал

 P.S. В коде имеются бесполезные значения (одно), потому что я писал ориентируясь на демонстративность и понятность,
 рамки по сложностям, тем не менее, соблюдены.
 Также их использование имеет смысл если расширять область применения алгоритма.
 Размер массива не нужен для реализации на моем языке, его я просто считал
*/

fun main() {
    SorterUI()
}