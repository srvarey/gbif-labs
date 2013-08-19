#! /bin/bash
ps aux | grep -e 'crawler-cli.jar' | grep -v grep | awk '{print $2}' | xargs -i kill -9 {}
