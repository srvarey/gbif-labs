#! /bin/bash
ps aux | grep -e 'occurrence-cli.jar' | grep -v grep | awk '{print $2}' | xargs -i kill -9 {}
