@file:Suppress("unused", "RemoveEmptyPrimaryConstructor")

package dev.browniebytes.challenge

import java.io.InputStream
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

class File(val name: String, val size: Int)
class Directory(
    val path: Path,
    val files: MutableMap<String, File> = HashMap(),
    val dirs: MutableMap<String, Directory> = HashMap()
) {
    val size: Int
        get() = files.values.sumOf { it.size } + dirs.values.sumOf { it.size }

    val name: String
        get() = path.segments.last()

    fun subDirs(): List<Directory> {
        return sequence {
            yieldAll(dirs.values)
            yieldAll(dirs.values.flatMap { it.subDirs() })
        }.toList()
    }
}
class Path(private var path: String) : Cloneable {
    val segments: List<String>
        get() = path.split('/')

    fun push(value: String) {
        var newSegment = value
        if (value.startsWith("/")) {
             newSegment = value.removePrefix("/")
        }

        if (path.endsWith("/")) {
            path += newSegment
        } else {
            path = "$path/$newSegment"
        }
    }

    fun pop(): String {
        if (path.isEmpty()) {
            return  ""
        }
        val segments = this.segments
        val last = segments.last()
        path = segments.subList(0, segments.size - 1).joinToString("/")
        return last
    }

    public override fun clone() = Path(this.path)

    override fun toString() = path

    operator fun plus(other: String): Path {
        val result = clone()
        result.push(other)
        return result
    }
}
class FileSystem(val root: Directory = Directory(Path("/"))) {
    private fun getDir(path: Path): Directory? {
        val segments = path.segments
        var cd = root
        for (segment in segments) {
            if (segment.isNotBlank()) {
                cd = cd.dirs[segment] ?: return null
            }
        }
        return cd
    }

    fun getFile(path: Path): File? {
        val copy = path.clone()
        val filename = copy.pop()
        val dir = getDir(copy) ?: return null
        return dir.files[filename]
    }

    fun mkdir(path: Path): Directory {
        val segments = path.segments
        var cd = root
        for (segment in segments) {
            if (segment.isNotBlank()) {
                cd = cd.dirs.getOrPut(segment) { Directory(cd.path + segment) }
            }
        }
        return cd
    }

    fun touch(path: Path, size: Int): File {
        val copy = path.clone()
        val filename = copy.pop()
        val dir = mkdir(copy)
        return dir.files.getOrPut(filename) { File(filename, size) }
    }

    fun dirs(): List<Directory> {
        return root.subDirs()
    }
}

private fun parseInput(input: InputStream): FileSystem {
    val scan = Scanner(input)
    val system = FileSystem()
    val cmdPattern = Pattern.compile("^\\$ (?<cmd>cd|ls)\\s*(?<arg>[/.\\w]*)$")
    val dirPattern = Pattern.compile("^dir (?<name>\\w+)$")
    val filePattern = Pattern.compile("^(?<size>\\d+) (?<name>[.\\w]+)$")
    var cwd = Path("")
    while (scan.hasNextLine()) {
        val line = scan.nextLine()
        when {
            line.startsWith("$") -> {
                val matcher = cmdPattern.matcher(line)
                if (!matcher.matches()) throw IllegalArgumentException("Invalid command")
                val cmd = matcher.group("cmd")
                val arg = matcher.group("arg")
                if (cmd == "cd") {
                    if (arg == "..") {
                        cwd.pop()
                    } else if (arg.startsWith("/")) {
                        cwd = Path(arg)
                    } else {
                        cwd.push(arg)
                        system.mkdir(cwd)
                    }
                } // ignore ls commands
            }

            line.startsWith("dir") -> {
                val matcher = dirPattern.matcher(line)
                if (!matcher.matches()) throw IllegalArgumentException("Invalid dir line")
                val name = matcher.group("name")
                val dirPath = cwd + name
                system.mkdir(dirPath)
            }

            else -> {
                val matcher = filePattern.matcher(line)
                if (!matcher.matches()) throw IllegalArgumentException("Invalid file line")
                val size = Integer.parseInt(matcher.group("size"))
                val name = matcher.group("name")
                val filePath = cwd + name
                system.touch(filePath, size)
            }
        }
    }
    return system
}

class NoSpaceDirSize() : Challenge<FileSystem, Int> {
    override fun name() = "No Space (Directory Size)"
    override fun day() = 7
    override fun part() = 1
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: FileSystem): Int {
        println(input.dirs().size)
        return input.dirs().filter { it.size <= 100000 }.sumOf { it.size }
    }
}


class NoSpace() : Challenge<FileSystem, Int> {
    override fun name() = "No Space (Remove Min)"
    override fun day() = 7
    override fun part() = 2
    override fun input(input: InputStream) = parseInput(input)

    override fun process(input: FileSystem): Int {
        val cap = 70_000_000
        val req = 30_000_000
        val used = input.root.size
        val free = cap - used
        val need = req - free
        val toDelete = input.dirs().filter { it.size >= need }.minBy { it.size }
        println("Used: $used Need: $need Found: ${toDelete.name} (${toDelete.size})")
        return toDelete.size
    }

}