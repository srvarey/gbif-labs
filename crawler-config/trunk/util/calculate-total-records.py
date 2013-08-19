#! /usr/bin/python

import os
import json
import collections

def listdir_fullpath(d):
    return [os.path.join(d, f) for f in os.listdir(d)]

def pretty_print(num):
	import locale
	locale.setlocale(locale.LC_ALL, '')
	return locale.format('%d', num, 1)

fragmentsEmitted = 0
rorNew = 0
rorUpdated = 0
rorUnchanged = 0
rorError = 0
verbatim = 0
verbatimError = 0
interpreted = 0
interpretedError = 0
total = 0
finishReason = collections.defaultdict(int) 

for file in listdir_fullpath('/home/crap/storage/results'):
	with open(file, 'r') as f:
		data = json.load(f)
	total += 1
	fragmentsEmitted += data['fragmentsEmitted']
	rorNew += data['rawOccurrencesPersistedNew']
	rorUpdated += data['rawOccurrencesPersistedUpdated']
	rorUnchanged += data['rawOccurrencesPersistedUnchanged']
	rorError += data['rawOccurrencesPersistedError']
	verbatim += data['verbatimOccurrencesPersistedSuccessful']
	verbatimError += data['verbatimOccurrencesPersistedError']
	interpreted += data['interpretedOccurrencesPersistedSuccessful']
	interpretedError += data['interpretedOccurrencesPersistedError']
	finishReason[data['finishReason']] += 1

print "total crawls finished:", total
print "fragments emitted:", pretty_print(fragmentsEmitted)
print "ror new:", pretty_print(rorNew)
print "ror updated:", pretty_print(rorUpdated)
print "ror unchanged:", pretty_print(rorUnchanged)
print "ror error:", pretty_print(rorError)
print "verbatim:", pretty_print(verbatim)
print "verbatim error:", pretty_print(verbatimError)
print "interpreted:", pretty_print(interpreted)
print "interpretedError:", pretty_print(interpretedError)
for reason, count in finishReason.items():
	print "finish reason ", reason, ": ", count
