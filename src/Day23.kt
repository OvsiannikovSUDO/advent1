fun main() {

    fun parseMap(input: List<String>): Array<CharArray> {
        return input.map { it.toCharArray() }.toTypedArray()
    }

    fun step(pos: Pair<Int,Int>, dir: Char): Pair<Int,Int> {
        return when (dir) {
            '>' -> pos.first + 1 to pos.second
            '<' -> pos.first - 1 to pos.second
            'v' -> pos.first to pos.second + 1
            '^' -> pos.first to pos.second - 1
            else -> pos
        }
    }

    fun checkStep(map: Array<CharArray>, route: Array<BooleanArray>, pos: Pair<Int,Int>): Boolean {
        var p = pos
        if (map[p.second][p.first] == '#') return false
        if (map[p.second][p.first] in "><v^") {
            p = step(p, map[p.second][p.first])
        }
        return !route[p.second][p.first]
    }

    fun printRoute(route: Array<BooleanArray>) {
        route.forEach { it.joinToString("") {b -> if(b) "0" else "." }.println() }
        println()
    }

    fun part1(input: List<String>): Int {
        val map = parseMap(input)
        val start = 1 to 0
        val finish = map.size-2 to map.first().size-1
        var maxRoute = 0

        // when it's a fork on a road
        val variants = mutableListOf("v")
        while (variants.isNotEmpty()) {
            val variant = variants.removeFirst()
            val route = Array(map.size){BooleanArray(map.first().size) {false} }
            var pos = start

            // can be optimized
            variant.forEach {
                route[pos.second][pos.first] = true
                pos = step(pos, it)
            }
            route[pos.second][pos.first] = true

            if (pos == finish) {
                if (maxRoute < variant.length) {
                    maxRoute = variant.length
                    printRoute(route)
                }
                continue
            }
            if (map[pos.second][pos.first] in "><v^") {
                variants.add(variant + map[pos.second][pos.first])
                continue
            }

            "><v^".filter {
                val p = step(pos, it)
                checkStep(map, route, p)
            }.forEach { variants.add(variant + it) }
        }
        return maxRoute
    }

    // part2

    fun checkStep2(map: Array<CharArray>, route: Set<Pair<Int,Int>>, pos: Pair<Int,Int>): Boolean {
        if (map[pos.second][pos.first] == '#') return false
        return !route.contains(pos)
    }

    fun parseForksAndRoads(
        map: Array<CharArray>,
        start: Pair<Int, Int>,
        finish: Pair<Int, Int>,
        roads: MutableMap<Int, MutableMap<Int, Int>>,
        forks: MutableMap<Pair<Int, Int>, Int>
    ) {
        val variants = mutableListOf(start to 'v')
        while (variants.isNotEmpty()) {
            var (pos, dir) = variants.removeFirst()
            val begin = pos
            val route = mutableSetOf(pos)
            pos = step(pos, dir)
            var next = "><v^".filter {
                val p = step(pos, it)
                checkStep2(map, route, p)
            }

            while (next.length == 1) {
                dir = next.first()
                route.add(pos)
                pos = step(pos, dir)
                next = if (pos == finish) "" else "><v^".filter {
                    val p = step(pos, it)
                    checkStep2(map, route, p)
                }
            }

            if (!forks.contains(pos)) {
                forks[pos] = forks.size
                variants.addAll(next.map { pos to it })
            }
            roads.computeIfAbsent(forks[begin]!!) { mutableMapOf() }[forks[pos]!!] = route.size // (Road(setOf(begin, pos), route.size))
            roads.computeIfAbsent(forks[pos]!!) { mutableMapOf() }[forks[begin]!!] = route.size
        }
    }

    data class PathsData(val begin: Int, val length: Int, val visited: Set<Int>, val left: Map<Int, Map<Int, Int>>)

    fun part2(input: List<String>): Int {
        val map = parseMap(input)
        val start = 1 to 0
        val finish = map.size-2 to map.first().size-1

        // roads graph
        val forks = mutableMapOf(start to 0, finish to 1)
        val roads = mutableMapOf<Int, MutableMap<Int,Int>>() // source to destination with weight
        parseForksAndRoads(map, start, finish, roads, forks)
        val start2 = 0
        val finish2 = 1

        "=== $forks".println()
        "=== $roads".println()

        // start traversal
        val variants = mutableListOf(PathsData(start2, 0, setOf(start2), roads))
        var maxRoute = 0
        while (variants.isNotEmpty()) {
            val variant = variants.removeLast()
            val begin = variant.begin
            val visited = variant.visited
            val left = variant.left
            val next = left[begin]!!.filter { it.key !in visited }

            next.forEach {
                val point = it.key
                if (point == finish2) {
                    val size = variant.length + it.value
                    if (maxRoute < size) {
                        maxRoute = size
                        "=== ${variants.size} ! $maxRoute -> $size".println()
                    }
                } else {
                    val newLeft = left.minus(begin)
                    if (newLeft.values.any {l -> finish2 in l.keys}) {
                        variants.add(PathsData(point, variant.length + it.value, visited.plus(point), newLeft))
                    }
                }
            }
        }

        return maxRoute
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 94)
    check(part2(testInput) == 154)

    println("start!")

    val input = readInput("Day23")
    part1(input).println() // 2186

    val start = System.currentTimeMillis()
    part2(input).println() // 6802
    "time: ${System.currentTimeMillis() - start} ms.".println()
    // 177385 - with fork filter
    // 59642 - with road filter
    // 20264 - use maps and sets for roads
}
