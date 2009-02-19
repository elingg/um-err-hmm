#!/usr/local/bin/perl

open ( FILE, "<ea980129-split001.ag.xml" ) || die "Unable to open file 'inputfile' <$!> \n";
open ( OUTFILE, ">>intervals.txt" ) || die "Unable to open file 'outputfile' <$!> \n";

my $test=0;
	while(my $line = <FILE>)
  	{
       	my @range = split (/\s+/, $line);
        if($range[2] eq "type=\"SU\""){
	  	$matrix2{$test,0}=$range[2]; 
	  	my @range1 = split ('=', $range[3]);
	  	$matrix2{$test,1}=$range1[1];
	  	$matrix2{$test,2}=$range[4];
	  	$test++;
          }
        }

close(FILE);

print $test;
print "\n";

open ( FILE, "<ea980129-split001.ag.xml" ) || die "Unable to open file 'inputfile' <$!> \n";

my $test=0;
        while(my $line = <FILE>)
        {
          my @range = split (/\s+/, $line);
	  	  my @range1 = split ('=', $range[1]);

          if($range1[1] eq $matrix2{$test,1}){
          	$matrix{$test,0}=$range1[2];
	  	    my @range2 = split ('=', $range[2]);
	  		@range3 = split ('"', $range2[1]);
	        if(@range3>1)
	        {
	  			print OUTFILE $range3[1];
	 			print OUTFILE "\n";
          		$test++;
			}
          }
        }

close(FILE);
