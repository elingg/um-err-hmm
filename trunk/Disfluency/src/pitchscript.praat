form 
     sentence wave_file test.wav
     sentence interval_file test.txt
endform

	Read from file... 'wave_file$'
	soundname$ = selected$ ("Sound", 1)
	To Pitch... 0.0 75.0 600.0
	Read from file... 'wave_file$'
	soundname$ = selected$ ("Sound", 1)
	To Intensity... 100 0 yes
	file$ < 'interval_file$'
	leftoverlength = length (file$)
	while leftoverlength  > 75
		
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
	
		indexOfDir= index(interval_file$,"int/")
		path$ = left$ (interval_file$, (indexOfDir-1))
		endpath$ = right$ (interval_file$, (indexOfDir-49))
		path$ = path$ + "feat/" + endpath$

		#fileappend "../features.txt" 'path$'
		fileappend 'path$' 'pitchrange'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'pitchrangenotnormalized'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'intensityvalue'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'intensityvaluenotnormalized'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'f0'
		fileappend 'path$' 'newline$'
		fileappend 'path$' 'f0notnormalized'
		fileappend 'path$' 'newline$'
	endwhile
