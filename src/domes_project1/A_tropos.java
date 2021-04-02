package domes_project1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
			System.out.println("Keys in block " + i + ": ");

			for(int j=0; j<4; j++) {
				keys[j] = Functions_misc.readInt(sys.buffer, j*record_size);
				System.out.println(keys[j]);
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

	public void createSortedFile_A(String filename) throws IOException {
		FileManager src_sys = new FileManager();
		FileManager dst_sys = new FileManager();
		// TODO make it write to blocks [1:] so block 0 is reserved for FileHandle info, and add FileHandle info before exiting
 
		String sorted_filename = filename + "_sorted";
		dst_sys.CreateFile(sorted_filename);
		dst_sys.OpenFile(sorted_filename);
		src_sys.OpenFile(filename);
		
		ArrayList<Integer> unsortedkeys = new ArrayList<Integer>();
		ArrayList<Record> records = new ArrayList<Record>();
		
		int filesize = (int) src_sys.file.length();
		int blocks = filesize/DataPageSize;
		int key, counter = 0, block_num=0;
		byte[] data = new byte[record_size-4];
		
		for(int i=0; i<blocks; i++) {
			src_sys.ReadBlock(i);
			for(int j=0; j<4; j++) {
				key = Functions_misc.readInt(src_sys.buffer, j*record_size);
				unsortedkeys.add(key);
				data = Arrays.copyOfRange(src_sys.buffer, j*record_size+4, (j+1)*record_size);
				Record rec = new Record(key, data);
				records.add(rec);
			}
		}
		
		ArrayList<Record> sorted_recs = Functions_misc.sortRecordsA(records);
		
		for(int i=0; i<sorted_recs.size(); i++) {
			data = sorted_recs.get(i).getData();
			key = sorted_recs.get(i).getKey();
			dst_sys.writeIntToBuffer(key, record_size*counter);
	    	System.arraycopy(data, 0, dst_sys.buffer, record_size*counter+4, data.length);
	    	
	    	if(counter==(DataPageSize/record_size)-1) {
		    	dst_sys.WriteBlock(block_num);
	    		block_num++;
		    	counter=0;
	    	}else {
	    		counter++;
	    	}		
	    }
		
	}
	
}
