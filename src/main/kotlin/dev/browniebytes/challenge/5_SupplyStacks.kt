@file:Suppress("unused", "RemoveEmptyPrimaryConstructor")

package dev.browniebytes.challenge

import java.io.InputStream
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayDeque
import kotlin.collections.ArrayList

class StackMove(val count: Int, val from: Int, val to: Int)

class StackState(size: Int) {
    private val stacks = Array<ArrayDeque<Char>>(size) { ArrayDeque() }
    val moves: MutableList<StackMove> = ArrayList()

    fun size(): Int = stacks.size
    fun pushLast(crate: Char, stack: Int) = stacks[stack].addLast(crate)
    fun push(crate: Char, stack: Int) = stacks[stack].addFirst(crate)
    fun peek(stack: Int): String = stacks[stack].firstOrNull()?.toString() ?: ""
    fun pop(stack: Int): Char = stacks[stack].removeFirst()
    fun move(command: StackMove) {
        for (i in 0 until command.count) {
            if (peek(command.from).isBlank()) {
                break // Nothing more to move if the stack is empty
            }
            push(pop(command.from), command.to)
        }
    }

    fun moveRetainOrder(command: StackMove) {
        val temp = Stack<Char>()
        for (i in 0 until command.count) {
            if (peek(command.from).isBlank()) {
                break // Nothing more to move if the stack is empty
            }
            temp.push(pop(command.from))
        }

        while (!temp.empty()) {
            push(temp.pop(), command.to)
        }
    }
}

private fun parseInput(input: InputStream): StackState {
    val scan = Scanner(input)
    var state: StackState? = null
    // First read in the stack state
    while (scan.hasNextLine()) {
        val line = scan.nextLine()
        // Continue until the line that labels the stacks 1-N
        if (line.any { it.isDigit() }) {
            scan.nextLine() // Consume the next blank line
            break
        }

        // Each crate takes up 4 characters (except the last one which takes 3)
        val chunks = line.chunked(4)
        if (state == null) {
            state = StackState(chunks.size)
        }

        for (i in chunks.indices) {
            val chunk = chunks[i]
            if (chunk.isNotBlank()) {
                val crate = chunk[1] // The second character will always be the letter
                state.pushLast(crate, i) // We are reading the stacks in reverse order, so we need to push to the end
            }
        }
    }

    // Now read in the list of moves
    val pattern = Pattern.compile("^move (?<count>\\d+) from (?<from>\\d+) to (?<to>\\d+)$")
    while (scan.hasNextLine()) {
        val line = scan.nextLine()
        val match = pattern.matcher(line)
        if (!match.matches()) {
            throw IllegalArgumentException("Unable to parse move command!")
        }
        val count = Integer.parseInt(match.group("count"))
        val from = Integer.parseInt(match.group("from")) - 1
        val to = Integer.parseInt(match.group("to")) - 1
        state?.moves?.add(StackMove(count, from, to))
    }

    return state ?: StackState(0)
}

class SupplyStacks() : Challenge<StackState, String> {
    override fun name() = "Supply Stacks (Normal)"
    override fun day() = 5
    override fun part() = 1
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: StackState): String {
        for (move in input.moves) {
            input.move(move)
        }

        val answer = StringBuilder()
        for (i in 0 until input.size()) {
            answer.append(input.peek(i))
        }

        return answer.toString()
    }
}

class SupplyStacksRetained() : Challenge<StackState, String> {
    override fun name() = "Supply Stacks (Retained)"
    override fun day() = 5
    override fun part() = 2
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: StackState): String {
        for (move in input.moves) {
            input.moveRetainOrder(move)
        }

        val answer = StringBuilder()
        for (i in 0 until input.size()) {
            answer.append(input.peek(i))
        }

        return answer.toString()
    }
}