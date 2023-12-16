fun main() {

    data class Beam(val x: Int, val y: Int, val dir: BeamDirection) {
        fun step(): Beam {
            return step(dir)
        }

        fun step(newDir: BeamDirection): Beam {
            return when (newDir) {
                BeamDirection.RIGHT -> Beam(x + 1, y, newDir)
                BeamDirection.LEFT -> Beam(x - 1, y, newDir)
                BeamDirection.DOWN -> Beam(x, y + 1, newDir)
                else -> Beam(x, y - 1, newDir)
            }
        }
    }

    val mirrorRight = mapOf(
        BeamDirection.RIGHT to BeamDirection.UP,
        BeamDirection.UP to BeamDirection.RIGHT,
        BeamDirection.LEFT to BeamDirection.DOWN,
        BeamDirection.DOWN to BeamDirection.LEFT,
    )
    val mirrorLeft = mapOf(
        BeamDirection.RIGHT to BeamDirection.DOWN,
        BeamDirection.UP to BeamDirection.LEFT,
        BeamDirection.LEFT to BeamDirection.UP,
        BeamDirection.DOWN to BeamDirection.RIGHT,
    )

    fun moveBeam(arr: Array<Array<Char>>, dirs: Array<Array<Int>>, initialBeam: Beam) {
        var beam = initialBeam
        while (true) {
            if (!(beam.x in arr.first().indices && beam.y in arr.indices)) return
            if ((dirs[beam.y][beam.x] and (1 shl beam.dir.code)) > 0) return
            dirs[beam.y][beam.x] = dirs[beam.y][beam.x] or (1 shl beam.dir.code)
            beam = when (arr[beam.y][beam.x]) {
                '/' -> beam.step(mirrorRight[beam.dir]!!)
                '\\' -> beam.step(mirrorLeft[beam.dir]!!)
                '-' -> {
                    if (beam.dir in setOf(BeamDirection.UP, BeamDirection.DOWN)) {
                        moveBeam(arr, dirs, beam.step(BeamDirection.LEFT))
                        beam.step(BeamDirection.RIGHT)
                    } else beam.step()
                }

                '|' -> {
                    if (beam.dir in setOf(BeamDirection.LEFT, BeamDirection.RIGHT)) {
                        moveBeam(arr, dirs, beam.step(BeamDirection.UP))
                        beam.step(BeamDirection.DOWN)
                    } else beam.step()
                }

                else -> beam.step()
            }
        }
    }

    fun part1(input: List<String>): Int {
        val arr = input.map { it.toCharArray().toTypedArray() }.toTypedArray()
        val dirs = Array(arr.size) { Array(arr.first().size) { 0 } }
        //arr.forEach { it.joinToString(separator = "").println() }
        val beam = Beam(0, 0, BeamDirection.RIGHT)
        moveBeam(arr, dirs, beam)
        //dirs.forEach { it.joinToString(separator = "").println() }
        return dirs.sumOf { it.count { i -> i > 0 } }
    }

    // part2

    fun rotate(arr: Array<Array<Char>>): Array<Array<Char>> {
        val result = Array(arr.first().size){ Array(arr.size) {'.'} }
        for(i in arr.indices) {
            for (j in arr[i].indices) {
                result[arr[i].size-1 - j][i] = when(arr[i][j]) {
                    '/' -> '\\'
                    '\\' -> '/'
                    '-' -> '|'
                    '|' -> '-'
                    else -> arr[i][j]
                }
            }
        }
        //result.forEach { it.joinToString(separator = "").println() }
        return result
    }

    fun part2(input: List<String>): Int {
        var arr = input.map { it.toCharArray().toTypedArray() }.toTypedArray()
        val m = mutableListOf<Int>()
        repeat(4) {
            m.add(input.indices.maxOf {
                val dirs = Array(arr.size) { Array(arr.first().size) { 0 } }
                moveBeam(arr, dirs, Beam(0, it, BeamDirection.RIGHT))
                dirs.sumOf { row -> row.count { i -> i > 0 } }
            })
            arr = rotate(arr)
        }
        return m.max()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    part1(input).println() // 7798
    part2(input).println() // 8026
}

enum class BeamDirection(val code: Int) {
    RIGHT(0),
    LEFT(1),
    DOWN(2),
    UP(3),
}