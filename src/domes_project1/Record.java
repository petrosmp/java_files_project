package domes_project1;


public class Record {

	int record_size = 32;
	public int key;
	public int block_ptr;
	public byte[] data;
	
	Record(int key, byte[] data){
		this.key = key;
		this.data = data;
	}
	
	Record(int key, int block_ptr){
		this.key = key;
		this.block_ptr = block_ptr;
	}
	
	public int getBlock_ptr() {
		return block_ptr;
	}

	public void setBlock_ptr(int block_ptr) {
		this.block_ptr = block_ptr;
	}

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
