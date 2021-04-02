package domes_project1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class A_tropos {

	
	
	int record_size = 32;
	FileManager sys;
	int DataPageSize = 128;
	
	A_tropos(){
		this.sys = new FileManager();
	}
	
	public void createFile(String filename, int numOfRecords, int maxKey) throws IOException {
		// TODO make it write to blocks [1:] so block 0 is reserved for FileHandle info, and add FileHandle info before exiting
		sys.CreateFile(filename);
		sys.OpenFile(filename);
			
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
	
	public void readFileForTestingPurposes(String filename) throws IOException {
		sys.OpenFile(filename);
		int filesize = (int) sys.file.length();
		int blocks = filesize/DataPageSize;
		int[] keys = {0, 0, 0 , 0};
		
		for(int i=0; i<blocks; i++) {
			sys.ReadBlock(i);
			//System.out.println("Keys in block " + i + ": ");

			for(int j=0; j<4; j++) {
				keys[j] = Functions_misc.readInt(sys.buffer, j*record_size);
				if(i==0) {
					System.out.println(keys[j]);
				}
			}
		}
		
	}
	
	public int serialSearch(String filename, int desired_key) throws IOException {
		sys.OpenFile(filename);
		int filesize = (int) sys.file.length();
		int blocks = filesize/DataPageSize;
		int key, disk_accesses=0;
		
		for(int i=0; i<blocks; i++) {
			sys.ReadBlock(i);
			disk_accesses++;
			for(int j=0; j<4; j++) {
				key = Functions_misc.readInt(sys.buffer, j*record_size);
				if(key == desired_key) {
					System.out.println("Desired key found in block " + i + ", record " + (j+1));
					System.out.println("It took " + disk_accesses + " disk access(es) to find it.");
					return 1;
				}
			}
		}
		System.out.println("The key was not found after the whole file was scanned (" + disk_accesses + " disk accesses).");
		return 0;
	}
}
