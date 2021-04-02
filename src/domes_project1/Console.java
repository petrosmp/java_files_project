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
		System.out.println("Enter key to search for: ");
		int des_key = in.nextInt();
		file_a.serialSearch(filename, des_key);
		in.close();
		return;
	}
}
