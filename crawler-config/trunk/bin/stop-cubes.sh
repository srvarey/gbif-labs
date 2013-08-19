#! /bin/bash
ps aux | grep -e 'Occurrence' | grep -e 'Cube' | grep -v grep | awk '{print $2}' | xargs -i kill -9 {}
