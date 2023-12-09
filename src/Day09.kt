fun main() {

    fun getNextForSequence(list: List<Int>): Int {
        val next = (1..<list.size).map { list[it] - list[it - 1] }
        if (next.toSet().size == 1) {
            return list.last() + next.first()
        }
        return list.last() + getNextForSequence(next)
    }
    
    fun part1(input: List<String>): Int {
        return input.map { it.split(' ').map { s -> s.toInt() } }
            .sumOf { getNextForSequence(it) }
    }

    // part2

    fun getPrevForSequence(list: List<Int>): Int {
        val next = (1..<list.size).map { list[it] - list[it - 1] }
        if (next.toSet().size == 1) {
            return list.first() - next.first()
        }
        return list.first() - getPrevForSequence(next)
    }

    fun part2(input: List<String>): Int {
        return input.map { it.split(' ').map { s -> s.toInt() } }
            .sumOf { getPrevForSequence(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println() // 1798691765
    part2(input).println() // 1104
}
