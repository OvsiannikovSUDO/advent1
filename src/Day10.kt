fun main() {

    data class Position(val x: Int, val y: Int) {
        fun stepNorth(): Position { return Position(this.x, this.y-1) }
        fun stepSouth(): Position { return Position(this.x, this.y+1) }
        fun stepEast(): Position { return Position(this.x+1, this.y) }
        fun stepWest(): Position { return Position(this.x-1, this.y) }
        fun step(direction: Direction): Position {
            return when (direction) {
                Direction.NORTH -> stepNorth()
                Direction.SOUTH -> stepSouth()
                Direction.EAST -> stepEast()
                Direction.WEST -> stepWest()
            }
        }
    }

    fun parseDirectionsMap(input: List<String>): Array<Array<Map<Direction, Direction>>> {
        val directions = Array(input.size + 2) { Array(input.first().length + 2) { mapOf<Direction, Direction>() } }
        for (i in input.indices) {
            for (j in input[i].indices) {
                directions[i + 1][j + 1] =
                    when (input[i][j]) {
                        '|' -> mapOf(Direction.NORTH to Direction.SOUTH, Direction.SOUTH to Direction.NORTH)
                        '-' -> mapOf(Direction.EAST to Direction.WEST, Direction.WEST to Direction.EAST)
                        'L' -> mapOf(Direction.NORTH to Direction.EAST, Direction.EAST to Direction.NORTH)
                        'J' -> mapOf(Direction.NORTH to Direction.WEST, Direction.WEST to Direction.NORTH)
                        '7' -> mapOf(Direction.SOUTH to Direction.WEST, Direction.WEST to Direction.SOUTH)
                        'F' -> mapOf(Direction.SOUTH to Direction.EAST, Direction.EAST to Direction.SOUTH)
                        else -> mapOf()
                    }
            }
        }
        return directions
    }

    fun resolveFirstStepDirection(
        directions: Array<Array<Map<Direction, Direction>>>,
        initialPosition: Position
    ): Direction {
        val firstStepDirection =
            if (directions[initialPosition.y][initialPosition.x + 1].containsKey(Direction.WEST)) {
                Direction.EAST
            } else if (directions[initialPosition.y][initialPosition.x - 1].containsKey(Direction.EAST)) {
                Direction.WEST
            } else if (directions[initialPosition.y + 1][initialPosition.x].containsKey(Direction.SOUTH)) {
                Direction.NORTH
            } else if (directions[initialPosition.y - 1][initialPosition.x].containsKey(Direction.NORTH)) {
                Direction.SOUTH
            } else Direction.EAST
        return firstStepDirection
    }

    fun getOpposite(direction: Direction): Direction {
        return when(direction) {
            Direction.WEST -> Direction.EAST
            Direction.EAST -> Direction.WEST
            Direction.NORTH -> Direction.SOUTH
            Direction.SOUTH -> Direction.NORTH
        }
    }

    fun part1(input: List<String>): Int {
        val directions = parseDirectionsMap(input)
        val initialPosition = input.mapIndexed { i, s ->
            Position(s.indexOf('S') + 1, i + 1)
        }.first { it.x > 0 }

        // find the first step
        val firstStepDirection = resolveFirstStepDirection(directions, initialPosition)

        // follow the path
        val path = mutableListOf(firstStepDirection)
        var currentPosition = initialPosition.step(firstStepDirection)
        var cameFrom = getOpposite(firstStepDirection)
        while (currentPosition != initialPosition) {
            val pipe = directions[currentPosition.y][currentPosition.x]
            val direction = pipe[cameFrom]!!
            path.add(direction)
            currentPosition = currentPosition.step(direction)
            cameFrom = getOpposite(direction)
        }

        //path.size.println()
        return path.size / 2
    }

    // part2

    fun markIfEmpty(matrix: Array<Array<Char>>, i: Int, j: Int): Boolean {
        val isEmpty = (matrix[i][j] == ' ')
        if (isEmpty) matrix[i][j] = '1'
        return isEmpty
    }

    fun part2(input: List<String>): Int {
        val directions = parseDirectionsMap(input)
        val initialPosition = input.mapIndexed { i, s ->
            Position(s.indexOf('S') + 1, i + 1)
        }.first { it.x > 0 }

        // find the first step
        val firstStepDirection = resolveFirstStepDirection(directions, initialPosition)

        // which part of the pipe to count.
        // if we change the direction, the sign will be positive
        val sign = -1
        val matrix = Array(input.size + 2){ Array(input.first().length + 2) { ' ' } }

        // follow the path
        var currentPosition = initialPosition.step(firstStepDirection)
        var cameFrom = getOpposite(firstStepDirection)
        while (currentPosition != initialPosition) {
            val pipe = directions[currentPosition.y][currentPosition.x]
            val direction = pipe[cameFrom]!!

            when (direction) {
                Direction.NORTH -> {
                    markIfEmpty(matrix, currentPosition.y, currentPosition.x + sign)
                    markIfEmpty(matrix, currentPosition.y-1, currentPosition.x + sign)
                }
                Direction.SOUTH -> {
                    markIfEmpty(matrix, currentPosition.y, currentPosition.x - sign)
                    markIfEmpty(matrix, currentPosition.y+1, currentPosition.x - sign)
                }
                Direction.EAST -> {
                    markIfEmpty(matrix, currentPosition.y + sign, currentPosition.x)
                    markIfEmpty(matrix, currentPosition.y + sign, currentPosition.x + 1)
                }
                Direction.WEST -> {
                    markIfEmpty(matrix, currentPosition.y - sign, currentPosition.x)
                    markIfEmpty(matrix, currentPosition.y - sign, currentPosition.x - 1)
                }
            }
            matrix[currentPosition.y][currentPosition.x] = '0'

            currentPosition = currentPosition.step(direction)
            cameFrom = getOpposite(direction)
        }
        matrix[initialPosition.y][initialPosition.x] = '*'
        //matrix.forEach{ it.joinToString(separator = "").println() }

        // mark the empty area
        var found = true
        while (found) {
            found = false
            for (i in matrix.indices) {
                for (j in matrix[i].indices) {
                    if (matrix[i][j] == '1') {
                        found = markIfEmpty(matrix, i+1, j) || found
                        found = markIfEmpty(matrix, i-1, j) || found
                        found = markIfEmpty(matrix, i, j+1) || found
                        found = markIfEmpty(matrix, i, j-1) || found
                    }
                }
            }
        }

        //matrix.forEach{ it.joinToString(separator = "").println() }
        return matrix.sumOf { it.sumOf { c -> 0 + (if (c == '1') 1 else 0) } }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    val testInput2 = readInput("Day10_test2")
    val testInput3 = readInput("Day10_test3")
    check(part1(testInput) == 8)
    check(part1(testInput2) == 4)
    check(part2(testInput3) == 10)

    val input = readInput("Day10")
    part1(input).println() // 6815
    part2(input).println() // 269 !!! 265
}

enum class Direction { NORTH, SOUTH, WEST, EAST }
