package me.xx2bab.scratchpaper

//{
//   "git":{
//      "latestCommit":"9072615dd9d1716829e42ee0d207c7b7b486ca29",
//      "branch":"dev_v2"
//   },
//   "base":{
//      "buildTime":"2021-04-30T17:03:16.166",
//      "buildType":"DemoDebug",
//      "versionName":"1.0"
//   },
//   "dependencies":[
//      {
//         "name": "fullDebugImplementation"
//         "list":[
//             "DefaultExternalModuleDependency{group='org.jetbrains.kotlin', name='kotlin-stdlib', version='1.4.32', configuration='default'}",
//             "DefaultExternalModuleDependency{group='androidx.core', name='core-ktx', version='1.3.1', configuration='default'}",
//             "DefaultExternalModuleDependency{group='androidx.appcompat', name='appcompat', version='1.2.0', configuration='default'}",
//             "DefaultExternalModuleDependency{group='com.google.android.material', name='material', version='1.2.1', configuration='default'}",
//             "DefaultExternalModuleDependency{group='androidx.constraintlayout', name='constraintlayout', version='2.0.1', configuration='default'}"
//          ],
//      },
//      ...
//   ]
//}
data class BuildInfo(
    val git: Git,
    val base: Base,
    val dependencies: List<Dependency>
)

data class Git(
    val head: String,
    val branch: String
)

data class Base(
    val buildTime: String,
    val buildType: String,
    val versionName: String
)

data class Dependency(
    val name: String,
    val list: List<String>
)
