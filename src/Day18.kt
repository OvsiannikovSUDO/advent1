import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {

    data class Move(val dir: BeamDirection, val steps: Int, val color: String)

    fun part1(input: List<String>): Int {
        val moves = input.map {
            val (d, s, c) = it.split(' ')
            val dir = when (d) {
                "R" -> BeamDirection.RIGHT
                "L" -> BeamDirection.LEFT
                "U" -> BeamDirection.UP
                else -> BeamDirection.DOWN
            }
            Move(dir, s.toInt(), c)
        }
        var lastDirection = moves.first().dir
        var turns = 0
        var maxX = 0
        var maxY = 0
        var minX = 0
        var minY = 0
        var x = 0
        var y = 0
        moves.forEach {
            val (nx, ny) = when (it.dir) {
                BeamDirection.LEFT -> listOf(-it.steps, 0)
                BeamDirection.RIGHT -> listOf(it.steps, 0)
                BeamDirection.UP -> listOf(0, -it.steps)
                else -> listOf(0, it.steps)
            }
            x += nx
            y += ny
            if (x > maxX) maxX = x
            if (x < minX) minX = x
            if (y > maxY) maxY = y
            if (y < minY) minY = y

            // count turns
            if ((it.dir == BeamDirection.LEFT && lastDirection == BeamDirection.UP) ||
                (it.dir == BeamDirection.RIGHT && lastDirection == BeamDirection.DOWN) ||
                (it.dir == BeamDirection.UP && lastDirection == BeamDirection.RIGHT) ||
                (it.dir == BeamDirection.DOWN && lastDirection == BeamDirection.LEFT)
            ) {
                turns--
            } else {
                turns++
            }
            lastDirection = it.dir
        }
        // "= $x, $minX, $maxX, $y, $minY, $maxY, $turns".println()

        x = -minX
        y = -minY
        val field = Array(maxY - minY + 1) { Array(maxX - minX + 1) { 0 } }
        field[y][x] = 1
        moves.forEach {
            val (nx, ny, dx, dy) = when (it.dir) {
                BeamDirection.LEFT -> listOf(-1, 0, 0, -1)
                BeamDirection.RIGHT -> listOf(1, 0, 0, 1)
                BeamDirection.UP -> listOf(0, -1, 1, 0)
                else -> listOf(0, 1, -1, 0)
            }
            if (field[y + dy][x + dx] == 0) field[y + dy][x + dx] = 2
            repeat(it.steps) {
                x += nx
                y += ny
                field[y][x] = 1
                if (field[y + dy][x + dx] == 0) field[y + dy][x + dx] = 2
            }
        }
        field.forEachIndexed { i, it1 ->
            var paint = false
            it1.forEachIndexed { j, it2 ->
                when (it2) {
                    0 -> if (paint) field[i][j] = 2
                    1 -> paint = false
                    2 -> paint = true
                }
            }
        }
        // field.forEach { it.joinToString(separator = "").println() }

        return field.sumOf { it.count { i -> i > 0 } }
    }

    // part2

    data class Point (val x: Int, val y: Int)
    data class Vector (val start: Point, val finish: Point) {
        fun distance(): Long {
            return abs(start.x - finish.x + start.y - finish.y).toLong()
        }

        fun intersect(other: Vector): Boolean {
            return (this.start.x in other.finish.x..other.start.x) ||
                    (this.finish.x in other.finish.x..other.start.x) ||
                    (other.start.x in this.start.x..this.finish.x) ||
                    (other.finish.x in this.start.x..this.finish.x)
        }
    }

    fun vector(start: Point, to: BeamDirection, steps: Int): Vector {
        val (dx, dy) = when (to) {
            BeamDirection.LEFT -> listOf(-1, 0)
            BeamDirection.RIGHT -> listOf(1, 0)
            BeamDirection.UP -> listOf(0, -1)
            else -> listOf(0, 1)
        }
        val finish = Point(start.x + steps * dx, start.y + steps * dy)
        return Vector(start, finish)
    }

    fun part2(input: List<String>): Long {
        var start = Point(0,0)

        val paths = input.map {
            val (_, c) = it.split('(', ')')
            val steps = c.drop(1).dropLast(1).toInt(16)
            val dir = when (c.last()) {
                '0' -> BeamDirection.RIGHT
                '1' -> BeamDirection.DOWN
                '2' -> BeamDirection.LEFT
                else -> BeamDirection.UP
            }
            val vector = vector(start, dir, steps)
            start = vector.finish
            vector
        }

        var result = paths.sumOf { it.distance() }
        // horizontal
        val toRight = paths.filter { it.start.y == it.finish.y }.filter { it.start.x < it.finish.x }
        val toLeft = paths.filter { it.start.y == it.finish.y }.filter { it.start.x > it.finish.x }
        // vertical
        val toDown = paths.filter { it.start.x == it.finish.x }.filter { it.start.y < it.finish.y }
        val toUp = paths.filter { it.start.x == it.finish.x }.filter { it.start.y > it.finish.y }
        result += toRight.sumOf { r ->
            val y1 = r.start.y

            val ranges = mutableListOf<Vector>()
            if (toUp.any { it.finish == r.start}) ranges.add(vector(Point(r.start.x, r.start.y+1), BeamDirection.LEFT, 0))
            if (toDown.any { it.start == r.finish}) ranges.add(vector(Point(r.finish.x, r.finish.y+1), BeamDirection.LEFT, 0))
            ranges.addAll(toLeft.filter{ it.start.y > y1 }.filter {r.intersect(it)})

            val sum = (r.start.x..r.finish.x).sumOf {x ->
                val vector = ranges
                    .filter { x in min(it.start.x,it.finish.x)..max(it.start.x,it.finish.x) }
                    .minBy { it.start.y }
                vector.start.y - y1 - 1L
            }

            //"= $sum".println()
            sum
        }
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62)
    check(part2(testInput) == 952408144115L)

    println("Start!!!")

    val input = readInput("Day18")
    part1(input).println() // 53844
    val start = System.currentTimeMillis()
    part2(input).println() // 42708339569950
    "time: ${System.currentTimeMillis() - start} ms.".println()
    // timings:
    // 142091 - with data classes
    // 25457 - inner filtered collection
    // 5233 - more filters
}
