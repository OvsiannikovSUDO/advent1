import kotlin.math.min

fun main() {
    val cardOrder = listOf('A','K','Q','J','T','9','8','7','6','5','4','3','2')
    val cardOrderJ = listOf('A','K','Q','T','9','8','7','6','5','4','3','2','J')

    fun resolveCombination(s: String): Int {
        val cardCounts = s.groupingBy { it }.eachCount()
        if (cardCounts.containsValue(5)) return 7
        if (cardCounts.containsValue(4)) return 6
        if (cardCounts.containsValue(3) && cardCounts.containsValue(2)) return 5
        if (cardCounts.containsValue(3)) return 4
        if (cardCounts.values.count { it == 2 } == 2) return 3
        if (cardCounts.containsValue(2)) return 2
        return 1
    }

    fun part1(input: List<String>): Long {
        val map = sortedMapOf<String, Long>(comparator = Comparator { s1, s2 ->
            val i1 = s1.map { cardOrder.indexOf(it) }
            val i2 = s2.map { cardOrder.indexOf(it) }
            var comb1 = resolveCombination(s1)
            var comb2 = resolveCombination(s2)
            //"$s1 = $comb1".println()
            if (comb1 > comb2) return@Comparator 1
            else if (comb1 < comb2) return@Comparator -1
            else {
                for (i in i1.indices) {
                    if (i1[i] > i2[i]) return@Comparator -1
                    else if (i1[i] < i2[i]) return@Comparator 1
                }
            }
            return@Comparator 0
        })
        input.forEach {
            val (key, value) = it.split(" ")
            map[key] = value.toLong()
        }
        var sum = 0L
        map.onEachIndexed { idx, v ->
            sum += (idx + 1) * v.value
        }
        return sum;
    }

    // part2

    fun resolveCombinationJ(s: String): Int {
        val cardCounts = s.groupingBy { it }.eachCount()
        if (cardCounts['J'] == 0) return resolveCombination(s)

        var max = 0;
        cardOrderJ.dropLast(1).forEach {
            val r = resolveCombination(s.replace('J', it))
            if (max < r) max = r
        }
        return max
    }

    fun part2(input: List<String>): Long {
        val map = sortedMapOf<String, Long>(comparator = Comparator { s1, s2 ->
            var comb1 = resolveCombinationJ(s1)
            var comb2 = resolveCombinationJ(s2)
            if (comb1 > comb2) return@Comparator 1
            else if (comb1 < comb2) return@Comparator -1
            else {
                val i1 = s1.map { cardOrderJ.indexOf(it) }
                val i2 = s2.map { cardOrderJ.indexOf(it) }
                for (i in i1.indices) {
                    if (i1[i] > i2[i]) return@Comparator -1
                    else if (i1[i] < i2[i]) return@Comparator 1
                }
            }
            return@Comparator 0
        })
        input.forEach {
            val (key, value) = it.split(" ")
            map[key] = value.toLong()
        }
        var sum = 0L
        map.onEachIndexed { idx, v ->
            sum += (idx + 1) * v.value
        }
        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440L)
    check(part2(testInput) == 5905L)

    val input = readInput("Day07")
    part1(input).println() //
    part2(input).println() //
}
