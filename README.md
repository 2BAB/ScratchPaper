<img src="./sp-banner.png" alt="ScratchPaper" width="771px">

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.2bab/scratchpaper/badge.svg)](https://search.maven.org/artifact/me.2bab/scratchpaper) [![Actions Status](https://github.com/2bab/ScratchPaper/workflows/CI/badge.svg)](https://github.com/2bab/ScratchPaper/actions) [![Apache 2](https://img.shields.io/badge/License-Apache%202-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[English][[中文说明]](./README_zh.md)

## How does it work?

![](./images/ic_launcher.png)![](./images/ic_launcher_round.png)

![](./images/scratch-paper-json.jpg)


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
        classpath 'com.android.tools.build:gradle:4.1.2'
        classpath 'me.2bab:scratch-paper:2.5.4'
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

Check screenshots on the top.

## Compatible

ScratchPaper is only supported & tested on LATEST ONE Minor versions of Android Gradle Plugin.

AGP Version|Latest Support Version
-----------|-----------------
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
> Copyright 2016-2021 2BAB
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

