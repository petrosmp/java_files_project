package domes_project1;

import java.io.IOException;
import java.util.Scanner;

public class Console {
	public static void main(String[] args) throws IOException {
		System.out.println("================================ File Manager Console ================================");
		Scanner in = new Scanner(System.in);
		String filename = in.nextLine();
		
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
		/*
		B_tropos file_b = new B_tropos();
		file_b.createFile(filename, 10000, 1000000 );
		file_b.readFilesForTestingPuproses(filename);
		System.out.println("Enter key to search for: ");
		int des_key = in.nextInt();
		file_b.serialSearch(filename, des_key);
		*/
		System.out.println("\n\nProgram execution finished.");
		in.close();
		return;
	}
}
