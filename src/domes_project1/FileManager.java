package domes_project1;
import java.io.*;

public class FileManager {
	
	RandomAccessFile file = null;
	byte [] buffer = null;
	
	private static final int DataPageSize = 128; // Default Data Page size
	private static final int max_filename_size = 35;
	
	
	public int FileHandle(String filename) throws IOException {
		this.OpenFile(filename);
		if(file == null) {
			System.out.println("File is null! Error occured in FileHandle()");
			return 0;
		}
		System.out.println("================== File info ==================");
		System.out.println("File name: " + filename);
		System.out.println("File size (in bytes) " + file.length());
		System.out.println("Pages in file: " + file.length()/DataPageSize);
		System.out.println("Current position of cursor: " + file.getFilePointer());
		//byte[] ReadDataPage = new byte[DataPageSize];
		//ByteArrayInputStream bis= new ByteArrayInputStream(ReadDataPage);
        //DataInputStream din = new DataInputStream(bis);
        
        
	
        return 0;
	}
	
	public int CreateFile(String filename) throws IOException {
		file = new RandomAccessFile(filename, "rw");
		ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
    	DataOutputStream dos = new DataOutputStream(bos);

		///////
		
		// put the bytes of filename in a byte array of size max_filename_size and put them in the buffer
		byte[] bfilename = filename.getBytes();
		byte[] fnBuf = new byte[max_filename_size];
		System.arraycopy(bfilename, 0, fnBuf, 0, bfilename.length);
		dos.write(fnBuf);
		
		// next we need to write the file size
		int filesize = (int)file.length() / 128;		// get the number of pages
		System.out.println("File size: " + filesize);
		dos.writeInt(filesize);
		
		// close DataOutputStream (we're done)
		dos.close();
		
		// write the contents of the bos in a page-sized byte array
		byte[] firstPage = new byte[DataPageSize];
		byte[] buffer1 = bos.toByteArray();
		System.arraycopy(buffer1, 0, firstPage, 0, buffer1.length);
		
		// close ByteArrayOutputStream (we're done)
		bos.close();
		
		// write the page to the file
		file.seek(0);
		file.write(firstPage);
		
		
		///////
		return 0;
	}
	
	public int OpenFile(String filename) throws IOException {
		try{
			file = new RandomAccessFile(filename, "rw");
		}
		catch (FileNotFoundException e){
			//TODO 
			System.out.println("kys");
			return -1;
		}
		return (int) (file.length()/128);
	}
	
	public int ReadBlock(int pos) throws IOException {
		// check if file is null
		if(file == null) {
			System.out.println("The file is null (error occured in ReadBlock)");
			return 0;
		}
		// check if file is smaller than the position specified
        int offset = DataPageSize*pos;
		if(file.length()<=offset) {
			System.out.println("The file does not have a " + pos + " page! (error occured in ReadBlock)");
			return 0;
		}
		// if none of the above are true, do the following
		byte[] ReadDataPage = new byte[DataPageSize];
        file.seek(offset);
        file.read(ReadDataPage);
        ByteArrayInputStream bis = new ByteArrayInputStream(ReadDataPage);
        DataInputStream din = new DataInputStream(bis);
        byte[] readBuf = new byte[DataPageSize];
        try {
        	din.read(readBuf, 0, DataPageSize);
        }catch(IndexOutOfBoundsException e) {
        	System.out.println("File is not properly formated. Last page is less than 128 bytes, couldn't be parsed.");
        	System.out.println("Error occured in readBlock(" + pos + ")");
        	return 0;
        }
        this.buffer = readBuf;
		return 0;
	}

	public int ReadNextBlock(int pos) throws IOException {
		int nextBlockPos = pos + 1;
		// check if file is null (might not be necessary because ReadBlock() does that)
		if(file == null) {
			System.out.println("The file is null (error occured in ReadNextBlock)");
			return 0;
		}
		// check if file is big enough to have a next block
		if(file.length()<= nextBlockPos*DataPageSize) {
			System.out.println("The file does not have a " + nextBlockPos + " page! (error occured in ReadNextBlock)");
			return 0;
		}
		this.ReadBlock(nextBlockPos);
		return 0;
	}
	
	public int WriteBlock(int pos) throws IOException {
		
		// check if file is null
		if(file == null) {
			System.out.println("The file is null (error occured in WriteBlock)");
			return 0;
		}
		// check if buffer has things in it
		if(buffer == null) {
			System.out.println("The buffer is empty (error occured in WriteBlock)");
			return 0;
		}
		// if file is bigger than offset, maybe call append block (?) which probably calls write block too though idk TODO
		
		int offset = pos*DataPageSize;
		ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
    	DataOutputStream out = new DataOutputStream(bos);
    	// TODO edit
    	byte[] writeBuffer = new byte[DataPageSize];
    	byte[] writeBuffer1 = new byte[DataPageSize];
    	out.write(buffer);
    	
    	
    	writeBuffer1 = bos.toByteArray();
    	System.arraycopy(writeBuffer1, 0, writeBuffer, 0, writeBuffer1.length);
    	file.seek(offset);
    	file.write(this.buffer);
    	out.flush();
    	out.close();
    	bos.flush();
    	bos.close();
    	Functions_misc.emptyArray(this.buffer);
		return 0;
	}
	
	public int WriteNextBlock(int pos) throws IOException {
		
		// check if file is null (might not be necessary because WriteBlock() does that)
		int nextBlockPosition = pos+1;
		this.WriteBlock(nextBlockPosition);
		return 0;
	}
	
	public int AppendBlock() throws IOException {
		
		// check if file is null
		if(file == null) {
			System.out.println("The file is null (error occured in AppendBlock)");
			return 0;
		}
		// add check if filesize isnt a product of DataPageSize, and fix it

		int fileSize = (int) file.length();
		fileSize+=DataPageSize;
		file.setLength(fileSize); // create space for 1 more block
		int lastBlock = fileSize/DataPageSize - 1;
		this.WriteBlock(lastBlock);
		return 0;
	}
	
	public int DeleteBlock(int pos) throws IOException {
		if(file == null) {
			System.out.println("The file is null (error occured at DeleteBlock");
			return 0;
		}
		// get file size
		int fileSize = (int) file.length();
		int lastBlockOffset = pos * DataPageSize; // this calulation is stupid, be samart and do it with lastBlock
		if(fileSize<lastBlockOffset) {
			System.out.println("The file does not have a block " + pos + " (error occured in DeleteBlock");
			return 0;
		}
		int lastBlock = (fileSize / DataPageSize) - 1;
		// read last block in a buffer
		ReadBlock(lastBlock);
		// save buffer in block pos
		WriteBlock(pos);		// TODO edit writeblock() to write from the buffer field and not an uninitialized fucking byte array
		// use file.setLength() to truncate file
		file.setLength(fileSize-DataPageSize);
		return 0;
	}
	
	public int CloseFile(String filename) {
		// check if file is NULL
		if(file == null) {
			System.out.println("The file is null (error occured in CloseFile)");
			return 0;
		}
		// check if file is anything weird (?)
		// write fileinfo (filehandle to file (TODO)
		try{
			file.close();
			file=null;
		}
		catch (IOException e){
			System.out.println("Something went wrong while closing the file. Please check that everything was run as it was supposed to");
			return -1;
		}
		return 1;
	}
	
	
	// dikes mou	
	public void PrintBufferAsString() {
		if(buffer == null) {
			System.out.println("Buffer is empty! Error occured in printBuffer");
			return;
		}
		String bufStr = new String(this.buffer);
		System.out.println("=================================== Buffer As String ==================================");
		System.out.print(bufStr);
	}
	
	public int writeStringToBuffer(String str, int offset) {
		if(str.getBytes().length>DataPageSize) {
			System.out.println("The string is too long! Try again with a smaller one. Error occured in writeStringToBuffer");
			return 0;
		}
		/*
		if(this.buffer!=null) {
			System.out.println("Buffer is not empty! It will be overwritten! Message by writeStringToBuffer");
		}
		*/
		if(offset<0) {
			System.out.println("Offset cant be negative. writeStringToBuffer() aborted.");
			return 0;
		}
		if(offset+str.length()>DataPageSize) {
			System.out.println("String is too long to fit in the buffer at that offset. writeStringToBuffer() aborted.");
			return 0;
		}
		byte[] middleman = str.getBytes();
		System.arraycopy(middleman, 0, this.buffer, offset, middleman.length);
		return 1;
	}
	
	public int writeIntToBuffer(int i, int offset) {
		/*
		if(this.buffer!=null) {
			System.out.println("Buffer is not empty, it will be overwritten! Message by writeIntToBuffer");
		}
		*/
		if(this.buffer == null) {
			this.buffer = new byte[DataPageSize];
			System.out.println("Buffer initialized in writeIntToBuffer().");
		}
		Functions_misc.writeInt(this.buffer, offset, i);
		return 0;
		
	}
	
	public int WriteIntToBlock(int pos, int toWrite) throws IOException {
		
		// check if file is null
		if(file == null) {
			System.out.println("The file is null (error occured in WriteBlock)");
			return 0;
		}
		
		
		int offset = pos*DataPageSize;
		ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
    	DataOutputStream out = new DataOutputStream(bos);

    	byte[] writeBuffer = new byte[DataPageSize];
    	out.writeInt(toWrite);
    	
    	
    	byte[] writeBuffer1 = bos.toByteArray();
    	System.out.println("ByteArrayOutputStream contents:");
    	System.out.println(bos.toString());
    	System.out.println("End of bos contents.");
    	System.arraycopy(writeBuffer1, 0, writeBuffer, 0, writeBuffer1.length);
    	file.seek(offset);
    	file.write(writeBuffer);
    	out.flush();
    	out.close();
    	bos.flush();
    	bos.close();
		return 0;
	}
	
	public int printFileContents() throws IOException{
		if(file==null) {
			System.out.println("The file is null (error occured in printFileContents)");
			return 0;
		}
		if(file.length()<=DataPageSize) {
			System.out.println("The file has less than 1 page and could not be read correctly");
			return 0;
		}
		int pages = (int) file.length()/DataPageSize;
		for(int i=0; i<pages; i++) {
			System.out.println("Page #" + i);
			this.ReadBlock(i);
			this.PrintBufferAsString();
			System.out.print("<-ends here\n");
		}
		return 1;
	}
		
}
