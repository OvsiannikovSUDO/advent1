import java.util.*

fun main() {

    data class Criteria(val cat: Int?, val op: Char?, val value: Int?, val result: String)

    fun isMatchFilter(rules: Map<String,List<Criteria>>, ruleName: String, part: Array<Int>): Boolean {
        for (c in rules[ruleName]!!) {
            val isMatch = when (c.op) {
                '>' -> part[c.cat!!] > c.value!!
                '<' -> part[c.cat!!] < c.value!!
                else -> true
            }
            if (isMatch) {
                return when (c.result) {
                    "A" -> true
                    "R" -> false
                    else -> isMatchFilter(rules, c.result, part)
                }
            }
        }
        // should never happen!
        assert(false)
        return false
    }

    fun part1(input: List<String>): Int {
        val (rulesStrings, partsStrings) = input.fold(mutableListOf(mutableListOf<String>())) { acc, s ->
            if (s.isEmpty()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(s)
            }
            acc
        }

        val parts = partsStrings.map {s ->
            val part = Array(4) { 0 }
            s.drop(1).dropLast(1).split(',').associate { c ->
                val (k, v) = c.split('=')
                "xmas".indexOf(k[0]) to v.toInt()
            }.forEach {part[it.key] = it.value}
            part
        }

        val rules = rulesStrings.associate { s->
            val (name, list) = s.split('{','}')
            val filter = list.split(',').map { c ->
                if (c.contains(':')) {
                    val (section, value, res) = c.split('<','>',':')
                    Criteria("xmas".indexOf(section[0]), c[1], value.toInt(), res)
                } else Criteria(null, null, null, c)
            }
            name to filter
        }

        rules.println()
        parts.first().println()

        parts.count { isMatchFilter(rules, "in", it) }.println()
        return parts.filter { isMatchFilter(rules, "in", it) }
            .sumOf { it.sum() }
    }

    // part2

    fun countParts(rules: Map<String, List<Criteria>>, ranges: MutableMap<Char, SortedSet<Int>>, part: Array<Int>, level: Int): Long {
        if (level == 4) {
            return if (isMatchFilter(rules, "in", part)) 1L else 0L
        }
        if (level == 1) {
            "! ${part.toList()}".println()
        }
        val sec = "xmas"[level]
        var sum = 0L
        var last = 1
        for (range in ranges[sec]!!) {
            part[level] = range
            sum += (range - last + 1) * countParts(rules, ranges, part, level + 1)
            last = range + 1
        }
        part[level] = 4000
        sum += (4001 - last) * countParts(rules, ranges, part, level + 1)
        return sum
    }

    fun part2(input: List<String>): Long {
        val rulesStrings = mutableListOf<String>()
        for (s in input) {
            if (s.isEmpty()) break
            rulesStrings.add(s)
        }

        val ranges = mutableMapOf<Char, SortedSet<Int>>()
        val rules = rulesStrings.associate { s ->
            val (name, list) = s.split('{', '}')
            val filter = list.split(',').map { c ->
                if (c.contains(':')) {
                    val (section, value, res) = c.split('<', '>', ':')
                    val r = ranges.computeIfAbsent(section[0]) { sortedSetOf() }
                    if (c[1] == '>') r.add(value.toInt()) else r.add(value.toInt() - 1)

                    Criteria("xmas".indexOf(section[0]), c[1], value.toInt(), res)
                } else Criteria(null, null, null, c)
            }
            name to filter
        }

        rules.println()
        ranges.println()

        return countParts(rules, ranges, Array(4) { 0 }, 0)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000L)

    println("Start!")

    val input = readInput("Day19")
    part1(input).println() // 476889
    part2(input).println() // 132380153677887
}
