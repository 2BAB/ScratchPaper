package me.xx2bab.scratchpaper

import me.xx2bab.scratchpaper.utils.CacheUtils
import me.xx2bab.polyfill.matrix.tool.CommandLineKit
import com.alibaba.fastjson.JSON
import java.io.File
import java.time.LocalDateTime

class BuildInfoGenerator(private val params: GeneratorParams) {

    private val buildInfoFileName = "scratch-paper.json"

    fun process() {
        val buildInfoDir = File(CacheUtils.getCacheDir(params.project, params.dimension), "assets")
        params.android.sourceSets.getByName(params.classicVariant.name).assets.srcDirs(buildInfoDir)

        params.project.tasks.getByName("pre${params.dimension}Build").doLast {
            val buildInfo = BuildInfo(
                base = generateBasicInfo(),
                git = generateGitInfo(),
                dependencies =  generateDependenciesInfo()
            )
            buildInfoDir.mkdirs()
            File(buildInfoDir, buildInfoFileName).apply {
                createNewFile()
                writeText(JSON.toJSONString(buildInfo))
            }

        }
    }

    private fun generateBasicInfo(): Base {
        return Base(
            buildTime = LocalDateTime.now().toString(),
            buildType = params.dimension,
            versionName = params.classicVariant.mergedFlavor.versionName ?: ""
        )
    }

    private fun generateGitInfo(): Git {
        return Git(
            branch =  CommandLineKit.runCommand("git rev-parse --abbrev-ref HEAD").let { it?.trim() ?: "" },
            head = CommandLineKit.runCommand("git rev-parse HEAD").let { it?.trim() ?: "" }
        )
    }

    private fun generateDependenciesInfo(): List<Dependency> {
        val deps = mutableListOf<Dependency>()
        params.project.configurations.all { config ->
            val singleDimensionSet = mutableListOf<String>()
            config.allDependencies.forEach { dep ->
                singleDimensionSet.add(dep.toString())
            }
            if (!singleDimensionSet.isEmpty()) {
                val dep = Dependency(
                    config.name,
                    singleDimensionSet
                )
                deps.add(dep)
            }
        }
        return deps
    }

}