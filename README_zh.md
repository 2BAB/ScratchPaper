<img src="./sp-banner.png" alt="ScratchPaper" width="771px">

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.2bab/scratchpaper/badge.svg)](https://search.maven.org/artifact/me.2bab/scratchpaper) 
[![Actions Status](https://github.com/2bab/ScratchPaper/workflows/CI/badge.svg)](https://github.com/2bab/ScratchPaper/actions) 
[![Apache 2](https://img.shields.io/badge/License-Apache%202-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[中文说明][[English]](./README.md)

ScratchPaper 是一个 Gradle 插件，用来给 APK 图标添加 variant/version/git-commit-id 等信息以区分不同版本，由[全新的 Variant/Artifact API](https://developer.android.com/studio/build/extend-agp) 和 [Polyfill](https://github.com/2BAB/Polyfill) 框架驱动。

## ScratchPaper 解决了什么问题？

![](./images/launcher_icons.png)

> 如果你在一台设备上同时安装一个 App 的 Debug 版和 Release 版，你可能很难区分出来到底哪个你要测试的版本（不打开的情况下）。

> 如果你同时打了多个测试包给测试或者产品（例如前后打了三次 "2.1.0-SNAPSHOT"），当他们给你反馈的问题时候你和他们可能都很难分别出每个 App 对应的具体的分支或者 commit 节点。

ScratchPaper 可以在你的 App 启动图标上加一个蒙层用以区分不同变体的 App，其承载了版本信息等附加文字。

- 支持 常规 和 圆形 的图标
- 支持 adaptive-icon
- 支持 AAPT2
- 支持多行自定义文字内容（包括一些内置的内容）

## 为什么一定要试试 ScratchPaper

其实市面上不乏有类似的解决方案，但是他们的最重要问题在于：多数都不支持 AAPT2 和新版的 AGP。ScratchPaper 提供了朴素的文字蒙层叠加，支持最新的 AGP 和 adaptive icons，以及 Gradle 惰性配置的特性（可减少配置期耗时）。如果你不需要多行文字的特性，也可以选择另外一个比较流行并且还在维护的方案 [sefulness/easylauncher-gradle-plugin](https://github.com/usefulness/easylauncher-gradle-plugin)。

## 如何使用？

**0x01. Add the plugin to classpath:**

``` kotlin
// 可选方式 1.
// 添加 `mavenCentral` 到 `settings.gradle.kts`（或根目录 `build.gradle.kts`） 的 `pluginManagement{}` 内， 
// 并且声明 scratchpaper 插件的 id.
pluginManagement {
	repositories {
        ...
        mavenCentral()
    }
    plugins {
    	...
    	id("me.2bab.scratchpaper") version "3.3.0" apply false
    }
}


// 可选方式 2.
// 使用经典的 `buildscript{}` 引入方式（在根目录的 build.gradle.kts）.
buildscript {
    repositories {
        ...
        mavenCentral()
    }
    dependencies {
    	...
        classpath("me.2bab:scratchpaper:3.3.0")
    }
}
```

**0x02. Apply Plugin:**

``` gradle
// 在 Application 模块的 build.gradle.kts (不要在 Library 模块使用)
plugin {
    ...
    id("me.2bab.scratchpaper")
}
```

**0x03. Advanced Configurations**

``` kotlin
scratchPaper {
    // 可以根据 variant 开启
    // Can not be lazily set, it's valid only before "afterEvaluate{}".
    // In this way, only "FullDebug" variant will get icon overlays
    enableByVariant { variant ->
        variant.name.contains("debug", true)
                && variant.name.contains("full", true)
    }

    // !!! Mandatory field.
    // Can be lazily set even after configuration phrase.
    iconNames.set("ic_launcher, ic_launcher_round")

    // Some sub-feature flags
    enableXmlIconsRemoval.set(false) // Can be lazily set even after configuration phrase.
    forceUpdateIcons = true // Can not be lazily set, it's valid only before "afterEvaluate{}".

    // ICON_OVERLAY styles, contents.
    style {
        textSize.set(9)
        textColor.set("#FFFFFFFF") // Accepts 3 kinds of format: "FFF", "FFFFFF", "FFFFFFFF".
        lineSpace.set(4)
        backgroundColor.set("#99000000") // Same as textColor.
    }

    content {
        showVersionName.set(true)
        showVariantName.set(true)
        showGitShortId.set(true)
        showDateTime.set(true)
        extraInfo.set("For QA")
    }
}
```

**0x04. Build your App and Enjoy!**

效果请看头部的截图。

## 兼容性

精力有限，ScratchPaper 只会支持最新两个 Minor 版本的 Android Gradle Plugin。从 `2.5.4` 开始，ScratchPaper 发布的仓库从 Jcenter 迁移到 **Maven Central**。

AGP Version|Latest Support Version
-----------|-----------------
8.1.x | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.2bab/scratchpaper/badge.svg)](https://search.maven.org/artifact/me.2bab/scratchpaper)
8.0.x | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.2bab/scratchpaper/badge.svg)](https://search.maven.org/artifact/me.2bab/scratchpaper)
7.2.x | 3.2.1
7.1.x | 3.2.1
7.0.x | 3.0.0
4.1.x | 2.5.4
4.0.x | 2.5.3
3.6.x | 2.5.1
3.5.x | 2.4.2
3.4.x | 2.4.1
3.3.x | 2.4.1
3.2.x | 2.4.0
3.1.x | 2.4.0
3.0.x (Aapt2) | Support
2.3.x (Aapt2) | Never Tested
2.3.x (Aapt1) | Not Support

## Git Commit Check

Check this [link](https://medium.com/walmartlabs/check-out-these-5-git-tips-before-your-next-commit-c1c7a5ae34d1) to make sure everyone will make a **meaningful** commit message.

So far we haven't added any hook tool, but follow the regex below:

```
(chore|feat|docs|fix|refactor|style|test|hack|release)(:)( )(.{0,80})
```


## v1.x (Deprecated)

The v1.x `IconCover` forked from [icon-version@akonior](https://github.com/akonior/icon-version). It provided icon editor functions that compatible with `Aapt1`, and I added some little enhancement like hex color support, custom text support. As time goes by, we have to move to `Aapt2` sooner or later. So I decide to revamp the whole project and add more fancy features. **If you are still using `Aapt1` with `IconCover`, now is the time to consider moving into the new one.**

## License

>
> Copyright Since 2016 2BAB
>
>Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
>
>   http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

