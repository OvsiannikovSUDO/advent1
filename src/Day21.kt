fun main() {

    fun parseMap(input: List<String>): Array<CharArray> {
        return input.map { line ->
            line.toCharArray()
        }.toTypedArray()
    }

    fun part1(input: List<String>, steps: Int): Int {
        val map = parseMap(input)
        val reach = Array(map.size) {IntArray(map.first().size) { -1 } }
        for (i in map.indices) {
            for (j in map[i].indices) {
                if (map[i][j] == 'S') {
                    reach[i][j] = 0
                    map[i][j] = '.'
                }
            }
        }

        repeat(steps) {
            for (i in reach.indices) {
                for (j in reach[i].indices) {
                    if (reach[i][j] == it) {
                        if (i > 0 && map[i-1][j] == '.') reach[i-1][j] = it+1
                        if (j > 0 && map[i][j-1] == '.') reach[i][j-1] = it+1
                        if (i < reach.size-1 && map[i+1][j] == '.') reach[i+1][j] = it+1
                        if (j < reach.first().size-1 && map[i][j+1] == '.') reach[i][j+1] = it+1
                    }
                }
            }
        }

        return reach.sumOf { row -> row.count { el -> el == steps} }
    }

    // part2

    fun part2(input: List<String>, steps: Int): Long {
        val map = parseMap(input)
        var reach = mutableSetOf<Pair<Int,Int>>()
        for (i in map.indices) {
            for (j in map[i].indices) {
                if (map[i][j] == 'S') {
                    reach.add(i to j)
                    map[i][j] = '.'
                }
            }
        }

        val cache = mutableMapOf(0 to 1L)

        val processed = reach.associateWith { 0 }.toMutableMap()

        val w = map.size
        var step = 1
        val patternBegin = 4 * w
        val patternRepeat = 2 * w // width + height = new corner
        while(step < patternBegin + 3 * patternRepeat) {
            if (step > steps) break
            reach = reach.flatMap{p ->
                val (i,j) = p.toList()
                val set = mutableSetOf<Pair<Int,Int>>()
                if (map[Math.floorMod(i-1, w)][Math.floorMod(j, w)] == '.') set.add(i-1 to j)
                if (map[Math.floorMod(i+1, w)][Math.floorMod(j, w)] == '.') set.add(i+1 to j)
                if (map[Math.floorMod(i, w)][Math.floorMod(j-1, w)] == '.') set.add(i to j-1)
                if (map[Math.floorMod(i, w)][Math.floorMod(j+1, w)] == '.') set.add(i to j+1)

                set.filter { pair -> !processed.contains(pair) }
                //set.associate {pair ->  }
            }.toMutableSet()
            //printMap(reach)
            processed.putAll(reach.associateWith { _ -> step % 2 })

            val isOdd = step % 2
            cache[step] = processed.count { it.value == isOdd }.toLong()
            step++
        }

        "=== finished: $steps ===".println()
        "=== ${cache.entries.last()} ===".println()
        if (cache.contains(steps)) return cache[steps]!!

        // try to predict
        /*
        (0..< patternRepeat).forEach {
            val delta = it // (steps - patternBegin) % w
            val x0 = patternBegin + delta
            val x1 = patternBegin + delta + w
            val x2 = patternBegin + delta + 2 * w
            val y0 = cache[x0]!!
            val y1 = cache[x1]!!
            val y2 = cache[x2]!!
            val a = ((y2.toDouble()-y0)/(x2-x0) - (y1.toDouble()-y0)/(x1-x0)) / (x2-x1)
            val b = (y1.toDouble()-y0)/(x1-x0) - a*(x1+x0)
            val c = y0.toDouble() - a*x0*x0 - b*x0

            val function = {x: Int -> (a * x * x) + (b * x) + c}
            "--- $it => ${Triple(a,b,c)} : ${function(patternBegin + 2*patternRepeat + it)} | ${cache[patternBegin  + 2*patternRepeat + it]}".println()
        }
         */

        val delta = (steps-patternBegin) % patternRepeat
        val x0 = patternBegin + delta
        val x1 = patternBegin + delta + w
        val x2 = patternBegin + delta + 2 * w
        val y0 = (cache[x0]!!).toDouble()
        val y1 = (cache[x1]!!).toDouble()
        val y2 = (cache[x2]!!).toDouble()
        val a = ((y2-y0)/(x2-x0) - (y1-y0)/(x1-x0)) / (x2-x1)
        val b = (y1-y0)/(x1-x0) - a*(x1+x0)
        val c = y0 - a*x0*x0 - b*x0

        val function = {x: Long -> Math.round((a * x * x) + (b * x) + c)}
        "--- $delta => ${Triple(a,b,c)} : ${function(steps.toLong())}".println()
        return function(steps.toLong())
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput, 6) == 16)
    check(part2(testInput, 6) == 16L)
    check(part2(testInput, 10) == 50L)
    check(part2(testInput, 50) == 1594L)
    check(part2(testInput, 100) == 6536L)
    check(part2(testInput, 500) == 167004L)
    check(part2(testInput, 1000) == 668697L)
    check(part2(testInput, 5000) == 16733044L)

    println("Start!")

    val input = readInput("Day21")
    part1(input, 64).println() // 3682
    part2(input, 26501365).println() // 609012263058042
}
