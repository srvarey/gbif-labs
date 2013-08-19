#! /usr/bin/python

import os
import json
import collections

def listdir_fullpath(d):
    return [os.path.join(d, f) for f in os.listdir(d)]

for file in listdir_fullpath('/home/crap/storage/results'):
	with open(file, 'r') as f:
		data = json.load(f)

	if not data['declaredCount']: continue
	if data['declaredCount'] == data['fragmentsEmitted']: continue
	if data['finishReason'] != 'NORMAL': continue

	print data['datasetUuid'], data['declaredCount'], data['fragmentsEmitted'], data['declaredCount'] - data['fragmentsEmitted'], data['interpretedOccurrencesPersistedSuccessful']

