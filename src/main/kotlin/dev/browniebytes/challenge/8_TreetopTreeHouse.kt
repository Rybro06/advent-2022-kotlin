@file:Suppress("unused", "RemoveEmptyPrimaryConstructor")

package dev.browniebytes.challenge

import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

enum class Direction { NORTH, EAST, SOUTH, WEST }
class TreeGrid(private val trees: List<List<Int>>) {
    val width: Int
        get() = trees.firstOrNull()?.size ?: 0

    val height: Int
        get() = trees.size

    private fun viewToward(x: Int, y: Int, from: Direction): List<Int> {
        return when (from) {
            Direction.NORTH -> trees.map { it[x] }.take(y)
            Direction.SOUTH -> trees.map { it[x] }.takeLast(height - 1 - y).reversed()
            Direction.EAST -> trees[y].takeLast(width - 1 - x).reversed()
            Direction.WEST -> trees[y].take(x)
        }
    }

    fun visible(x: Int, y: Int): Boolean {
        val top = trees[y][x]
        return Direction.values().any { dir -> viewToward(x, y, dir).all { it < top } }
    }

    private fun viewFrom(x: Int, y: Int, toward: Direction) = viewToward(x, y, from = toward).reversed()

    private fun viewDistanceFrom(x: Int, y: Int, toward: Direction): Int {
        val top = trees[y][x]
        val view = viewFrom(x, y, toward)
        var count = 0
        for (tree in view)
        {
            count += 1
            if (tree >= top) return count
        }
        return count
    }

    fun scenicScore(x: Int, y: Int) =
        Direction.values().map { viewDistanceFrom(x, y, it) }.fold(1) { acc, i -> acc * i }
}

private fun parseInput(input: InputStream): TreeGrid {
    val scan = Scanner(input)
    val grid = ArrayList<List<Int>>()
    while (scan.hasNextLine()) {
        val line = scan.nextLine()
        val treeline = line.map { c -> Integer.parseInt(c.toString()) }.toList()
        grid.add(treeline)
    }
    return TreeGrid(grid)
}

class VisibleTrees() : Challenge<TreeGrid, Int> {
    override fun name() = "Treeline Tree House (Visible)"
    override fun day() = 8
    override fun part() = 1
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: TreeGrid): Int {
        var count = 0
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                if (input.visible(x, y)) count += 1
            }
        }
        return count
    }
}


class TreeHouse() : Challenge<TreeGrid, Int> {
    override fun name() = "Treetop Tree House (Placement)"
    override fun day() = 8
    override fun part() = 2
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: TreeGrid): Int {
        var score = 0
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                val localScore = input.scenicScore(x, y)
                score = max(localScore, score)
            }
        }
        return score
    }
}