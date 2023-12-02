fun main() {
    val map = mapOf(
            "red" to 12,
            "green" to 13,
            "blue" to 14,
    )

    fun valid(s: String): Boolean {
        val game = s.split(":")
        val tries = game[1].split(";")
        var isValid = true
        tries.forEach {
            val colors = it.split(",")
            colors.forEach {color ->
                val colorValue = color.trim().split(" ")
                val value = colorValue[0].toInt()
                if (map[colorValue[1]] !!< value) {
                    isValid = false
                }
            }
        }
        return isValid
    }

    fun part1(input: List<String>): Int {
        var sum = 0
        var lineNo = 1
        for (s in input) {
            if (valid(s)) {
                sum += lineNo
            }
            lineNo++
        }
        return sum
    }

    fun calculatePower(s: String): Int {
        val game = s.split(":")
        val tries = game[1].split(";")
        val maxPerColorMap = mutableMapOf(
                "red" to 0,
                "green" to 0,
                "blue" to 0,
        )
        tries.forEach {
            val colors = it.split(",")
            colors.forEach {color ->
                val colorValue = color.trim().split(" ")
                val value = colorValue[0].toInt()
                if (maxPerColorMap[colorValue[1]] !!< value) {
                    maxPerColorMap[colorValue[1]] = value
                }
            }
        }
        return maxPerColorMap.values.reduce {acc,i -> acc * i}
    }

    fun part2(input: List<String>): Int {
        return input.map { calculatePower(it) }
                .reduce { acc, i -> acc + i }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println() // 2176
    part2(input).println() // 63700
}
