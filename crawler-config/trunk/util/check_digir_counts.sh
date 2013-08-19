# !/bin/bash
if [ $# -ne 2 ]
then
  echo "Usage: `basename $0` <datasetUuid> <crawlId>"
  exit 
fi

XML_DIR=/home/crap/storage/xml
DATASET_DIR=$XML_DIR/$1/$2
SUM=0
for response_dir in $DATASET_DIR/*
do
	echo "Processing $response_dir"
	for response in $response_dir/*response
	do
		#echo "	Processing $response"
		count=`grep 'RECORD_COUNT' $response | cut -d">" -f2 | cut -d"<" -f1`
		#echo "	got count: $count"
		if [ -n "$count" ]; then
			SUM=$(($SUM+$count))
		fi
		#echo "  current sum $SUM"
	done
done
echo "Final sum of RECORD_COUNT: $SUM"
