@file:Suppress("unused", "RemoveEmptyPrimaryConstructor")

package dev.browniebytes.challenge

import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

enum class RpsMove(
    val openSymbol: String,
    val counterSymbol: String,
    val points: Int,
)
{
    Rock("A", "X", 1),
    Paper("B", "Y", 2),
    Scissors("C", "Z", 3);
    object By {
        val opening = RpsMove.values().associateBy { it.openSymbol }
        val counter = RpsMove.values().associateBy { it.counterSymbol }
    }
    fun weakness(): RpsMove =
        when(this) {
            Rock -> Paper
            Paper -> Scissors
            Scissors -> Rock
        }

    fun strength(): RpsMove =
        when(this) {
            Rock -> Scissors
            Paper -> Rock
            Scissors -> Paper
        }

    fun versus(other: RpsMove): RpsResult =
        when (other) {
            this.strength() -> RpsResult.Win
            this.weakness() -> RpsResult.Loss
            else -> RpsResult.Draw
        }

    fun matchup(result: RpsResult) =
        when (result) {
            RpsResult.Loss -> this.strength()
            RpsResult.Draw -> this
            RpsResult.Win -> this.weakness()
        }
}

enum class RpsResult(val symbol: String, val points: Int) {
    Loss("X", 0),
    Draw("Y", 3),
    Win("Z", 6);
    object By {
        val symbol = RpsResult.values().associateBy { it.symbol }
    }
}

private typealias SimpleRpsRound = Pair<RpsMove, RpsMove>

class RpsByCounterMove() : Challenge<List<SimpleRpsRound>, Int> {
    override fun name() = "Rock Paper Scissors"
    override fun day() = 2
    override fun part() = 1
    override fun input(input: InputStream): List<SimpleRpsRound> {
        val scan = Scanner(input)
        val rounds = ArrayList<SimpleRpsRound>()
        while(scan.hasNextLine())
        {
            val first = RpsMove.By.opening[scan.next().trim()]
            val second = RpsMove.By.counter[scan.nextLine().trim()]
            if (first == null || second == null) {
                throw IllegalArgumentException("Invalid input")
            }
            rounds.add(SimpleRpsRound(first, second))
        }
        return rounds
    }

    override fun process(input: List<SimpleRpsRound>): Int {
        return input.sumOf { it.second.points + it.second.versus(it.first).points }
    }
}

private typealias OutcomeRpsRound = Pair<RpsMove, RpsResult>

class RpsByOutcome() : Challenge<List<OutcomeRpsRound>, Int> {
    override fun name() = "Rock Paper Scissors"
    override fun day() = 2
    override fun part() = 2

    override fun input(input: InputStream): List<OutcomeRpsRound> {
        val scan = Scanner(input)
        val rounds = ArrayList<OutcomeRpsRound>()
        while(scan.hasNextLine())
        {
            val first = RpsMove.By.opening[scan.next().trim()]
            val second = RpsResult.By.symbol[scan.nextLine().trim()]
            if (first == null || second == null) {
                throw IllegalArgumentException("Invalid input")
            }
            rounds.add(OutcomeRpsRound(first, second))
        }
        return rounds
    }

    override fun process(input: List<OutcomeRpsRound>): Int {
        return input.sumOf { it.first.matchup(it.second).points + it.second.points }
    }
}