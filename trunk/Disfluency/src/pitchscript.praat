form 
     sentence wave_file test.wav
endform



	Read from file... 'wave_file$'
	soundname$ = selected$ ("Sound", 1)
	To Pitch... 0.0 75.0 600.0

	file$ < intervals.txt
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
	         #printline 'newlabel$'
	         #printline  'newlabel1$'
		pitchmax = Get maximum... newlabel newlabel1 Hertz Parabolic
        	#printline 'pitchmax'
        	pitchmin = Get minimum... newlabel newlabel1 Hertz Parabolic
       		 #printline 'pitchmin'	
		pitchrange = pitchmax - pitchmin
		fileappend "features.txt" 'pitchrange'
	        fileappend "features.txt" 'newline$'
	endwhile
