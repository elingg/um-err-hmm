form 
     sentence wave_file ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data/speech/dev1_wav/fsh_60262.wav
     sentence interval_file ../corpus/LDC2009T01/CTSTreebankWithStructuralMetadata/data/text/dev1_int/fsh_60262.interval.txt
endform

	counta = 0
	countb = 0
	pitcha = 0
	pitchb = 0
	intensitya = 0
	intensityb = 0
	intervala = 0
	intervalb = 0
	
	
	Read from file... 'wave_file$'
	soundname$ = selected$ ("Sound", 1)
	To Pitch... 0.0 75.0 600.0
	Read from file... 'wave_file$'
	soundname$ = selected$ ("Sound", 1)
	To Intensity... 100 0 yes

	iteration = 1
	while iteration < 3

	prevlabel = -1

	file$ < 'interval_file$'
	leftoverlength = length (file$)
	while leftoverlength  > 2

		#printline 'leftoverlength'
		
 	      	firstnewline = index (file$, newline$)
		newlabel$ = left$ (file$, (firstnewline - 1))
		leftoverlength = length (file$)
		file$ = right$ (file$, (leftoverlength - firstnewline))
		
		secondnewline = index (file$, newline$)
		newlabel1$ = left$ (file$, (secondnewline - 1))
		leftoverlength = length (file$)
		file$ = right$ (file$, (leftoverlength - secondnewline))

		thirdnewline = index (file$, newline$)
		newlabel2$ = left$ (file$, (thirdnewline - 1))
		leftoverlength = length (file$)
		file$ = right$ (file$, (leftoverlength - thirdnewline))
	
      		select Pitch 'soundname$'
		newlabel = 'newlabel$'
		newlabel = newlabel/1000
		newlabel1 = 'newlabel1$'
		newlabel1 = newlabel1/1000
		pitchmax = Get maximum... newlabel newlabel1 Hertz Parabolic
		pitchmin = Get minimum... newlabel newlabel1 Hertz Parabolic
		pitchmean = Get mean... 0 0 Hertz
		pitchrange = (pitchmax - pitchmin)/pitchmean
		pitchrangenotnormalized = pitchmax - pitchmin

		pitchwordmean = Get mean... newlabel newlabel1 Hertz
		pitchoverallmin = Get minimum... 0 0 Hertz Parabolic
		f0 = (pitchwordmean -pitchoverallmin)/pitchmean
		f0notnormalized = 	(pitchwordmean -pitchoverallmin)	


		select Intensity 'soundname$'
		intensity = Get mean... newlabel newlabel1 energy
		intensitymean = Get mean... 0 0 energy
		intensityvalue = intensity/intensitymean
		intensityvaluenotnormalized = intensity

		interval = newlabel1 - newlabel

		if prevlabel = -1
			pause = 0
		else
			pause = newlabel - prevlabel
		endif

		prevlabel = newlabel

		indexOfDir= index(interval_file$,"int/")
		path$ = left$ (interval_file$, (indexOfDir-1))
		endpath$ = right$ (interval_file$, (indexOfDir-49))
		path$ = path$ + "feat/" + endpath$

		
		if newlabel2$ = "a"
			if iteration = 1
			counta = counta + 1
			if pitchwordmean != undefined
			pitcha = pitcha + pitchwordmean
			endif
			intensitya = intensitya + intensity
			intervala = intervala + interval
			endif
			
			if iteration = 2
			pitchspeaker = (pitchmax - pitchmin) / pitcha
			f0speaker = (pitchwordmean -pitchoverallmin) / pitcha
			energyspeaker =  intensity / intensitya
			pausespeaker = pause / intervala
			endif
		endif

		if newlabel2$ = "b"
			if iteration = 1
			countb = countb + 1
			if pitchwordmean != undefined
			pitchb = pitchb + pitchwordmean
			endif
			intensityb = intensityb + intensity
			intervalb = intervalb + interval
			endif
	
			if iteration = 2
			pitchspeaker = (pitchmax - pitchmin) / pitchb
			f0speaker = (pitchwordmean -pitchoverallmin) / pitchb
			energyspeaker =  intensity / intensityb
			pausespeaker = pause / intervalb
			endif
		endif

		if iteration = 2

		#fileappend "../features.txt" 'path$'
		fileappend 'path$' 'pitchrange'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'pitchspeaker'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'pitchrangenotnormalized'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'intensityvalue'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'energyspeaker'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'intensityvaluenotnormalized'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'f0'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'f0speaker'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'f0notnormalized'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'interval'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'pause'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'pausespeaker'
		fileappend 'path$' 'newline$'
		if newlabel2$ = "a"
		fileappend 'path$' 'pitcha'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'intensitya'
		fileappend 'path$' 'newline$'
		else
		fileappend 'path$' 'pitchb'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'intensityb'
		fileappend 'path$' 'newline$'
		endif
		endif

	endwhile
		iteration = iteration + 1
		pitcha = pitcha / counta
		intensitya = intensitya / counta
		pitchb = pitchb / countb
		intensityb = intensityb / countb
		
	endwhile
