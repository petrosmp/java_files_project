package domes_project1;

import java.util.Random;
public class Functions_misc {

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
	 
}
