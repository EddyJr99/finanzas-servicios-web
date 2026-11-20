#!/bin/bash
# Arranca la API REST con el Maven incluido en tools/ (solo requiere Java 17 e internet la primera vez).
# Uso: ./ejecutar_api.sh [puerto]
cd "$(dirname "$0")" || exit 1
[ -n "$1" ] && export SERVER_PORT="$1"
chmod +x tools/apache-maven-3.9.11/bin/mvn
tools/apache-maven-3.9.11/bin/mvn -q spring-boot:run
