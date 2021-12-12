#!/usr/bin/env bash
./gradlew clean assembleFullDebug --no-daemon -Dorg.gradle.debug=true --info

echo 'Done. Check the outputs on ./app/build/intermediates/scratch-paper/'