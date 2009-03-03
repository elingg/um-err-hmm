#!/bin/bash
# Script to convert .sph files to .wav files from the CTS tree bank directories
# Relies on corpus existing under ../corpus/LDC...

targetdir=../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data/speech/dev1_wav
mkdir -p ${targetdir}
for i in $( find ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data/speech/dev1 -iname '*.sph'| sort ) ; do 
    sourcefile=${i##*/} # extracts just filename (1.sph)
    targetfile=${sourcefile%%.*}.wav # replaces extension (/tmp/1.wav)
    targetfile=${targetdir}/${targetfile}
    echo ../sox-14.2.0/sox $i $targetfile
    ../sox-14.2.0/sox $i $targetfile
done

targetdir=../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data/speech/dev2_wav
mkdir -p ${targetdir}
for i in $( find ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data/speech/dev2 -iname '*.sph'| sort ) ; do 
    sourcefile=${i##*/} # extracts just filename (1.sph)
    targetfile=${sourcefile%%.*}.wav # replaces extension (/tmp/1.wav)
    targetfile=${targetdir}/${targetfile}
    echo ../sox-14.2.0/sox $i $targetfile
    ../sox-14.2.0/sox $i $targetfile
done

targetdir=../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data/speech/eval_wav
mkdir -p ${targetdir}
for i in $( find ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data/speech/\eval -iname '*.sph'| sort ) ; do 
    sourcefile=${i##*/} # extracts just filename (1.sph)
    targetfile=${sourcefile%%.*}.wav # replaces extension (/tmp/1.wav)
    #targetfile=${targetfile#*/} # deletes second dir (1.wav)
    #echo $targetfile
    targetfile=${targetdir}/${targetfile}
    echo ../sox-14.2.0/sox $i $targetfile
    ../sox-14.2.0/sox $i $targetfile
done

