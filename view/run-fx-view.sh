#!/bin/bash
set -euo pipefail
cd ${0%/*}

java -jar --module-path="/opt/java/javafx-sdk-11.0.2/lib" --add-modules=javafx.controls fx-view.jar
