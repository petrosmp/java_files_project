package domes_project1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;



public class Functions_misc {

	static int DataPageSize = 128;
	static int record_size = 32;
	
	static void writeInt(byte[] arr, int offset, int data){
	     //Writes an int in a byte array
	     int k = 0;
	     for(int i = offset; i < arr.length; i++){
	    	 arr[i] = (byte)(data >>> (24-8*k)); 
	     	k += 1;
	     	if(k == 4) break;
	     }
	 }
	 
	static int readInt(byte[] arr, int offset){
		 int data = 0;
		 int k = 0;
		 for(int i = offset; i < arr.length; i++){
			 data |= ( arr[i] & 0xFF) << (24-8*k); 
			 k += 1;
			 if(k == 4) break;
		 }
		 return data;     
	 }
	 
	static int getRandomNum(int max, Random rng) {
			// very bad way to do this, slow asf, to be mentioned in the report essay TODO
	    	int random = rng.nextInt(1000000);
	    	return random;
	}
	 
	static byte[] getRandomData(int length, Random rng) {
		byte[] data = new byte[length];
		rng.nextBytes(data);
		return data;
	}

	static void emptyArray(byte[] array) {
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
		int[] keys = new int[records.size()];
		ArrayList<Record> sorted = new ArrayList<Record>();
		int key;
		byte[] data;
		for(int i=0; i<records.size(); i++) {
			keys[i] = records.get(i).getKey();
		}
		Arrays.sort(keys);
		for(int i=0; i<keys.length; i++) {
			key = keys[i];
			for(int j=0; j<records.size(); j++) {
				if(records.get(j).getKey() == key) {
					data = records.get(j).getData();
					Record rec = new Record(key, data);
					sorted.add(rec);
					records.remove(j);
					break;
				}
			}
		}
		return sorted;
	}
	
	static ArrayList<Record> sortRecordsB(ArrayList<Record> records){
		int[] keys = new int[records.size()];
		ArrayList<Record> sorted = new ArrayList<Record>();
		int key, block_ptr;
		for(int i=0; i<records.size(); i++) {
			keys[i] = records.get(i).getKey();
		}
		Arrays.sort(keys);
		for(int i=0; i<keys.length; i++) {
			key = keys[i];
			for(int j=0; j<records.size(); j++) {
				if(records.get(j).getKey() == key) {
					block_ptr = records.get(j).getBlock_ptr();
					Record rec = new Record(key, block_ptr);
					sorted.add(rec);
					records.remove(j);
					break;
				}
			}
		}
		return sorted;
	}
		
	static void addRecordToArray(Record[] array, Record rec, int index) {
		array[index] = rec;
	}
}
