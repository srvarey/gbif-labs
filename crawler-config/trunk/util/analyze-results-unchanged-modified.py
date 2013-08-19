#! /usr/bin/python

import os
import json

def listdir_fullpath(d):
    return [os.path.join(d, f) for f in os.listdir(d)]

for file in sorted(listdir_fullpath('/home/crap/storage/results')):
	data = json.load(open(file))
	if data['rawOccurrencesPersistedUpdated'] != 0 or data['rawOccurrencesPersistedUnchanged'] != 0:
		print(data['datasetUuid'], data['rawOccurrencesPersistedNew'],  data['rawOccurrencesPersistedUpdated'], data['rawOccurrencesPersistedUnchanged'])
