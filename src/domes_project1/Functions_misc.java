package domes_project1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;



public class Functions_misc {

	static int DataPageSize = 128;
	static int record_size = 32;
	
	static void writeInt(byte[] arr, int offset, int data){
	     // write the binary representation of an integer in 4 bytes at arr[offset]
	     int k = 0;
	     for(int i = offset; i < arr.length; i++){
	    	 arr[i] = (byte)(data >>> (24-8*k)); 
	     	k++;
	     	if(k == 4) {
	     		break;
	     	}
	     }
	 }
	 
	static int readInt(byte[] arr, int offset){
		// interpret 4 bytes at arr[offset] as an integer
		int data = 0;
		int k = 0;
		for(int i = offset; i < arr.length; i++){
			data |= ( arr[i] & 0xFF) << (24-8*k); 
			k++;
			if(k == 4) {
				break;
			}
		}
		return data;     
	 }
	 
	static int getRandomNum(int max, Random rng) {
		// generate a random number
	    int random = rng.nextInt(max);
	    return random;
	}
	 
	static byte[] getRandomData(int length, Random rng) {
		// generate a random byte array of fixed length
		byte[] data = new byte[length];
		rng.nextBytes(data);
		return data;
	}

	static void emptyArray(byte[] array) {
		// "empty" a byte array (make all of its elements 0)
		if(array==null) {
			System.out.println("emptyArray() was called on a null array! Array will remain null and of unknown size");
			return;
		}
		for(int i=0; i<array.length; i++) {
			array[i] = 0;
		}
		return;
	}
	
	static ArrayList<Record> sortRecordsA(ArrayList<Record> records){
		/*
		 * Sort an ArrayList of records by ascending order of key 
		 * (record = 4 bytes key and 28 bytes data)
		 */
		// initialize variables
		int[] keys = new int[records.size()];
		ArrayList<Record> sorted = new ArrayList<Record>();
		int key;
		byte[] data;
		// get all the keys in an array
		for(int i=0; i<records.size(); i++) {
			keys[i] = records.get(i).getKey();
		}
		// sort the array of keys
		Arrays.sort(keys);
		// iterate over the arrays of keys, and add the record corresponding to each key to the sorted ArrayList
		for(int i=0; i<keys.length; i++) {
			key = keys[i];									// get the key
			for(int j=0; j<records.size(); j++) {			// iterate over the records array (the unsorted one)
				if(records.get(j).getKey() == key) {		// when the record with the wanted key is found
					data = records.get(j).getData();		// get the data of the record with the wanted key
					Record rec = new Record(key, data);		// create a record with the key, data
					sorted.add(rec);						// add the new record to the sorted array (might as well add the old one, no need to create a new one)
					records.remove(j);						// remove the record from the unsorted array so that the search takes less time each iteration (see line 85)
					break;
				}
			}
		}
		return sorted;
	}
	
	static ArrayList<Record> sortRecordsB(ArrayList<Record> records){
		/*
		 * Sort an ArrayList of records by ascending order of key 
		 * (record = 4 bytes key and 4 bytes data)
		 */
		// initialize variables
		int[] keys = new int[records.size()];
		ArrayList<Record> sorted = new ArrayList<Record>();
		int key, block_ptr;
		// get all the keys in an array
		for(int i=0; i<records.size(); i++) {
			keys[i] = records.get(i).getKey();
		}
		// sort the array of keys
		Arrays.sort(keys);
		// iterate over the array of keys and add the records to the new, sorted array in the correct order
		// the process is the same as in sortRecordsA() but with Records that have a key, a block_ptr and no data
		int records_size = records.size();
		for(int i=0; i<keys.length; i++) {
			key = keys[i];
			for(int j=0; j<records_size; j++) {
				if(records.get(j).getKey() == key) {
					block_ptr = records.get(j).getBlock_ptr();
					Record rec = new Record(key, block_ptr);
					sorted.add(i, rec);
					records.remove(j);
					break;
				}
			}
		}
		return sorted;
	}

	static void binarySearchInFile(String filename, int rec_size, int desired_key, FileManager sys, int left_index, int right_index, int disk_accesses) throws IOException {
		/*
		File MUST be SORTED for this to work!
		*/
		// open files and initialize variables
		sys.OpenFile(filename);
		int recs_per_block = DataPageSize/rec_size;
		// verbose things for debbuging TODO remove
			//System.out.println("\nbinarySearch called with:");
			//System.out.println("Left index is block " + left_index);
			//System.out.println("Right index is block " + right_index);
			//System.out.println("Disk accesses: " + disk_accesses);
		
		// binarySearch implementation based off the org.tuc.binarysearcharray.recursive package, binarySearch class (also included in this project)
		// Necessary modifications were made so that the algorithm searches for the correct block in a binary fashion
		// and then searches that block for the key in a binary fashion
		
		if (right_index >= left_index) {
            int mid_block = left_index + (right_index - left_index) / 2; 	// start at the middle block of the file (if the number isn't even, it's floor(mid))
            sys.ReadBlock(mid_block);										// read the middle block
    		disk_accesses++;												// increment the disk access counter
    		// verbose things for debugging TODO remove
    			//System.out.println("Smallest key in block " + mid_block + ": " + readInt(sys.buffer, 0));
    			//System.out.println("Largest key in block " + mid_block + ": " + readInt(sys.buffer, (recs_per_block-1)*rec_size));
    		
    		// since the block is also sorted, the smallest key in it is the first one. That means that if the desired_key is smaller than that, we need to search the left sub-file
    		if(desired_key<readInt(sys.buffer, 0)) {						// check if the desired key is smaller than the smallest key in the block
    			// binary search the left sub-file
    			// left index = left index
    			// right index = mid_block-1
            	binarySearchInFile(filename, rec_size, desired_key, sys, left_index, mid_block-1, disk_accesses);
    			return;
    		}
    		// since the block is also sorted, the largest key in it is the last one. That means that if the desired_key is greater than that, we need to search the right sub-file
    		if(desired_key>readInt(sys.buffer, (recs_per_block-1)*rec_size)) {		// check if the desired key is greater than the largest key in the block
    			// binary search the right sub-file
    			// left index = mid_block+1
    			// right index = right index
            	binarySearchInFile(filename, rec_size, desired_key, sys, mid_block+1, right_index, disk_accesses);
    			return;
    		}
    		
    		// if the program reaches here it means that the desired key is in the range of the keys in the current block
    		// so it is either here or nowhere at all
    		// this will normally only be executed once, since the desired key can only be in range of 1 block (because the keys are unique)
    		
    		// load all the keys of the block in an array
    		int [] keys_in_block = new int[recs_per_block];
    		for(int i=0; i<recs_per_block; i++) {
    			keys_in_block[i] = readInt(sys.buffer, i*rec_size);
    		}
    		
    		// instantiate the binarySearch class (org.tuc.binarysearcharray.recursive.BinarySearch)
    		BinarySearch bs = new BinarySearch(keys_in_block);
    		// perform a binary search on the array
    		int found_key = bs.search(desired_key);
    		
    		if(found_key==Integer.MIN_VALUE) {
    			// if binarySearch returns Integer.MIN_VALUE it means that the key wasn't found.
    			// "announce" it and exit
    			System.out.println("Key not found after " + disk_accesses + " disk accesses");
    		}else {
    			// if binarySearch returns anything that is not Integer.MIN_VALUE, it means that the key has been found
    			// in that case, the key is returned.
    			// "announce" it and exit
    			System.out.println("Key " + found_key + " found after " + disk_accesses + " disk accesses");
    		}
    		return;
        }
		// the program reaches here when the binarySearch that searches for the right block doesn't find a block that could contain the key
		// that means that the key is somewhere between the largest value of the left block and the smaller value of the right one
			//System.out.println("Left index is the same or more than the right index");
			//System.out.println("This means that the desired key wasn't found, but in a special way");
			System.out.println("The key was between the greatest value of one block and the smallest value of the next!");
			//System.out.println("Left index is block " + left_index);
			//System.out.println("Right index is block " + right_index);
			System.out.println("Disk accesses: " + disk_accesses);
		return;
		
	}
}
