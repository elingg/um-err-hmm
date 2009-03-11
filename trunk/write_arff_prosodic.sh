#!/bin/bash
# Experiments...
# with 2 pre-gram and 2 post-gram as well as number of words unclassified before it with prosodic:
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/wordpitchrange.arff -wordpitchrange
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/wordpitchrangespeakernormalized.arff -wordpitchrangespeakernormalized
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/wordpitchrangenotnormalized.arff -wordpitchrangenotnormalized
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/energy.arff  -energy
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/energyspeakernormalized.arff -energyspeakernormalized
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/energynotnormalized.arff -energynotnormalized
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/f0.arff -f0
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/f0speakernormalized.arff -f0speakernormalized
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/f0notnormalized.arff -f0notnormalized
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/intervalSize.arff -intervalSize
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/pauseLengthBeforeWord.arff -pauseLengthBeforeWord
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/pauseLengthBeforeWordSpeakerNormalized.arff -pauseLengthBeforeWordSpeakerNormalized
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/AverageSpeakerPitch.arff -AverageSpeakerPitch
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/AverageSpeakerEnergy.arff -AverageSpeakerEnergy
java -cp Disfluency/bin/ WordStream -npostgram 2 -npregram 2 -nprev -prevstlabel -prosodic -dir ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data -wekafile expts/allprosodic.arff
