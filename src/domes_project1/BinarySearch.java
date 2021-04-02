package domes_project1;

/**
 * A class implementing the binary search algorithm.
 * Keys can have values from Integer.MIN_VALUE + 1 to Integer.MAX_VALUE, so Integer.MIN_VALUE itself is not a valid key in the array.
 * Based on https://www.geeksforgeeks.org/binary-search/
 * @author sk
 *
 */
public class BinarySearch {
	private int data[];

	/**
	 * Constructor. Given newData must be sorted!
	 * @param newData
	 */
	public BinarySearch(int newData[]) {
		this.data = newData;
	}
	
	/**
	 * Given newData must be sorted!
	 * @param newData
	 */
	public void setData(int newData[]) {
		this.data = newData;
	}
	
	/**
	 * Searches data array for given key. Returns the key if found, otherwise Integer.MIN_VALUE
	 * @param key
	 * @return
	 */
	public int search(int key) {
		if (data == null) {
			return Integer.MIN_VALUE;
		}
		return doSearch(0, data.length - 1, key);
	}	
	
	/**
	 * Searches data array for given key. Returns the key if found, otherwise Integer.MIN_VALUE
	 * @param leftIndex
	 * @param rightIndex
	 * @param key
	 * @return key if found or Integer.MIN_VALUE otherwise
	 */
    private int doSearch(int leftIndex, int rightIndex, int key) 
    { 
        if (rightIndex >= leftIndex) { 
            int mid = leftIndex + (rightIndex - leftIndex) / 2; 
  
            // If the element is present at the 
            // middle itself 
            if (data[mid] == key) 
                return mid; 
  
            // If element is smaller than mid, then 
            // it can only be present in left subarray 
            if (data[mid] > key) 
                return doSearch(leftIndex, mid - 1, key); 
  
            // Else the element can only be present 
            // in right subarray 
            return doSearch(mid + 1, rightIndex, key); 
        } 
  
        // We reach here when element is not present in array. 
        // We return Integer.MIN_VALUE in this case, so the data array can not contain this value!
        // TODO add this in porject essay
        // this is changed to return -1 if failed
        return -1; 
    } 
}
