#!/bin/bash

export tomee_home=/c/apache-tomee-plus-8.0.2-SNAPSHOT

java \
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=28030 \
-Dopenejb.server.uri=http://127.0.0.1:8080/tomee/ejb \
-Dopenejb.client.moduleId=stateful_remove_annotated_client \
-classpath $tomee_home/lib/openejb-client-8.0.2-SNAPSHOT.jar:$tomee_home/lib/javaee-api-8.0-4.jar \
org.apache.openejb.client.Main