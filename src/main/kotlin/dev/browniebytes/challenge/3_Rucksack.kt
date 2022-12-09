@file:Suppress("unused", "RemoveEmptyPrimaryConstructor")

package dev.browniebytes.challenge

import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class Rucksack(private val pockets: List<Set<Char>>) {
    object Priority {
        fun of(c: Char) = if (c.isUpperCase()) {
            c.code - 'A'.code + 27
        } else {
            c.code - 'a'.code + 1
        }
    }

    fun all() = pockets.first() union pockets.last()

    fun matches() = pockets.first() intersect pockets.last()
}

private fun parseInput(input: InputStream): List<Rucksack> {
    val scan = Scanner(input)
    val sacks = ArrayList<Rucksack>()
    while (scan.hasNextLine()) {
        val line = scan.nextLine().trim()
        val len = line.length / 2
        val first = line.substring(0 until len).toSet()
        val second = line.substring(len).toSet()
        sacks.add(Rucksack(listOf(first, second)))
    }
    return sacks
}

class RucksackPriorities() : Challenge<List<Rucksack>, Int> {
    override fun name() = "Rucksack Priorities"
    override fun day() = 3
    override fun part() = 1
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: List<Rucksack>): Int {
        return input.map { it.matches() }
            .flatten()
            .sumOf { Rucksack.Priority.of(it) }
    }
}


class RucksackBadge() : Challenge<List<Rucksack>, Int> {
    override fun name() = "Rucksack Badges"
    override fun day() = 3
    override fun part() = 2
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: List<Rucksack>): Int {
        return input.asSequence()
            .chunked(3)
            .map { rucksacks -> rucksacks.map { it.all() } }
            .map { it.drop(1).fold(it.first()) { sum, next -> sum intersect next } }
            .flatten()
            .sumOf { Rucksack.Priority.of(it) }
    }

}