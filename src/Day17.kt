import java.util.*

fun main() {

    data class Crucible(val x:Int, val y: Int, val direction: BeamDirection?) {
        fun step(newDir: BeamDirection): Crucible {
            val (newX, newY) = when (newDir) {
                BeamDirection.RIGHT -> listOf(x + 1, y)
                BeamDirection.LEFT -> listOf(x - 1, y)
                BeamDirection.DOWN -> listOf(x, y + 1)
                else -> listOf(x, y - 1)
            }
            return Crucible(newX, newY, newDir)
        }
    }

    fun solve(arr: Array<Array<Int>>, minSteps: Int, maxSteps: Int): Int {
        val weights = mutableMapOf<Crucible,Int>()
        val variants = PriorityQueue<Pair<Crucible,Int>>(compareBy { it.second })
        variants.add(Crucible(0,0,null) to 0)

        val destX = arr.first().size-1
        val destY = arr.size-1
        while (variants.isNotEmpty()) {
            val current = variants.poll()
            variants.removeAll { it.first == current.first }
            val pos = current.first
            val weight = current.second
            weights[pos] = weight
            if (pos.x == destX && pos.y == destY) {
                return weight
            }

            val newVariants = mutableListOf<Pair<Crucible,Int>>()
            if ((pos.direction?:BeamDirection.RIGHT) in setOf(BeamDirection.LEFT, BeamDirection.RIGHT)) {
                var cUp = pos to weight
                var cDown = pos to weight
                for (i in 1..maxSteps) {
                    if (cUp.first.y > 0) {
                        val l = cUp.first.step(BeamDirection.UP)
                        cUp = l to cUp.second + arr[l.y][l.x]
                        if (i >= minSteps) newVariants.add(cUp)
                    }
                    if (cDown.first.y < destY) {
                        val l = cDown.first.step(BeamDirection.DOWN)
                        cDown = l to cDown.second + arr[l.y][l.x]
                        if (i >= minSteps) newVariants.add(cDown)
                    }
                }
            }

            if ((pos.direction?:BeamDirection.DOWN) in setOf(BeamDirection.DOWN, BeamDirection.UP)) {
                var cLeft = pos to weight
                var cRight = pos to weight
                for (i in 1..maxSteps) {
                    if (cLeft.first.x > 0) {
                        val l = cLeft.first.step(BeamDirection.LEFT)
                        cLeft = l to cLeft.second + arr[l.y][l.x]
                        if (i >= minSteps) newVariants.add(cLeft)
                    }
                    if (cRight.first.x < destX) {
                        val l = cRight.first.step(BeamDirection.RIGHT)
                        cRight = l to cRight.second + arr[l.y][l.x]
                        if (i >= minSteps) newVariants.add(cRight)
                    }
                }
            }

            variants.addAll(newVariants.filter { !weights.containsKey(it.first) })
        }

        return 0
    }

    fun part1(input: List<String>): Int {
        val arr = input.map { it.toCharArray().map { char -> char.digitToInt() }.toTypedArray() }.toTypedArray()
        return solve(arr, 0, 3)
    }

    // part2

    fun part2(input: List<String>): Int {
        val arr = input.map { it.toCharArray().map { char -> char.digitToInt() }.toTypedArray() }.toTypedArray()
        return solve(arr, 4, 10)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part2(testInput) == 94)

    println("Start!")

    val input = readInput("Day17")
    part1(input).println() // 1065
    part2(input).println() // 1249
}
