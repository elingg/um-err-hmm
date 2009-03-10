#!/bin/bash
# Experiments...
# with just pos tag:
java -cp Disfluency/bin/ WordStream -npostgram 0 -npregram 0 -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_0_0.arff

# with 1 pre-gram alone:
java -cp Disfluency/bin/ WordStream -npostgram 0 -npregram 1 -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_1_0.arff

# with 1 post-gram alone:
java -cp Disfluency/bin/ WordStream -npostgram 1 -npregram 0 -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_0_1.arff

# with 1 pre-gram  and 1 post-gram :
java -cp Disfluency/bin/ WordStream -npostgram 1 -npregram 1 -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_1_1.arff

# with 2 pre-gram and 2 post-gram :
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_2_2.arff

# with 3 pre-gram and 3 post-grams:
java -cp Disfluency/bin/ WordStream -npostgram 3 -npregram 3 -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_3_3.arff

# with 2 pre-gram and 2 post-gram as well as number of words unclassified before it:
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_2_2_nuncl.arff

# with 2 pre-gram and 2 post-gram as well as number of words unclassified before it, on fisher:
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -corpus fsh -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_2_2_nuncl_fsh.arff

# with 2 pre-gram and 2 post-gram as well as number of words unclassified before it:
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -corpus sw -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_2_2_nuncl_sw.arff

# with 3 pre-gram and 3 post-gram as well as number of words unclassified before it:
java -cp Disfluency/bin/ WordStream -npostgram 3 -npregram 3 -nprev -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_3_3_nuncl.arff

# with 2 pre-gram and 2 post-gram as well as number of words unclassified before it, on fisher:
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/disfluency_2_2_nuncl_prevstlabel.arff
