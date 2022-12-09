package dev.browniebytes.cli

import com.varabyte.kotter.foundation.*
import com.varabyte.kotter.foundation.collections.*
import com.varabyte.kotter.foundation.text.*
import com.varabyte.kotter.foundation.input.*
import com.willowtreeapps.fuzzywuzzy.diffutils.FuzzySearch
import dev.browniebytes.challenge.Challenge
import java.io.File
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Path
import kotlin.reflect.full.createInstance

class Cli {
    object HttpClient {
        private fun remoteInputURL(challenge: Challenge<*, *>) =
            URL("https://adventofcode.com/2022/day/${challenge.day()}/input")

        private fun sessionCookie(): String = System.getenv("AOC_SESSION")

        fun fetchInputFileFor(challenge: Challenge<*, *>, inputFile: File) {
            val url = remoteInputURL(challenge)
            println("Downloading input: $url")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("Accept", "text/plain")
            connection.setRequestProperty("Cookie", sessionCookie())
            connection.requestMethod = "GET"
            connection.doInput = true
            connection.doOutput = false
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw RuntimeException("Bad response ($responseCode - ${connection.responseMessage})")
            }

            println("Caching input to: ${inputFile.path}")
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            inputFile.outputStream().bufferedWriter().use { it.write(response) }
            connection.disconnect()
        }
    }

    private val challenges: List<Challenge<*, *>> = Challenge::class.sealedSubclasses.map { it.createInstance() }

    private fun localInputPathDir(): Path = Path.of(System.getProperty("java.io.tmpdir"), "advent")

    private fun localInputPathFor(challenge: Challenge<*, *>): Path =
        Path.of(localInputPathDir().toString(), "2022", "day", challenge.day().toString())

    private fun challengeList() =
        challenges.map { String.format("%d.%d - %s", it.day(), it.part(), it.name()) }.sorted()

    private fun challenge(prettyName: String): Challenge<*, *> =
        challenges[challengeList().indexOf(prettyName)]

    private fun prompt(prompt: String, options: List<String>): String {
        var choice = ""
        session {
            val filteredList = liveListOf(options)
            var selected by liveVarOf(options[0])
            section {
                text(prompt); input(Completions(selected)); textLine()
                for(item in filteredList) {
                    scopedState {
                        val bullet: String
                        if (item == selected) {
                            bullet = " > "
                            bold()
                        } else {
                            bullet = "   "
                        }
                        textLine("$bullet $item")
                    }
                }
            }
            .runUntilSignal {
                onInputChanged {
                    filteredList.withWriteLock {
                        clear()
                        FuzzySearch.extractSorted(input, options)
                            .mapNotNull { it.string }
                            .forEach { add(it) }
                    }
                    selected = filteredList[0]
                }
                onKeyPressed {
                    when(key) {
                        Keys.UP -> {
                            val len = filteredList.size
                            val index = (filteredList.indexOf(selected) - 1) % len
                            selected = filteredList[index]
                        }
                        Keys.DOWN -> {
                            val len = filteredList.size
                            val index = (filteredList.indexOf(selected) + 1) % len
                            selected = filteredList[index]
                        }
                        Keys.ESC -> signal()
                    }
                }
                onInputEntered { choice = selected; signal() }
            }
        }
        return choice
    }

    private fun getInputFileFor(challenge: Challenge<*, *>): File {
        val inputDir = localInputPathFor(challenge).toFile()
        val inputFile = Path.of(inputDir.path, "input.txt").toFile()
        if (!inputFile.exists() || !inputFile.isFile)
        {
            if (!inputDir.exists() && !inputDir.isDirectory && !inputDir.mkdirs())
            {
                throw RuntimeException("Unable to create local input cache directory for ${challenge.name()}")
            }

            if (!inputFile.createNewFile()){
                throw RuntimeException("Unable to create local input file: ${inputFile.path}")
            }

            HttpClient.fetchInputFileFor(challenge, inputFile)
        }
        return inputFile
    }

    fun start() {
        val tmpDirPath = localInputPathDir()
        val tmpDir = tmpDirPath.toFile()
        if (!tmpDir.exists() && !tmpDir.isDirectory && !tmpDir.mkdirs())
        {
            throw RuntimeException("Unable to create input download cache")
        }

        val title = "| Advent of Code 2022 Challenge Runner |"
        val border = '+' + "-".repeat(title.length - 2) + '+'
        println(border)
        println(title)
        println(border)

        val choice = prompt("Choose challenge: ", challengeList())
        val challenge = challenge(choice)
        println("Loaded: ${challenge.javaClass.simpleName}")

        val inputFile = getInputFileFor(challenge)
        println("Opening: ${inputFile.path}")
        val inputStream = inputFile.inputStream()
        challenge.run(inputStream, System.out)
    }
}