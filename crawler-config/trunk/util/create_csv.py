#! /usr/bin/python

import os
import json
import csv
import datetime

def listdir_fullpath(d):
    return [os.path.join(d, f) for f in os.listdir(d)]

def pretty_print(num):
	import locale
	locale.setlocale(locale.LC_ALL, '')
	return locale.format('%d', num, 1)

with open('results.csv', 'wb') as csvfile:
	csvwriter = csv.writer(csvfile, dialect='excel')
	csvwriter.writerow(['datasetUuid', 'endpointType', 'startedCrawling', 'finishedCrawling', 'duration', 'finishReason',
						'pagesCrawled', 'pagesFragmentedSuccessful', 'pagesFragmentedError', 'fragmentsEmitted',
                        'fragmentsReceived', 'rawOccurrencesPersistedNew', 'rawOccurrencesPersistedUpdated',
                        'rawOccurrencesPersistedUnchanged', 'rawOccurrencesPersistedError', 'fragmentsProcessed',
                        'verbatimOccurrencesPersistedSuccessful', 'verbatimOccurrencesPersistedError',
                        'interpretedOccurrencesPersistedSuccessful', 'interpretedOccurrencesPersistedError'])
	for file in listdir_fullpath('/home/crap/storage/results'):
		with open(file, 'r') as f:
			data = json.load(f)
			csvwriter.writerow([
				"http://gbrds.gbif.org/browse/agent?uuid=" + data['datasetUuid'],
				data['crawlJob']['endpointType'],
				datetime.datetime.fromtimestamp(data['startedCrawling'] / 1000).isoformat(),
				datetime.datetime.fromtimestamp(data['finishedCrawling'] / 1000).isoformat(),
				(data['finishedCrawling'] - data['startedCrawling']) / 1000,
				data['finishReason'],
				data['pagesCrawled'],
				data['pagesFragmentedSuccessful'],
				data['pagesFragmentedError'],
				data['fragmentsEmitted'],
                data['fragmentsReceived'],
                data['rawOccurrencesPersistedNew'],
                data['rawOccurrencesPersistedUpdated'],
                data['rawOccurrencesPersistedUnchanged'],
                data['rawOccurrencesPersistedError'],
                data['fragmentsProcessed'],
                data['verbatimOccurrencesPersistedSuccessful'],
                data['verbatimOccurrencesPersistedError'],
                data['interpretedOccurrencesPersistedSuccessful'],
                data['interpretedOccurrencesPersistedError']
            ])
