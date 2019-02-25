#!/bin/bash
set -euo pipefail

function dockerBuildPush() {
  docker build -t docker.sebastian-daschner.com/${1}:1 .
  docker push docker.sebastian-daschner.com/${1}:1
}

#mvn clean install -DskipTests

pushd gcmon-datasource
  dockerBuildPush 'datasource'
popd

pushd gcmon-parser
  dockerBuildPush 'parser'
popd

pushd gcmon-aggregators
  dockerBuildPush 'aggregators'
popd

pushd gcmon-web-view
  dockerBuildPush 'web-view'
popd
