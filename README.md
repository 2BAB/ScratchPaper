# ScratchPaper (v2.x)

[English][[中文说明]](./README_zh.md)

## How it works

> If you install your debug App and release App at the same time in one screen, you can not distinguish which one is what you want.

ScatchPaper can add a overlay on your icon, and put some given information on it.

> If you have more than one staging App for QA or other colleagues, when they found some issues you may don't know how to match the App to your commit, because all of them share the same versions like "2.1.0-SNAPSHOT".

ScatchPaper supports generating build information into your artifact (which can read from /assets/scratch-paper.json) and also `/intermedias/scratch-paper/assets` directory including:

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
        classpath 'com.android.tools.build:gradle:3.1.4'
        classpath 'me.2bab:scratch-paper:2.1.0'
    }
}
```

**0x02. Apply Plugin:**

``` gradle
// On Application's build.gradle (do not use in Library project)
apply plugin: 'me.2bab.scratchpaper'
```


**0x03. Build your App and Enjoy!**

![](./images/ic_launcher.png)![](./images/ic_launcher_round.png)

![](./images/scratch-paper-json.jpg)


**0x04. Advanced Configurations**

``` gradle
scratchPaper {
    textSize = 12
    textColor = "#FFFFFFFF"
    verticalLinePadding = 4
    backgroundColor = "#99000000"
    extraInfo = "This is a sample!"
    enableGenerateIconOverlay = true
    enableGenerateBuildInfo = true
    
    // Experimental field
    // @see IconOverlayGenerator#removeXmlIconFiles
    enableXmlIconRemove = false
}
```

## Compatible

ScratchPaper only tests in Latest TWO Minor versions of Android Gradle Plugin.

AGP Version|Compatible Status
-----------|-----------------
3.1.x (Aapt2) | Support
3.0.x (Aapt2) | Support
2.3.x (Aapt2) | Never Tested
2.3.x (Aapt1) | Not Support


## v1.x (Deprecated)

The v1.x `IconCover` forked from [icon-version@akonior](https://github.com/akonior/icon-version). It provided icon editor functions that compatible with `Aapt1`, and I added some little enhancement like hex color support, custom text support. As time goes by, we have to move to `Aapt2` sooner or later. So I decide to revamp the whole project and add more fancy features. **If you are still using `Aapt1` with `IconCover`, now is the time to consider moving into the new one.**

## License

>
> Copyright 2018 2BAB
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

