#!/bin/bash
# This is how we run our code for extracting Disfluency features from xml and wav files:
java -cp Disfluency/bin/ WordStream -npostgram 3 -npregram 3 -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_3_3.arff
