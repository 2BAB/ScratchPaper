# ScratchPaper (v2)

[![JCenter](https://api.bintray.com/packages/2bab/maven/scratch-paper/images/download.svg)](https://bintray.com/2bab/maven/scratch-paper/_latestVersion) [![Apache 2](https://img.shields.io/badge/License-Apache%202-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0) [![Welcome PRs](https://img.shields.io/badge/PRs-Welcome-orange.svg)](https://github.com/2BAB/ScratchPaper/pulls)

[English][[中文说明]](./README_zh.md)

## How does it work?

> If you install both debug&release Apps in one device, you can not distinguish which is the one you want to test.

ScratchPaper can add a overlay on your icon, and put given information (like buildType, buildVersion) on it.

- Support regular & round Icons 
- Support adaptive-icon
- Support AAPT2

> If you have more than one staging Apps for QA or other colleagues, when they found some issues you may don't know how to match the App version to your code base (branch/commit/etc..), because all of them share the same version like "2.1.0-SNAPSHOT".

ScratchPaper supports generating build information into your artifact (which can read from /assets/scratch-paper.json) and also `/intermedias/scratch-paper/assets` directory including:

- Base: Build Time, Build Type, etc.
- Git: Latest Commit ID & commit branch, etc.
- Dependencies
- ...

## Usage

**0x01. Add the plugin to classpath:**

``` gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'me.2bab:scratch-paper:2.5.1'
    }
}
```

**0x02. Apply Plugin:**

``` gradle
// On Application's build.gradle (do not use in Library project)
apply plugin: 'me.2bab.scratchpaper'
```

**0x03. Advanced Configurations**

``` gradle
scratchPaper {
    textSize = 10
    textColor = "#FFFFFFFF"
    verticalLinePadding = 4
    backgroundColor = "#99000000"
    extraInfo = new Date().format("MM-dd,HH:mm")
    enableGenerateIconOverlay = true
    enableGenerateBuildInfo = true
    enableVersionNameSuffixDisplay = true
    
    // Experimental field
    // @see IconOverlayGenerator#removeXmlIconFiles
    enableXmlIconRemove = false
}
```

**0x04. Build your App and Enjoy!**

![](./images/ic_launcher.png)![](./images/ic_launcher_round.png)

![](./images/scratch-paper-json.jpg)

## Compatible

ScratchPaper is only supported & tested on LATEST ONE Minor versions of Android Gradle Plugin.

AGP Version|Compatible Status
-----------|-----------------
3.6.x (Aapt2) | Support (last support version - 2.5.1)
3.5.x (Aapt2) | Support (last support version - 2.4.2)
3.4.x (Aapt2) | Support (last support version - 2.4.1)
3.3.x (Aapt2) | Support (last support version - 2.4.1)
3.2.x (Aapt2) | Support (last support version - 2.4.0)
3.1.x (Aapt2) | Support (last support version - 2.4.0)
3.0.x (Aapt2) | Support
2.3.x (Aapt2) | Never Tested
2.3.x (Aapt1) | Not Support


## v1.x (Deprecated)

The v1.x `IconCover` forked from [icon-version@akonior](https://github.com/akonior/icon-version). It provided icon editor functions that compatible with `Aapt1`, and I added some little enhancement like hex color support, custom text support. As time goes by, we have to move to `Aapt2` sooner or later. So I decide to revamp the whole project and add more fancy features. **If you are still using `Aapt1` with `IconCover`, now is the time to consider moving into the new one.**

## License

>
> Copyright 2016-2020 2BAB
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

