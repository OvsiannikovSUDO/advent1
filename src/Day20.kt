import java.util.*

interface Day20Module {
    // pulse: true=high, false=low
    fun apply(source: String, pulse: Boolean): Boolean?
    fun type(): Char
}
fun main() {

    data class Action(val source: String, val pulse: Boolean, val target: String)

    class Broadcaster: Day20Module {
        override fun apply(source: String, pulse: Boolean): Boolean {
            return pulse
        }

        override fun type(): Char {
            return 'b'
        }

    }

    data class FlipFlopModule(var state: Boolean = false): Day20Module {
        override fun apply(source: String, pulse: Boolean): Boolean? {
            if (pulse) return null
            state = !state
            return state
        }

        override fun type(): Char {
            return 'f'
        }
    }

    data class ConjunctionModule(val state: MutableMap<String,Boolean>): Day20Module {
        override fun apply(source: String, pulse: Boolean): Boolean {
            state[source] = pulse
            return !state.values.all { it }
        }
        override fun type(): Char {
            return 'c'
        }
    }

    fun parseModules(input: List<String>): Map<String, Pair<Day20Module, List<String>>> {
        val modules = mutableMapOf<String,Pair<Day20Module,List<String>>>()
        input.forEach { line ->
            val (module, target) = line.split(" -> ")
            if (module == "broadcaster") {
                modules[module] = Broadcaster() to target.split(", ")
            } else if (module.startsWith("%")) {
                // flip-flop
                val flipFlop = FlipFlopModule()
                modules[module.drop(1)] = flipFlop to target.split(", ")
            } else if (module.startsWith("&")) {
                // conjunction
                val name = module.drop(1)
                val sources = input.map { it.split(" -> ") }
                    .flatMap { it[1].split(", ").map { t -> it[0] to t } }
                    .filter { it.second == name }
                    .map { if (it.first[0] in listOf('%', '&')) it.first.drop(1) else it.first }
                    .associateWith { false }
                    .toMutableMap()
                val conjunction = ConjunctionModule(sources)
                modules[name] = conjunction to target.split(", ")
            }
        }
        return modules
    }

    fun part1(input: List<String>): Int {
        val modules = parseModules(input)

        // start pushing button
        var low = 0 // button sends low to broadcaster
        var high = 0
        repeat(1000) {
            val sequence = LinkedList<Action>()
            sequence.add(Action("Button", false, "broadcaster"))
            while (sequence.isNotEmpty()) {
                val action = sequence.poll()
                if (action.pulse) high++ else low++

                val module = modules[action.target]
                if (module != null) {
                    val result = module.first.apply(action.source, action.pulse)
                    if (result != null) {
                        sequence.addAll(module.second.map { Action(action.target, result, it) })
                    }
                }
            }
        }
        "= $low, $high".println()

        return low * high
    }

    // part2

    // not universal solution, but works for the specific puzzle input

    fun findLCM(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLcm = a * b
        var lcm = larger
        while (lcm <= maxLcm) {
            if (lcm % a == 0L && lcm % b == 0L) {
                return lcm
            }
            lcm += larger
        }
        return maxLcm
    }

    fun findLCMOfListOfNumbers(numbers: List<Long>): Long {
        var result = numbers[0]
        for (i in 1 until numbers.size) {
            result = findLCM(result, numbers[i])
        }
        return result
    }

    fun calcPeriod(modules: Map<String, Pair<Day20Module, List<String>>>, s: String, pulse: Boolean): Long {
        val filtered = modules.filter { it.value.second.contains(s) }
        if (pulse) {
            if (filtered.all { it.value.first.type() == 'c' }) {
                val minConj = mutableMapOf<String, Long>()
                var timesPressed = 0
                while (minConj.size < filtered.size) {
                    timesPressed++
                    val sequence = LinkedList<Action>()
                    sequence.add(Action("Button", false, "broadcaster"))
                    while (sequence.isNotEmpty()) {
                        val action = sequence.poll()

                        val module = modules[action.target]
                        if (module != null) {
                            val result = module.first.apply(action.source, action.pulse)
                            if (result == true && module.first.type() == 'c' && action.target in filtered.keys) {
                                minConj.putIfAbsent(action.target, timesPressed.toLong())
                            }
                            if (result != null) {
                                sequence.addAll(module.second.map { Action(action.target, result, it) })
                            }
                        }
                    }
                }
                return findLCMOfListOfNumbers(minConj.values.toList())
            }
        }

        return calcPeriod(modules, filtered.keys.first(), !pulse)
    }

    fun part2(input: List<String>): Long {
        val modules = parseModules(input)
        //modules.println()
        return calcPeriod(modules, "rx", false)
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day20_test1")
    val testInput2 = readInput("Day20_test2")
    check(part1(testInput1) == 32000000)
    check(part1(testInput2) == 11687500)
    //check(part2(testInput) == 30)

    "Start!".println()

    val input = readInput("Day20")
    part1(input).println() // 861743850
    part2(input).println() // 247023644760071
}
