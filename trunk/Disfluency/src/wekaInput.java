
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;


public class wekaInput {
	
	private int numFeatures = 5; 
	File outputfile;
	BufferedWriter br; 

	public wekaInput(File outputfile)
	{
		this.outputfile = outputfile;
		try{
		br= new BufferedWriter(new FileWriter(outputfile));
		}
		catch(Exception e){
			
		}
		
	}
	public void startHeaderWrite(){
		try{
			br.write("@relation disfluency\n\n");
		}
		catch(Exception e){
			
		}
	}
	
    public void	writeFeatureHeader(String feature, String type){
    	try{
			br.write("@attribute "+feature+" "+type+"\n");
		}
		catch(Exception e){
			
		}
    }
    
	public void startDataWrite(){
		try{
			br.write("\n@data\n");
		}
		catch(Exception e){
			
		}
	}
	public void writeData(Vector v){
		String data="";
		if(v.size() < numFeatures)
		{
			System.out.println("Number of features is less than required");
			return;
		}
		int size= v.size();
		for(int i=0; i< size; i++)
		{
			data+=((String) v.get(i) + ", ");
		}
		try{
			br.write(data+"\n");
		}
		catch(Exception e){
			
		}
		
	}
	
	
	
}
