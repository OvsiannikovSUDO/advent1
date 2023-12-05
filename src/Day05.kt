fun main() {

    fun part1(input: List<String>): Long {
        "=== Part 1 ===".println()
        var seeds = listOf<Long>()
        val newSeeds = mutableListOf<Long>()

        for (s in input) {
            if (s.startsWith("seeds: ")) {
                seeds = "\\d+".toRegex().findAll(s).map{it.value.toLong()}.toList()
                seeds.println()
                newSeeds.clear()
                newSeeds.addAll(seeds)
            } else if (s.isEmpty()) {
                seeds = newSeeds.toList()
            } else if (!s.endsWith("map:")){
                val line = "\\d+".toRegex()
                        .findAll(s)
                        .map { it.value.toLong() }
                        .toList()

                seeds.forEachIndexed { idx, value ->
                    if (value in line[1]..<line[1] + line[2]) {
                        newSeeds[idx] = value - line[1] + line[0]
                    }
                }
            }
        }
        newSeeds.println()
        return newSeeds.min()
    }

    // part2

    fun getKeyPoints(mutations: MutableList<List<List<Long>>>, i: Int): Set<Long> {
        if (i >= mutations.size) return emptySet()
        val mutation = mutations[i]
        val points = mutation.flatMap { listOf(it[1], it[1] + it[2]) }.toMutableSet()
        val rec = getKeyPoints(mutations, i+1).toList()
        for (l in rec) {
            var isProcessed = false
            for (longs in mutation) {
                if (l in longs[0]..<longs[0]+longs[2]) {
                    points.add(l - longs[0] + longs[1])
                    isProcessed = true
                }
            }
            if (!isProcessed) {
                points.add(l)
            }
        }
        return points.toSet()
    }

    fun applyMutation(s: Long, mutations: MutableList<List<List<Long>>>, i: Int): Long {
        if (i >= mutations.size) return s
        var n = s
        for (longs in mutations[i]) {
            if (s in longs[1]..<longs[1]+longs[2]) {
                n = s - longs[1] + longs[0]
            }
        }
        return applyMutation(n, mutations, i+1)
    }

    fun part2(input: List<String>): Long {
        "=== Part 2 ===".println()
        val seeds = mutableListOf<Pair<Long, Long>>()
        val mutations = mutableListOf<List<List<Long>>>()
        val mutation = mutableListOf<List<Long>>()
        for (s in input) {
            if (s.startsWith("seeds: ")) {
                val numbers = "\\d+".toRegex().findAll(s).map { it.value.toLong() }.toList()
                for (i in 0..<numbers.size / 2) {
                    seeds.add(Pair(numbers[2 * i], numbers[2 * i + 1]))
                }
                seeds.println()
            } else if (s.isEmpty()) {
                if (mutation.isNotEmpty()) mutations.add(mutation.toList())
                mutation.clear()
            } else if (!s.endsWith("map:")) {
                val line = "\\d+".toRegex()
                        .findAll(s)
                        .map { it.value.toLong() }
                        .toList()
                mutation.add(line)
            }
        }
        mutations.add(mutation.toList())
        val keyPoints = getKeyPoints(mutations, 0)
                .filter { kp ->
                    seeds.any { range -> kp in range.first..<range.first + range.second }
                }
                .plus(seeds.map { it.first })

        keyPoints.println()
        return keyPoints.map { applyMutation(it, mutations, 0) }.min()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println() //
    part2(input).println() //
}
