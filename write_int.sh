#!/bin/bash
# This is how we run our code for extracting Disfluency features from xml and wav files:
java -cp Disfluency/bin/ WordStream -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data  -writeintervals
