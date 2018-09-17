package me.xx2bab.scratchpaper

import me.xx2bab.scratchpaper.utils.CacheUtils
import me.xx2bab.scratchpaper.utils.CommandUtils
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.io.File
import java.time.LocalDateTime

class BuildInfoGenerator(private val params: GeneratorParams) {

    fun process() {
        params.project.tasks.getByName("pre${params.variantCapedName}Build").doLast(
                "generate${params.variantCapedName}BuildInfoByScratchPaper") {
            val root = JSONObject()

            val base = generateBasicInfo()
            root[base.first] = base.second

            val git = generateGitInfo()
            root[git.first] = git.second

            val deps = generateDependenciesInfo()
            root[deps.first] = deps.second

            val buildInfoFile = File(CacheUtils.getCacheDir(params.project, params.buildName),
                    "scratch-paper.json")
            buildInfoFile.createNewFile()
            buildInfoFile.writeText(root.toJSONString())
        }
    }

    private fun generateBasicInfo(): Pair<String, JSONObject> {
        val base = JSONObject()
        base["buildType"] = params.buildName
        base["versionName"] = params.variant.mergedFlavor.versionName
        base["buildTime"] = LocalDateTime.now().toString()
        return Pair("base", base)
    }

    private fun generateGitInfo(): Pair<String, JSONObject> {
        val git = JSONObject()
        git["branch"] = CommandUtils.runCommand("git rev-parse --abbrev-ref HEAD")?.trim()
        git["latestCommit"] = CommandUtils.runCommand("git rev-parse HEAD")?.trim()
        return Pair("git", git)
    }

    private fun generateDependenciesInfo(): Pair<String, JSONObject> {
        val deps = JSONObject()
        params.project.configurations.all { config ->
            val configObj = JSONArray()
            config.allDependencies.forEach { dep ->
                configObj.add(dep.toString())
            }
            if (!configObj.isEmpty()) {
                deps[config.name] = configObj
            }
        }
        return Pair("dependencies", deps)
    }

}