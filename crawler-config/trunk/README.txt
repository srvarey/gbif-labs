The contents of this directory should be copied to the home directory of the crap user (/home/crap) 
on the machine you wish to run some or all of the crawling infrastructure. Make sure to mount the
storage directory as per util/mount.sh, and then change bin/start-all.sh to start the pieces you
want. The memory and thread settings in the config/*yaml are reasonably well tuned so don't mess
with them unless you have to.

It's up to you to put recent copies of occurrence-cli.jar, metrics-cli.jar and crawler-cli.jar
in the lib directory.
