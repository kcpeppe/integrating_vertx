#!/bin/bash
set -euo pipefail

java -jar gcmon-aggregators/target/aggregators.jar &
java -jar gcmon-datasource/target/datasource.jar &
java -jar gcmon-parser/target/parser.jar &
java -jar gcmon-web-view/target/web-view.jar &
