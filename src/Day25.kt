fun main() {

    fun traverse(graph: Map<String, Set<String>>): Int {
        val visited = mutableSetOf<String>()
        val stack = mutableListOf(graph.keys.first())
        while (stack.isNotEmpty()) {
            val next = stack.removeLast()
            visited.add(next)
            stack.addAll(graph.getOrDefault(next, listOf()).filter { it !in visited })
        }
        return visited.size
    }

    fun traverse2(graph2: Set<Pair<String,String>>): Int {
        val graph = mutableMapOf<String,MutableSet<String>>()
        graph2.forEach {
            graph.computeIfAbsent(it.first) { mutableSetOf() }.add(it.second)
            graph.computeIfAbsent(it.second) { mutableSetOf() }.add(it.first)
        }
        return traverse(graph)
    }

    fun part1(input: List<String>): Int {
        val graph = mutableMapOf<String,MutableSet<String>>()
        val graph2 = mutableSetOf<Pair<String,String>>()
        input.forEach { line ->
            val (key, s) = line.split(": ")
            val values = s.split(" ").toSet()
            graph.computeIfAbsent(key) { mutableSetOf() }.addAll(values)
            values.forEach {
                graph.computeIfAbsent(it) { mutableSetOf() }.add(key)
                graph2.add(key to it)
            }
        }

        val set = graph.flatMap { it.value.plus(it.key) }.toSet()
        //var subset = traverse2(graph3) //traverse(graph)
        //"=== $set - $subset".println()

        // build a tree
        val candidates = mutableSetOf<Pair<String,String>>()
        for (i in set) {
            val visited = mutableSetOf(i)
            val level = mutableSetOf(i)
            while (level.isNotEmpty()) {
                //"~ $level".println()
                val next = mutableMapOf<String,String>()
                level.forEach {
                    graph[it]!!.forEach { l ->
                        if (l !in visited) {
                            visited.add(l)
                            next[l] = it
                        }
                    }
                }
                if (next.size == 3) {
                    candidates.addAll(next.entries.flatMap { listOf(it.key to it.value, it.value to it.key) }
                        .filter { it in graph2 })
                }

                level.clear()
                level.addAll(next.keys)
            }
            //println()
        }

        "!!! $candidates".println()

        val graph3 = candidates.toList()
        for (i in 0..graph3.size-3) {
            for (j in i+1..graph3.size-2) {
                for (k in j+1..<graph3.size) {
                    val subset = traverse2(graph2.minus(setOf(graph3[i],graph3[j],graph3[k])))
                    if (set.size != subset) {
                        return subset * (set.size-subset)
                    }
                }
            }
        }
        return 0
    }

    // part2

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    check(part1(testInput) == 54)
    //check(part2(testInput) == 30)

    println("Start!")

    val input = readInput("Day25")
    part1(input).println() //
    part2(input).println() //
}
