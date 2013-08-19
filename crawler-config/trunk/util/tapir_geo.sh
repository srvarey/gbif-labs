# !/bin/bash
if [ $# -ne 2 ]
then
  echo "Usage: `basename $0` <datasetUuid> <crawlId>"
  exit 
fi

XML_DIR=/home/crap/storage/xml
DATASET_DIR=$XML_DIR/$1/$2
for response_dir in $DATASET_DIR/*
do
	#echo "Processing $response_dir"
	for response in $response_dir/*response
	do
		#echo "	Processing $response"
		#cc=`grep 'CollectionCode' $response`
		#cc=`grep 'InstitutionCode' | grep 'MHP' $response`
		#cc=`grep ':Latitude' $response`
		cc=`grep 'Agrostis laxissima Swallen' $response`
		if [ -n "$cc" ]; then
			echo "	$cc"
		fi
	done
done
