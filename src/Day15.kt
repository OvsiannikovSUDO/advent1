fun main() {

    fun calcHash(s: String): Int {
        var hash = 0
        s.forEach {
            hash += it.code
            hash = (hash * 17) % 256
        }
        //"=== $s = $hash".println()
        return hash
    }

    fun part1(input: List<String>): Int {
        return input.flatMap { it.split(",") }
            .sumOf { calcHash(it) }
    }

    // part2

    fun part2(input: List<String>): Int {
        val arr = Array(256) { mutableListOf<Pair<String, Int>>() }
        input.flatMap { it.split(",") }
            .forEach {
                val (key, value) = it.split('=','-')
                val hash = calcHash(key)
                if (it.contains('=')) {
                    if (arr[hash].none { pair -> pair.first == key }) {
                        arr[hash].add(key to value.toInt())
                    } else {
                        arr[hash].replaceAll{ pair -> if(pair.first == key) key to value.toInt() else pair }
                    }
                } else {
                    arr[hash].removeIf { entry -> entry.first == key }
                }
                arr[hash]
            }
        //arr.forEachIndexed { index, pairs -> if(pairs.isNotEmpty()) "$index $pairs".println() }

        return arr.mapIndexed { i1, pairs ->
            (i1 + 1) * pairs.mapIndexed { i2, pair ->
                (i2 + 1) * pair.second
            }.sum()
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println() //
    part2(input).println() //
}
