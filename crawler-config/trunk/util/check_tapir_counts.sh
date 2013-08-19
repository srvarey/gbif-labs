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
	RESPONSE_SUM=0
	for response in $response_dir/*response
	do
		#echo "	Processing $response"
		count=`grep 'totalReturned' $response | rev | cut -d"\"" -f2 | rev`
		#echo "	got count: $count"
		if [ -n "$count" ]; then
			RESPONSE_SUM=$(($RESPONSE_SUM+$count))
		fi
	done
	echo "Processed $response_dir (got: $RESPONSE_SUM)"
	SUM=$(($SUM+$RESPONSE_SUM))
done
echo "Final sum of totalReturned: $SUM"
