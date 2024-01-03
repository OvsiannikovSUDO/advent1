import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

fun main() {

    data class Position(val x:Long, val y:Long, val z:Long)
    data class Velocity(val u:Long, val v:Long, val w:Long)
    data class Hail(val position: Position, val velocity: Velocity)

    fun willIntersect2D(hail1: Hail, hail2: Hail, min: Long, max: Long): Boolean {
        // do math
        val (x1, y1, u1, v1) = listOf(hail1.position.x, hail1.position.y, hail1.velocity.u, hail1.velocity.v)
        val (x2, y2, u2, v2) = listOf(hail2.position.x, hail2.position.y, hail2.velocity.u, hail2.velocity.v)
        assert (u1 != 0L)
        assert (u2 != 0L)
        // parallel
        if (v1.toDouble()/u1 == v2.toDouble()/u2) return false
        val t2 = (y1.toDouble()-y2 + (x2-x1)*v1.toDouble()/u1) / (v2-u2*v1.toDouble()/u1)
        val t1 = (x2-x1 + u2*t2) / u1
        val x = x1 + u1*t1
        val y = y1 + v1*t1
        //"=== $t1 $t2 ($x,$y)".println()
        return (t1 > 0) && (t2 > 0) && (x>=min && x<=max) && (y>=min && y<=max)
    }

    fun part1(input: List<String>, min: Long, max: Long): Int {
        val hails = input.map {
            val (p,v) = it.split(" @ ")
                .map {c -> c.split(", ").map { s -> s.trim().toLong() } }
            val position = Position(p[0], p[1], p[2])
            val velocity = Velocity(v[0], v[1], v[2])
            Hail(position, velocity)
        }

        var count = 0
        for (i in 0..hails.size-2) {
            for (j in i..< hails.size) {
                if (willIntersect2D(hails[i],hails[j], min, max)) count++
            }
        }

        return count
    }

    // part2
    data class Matrix(val values: List<List<Double>>) {
        fun determinant(): Double {
            if (values.size == 1) return values[0][0]
            var d = 0.0
            var sign = 1
            for (i in values.indices) {
                d += sign * values[0][i] * minor(0, i).determinant()
                sign = -sign
            }
            return d
        }

        fun minor (i: Int, j: Int): Matrix {
            return Matrix(values.filterIndexed { idx,_ -> idx != i }.map {
                it.filterIndexed { idx,_ -> idx != j }
            })
        }

        fun calcRoots(b: List<Double>): List<Double> {
            val determinant = this.determinant()
            return values.indices.map { i ->
                values.indices.sumOf { j ->
                    (if ((i+j)%2 == 0) 1 else -1) * minor(j, i).determinant() * b[j]
                }/determinant
            }
        }
    }

    // system of nonlinear equations
    fun calcSONE(a: List<(List<Double>) -> Double>, jacob: List<List<(List<Double>) -> Double>>, x0: List<Double>): List<Double>
    {
        var dx = x0.map { _ -> 1.0 }
        var xk = x0
        while (!dx.all { abs(it) < 0.00001 }) {
            val l = jacob.map { row -> row.map { f -> f.invoke(xk) } }
            val m = Matrix(l)
            dx = m.calcRoots(a.map { f -> -f.invoke(xk) })
            "--- $xk $dx".println()
            xk = xk.mapIndexed { idx, value -> value + dx[idx]}
        }
        return xk
    }

    fun calcCoordinates(hail1: Hail, hail2: Hail, hail3: Hail): List<Double> {
        val (x1,x2,x3) = listOf(hail1.position.x, hail2.position.x, hail3.position.x)
        val (u1,u2,u3) = listOf(hail1.velocity.u, hail2.velocity.u, hail3.velocity.u)
        val (y1,y2,y3) = listOf(hail1.position.y, hail2.position.y, hail3.position.y)
        val (v1,v2,v3) = listOf(hail1.velocity.v, hail2.velocity.v, hail3.velocity.v)
        val (z1,z2,z3) = listOf(hail1.position.z, hail2.position.z, hail3.position.z)
        val (w1,w2,w3) = listOf(hail1.velocity.w, hail2.velocity.w, hail3.velocity.w)

        // x + u*t1 = x1 + u1*t1
        // x = {x,y,z,u,v,w,t1,t2,t3}
        val a = listOf(
            {x: List<Double> -> x[0]-x1 + x[6] * (x[3] - u1) },
            {x: List<Double> -> x[0]-x2 + x[7] * (x[3] - u2) },
            {x: List<Double> -> x[0]-x3 + x[8] * (x[3] - u3) },
            {x: List<Double> -> x[1]-y1 + x[6] * (x[4] - v1) },
            {x: List<Double> -> x[1]-y2 + x[7] * (x[4] - v2) },
            {x: List<Double> -> x[1]-y3 + x[8] * (x[4] - v3) },
            {x: List<Double> -> x[2]-z1 + x[6] * (x[5] - w1) },
            {x: List<Double> -> x[2]-z2 + x[7] * (x[5] - w2) },
            {x: List<Double> -> x[2]-z3 + x[8] * (x[5] - w3) },
        )
        val jacob = listOf(
            listOf(
                {_: List<Double> -> 1.0},
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> x[6] },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> (x[3] - u1) },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
            ),
            listOf(
                {_: List<Double> -> 1.0},
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> x[7] },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> (x[3] - u2) },
                {_: List<Double> -> .0 },
            ),
            listOf(
                {_: List<Double> -> 1.0},
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> x[8] },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> (x[3] - u3) },
            ),
            // y
            listOf(
                {_: List<Double> -> .0 },
                {_: List<Double> -> 1.0},
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> x[6] },
                {_: List<Double> -> .0 },
                {x: List<Double> -> (x[4] - v1) },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
            ),
            listOf(
                {_: List<Double> -> .0 },
                {_: List<Double> -> 1.0},
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> x[7] },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> (x[4] - v2) },
                {_: List<Double> -> .0 },
            ),
            listOf(
                {_: List<Double> -> .0 },
                {_: List<Double> -> 1.0},
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> x[8] },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> (x[4] - v3) },
            ),
            // z
            listOf(
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {_: List<Double> -> 1.0},
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> x[6] },
                {x: List<Double> -> (x[5] - w1) },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
            ),
            listOf(
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {_: List<Double> -> 1.0},
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> x[7] },
                {_: List<Double> -> .0 },
                {x: List<Double> -> (x[5] - w2) },
                {_: List<Double> -> .0 },
            ),
            listOf(
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {_: List<Double> -> 1.0},
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> x[8] },
                {_: List<Double> -> .0 },
                {_: List<Double> -> .0 },
                {x: List<Double> -> (x[5] - w3) },
            ),
        )
        return calcSONE(a, jacob, listOf(1.0, 1.0, 1.0,
            1.0, 1.0, 1.0,
            10_000.0, 200_000.0, 300_000.0))
    }

    fun part2(input: List<String>): Long {

        val hails = input.map {
            val (p,v) = it.split(" @ ")
                .map {c -> c.split(", ").map { s -> s.trim().toLong() } }
            val position = Position(p[0], p[1], p[2])
            val velocity = Velocity(v[0], v[1], v[2])
            Hail(position, velocity)
        }

        val pos = calcCoordinates(hails[0], hails[1], hails[2])
        pos.map { it.toLong() }.println()
        return pos[0].toLong() + pos[1].toLong() + pos[2].toLong()
    }

    fun testMatrix() {
        // check system of linear equations
        val m = Matrix(listOf(listOf(2.0, 3.0, 1.0), listOf(-1.0, 5.0, 2.0), listOf(4.0, -1.0, 6.0)))
        check(m.determinant() == 87.0)
        check(m.minor(0,0).determinant() == 32.0)
        val solution = m.calcRoots(listOf(6.0,6.0,9.0))
        solution.println()

        // check system of nonlinear equations
        val a = listOf(
            {x: List<Double> -> sin(x[0] - 0.5) - x[1] - 1.5 },
            {x: List<Double> -> 2 * x[0] - cos(x[1]) - 0.6 },
        )
        val jacob = listOf(
            listOf(
                {x: List<Double> -> cos(x[0] - 0.5) },
                {_: List<Double> -> -1.0 }
            ),
            listOf(
                {_: List<Double> -> 2.0 },
                {x: List<Double> -> -sin(x[1]) }
            )
        )
        calcSONE(a, jacob, listOf(0.13, -1.8)).println()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day24_test")
    check(part1(testInput, 7, 27) == 2)
    check(part2(testInput) == 47L)

    // test
    //testMatrix()

    println("Start!")

    val input = readInput("Day24")
    part1(input, 200000000000000, 400000000000000).println() // 18184
    part2(input).println() // 557789988450159
}
