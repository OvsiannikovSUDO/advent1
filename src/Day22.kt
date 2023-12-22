fun main() {

    data class Point(val x: Int, val y: Int, val z: Int)

    fun parseBricks(input: List<String>): List<Pair<Point,Point>> {
        return input.map { line ->
            val (p1,p2) = line.split('~')
            val (x1,y1,z1) = p1.split(',').map { it.toInt() }
            val (x2,y2,z2) = p2.split(',').map { it.toInt() }
            Point(x1,y1,z1) to Point(x2,y2,z2)
        }
    }

    fun getSupportsMap(bricks: List<Pair<Point, Point>>): Map<Int,Set<Int>> {
        val heights = mutableMapOf<Pair<Int,Int>,Pair<Int,Int>>() // perhaps array will be better
        val supports = mutableMapOf<Int,Set<Int>>()
        bricks.sortedBy { it.first.z.coerceAtMost(it.second.z) }.forEachIndexed { idx, brick ->
            //brick.println()
            var peak = 0
            val support = mutableSetOf<Int>()
            for (x in brick.first.x..brick.second.x) {
                for (y in brick.first.y..brick.second.y) {
                    val s = heights.computeIfAbsent(x to y) { 0 to -1}
                    if (s.first > peak) {
                        peak = s.first
                        support.clear()
                        support.add(s.second)
                    } else if (s.first == peak) {
                        support.add(s.second)
                    }
                }
            }
            supports[idx] = support
            val height = peak + brick.second.z - brick.first.z + 1
            for (x in brick.first.x..brick.second.x) {
                for (y in brick.first.y..brick.second.y) {
                    heights[x to y] = height to idx
                }
            }
        }
        return supports
    }

    fun part1(input: List<String>): Int {
        val bricks = parseBricks(input)
        val supports = getSupportsMap(bricks)
        //supports.println()

        val keySupports = supports.values.filter { it.size == 1 }.flatten().toSet()
        val neverUsed = supports.keys.filter { it !in keySupports }
        return neverUsed.count()
    }

    // part2

    fun calcRelated(supports: Map<Int, Set<Int>>, toRemove: Set<Int>): Int {
        val willFall = supports.filter { it.value.minus(toRemove).isEmpty() }
        return willFall.size + if (willFall.isEmpty()) 0 else
            calcRelated(supports.minus(willFall.keys), toRemove.plus(willFall.keys))
    }

    fun part2(input: List<String>): Int {
        val bricks = parseBricks(input)
        val supports = getSupportsMap(bricks)

        //supports.println()
        return supports.keys.sumOf { calcRelated(supports, setOf(it)) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 7)

    println("Start!")

    val input = readInput("Day22")
    part1(input).println() // 405
    part2(input).println() // 33387 - too low, right answer = 61297
}
