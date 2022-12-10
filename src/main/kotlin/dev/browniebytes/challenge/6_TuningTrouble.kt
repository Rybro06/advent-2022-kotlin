@file:Suppress("unused", "RemoveEmptyPrimaryConstructor")

package dev.browniebytes.challenge

import java.io.InputStream
import java.lang.StringBuilder
import java.util.*

private fun parseInput(input: InputStream): String {
    val scan = Scanner(input)
    val signal = StringBuilder()
    while (scan.hasNextLine()) {
        signal.append(scan.nextLine())
    }
    return signal.toString()
}

class TuningTroublePacket() : Challenge<String, Int> {
    override fun name() = "Tuning Trouble (Packet)"
    override fun day() = 6
    override fun part() = 1
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: String): Int {
        val size = 4
        return input.windowed(size).indexOfFirst { it.toCharArray().distinct().size == size } + size
    }
}


class TuningTroubleMessage() : Challenge<String, Int> {
    override fun name() = "Tuning Trouble (Message)"
    override fun day() = 6
    override fun part() = 2
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: String): Int {
        val size = 14
        return input.windowed(size).indexOfFirst { it.toCharArray().distinct().size == size } + size
    }

}