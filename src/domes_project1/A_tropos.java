package domes_project1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class A_tropos {

	
	
	int record_size = 32;
	FileManager sys;
	
	A_tropos(){
		this.sys = new FileManager();
	}
	
	public void createFile(String filename, int numOfRecords, int maxKey) throws IOException {
		sys.CreateFile(filename);
		sys.OpenFile(filename);
			
		String s = "hello";
		ArrayList<Integer> used_keys = new ArrayList<Integer>();
		boolean is_not_good;
		int random, counter=0, block_num=0;
		byte[] data;
		Random rng = new Random();
		
    	///////////
		for(int i=0; i<numOfRecords; i++){
			do{
				is_not_good = false;
				random = Functions_misc.getRandomNum(maxKey, rng);
	    		for(int j=0; j<used_keys.size(); j++) {
	    			if(random == used_keys.get(j)) {
	    			    is_not_good = true;
	    			    break;
	    			}
	    		}
	    	}while(is_not_good);
			
	    	// got the unique random here
	    	used_keys.add(random);
	    	System.out.println(random + " is key #" + i);
	    	sys.writeIntToBuffer(random, record_size*counter);
	    	
	    	data = Functions_misc.getRandomData(28, rng);
	    	System.arraycopy(data, 0, sys.buffer, record_size*counter+4, data.length);
	    	
	    	//sys.writeStringToBuffer(s, record_size*counter+4);
	    	if(counter==3) {
		    	sys.WriteBlock(block_num);
	    		block_num++;
		    	counter=0;
	    	}else {
	    		counter++;
	    	}
		}
		System.out.println("File " + filename + " created with " + block_num + " disk accesses.");
	}
	
}
