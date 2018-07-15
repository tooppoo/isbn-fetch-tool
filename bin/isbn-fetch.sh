#!/bin/sh

function main() {
    filepath=$(dirname $0)
    scalaVersion=2.12
    toolVersion=0.1

    jarName="isbn-fetch-tool-assembly-$toolVersion.jar"

    java -jar "$filepath/../target/scala-$scalaVersion/$jarName" $@
}

main $@