#! /bin/bash
ps aux | grep -e 'DensityCube' | grep -v grep | awk '{print $2}' | xargs -i kill -9 {}
