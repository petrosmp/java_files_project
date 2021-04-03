package domes_project1;

import java.io.IOException;
//import java.util.Scanner;
import java.util.Random;

public class Console {
	public static void main(String[] args) throws IOException {
		
		int DataPageSize = 128;
		int key;
		Random rng = new Random();
		System.out.println("================================ File Manager Console ================================");
		
		// test for file type A
		System.out.println("================================ Testing File Type A =================================");
		A_tropos file_a = new A_tropos();
		file_a.createFile("file_A", 10000, 1000000);
		System.out.println("================================ Serial Search Tests =================================");
		for(int i=1; i<=20; i++) {
			key = Functions_misc.getRandomNum(1000000, rng);
			System.out.println("Search #" + i + ":");
			System.out.println("Key: " + key);
			file_a.serialSearch("file_A", key);
		}
		
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// test for file type B
		System.out.print("\n\n\n\n\n");
		System.out.println("================================ Testing File Type B =================================");
		B_tropos file_b = new B_tropos();
		file_b.createFile("file_B", 10000, 1000000);
		System.out.println("================================ Serial Search Tests =================================");
		for(int i=1; i<=20; i++) {
			key = Functions_misc.getRandomNum(1000000, rng);
			System.out.println("Search #" + i + ":");
			System.out.println("Key: " + key);
			file_b.serialSearch("file_B", key);
		}
				
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// test for file type C
		System.out.print("\n\n\n\n\n");
		System.out.println("================================ Testing File Type C =================================");
		file_a.createSortedFile_A("file_A");
		System.out.println("================================ Binary Search Tests =================================");
		for(int i=1; i<=20; i++) {
			key = Functions_misc.getRandomNum(1000000, rng);
			System.out.println("Search #" + i + ":");
			System.out.println("Key: " + key);
			Functions_misc.binarySearchInFile("file_A_sorted", 32, key, file_a.sys, 0, (int) file_a.sys.file.length()/DataPageSize, 0);
		}
		
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// test for file type D
		System.out.print("\n\n\n\n\n");
		System.out.println("================================ Testing File Type D =================================");
		file_b.createSortedFile_B("file_B");
		System.out.println("================================ Binary Search Tests =================================");
		for(int i=1; i<=20; i++) {
			key = Functions_misc.getRandomNum(1000000, rng);
			System.out.println("Search #" + i + ":");
			System.out.println("Key: " + key);
			Functions_misc.binarySearchInFile("file_B_ptrs_sorted", 8, key, file_b.sys_ptrs, 0, (int) file_b.sys_ptrs.file.length()/DataPageSize, 0);
		}
		
	
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
		 

		B_tropos file_b = new B_tropos();
		file_b.createFile(filename, 10000, 1000000 );

		//file_b.readFilesForTestingPuproses(filename);
		file_b.createSortedFile_B(filename);

		file_b.readSortedFileForTestingPuproses(filename);

		System.out.println("Enter key to search for: ");
		int des_key = in.nextInt();
		//file_b.serialSearch(filename, des_key);
		Functions_misc.binarySearchInFile(filename+"_ptrs_sorted", 8, des_key, file_b.sys_ptrs, 0, (int) file_b.sys_ptrs.file.length()/DataPageSize, 0);
		*/
		
		
		
		System.out.println("\n\nProgram execution finished.");
		//in.close();
		return;
	}
}
