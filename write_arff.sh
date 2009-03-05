#!/bin/bash
# This is how we run our code for extracting Disfluency features from xml and wav files:
java -cp Disfluency/bin/ Disfluency -npostgram 2 -npregram 2 -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile tmp.arff
