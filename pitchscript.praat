directory$ = "./"
Create Strings as file list... list 'directory$'*.wav
#Write to raw text file... 'directory$'FileList.txt
numberOfFiles = Get number of strings
if !numberOfFiles
	Create Strings as file list... list 'directory$'*.WAV
	numberOfFiles = Get number of strings
#	Write to raw text file... 'directory$'FileList.txt
endif
if !numberOfFiles
	exit There are no sound files in the folder!
else
	#Write to raw text file... 'directory$'FileList.txt
endif

Create Strings as file list... listtxt 'directory$'*.txt
input_File_No = 1

for ifile from input_File_No to numberOfFiles
	select Strings list
        fileName$ = Get string... ifile
	Read from file... 'directory$'/'fileName$'
	soundname$ = selected$ ("Sound", 1)
	To Pitch... 0.0 75.0 600.0

	select Strings listtxt
        fileNameTxt$ = Get string... ifile
	file$ < 'directory$'/'fileNameTxt$'
	leftoverlength = length (file$)
	while leftoverlength  > 20
		printline 'leftoverlength'
        	firstnewline = index (file$, newline$)
		newlabel$ = left$ (file$, (firstnewline - 1))
		leftoverlength = length (file$)
		file$ = right$ (file$, (leftoverlength - firstnewline))
	
      		 select Pitch 'soundname$'
		newlabel = 'newlabel$'
		newlabel1 = newlabel + 5
		pitchmax = Get maximum... newlabel newlabel1 Hertz Parabolic
        	printline 'pitchmax'
        	pitchmin = Get minimum... newlabel newlabel1 Hertz Parabolic
       		 printline 'pitchmin'	
		fileappend "features.txt" 'pitchmax'
	        fileappend "features.txt" 'newline$'
		fileappend "features.txt" 'pitchmin'
	        fileappend "features.txt" 'newline$'
	endwhile
endfor
