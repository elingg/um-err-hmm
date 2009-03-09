form 
     sentence wave_file test.wav
endform

	Read from file... 'wave_file$'
	soundname$ = selected$ ("Sound", 1)
	To Pitch... 0.0 75.0 600.0
	Read from file... 'wave_file$'
	soundname$ = selected$ ("Sound", 1)
	To Intensity... 100 0 yes
	file$ < ../intervals.txt
	leftoverlength = length (file$)
	while leftoverlength  > 50
		
 	      	firstnewline = index (file$, newline$)
		newlabel$ = left$ (file$, (firstnewline - 1))
		leftoverlength = length (file$)
		file$ = right$ (file$, (leftoverlength - firstnewline))
		
		secondnewline = index (file$, newline$)
		newlabel1$ = left$ (file$, (secondnewline - 1))
		leftoverlength = length (file$)
		file$ = right$ (file$, (leftoverlength - secondnewline))
	
      		select Pitch 'soundname$'
		newlabel = 'newlabel$'
		newlabel1 = 'newlabel1$'
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

		fileappend "../features.txt" 'pitchrange'
		fileappend "../features.txt" 'newline$'
		fileappend "../features.txt" 'pitchrangenotnormalized'
		fileappend "../features.txt" 'newline$'
		fileappend "../features.txt" 'intensityvalue'
		fileappend "../features.txt" 'newline$'
		fileappend "../features.txt" 'intensityvaluenotnormalized'
		fileappend "../features.txt" 'newline$'
		fileappend "../features.txt" 'f0'
		fileappend "../features.txt" 'newline$'
		fileappend "../features.txt" 'f0notnormalized'
		fileappend "../features.txt" 'newline$'
	endwhile
