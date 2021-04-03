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
		//sys.CreateFile(filename);
		sys.OpenFile(filename);
		
		// initialize variables
		ArrayList<Integer> used_keys = new ArrayList<Integer>();
		boolean is_not_good;
		int random, counter=0, block_num=0;
		byte[] data;
		Random rng = new Random();
		
    	// create records with random keys and random binary data
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
	    	}while(is_not_good);		// process is repeated while the key is not unique
			
	    	// write the key
	    	used_keys.add(random);
	    	//System.out.println(random + " is key #" + i);
	    	sys.writeIntToBuffer(random, record_size*counter);
	    	
	    	// write the data
	    	data = Functions_misc.getRandomData(28, rng);
	    	System.arraycopy(data, 0, sys.buffer, record_size*counter+4, data.length);
	    	
	    	// once a whole block of records (4 records) is written to the buffer, write it to the file
	    	if(counter==3) {
		    	sys.WriteBlock(block_num);
	    		block_num++;
		    	counter=0;
	    	}else {
	    		counter++;
	    	}
		}
		// print how many disk accesses the file creation costed
		// (the number of disk accesses is the same as the number of blocks in the file)
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
		/*
		 * Search a file organized in this way for a desired key.
		 * The search is done serially (the blocks are loaded
		 * to memory and scanned one by one until the key is found)
		 */
		// open file and initialize variables
		sys.OpenFile(filename);
		int filesize = (int) sys.file.length();
		int blocks = filesize/DataPageSize;
		int key, disk_accesses=0;
		
		// iterate over the file's blocks
		for(int i=0; i<blocks; i++) {
			sys.ReadBlock(i);				// read one block
			disk_accesses++;				// every time a block is read increment the disk access counter
			for(int j=0; j<4; j++) {		// iterate over the records of the block
				key = Functions_misc.readInt(sys.buffer, j*record_size);
				if(key == desired_key) {	// if the key is found, "announce" it and exit
					System.out.println("Desired key found in block " + i + ", record " + (j+1));
					System.out.println("It took " + disk_accesses + " disk access(es) to find it.");
					return 1;
				}
			}
		}
		// if the key was not found, "anounce" it and exit with code 0
		System.out.println("The key was not found after the whole file was scanned (" + disk_accesses + " disk accesses).");
		return 0;
	}

	public void createSortedFile_A(String filename) throws IOException {
		/*
		 * Sort a file organized in this way.
		 * The records in the new file are sorted by ascending order of keys
		 * making it a file organized according to the 3rd way
		 * described in the project requirements (way C)
		 */
		// TODO make it write to blocks [1:] so block 0 is reserved for FileHandle info, and add FileHandle info before exiting

		// initialize variables, instantiate classes and open files
		FileManager src_sys = new FileManager();
		FileManager dst_sys = new FileManager();
		String sorted_filename = filename + "_sorted";
		//dst_sys.CreateFile(sorted_filename);
		dst_sys.OpenFile(sorted_filename);
		src_sys.OpenFile(filename);
		ArrayList<Record> records = new ArrayList<Record>();
		int filesize = (int) src_sys.file.length();
		int blocks = filesize/DataPageSize;
		int key, counter = 0, block_num=0, disk_reads=0, disk_writes=0;
		byte[] data = new byte[record_size-4];
		
		// read the records off the unsorted file and store them in memory in an ArrayList
		// (this could have been done in a better way by using mergeSort() but it wasn't
		//  for simplicity and timing reasons)
		for(int i=0; i<blocks; i++) {				// iterate over the blocks
			src_sys.ReadBlock(i);					// read a single block
			disk_reads++;							// increment the disk access counter
			for(int j=0; j<4; j++) {				// iterate over the records in the block
				key = Functions_misc.readInt(src_sys.buffer, j*record_size);
				data = Arrays.copyOfRange(src_sys.buffer, j*record_size+4, (j+1)*record_size);
				Record rec = new Record(key, data);	
				records.add(rec);					// add the record in the records ArrayList
			}
		}
		
		// sort the records ArrayList
		ArrayList<Record> sorted_recs = Functions_misc.sortRecordsA(records);
		
		// write the sorted records in the new file
		for(int i=0; i<sorted_recs.size(); i++) {				// iterate over the records of the sorted ArrayList
			data = sorted_recs.get(i).getData();				// get the key and the data of each element
			key = sorted_recs.get(i).getKey();
			dst_sys.writeIntToBuffer(key, record_size*counter);	// write the key of each record in the buffer
	    	System.arraycopy(data, 0, dst_sys.buffer, record_size*counter+4, data.length); // write the data of each record in the buffer
	    	
	    	// write to the file each time enough (4) records have been written to the buffer
	    	if(counter==(DataPageSize/record_size)-1) {
		    	dst_sys.WriteBlock(block_num);
		    	disk_writes++;
	    		block_num++;
		    	counter=0;
	    	}else {
	    		counter++;
	    	}		
	    }
		System.out.println("Sorted records file created with " + (disk_reads+disk_writes) + " disk accesses. (" + disk_reads +" reads and " + disk_writes + " writes)");
	}
	
}
