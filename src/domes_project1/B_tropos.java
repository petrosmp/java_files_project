package domes_project1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


@SuppressWarnings("unused")
public class B_tropos {
	
	int record_size = 32;
	FileManager sys_ptrs;
	FileManager sys_recs;
	int DataPageSize = 128;
	
	B_tropos(){
		this.sys_ptrs = new FileManager();
		this.sys_recs = new FileManager();
	}
	
	public void createFile(String filename, int numOfRecords, int maxKey) throws IOException {
		// TODO make it write to blocks [1:] so block 0 is reserved for FileHandle info, and add FileHandle info before exiting
		
		String recs_name = filename + "_recs";			// records filename
		String ptrs_name = filename + "_ptrs";			// pointers filename
		
		// open files
		sys_recs.OpenFile(recs_name);
		sys_ptrs.OpenFile(ptrs_name);
		
		// initialize variables and RNG
		ArrayList<Integer> used_keys = new ArrayList<Integer>();
		boolean is_not_good;
		int random, ptrs_counter=0, recs_counter=0, ptrs_block_num=0, rec_block_num=0;
		byte[] data;
		Random rng = new Random();
		
    	// create records
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
	    	}while(is_not_good);	// if the key is found in the used_keys array, repeat the process
			
	    	// add the key to the used_keys array, write it to the file
	    	used_keys.add(random);
	    	System.out.println(random + " is key #" + i);		// testing purposes
	    	sys_recs.writeIntToBuffer(random, record_size*recs_counter);
	    	
	    	// get random binary data and write it to the file
	    	data = Functions_misc.getRandomData(28, rng);
	    	System.arraycopy(data, 0, sys_recs.buffer, record_size*recs_counter+4, data.length);
	    	
	    	// write the key and the block number of the record in the pointers file
	    	sys_ptrs.writeIntToBuffer(random, ptrs_counter*8);
	    	sys_ptrs.writeIntToBuffer(rec_block_num, ptrs_counter*8+4);
	    	
	    	// check when to write to disc (every time a whole block of information has been written to the buffer)
	    	// also increment counters (or reset them when writing a block)
	    	
	    	if(recs_counter==(DataPageSize/record_size)-1) {
		    	sys_recs.WriteBlock(rec_block_num);
	    		rec_block_num++;
		    	recs_counter=0;
	    	}else {
	    		recs_counter++;
	    	}
	    	
	    	if(ptrs_counter==(DataPageSize/8)-1) {
		    	sys_ptrs.WriteBlock(ptrs_block_num);
	    		ptrs_block_num++;
		    	ptrs_counter=0;
	    	}else {
	    		ptrs_counter++;
	    	}
	    	
		}
		int total_da = ptrs_block_num + rec_block_num;
		System.out.println("File " + filename + " created with " + total_da + " disk accesses. (" + ptrs_block_num + " for the pointers file and " + rec_block_num + " for the records file)");
	}

	public void readFilesForTestingPuproses(String filename) throws IOException {
		
		String recs_name = filename + "_recs";			// records filename
		String ptrs_name = filename + "_ptrs";			// pointers filename
		
		// open files
		sys_recs.OpenFile(recs_name);
		sys_ptrs.OpenFile(ptrs_name);
		
		// initialize variables
		int recs_filesize = (int) sys_recs.file.length();
		int ptrs_filesize = (int) sys_ptrs.file.length();
		int[] keys = {0, 0, 0, 0};
		int test;
		int recs_blocks = recs_filesize/DataPageSize;
		int ptrs_blocks = ptrs_filesize/DataPageSize;
		
		for(int i=0; i<recs_blocks; i++) {
			sys_recs.ReadBlock(i);
			if(i==1337) {
				System.out.println("Keys found in block " + i);
				for(int j=0; j<4; j++) {
					keys[j] = Functions_misc.readInt(sys_recs.buffer, j*record_size);
					System.out.println(keys[j]);
				}
			}
		}
		for(int i=0; i<ptrs_blocks; i++) {
			sys_ptrs.ReadBlock(i);
			for(int j=0; j<DataPageSize/8; j++) {
				if(i>620) {
					test = Functions_misc.readInt(sys_ptrs.buffer, j*8);
					//System.out.print("Key: " + test + " ");
					test = Functions_misc.readInt(sys_ptrs.buffer, (j*8)+4);
					//System.out.print("Block: " + test + "\n");
				}
			}
		}
		
	}

	public void serialSearch(String filename, int desired_key) throws IOException {
		
		String recs_name = filename + "_recs";			// records filename
		String ptrs_name = filename + "_ptrs";			// pointers filename
		
		// open files
		sys_recs.OpenFile(recs_name);
		sys_ptrs.OpenFile(ptrs_name);
		
		// initialize variables
		int recs_filesize = (int) sys_recs.file.length();
		int ptrs_filesize = (int) sys_ptrs.file.length();
		int[] keys = {0, 0, 0, 0};
		int key, block=-1, disk_accesses=0;
		int recs_blocks = recs_filesize/DataPageSize;
		int ptrs_blocks = ptrs_filesize/DataPageSize;
		
		// search in for key pointers file
		outer_loop:
		for(int i=0; i<ptrs_blocks; i++) {
			sys_ptrs.ReadBlock(i);
			disk_accesses++;			// for each block read, increment the disk access counter
			for(int j=0; j<DataPageSize/8; j++) {
				key = Functions_misc.readInt(sys_ptrs.buffer, j*8);		// read the key from the buffer (16 key-block pairs in the file, so this has to be done iteratively)
				if(key == desired_key) {
					System.out.print("Found desired key: " + key + " ");
					block = Functions_misc.readInt(sys_ptrs.buffer, (j*8)+4);		// if the key is what we're searching for, "announce" it and save the block
					System.out.print("in block: " + block + "\n");
					break outer_loop;		// this (and the label in line 147) breaks the outer loop (a single break statement only breaks one, see https://stackoverflow.com/a/886979)
				}
			}
		}
		
		if(block==-1) {				// the block variable is initialized to -1 (see line 142) and if it hasn't changed until here, the key wasn't found (
			System.out.println("The desired key (" + desired_key +") was not found in the pointers file.");
			return;					// if the key wasn't found in the pointers file, we have no reason to go through the records one
		}
		
		sys_recs.ReadBlock(block);
		disk_accesses++;				// increment the disk access counter because we read from the records file
		for(int i=0; i<4; i++) {
			key = Functions_misc.readInt(sys_recs.buffer, i*record_size);
			if(key == desired_key) {	// the block has 4 keys, we need to search through them
				System.out.println("Found desired key " + key + " in desired block in the record file.");
				System.out.println("It took " + disk_accesses + " disk accesses to find it (1 in the records file and " + (disk_accesses-1) + " in the pointers file).");
				return;
			}
		}
		
		// the program should NEVER reach this point, since it exits when it finds the key in the records file
		// It would mean that the information in the pointers file is wrong or that some weird thing has happened to our variables in the memory
		// If it does however reach here, it tells us that something has gone terribly wrong.
		System.out.println("The key was not found in the expected block. That shouldnt happen. Please check the file.");
		return;
	}
}
