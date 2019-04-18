#!/bin/bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5001 -Duser.country=US -Duser.language=en -jar gilgamesh.jar

