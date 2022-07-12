#!/usr/bin/env bash

echo "$0"
echo "$1"
echo "$@"
# shellcheck disable=SC2068
$@ >/tmp/process-startup-stdout.log 2>/tmp/process-startup-stderr.log
