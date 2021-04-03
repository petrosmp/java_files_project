package domes_project1;

import java.io.IOException;
import java.util.Scanner;

public class Console {
	public static void main(String[] args) throws IOException {
		
		int DataPageSize = 128;
		
		System.out.println("================================ File Manager Console ================================");
		Scanner in = new Scanner(System.in);
		String filename = in.nextLine();
		/*
		A_tropos file_a = new A_tropos();
		file_a.createFile(filename, 10000, 1000000);
		file_a.readFileForTestingPurposes(filename);
		
		file_a.createSortedFile_A(filename);
		String sorted_filename = filename+"_sorted";
		file_a.readFileForTestingPurposes(sorted_filename);
		for(int i=0; i<4; i++) {
			System.out.println("Enter key to search for: ");
			int des_key = in.nextInt();
			file_a.serialSearch(filename, des_key);
		}
		 */

		B_tropos file_b = new B_tropos();
		file_b.createFile(filename, 10000, 1000000 );

		//file_b.readFilesForTestingPuproses(filename);
		file_b.createSortedFile_B(filename);

		file_b.readSortedFileForTestingPuproses(filename);

		System.out.println("Enter key to search for: ");
		int des_key = in.nextInt();
		//file_b.serialSearch(filename, des_key);
		Functions_misc.binarySearchInFile(filename+"_ptrs_sorted", 8, des_key, file_b.sys_ptrs, 0, (int) file_b.sys_ptrs.file.length()/DataPageSize, 0);
		
		System.out.println("\n\nProgram execution finished.");
		in.close();
		return;
	}
}
