@file:Suppress("unused", "RemoveEmptyPrimaryConstructor")

package dev.browniebytes.challenge

import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

private fun parseInput(input: InputStream): List<List<Int>> {
    val scan = Scanner(input)
    val elves = ArrayList<ArrayList<Int>>()
    var elf = ArrayList<Int>()
    elves.add(elf)
    while (scan.hasNextLine()) {
        val line = scan.nextLine()
        if (line.isBlank()) {
            elf = ArrayList()
            elves.add(elf)
            continue
        }
        elf.add(Integer.parseInt(line))
    }
    return elves
}

class MaxCalories() : Challenge<List<List<Int>>, Int> {
    override fun name() = "Calorie Count"
    override fun day() = 1
    override fun part() = 1
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: List<List<Int>>): Int {
        return input.maxOf { it.sum() }
    }
}


class TopCalories() : Challenge<List<List<Int>>, Int> {
    override fun name() = "Calorie Count"
    override fun day() = 1
    override fun part() = 2
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: List<List<Int>>): Int {
        return input.map { it.sum() }
            .sortedDescending()
            .take(3)
            .sum()
    }

}