#!/bin/bash
# Experiments...
# with 2 pre-gram and 2 post-gram as well as number of words unclassified before it with prosodic:
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_2_2_nuncl_prevstlabel_prosodic.arff
