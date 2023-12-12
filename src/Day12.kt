fun main() {

    fun isValid(springs: String, records: List<Int>): Boolean {
        val check = mutableListOf<Int>()
        var count = 0
        springs.forEach {
            if (it == '#') count++ else {
                if (count != 0) check.add(count)
                count = 0
            }
        }
        if (count != 0)
            check.add(count)
        return check == records
    }

    fun countValidArrangements(springs: String, records: List<Int>): Int {
        val count = springs.count { it == '#' }
        val sum = records.sum()
        if (count > sum) return 0
        if (count == sum) {
            //println(springs)
            return if (isValid(springs, records)) 1 else 0
        }
        if (!springs.contains('?')) return 0

        return countValidArrangements(springs.replaceFirst('?', '#'), records) +
                countValidArrangements(springs.replaceFirst('?', '.'), records)
    }

    fun part1(input: List<String>): Int {
        return input.sumOf {
            val (springs, s) = it.split(' ')
            val records = s.split(',').map { it.toInt() }
            countValidArrangements(springs, records)
        }
    }

    // part2

    fun matchPattern(springs: String, pattern: String): Boolean {
        for (i in springs.indices) {
            if (pattern[i] != '?') {
                if (pattern[i] != springs[i]) return false
            }
        }
        return true
    }

    val cache = mutableMapOf<List<Int>, MutableMap<String, Long>>()

    fun calculateLastItem(pattern: String, size: Int): Long {
        val stringSize = pattern.length
        return (0..stringSize - size)
            .count {
                val s = ".".repeat(it) + "#".repeat(size) + ".".repeat(stringSize - size - it)
                matchPattern(s, pattern)
            }.toLong()

    }

    fun countValidArrangements3(pattern: String, records: List<Int>): Long {
        val lastRecordCache = cache.computeIfAbsent(records) { mutableMapOf() }
        return lastRecordCache.computeIfAbsent(pattern) {
            if (records.size == 1) {
                calculateLastItem(pattern, records.first())
            } else {
                val stringSize = pattern.length - records.drop(1).sumOf { it + 1 }
                val size = records.first()
                (0..stringSize - size)
                    .sumOf {
                        val s = ".".repeat(it) + "#".repeat(size) + "."
                        if (matchPattern(s, pattern))
                            countValidArrangements3(pattern.substring(s.length), records.drop(1))
                        else 0
                    }
            }
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf {
            val (springs, s) = it.split(' ')
            val records = s.split(',').map { num -> num.toInt() }

            countValidArrangements3(
                (1..5).joinToString(separator = "?") { springs },
                (1..5).flatMap { records }
            )
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")
    part1(input).println() // 7017
    part2(input).println() // 527570479489
}
