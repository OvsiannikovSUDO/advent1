fun main() {
    val map = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9
    )

    fun part1(input: List<String>): Int {
        var sum = 0
        val reg = "\\d".toRegex()
        for (s in input) {
            val match = reg.findAll(s)
            val first = match.first().value
            val last = match.last().value
            sum += (first + last).toInt()
        }
        return sum
    }

    fun parse(value: String): Int {
        return map.getOrElse(value) {
            value.toInt()
        }
    }

    fun part2(input: List<String>): Int {
        var sum = 0
        val reg = "(one|two|three|four|five|six|seven|eight|nine|\\d)".toRegex()
        for (s in input) {
            var match = reg.find(s)
            val first = parse(match!!.value)
            var last = first
            for (i in s.indices) {
                match = reg.find(s, i)
                if (match != null) {
                    last = parse(match.value)
                }
            }
            sum += (first * 10 + last)
        }
        return sum
    }

    fun part3(input: List<String>): Int {
        var sum = 0
        val reg = "(one|two|three|four|five|six|seven|eight|nine|\\d)".toRegex()
        for (s in input) {
            val match = reg.findAll(s)
            val first = parse(match.first().value)
            val last = parse(match.last().value)
            sum += (first * 10 + last)
        }
        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part2(testInput) == 302)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
    part3(input).println()
}
