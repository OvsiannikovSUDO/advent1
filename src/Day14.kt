fun main() {

    fun tiltNorth(matrix: Array<Array<Char>>) {
        for (i in matrix.first().indices) {
            var top = 0
            for(j in matrix.indices) {
                when(matrix[j][i]){
                    'O' -> {
                        matrix[j][i] = '.'
                        matrix[top++][i] = 'O'
                    }
                    '#' -> {
                        top = j+1
                    }
                }
            }
        }
    }

    fun tiltSouth(matrix: Array<Array<Char>>) {
        for (i in matrix.first().indices) {
            var top = matrix.size-1
            for(j in matrix.size-1 downTo 0) {
                when(matrix[j][i]){
                    'O' -> {
                        matrix[j][i] = '.'
                        matrix[top--][i] = 'O'
                    }
                    '#' -> {
                        top = j-1
                    }
                }
            }
        }
    }

    fun tiltEast(matrix: Array<Array<Char>>) {
        for (i in matrix.indices) {
            var top = matrix.first().size-1
            for(j in matrix.first().size-1 downTo 0) {
                when(matrix[i][j]){
                    'O' -> {
                        matrix[i][j] = '.'
                        matrix[i][top--] = 'O'
                    }
                    '#' -> {
                        top = j-1
                    }
                }
            }
        }
    }

    fun tiltWest(matrix: Array<Array<Char>>) {
        for (i in matrix.indices) {
            var top = 0
            for(j in matrix.first().indices) {
                when(matrix[i][j]){
                    'O' -> {
                        matrix[i][j] = '.'
                        matrix[i][top++] = 'O'
                    }
                    '#' -> {
                        top = j+1
                    }
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val matrix = input.map { it.toCharArray().toTypedArray() }.toTypedArray()
        tiltNorth(matrix)
        //matrix.forEach { it.joinToString(separator = "").println() }
        return matrix.mapIndexed{ idx, it ->
            (matrix.size - idx) * it.count { it == 'O' }
        }.sum()
    }

    // part2

    fun part2(input: List<String>): Int {
        val matrix = input.map { it.toCharArray().toTypedArray() }.toTypedArray()
        val cache = mutableMapOf(input.joinToString() to 0)
        val cycles = 1_000_000_000
        for (it in 1..cycles) {
            tiltNorth(matrix)
            tiltWest(matrix)
            tiltSouth(matrix)
            tiltEast(matrix)
            val key = matrix.joinToString { it.joinToString(separator = "") }
            val cachedIndex = cache[key]
            if (cachedIndex!=null && cycles % (it-cachedIndex) == it % (it-cachedIndex)) {
                break
            }
            cache[key] = it
        }
        return matrix.mapIndexed{ idx, it ->
            (matrix.size - idx) * it.count { it == 'O' }
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println() // 107951
    part2(input).println() // 95736
}
