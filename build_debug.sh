#!/usr/bin/env bash
./gradlew clean assembleDemoDebug --no-daemon -Dorg.gradle.debug=true --info

echo 'Done. Check the outputs on ./app/build/intermediates/scratch-paper/'