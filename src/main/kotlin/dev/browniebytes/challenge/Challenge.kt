package dev.browniebytes.challenge

import java.io.InputStream
import java.io.OutputStream

/**
 * Basic skeleton that defines metadata and processing steps to complete an advent calendar challenge.
 */
sealed interface Challenge<I, R> {
    /**
     * The user-readable name for the challenge.
     * This is used in the menu system to make it easy to search for and run the challenges
     */
    fun name(): String

    /**
     * The day that the challenge was for.
     * This is used to automatically download the input
     */
    fun day(): Int
    fun part(): Int
    fun input(input: InputStream): I
    fun process(input: I): R
    fun output(result: R, output: OutputStream) {
        output.bufferedWriter().use {
            it.write(result.toString())
            it.write("\n")
            it.flush()
        }
    }
    fun run(inputStream: InputStream, outputStream: OutputStream) {
        val input = input(inputStream)
        val result = process(input)
        output(result, outputStream)
    }
}