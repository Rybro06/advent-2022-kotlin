@file:Suppress("unused", "RemoveEmptyPrimaryConstructor")

package dev.browniebytes.challenge

import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class CleaningAssignment(private val first: IntRange, private val second: IntRange) {
    fun contains() =
        first.contains(second.first) && first.contains(second.last) ||
        second.contains(first.first) && second.contains(first.last)
    fun overlaps() =
        first.contains(second.first) || first.contains(second.last) ||
        second.contains(first.first) || second.contains(first.last)
}

private fun parseInput(input: InputStream): List<CleaningAssignment> {
    val scan = Scanner(input)
    val assignments = ArrayList<CleaningAssignment>()
    while (scan.hasNextLine()) {
        val line = scan.nextLine()
        val parts = line.split(',')
        val first = parts[0].split('-')
        val second = parts[1].split('-')
        assignments.add(CleaningAssignment(
            Integer.parseInt(first[0])..Integer.parseInt(first[1]),
            Integer.parseInt(second[0])..Integer.parseInt(second[1])
        ))
    }
    return assignments
}

class AssignmentsContained() : Challenge<List<CleaningAssignment>, Int> {
    override fun name() = "Camp Cleanup (Contain)"
    override fun day() = 4
    override fun part() = 1
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: List<CleaningAssignment>): Int {
        return input.count { it.contains() }
    }
}


class CampCleanup() : Challenge<List<CleaningAssignment>, Int> {
    override fun name() = "Camp Cleanup (Overlap)"
    override fun day() = 4
    override fun part() = 2
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: List<CleaningAssignment>): Int {
        return input.count { it.overlaps() }
    }

}