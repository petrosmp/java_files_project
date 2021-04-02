package domes_project1;


public class Record {

	int record_size = 32;
	public int key;
	public byte[] data;
	
	public int getKey() {
		return key;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void setKey(int key) {
		this.key = key;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	
	
	
	
}
