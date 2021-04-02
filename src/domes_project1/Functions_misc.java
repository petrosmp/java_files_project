package domes_project1;

import java.io.IOException;
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

	
	static ArrayList<Record> sortRecords(ArrayList<Record> records){
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
		int[] keys_array = new int[unsortedkeys.size()];
	    
		for (int i=0; i < keys_array.length; i++){
	        keys_array[i] = unsortedkeys.get(i).intValue();
	    }
		
		Arrays.sort(keys_array);
		for(int i=0; i<keys_array.length; i++) {
			data = records.get(i).getData();
			dst_sys.writeIntToBuffer(i, record_size*counter);
	    	
	    	System.arraycopy(data, 0, dst_sys.buffer, record_size*counter+4, data.length);
	    	
	    	if(counter==3) {
		    	dst_sys.WriteBlock(block_num);
	    		block_num++;
		    	counter=0;
	    	}else {
	    		counter++;
	    	}		
	    }
		
	}
	
	static void addRecordToArray(Record[] array, Record rec, int index) {
		array[index] = rec;
	}
}
