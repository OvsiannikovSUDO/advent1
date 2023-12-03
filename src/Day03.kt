fun main() {
    fun hasAdjacent(previous: String, current: String, next: String, range: IntRange): Boolean {
        if (current[range.first-1] != '.' || current[range.last+1] != '.') return true
        for (i in range.first-1..range.last+1) {
            if (previous[i] != '.' || next[i] != '.') return true
        }
        return false
    }

    fun part1(input: List<String>): Int {
        val reg = "\\d+".toRegex()
        var sum = 0
        var prev = ".".repeat(input.first().length + 2)
        for ((index, s) in input.withIndex()) {
            val current = ".$s."
            val next = if (index < input.size - 1) "." + input[index+1] + "." else ".".repeat(s.length + 2)
            val numbers = reg.findAll(current)
            sum += numbers
                    .filter { m -> hasAdjacent(prev, current, next, m.range)}
                    .map { m -> m.value.toInt() }
                    .sum()
            prev = current
        }
        return sum
    }

    // part 2
    val gears = mutableMapOf<Pair<Int,Int>,MutableList<Int>>()

    fun findGears(previous: String, current: String, next: String, lineNo: Int, range: IntRange, value: Int) {
        if (current[range.first-1] == '*') {
            gears.computeIfAbsent(Pair(range.first-1, lineNo)) {mutableListOf()}
                    .add(value)
        }
        if (current[range.last+1] == '*') {
            gears.computeIfAbsent(Pair(range.last+1, lineNo)) {mutableListOf()}
                    .add(value)
        }
        for (i in range.first-1..range.last+1) {
            if (previous[i] == '*') {
                gears.computeIfAbsent(Pair(i, lineNo-1)) {mutableListOf()}
                        .add(value)
            }
            if(next[i] == '*') {
                gears.computeIfAbsent(Pair(i, lineNo+1)) {mutableListOf()}
                        .add(value)
            }
        }
    }

    fun part2(input: List<String>): Int {
        gears.clear()
        val reg = "\\d+".toRegex()
        var prev = ".".repeat(input.first().length + 2)
        for ((index, s) in input.withIndex()) {
            val current = ".$s."
            val next = if (index < input.size - 1) "." + input[index + 1] + "." else ".".repeat(s.length + 2)
            val numbers = reg.findAll(current)
            numbers.forEach { m -> findGears(prev, current, next, index, m.range, m.value.toInt()) }
            prev = current
        }
        return gears
                .filter { entry -> entry.value.size == 2 }
                .map { entry -> entry.value.reduce { acc, i -> acc * i } }
                .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)
    //check(part2(readInput("Day03_test2")) == 919325)

    val input = readInput("Day03")
    part1(input).println() // 532445
    part2(input).println() // 79842967
}
