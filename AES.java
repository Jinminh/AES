import java.lang.*;
import java.io.*;
import java.util.*;

/**
 * Abstract extension of {@code Crypto}.
 */
public class AES extends Crypto {
	/**
	 * Global variables to be use later.
	 */
	static int [][] keyExpansionArray = new int [4][60];
	static char mode = 0;
	static String keyFileName = null;
	static String inputFileName = null;
	static String key = "";
	static String inputText = "";
	static String contentForWrite = "";
	static Set<String> validCharacterSet = new HashSet<String>();
	static final char eTable[] = {
		0x01, 0x03, 0x05, 0x0F, 0x11, 0x33, 0x55, 0xFF, 0x1A, 0x2E, 0x72, 0x96, 0xA1, 0xF8, 0x13, 0x35,
		0x5F, 0xE1, 0x38, 0x48, 0xD8, 0x73, 0x95, 0xA4, 0xF7, 0x02, 0x06, 0x0A, 0x1E, 0x22, 0x66, 0xAA,
		0xE5, 0x34, 0x5C, 0xE4, 0x37, 0x59, 0xEB, 0x26, 0x6A, 0xBE, 0xD9, 0x70, 0x90, 0xAB, 0xE6, 0x31,
		0x53, 0xF5, 0x04, 0x0C, 0x14, 0x3C, 0x44, 0xCC, 0x4F, 0xD1, 0x68, 0xB8, 0xD3, 0x6E, 0xB2, 0xCD,
		0x4C, 0xD4, 0x67, 0xA9, 0xE0, 0x3B, 0x4D, 0xD7, 0x62, 0xA6, 0xF1, 0x08, 0x18, 0x28, 0x78, 0x88,
		0x83, 0x9E, 0xB9, 0xD0, 0x6B, 0xBD, 0xDC, 0x7F, 0x81, 0x98, 0xB3, 0xCE, 0x49, 0xDB, 0x76, 0x9A,
		0xB5, 0xC4, 0x57, 0xF9, 0x10, 0x30, 0x50, 0xF0, 0x0B, 0x1D, 0x27, 0x69, 0xBB, 0xD6, 0x61, 0xA3,
		0xFE, 0x19, 0x2B, 0x7D, 0x87, 0x92, 0xAD, 0xEC, 0x2F, 0x71, 0x93, 0xAE, 0xE9, 0x20, 0x60, 0xA0,
		0xFB, 0x16, 0x3A, 0x4E, 0xD2, 0x6D, 0xB7, 0xC2, 0x5D, 0xE7, 0x32, 0x56, 0xFA, 0x15, 0x3F, 0x41,
		0xC3, 0x5E, 0xE2, 0x3D, 0x47, 0xC9, 0x40, 0xC0, 0x5B, 0xED, 0x2C, 0x74, 0x9C, 0xBF, 0xDA, 0x75,
		0x9F, 0xBA, 0xD5, 0x64, 0xAC, 0xEF, 0x2A, 0x7E, 0x82, 0x9D, 0xBC, 0xDF, 0x7A, 0x8E, 0x89, 0x80,
		0x9B, 0xB6, 0xC1, 0x58, 0xE8, 0x23, 0x65, 0xAF, 0xEA, 0x25, 0x6F, 0xB1, 0xC8, 0x43, 0xC5, 0x54,
		0xFC, 0x1F, 0x21, 0x63, 0xA5, 0xF4, 0x07, 0x09, 0x1B, 0x2D, 0x77, 0x99, 0xB0, 0xCB, 0x46, 0xCA,
		0x45, 0xCF, 0x4A, 0xDE, 0x79, 0x8B, 0x86, 0x91, 0xA8, 0xE3, 0x3E, 0x42, 0xC6, 0x51, 0xF3, 0x0E,
		0x12, 0x36, 0x5A, 0xEE, 0x29, 0x7B, 0x8D, 0x8C, 0x8F, 0x8A, 0x85, 0x94, 0xA7, 0xF2, 0x0D, 0x17,
		0x39, 0x4B, 0xDD, 0x7C, 0x84, 0x97, 0xA2, 0xFD, 0x1C, 0x24, 0x6C, 0xB4, 0xC7, 0x52, 0xF6, 0x01
	};
	static final char lTable[] = {
		0x00, 0x00, 0x19, 0x01, 0x32, 0x02, 0x1A, 0xC6, 0x4B, 0xC7, 0x1B, 0x68, 0x33, 0xEE, 0xDF, 0x03,
		0x64, 0x04, 0xE0, 0x0E, 0x34, 0x8D, 0x81, 0xEF, 0x4C, 0x71, 0x08, 0xC8, 0xF8, 0x69, 0x1C, 0xC1,
		0x7D, 0xC2, 0x1D, 0xB5, 0xF9, 0xB9, 0x27, 0x6A, 0x4D, 0xE4, 0xA6, 0x72, 0x9A, 0xC9, 0x09, 0x78,
		0x65, 0x2F, 0x8A, 0x05, 0x21, 0x0F, 0xE1, 0x24, 0x12, 0xF0, 0x82, 0x45, 0x35, 0x93, 0xDA, 0x8E,
		0x96, 0x8F, 0xDB, 0xBD, 0x36, 0xD0, 0xCE, 0x94, 0x13, 0x5C, 0xD2, 0xF1, 0x40, 0x46, 0x83, 0x38,
		0x66, 0xDD, 0xFD, 0x30, 0xBF, 0x06, 0x8B, 0x62, 0xB3, 0x25, 0xE2, 0x98, 0x22, 0x88, 0x91, 0x10,
		0x7E, 0x6E, 0x48, 0xC3, 0xA3, 0xB6, 0x1E, 0x42, 0x3A, 0x6B, 0x28, 0x54, 0xFA, 0x85, 0x3D, 0xBA,
		0x2B, 0x79, 0x0A, 0x15, 0x9B, 0x9F, 0x5E, 0xCA, 0x4E, 0xD4, 0xAC, 0xE5, 0xF3, 0x73, 0xA7, 0x57,
		0xAF, 0x58, 0xA8, 0x50, 0xF4, 0xEA, 0xD6, 0x74, 0x4F, 0xAE, 0xE9, 0xD5, 0xE7, 0xE6, 0xAD, 0xE8,
		0x2C, 0xD7, 0x75, 0x7A, 0xEB, 0x16, 0x0B, 0xF5, 0x59, 0xCB, 0x5F, 0xB0, 0x9C, 0xA9, 0x51, 0xA0,
		0x7F, 0x0C, 0xF6, 0x6F, 0x17, 0xC4, 0x49, 0xEC, 0xD8, 0x43, 0x1F, 0x2D, 0xA4, 0x76, 0x7B, 0xB7,
		0xCC, 0xBB, 0x3E, 0x5A, 0xFB, 0x60, 0xB1, 0x86, 0x3B, 0x52, 0xA1, 0x6C, 0xAA, 0x55, 0x29, 0x9D,
		0x97, 0xB2, 0x87, 0x90, 0x61, 0xBE, 0xDC, 0xFC, 0xBC, 0x95, 0xCF, 0xCD, 0x37, 0x3F, 0x5B, 0xD1,
		0x53, 0x39, 0x84, 0x3C, 0x41, 0xA2, 0x6D, 0x47, 0x14, 0x2A, 0x9E, 0x5D, 0x56, 0xF2, 0xD3, 0xAB,
		0x44, 0x11, 0x92, 0xD9, 0x23, 0x20, 0x2E, 0x89, 0xB4, 0x7C, 0xB8, 0x26, 0x77, 0x99, 0xE3, 0xA5,
		0x67, 0x4A, 0xED, 0xDE, 0xC5, 0x31, 0xFE, 0x18, 0x0D, 0x63, 0x8C, 0x80, 0xC0, 0xF7, 0x70, 0x07
	};
	static final char forward[] = {
		0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76,
		0xCA, 0x82, 0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C, 0xA4, 0x72, 0xC0,
		0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC, 0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15,
		0x04, 0xC7, 0x23, 0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27, 0xB2, 0x75,
		0x09, 0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52, 0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84,
		0x53, 0xD1, 0x00, 0xED, 0x20, 0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58, 0xCF,
		0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33, 0x85, 0x45, 0xF9, 0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8,
		0x51, 0xA3, 0x40, 0x8F, 0x92, 0x9D, 0x38, 0xF5, 0xBC, 0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2,
		0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4, 0xA7, 0x7E, 0x3D, 0x64, 0x5D, 0x19, 0x73,
		0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A, 0x90, 0x88, 0x46, 0xEE, 0xB8, 0x14, 0xDE, 0x5E, 0x0B, 0xDB,
		0xE0, 0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62, 0x91, 0x95, 0xE4, 0x79,
		0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E, 0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE, 0x08,
		0xBA, 0x78, 0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B, 0xBD, 0x8B, 0x8A,
		0x70, 0x3E, 0xB5, 0x66, 0x48, 0x03, 0xF6, 0x0E, 0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E,
		0xE1, 0xF8, 0x98, 0x11, 0x69, 0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55, 0x28, 0xDF,
		0x8C, 0xA1, 0x89, 0x0D, 0xBF, 0xE6, 0x42, 0x68, 0x41, 0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16
	};
	static final char inverse[] = {
		0x52, 0x09, 0x6A, 0xD5, 0x30, 0x36, 0xA5, 0x38, 0xBF, 0x40, 0xA3, 0x9E, 0x81, 0xF3, 0xD7, 0xFB,
		0x7C, 0xE3, 0x39, 0x82, 0x9B, 0x2F, 0xFF, 0x87, 0x34, 0x8E, 0x43, 0x44, 0xC4, 0xDE, 0xE9, 0xCB,
		0x54, 0x7B, 0x94, 0x32, 0xA6, 0xC2, 0x23, 0x3D, 0xEE, 0x4C, 0x95, 0x0B, 0x42, 0xFA, 0xC3, 0x4E,
		0x08, 0x2E, 0xA1, 0x66, 0x28, 0xD9, 0x24, 0xB2, 0x76, 0x5B, 0xA2, 0x49, 0x6D, 0x8B, 0xD1, 0x25,
		0x72, 0xF8, 0xF6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xD4, 0xA4, 0x5C, 0xCC, 0x5D, 0x65, 0xB6, 0x92,
		0x6C, 0x70, 0x48, 0x50, 0xFD, 0xED, 0xB9, 0xDA, 0x5E, 0x15, 0x46, 0x57, 0xA7, 0x8D, 0x9D, 0x84,
		0x90, 0xD8, 0xAB, 0x00, 0x8C, 0xBC, 0xD3, 0x0A, 0xF7, 0xE4, 0x58, 0x05, 0xB8, 0xB3, 0x45, 0x06,
		0xD0, 0x2C, 0x1E, 0x8F, 0xCA, 0x3F, 0x0F, 0x02, 0xC1, 0xAF, 0xBD, 0x03, 0x01, 0x13, 0x8A, 0x6B,
		0x3A, 0x91, 0x11, 0x41, 0x4F, 0x67, 0xDC, 0xEA, 0x97, 0xF2, 0xCF, 0xCE, 0xF0, 0xB4, 0xE6, 0x73,
		0x96, 0xAC, 0x74, 0x22, 0xE7, 0xAD, 0x35, 0x85, 0xE2, 0xF9, 0x37, 0xE8, 0x1C, 0x75, 0xDF, 0x6E,
		0x47, 0xF1, 0x1A, 0x71, 0x1D, 0x29, 0xC5, 0x89, 0x6F, 0xB7, 0x62, 0x0E, 0xAA, 0x18, 0xBE, 0x1B,
		0xFC, 0x56, 0x3E, 0x4B, 0xC6, 0xD2, 0x79, 0x20, 0x9A, 0xDB, 0xC0, 0xFE, 0x78, 0xCD, 0x5A, 0xF4,
		0x1F, 0xDD, 0xA8, 0x33, 0x88, 0x07, 0xC7, 0x31, 0xB1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xEC, 0x5F,
		0x60, 0x51, 0x7F, 0xA9, 0x19, 0xB5, 0x4A, 0x0D, 0x2D, 0xE5, 0x7A, 0x9F, 0x93, 0xC9, 0x9C, 0xEF,
		0xA0, 0xE0, 0x3B, 0x4D, 0xAE, 0x2A, 0xF5, 0xB0, 0xC8, 0xEB, 0xBB, 0x3C, 0x83, 0x53, 0x99, 0x61,
		0x17, 0x2B, 0x04, 0x7E, 0xBA, 0x77, 0xD6, 0x26, 0xE1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0C, 0x7D
	};
	static final char galoisTable[][] = {
		{0x02, 0x03, 0x01, 0x01},
		{0x01, 0x02, 0x03, 0x01},
		{0x01, 0x01, 0x02, 0x03},
		{0x03, 0x01, 0x01, 0x02}
	};

	public String readFile(String fileName, boolean flag) {
		String lineFromInputFile = null;
		String result = "";
		BufferedReader fileReader = null;
		File inputFileName = new File(fileName);

		try{
			fileReader = new BufferedReader(new FileReader(fileName));
			while ((lineFromInputFile = fileReader.readLine()) != null) {
				if (lineFromInputFile.length() != 32 && flag) {
					System.out.println("Please make sure each line in the input file is exactly 32 characters!");
					exitTheProgram();
				}
				if (lineFromInputFile.length() != 64 && flag == false) {
					System.out.println("Please make sure your key file includes exactly 64 characters!");
					exitTheProgram();
				}
				checkIfContainsInvalidCharacter(lineFromInputFile, flag);
				result += lineFromInputFile + "\n";
			}
		} catch (FileNotFoundException e) {
			System.out.println("Warning, '" + fileName + "' cannot be found! Please try again!");
			exitTheProgram();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				if (fileReader != null) {
					fileReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public int [] readKey(String tempKey, int index) {
		int a = 0;
		int j = 0;
		int [][] intArray = new int [4][4];
		char [] keyInChar = new char [32];
		char [] keyInByte = tempKey.toCharArray();
		int [] result = new int [32];
		String temp = "";

		for(int i = 0; i < 32; i++) {
			temp += keyInByte[i];
			a++;

			if (a == 2) {
				result[j] = Integer.parseInt(temp.trim(), 16);
				a = 0;
				temp = "";
				j++;
			}
		}
		a = 0;
		intArray = convert16BytesToFourByFourArray(result);

		for (int i = index; i < index + 4; i++) {
			for (int q = 0; q < 4; q++) {
				// System.out.print(q + "-" + a);
				// System.out.print(q + "-" + i);
				keyExpansionArray[q][i] = intArray[q][a];
			}
			a++;
			// System.out.println(" ");
		}
		// 0-0 0-1 0-2 0-3 0-4 0-5 0-6 0-7
		// 1-0 1-1 1-2 1-3 1-4 1-5 1-6 1-7
		// 2-0 2-1 2-2 2-3 2-4 2-5 2-6 2-7
		// 3-0 3-1 3-2 3-3 3-4 3-5 3-6 3-7
		return result;
	}

	public void writeFile(String fileName) {
		String fileExtension = null;
		String newFileName = "";
		String typeOfFile = "";
		FileOutputStream fileWriter = null;
		File inputFileName;
		
		if (new Character(mode).compareTo(ENC) == 0) {
			fileExtension = ENCEXTENSION;
			typeOfFile = " for encrypted message!";
		}else {
			fileExtension = DECEXTENSION;
			typeOfFile = " for decrypted message!";
		}
		newFileName = fileName + fileExtension;
		inputFileName = new File(newFileName);

		try{
			if (!inputFileName.exists()) {
				inputFileName.createNewFile();
			}
			fileWriter = new FileOutputStream(inputFileName, false);
			byte[] contentInBytes = contentForWrite.getBytes();
			fileWriter.write(contentInBytes);
			System.out.print("Please check the " + newFileName + typeOfFile);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				if (fileWriter != null) {
					fileWriter.flush();
					fileWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void buildSet() {
		boolean flag = false;

		try {
			validCharacterSet.add("0");
			validCharacterSet.add("1");
			validCharacterSet.add("2");
			validCharacterSet.add("3");
			validCharacterSet.add("4");
			validCharacterSet.add("5");
			validCharacterSet.add("6");
			validCharacterSet.add("7");
			validCharacterSet.add("8");
			validCharacterSet.add("9");
			validCharacterSet.add("a");
			validCharacterSet.add("b");
			validCharacterSet.add("c");
			validCharacterSet.add("d");
			validCharacterSet.add("e");
			validCharacterSet.add("f");
			if (validCharacterSet.size() != 16) flag = true;
		} catch(Exception e) {
			flag = true;
		}
		if (validCharacterSet.size() != 16) {
			System.out.println("Something went wrong...Please check back!");
			exitTheProgram();
		}
	}

	public void checkEmpty() {
		if (mode == 0 || keyFileName == null || inputFileName == null) {
			System.out.println("Parameters cannot be empty!");
			exitTheProgram();
		}
	}

	public void checkIfValidMode() {
		if (new Character(mode).compareTo(ENC) != 0 && new Character(mode).compareTo(DEC) != 0) {
			printSampleCommandUsage();
			exitTheProgram();
		}
	}
	public static void exitTheProgram() {
		System.out.print("Good Bye~");
		System.exit(1);
	}

	public void checkIfContainsInvalidCharacter(String stringToCheck, boolean flag) {
		char [] characterInByte = stringToCheck.toCharArray();
		String temp = "";
		String typeOfFile = "";

		for(int i = 0; i < characterInByte.length; i++) {
			temp += characterInByte[i];
			temp = temp.toLowerCase();
			if (validCharacterSet.contains(temp) == false) {
				if (flag) {
					typeOfFile = "input file";
				}else {
					typeOfFile = "key file";
				}
				System.out.println("Please make sure all the charcters in the " + typeOfFile + " is all valid character.");
				System.out.println("Valid charcter includes '0', '1', '2', '3', '4', '5', '6', '7,', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'");
				exitTheProgram();
			}
			temp = "";
		}
	}

	public void printSampleCommandUsage() {
		System.out.println("Please make sure you have all the paramters or you have the correct mode!");
		System.out.println("For example, java AES [mode] [key file name] [input file name]");
		System.out.println("For example, java AES e key.txt plaintext.txt");
		exitTheProgram();
	}

	/**
	 * 
	 */
	public int [][] subBytes(int [][] intArray) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (new Character(mode).compareTo(ENC) == 0) {
					intArray[i][j] = forwardTableLookUp(intArray[i][j]);
				}else {
					intArray[i][j] = inverseTableLookUp(intArray[i][j]);
				}
			}
		}
		return intArray;
	}

	/**
	 * 
	 */
	public int[][] shiftRows(int[][] state) {
		//shifting R1
		int temp1 = state[1][0];
		for(int i=0;i<3;i++){
			state[1][i]=state[1][i+1];
		}
		state[1][3]=temp1;
		
		//shifting R2
		int temp2 =state[2][0];
		int temp3 =state[2][1];
		for(int k=0;k<2;k++){
			state[2][k]=state[2][k+2];
		}
		state[2][2]=temp2;
		state[2][3]=temp3;
		
		//shifting R3
		int temp4 =state[3][3];
		for(int j=3;j>0;j--){
			state[3][j]=state[3][j-1];
		}
		state[3][0]=temp4;
		
		return state;
	}

	/**
	 * 
	 */
	public int[][] inverseShiftRows(int[][] state) {
		//shifting R1
		int temp1 = state[1][3];
		for(int i=3;i>0;i--){
			state[1][i]=state[1][i-1];
		}
		state[1][0]=temp1;
		
		//shifting R2
		int temp2 =state[2][0];
		int temp3 =state[2][1];
		for(int k=0;k<2;k++){
			state[2][k]=state[2][k+2];
		}
		state[2][2]=temp2;
		state[2][3]=temp3;
		
		//shifting R3
		int temp4 =state[3][0];
		for(int j=0;j>3;j--){
			state[3][j]=state[3][j+1];
		}
		state[3][3]=temp4;

		return state;
	}

	/**
	 * 
	 */
	public static void keyGen(){

		char[] rcon = {0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a};
		int temp;
		int count=1;
		int flag=0;
		int [] tempcol=new int[4];
		for(int j=8;j<60;j++){
			if(j%4==0){
				
				for(int i=0;i<4;i++){
					tempcol[i]=keyExpansionArray[i][j-1];
				}
				temp=tempcol[0];
				if(flag==0){
				for(int z=0;z<3;z++){
					tempcol[z]=tempcol[z+1];
				}
				tempcol[3]=temp;
				}
				for(int x=0;x<4;x++){
					tempcol[x]= forwardTableLookUp(tempcol[x]);
				}
				
				for(int q=0;q<4;q++){
					tempcol[q]= tempcol[q]^keyExpansionArray[q][j-8];
				}
				if(flag==0){
				tempcol[0]=tempcol[0]^rcon[count++];
					flag=1;
				}else{
					flag=0;
				}
				for(int o=0;o<4;o++){
					keyExpansionArray[o][j]=tempcol[o];
				}
				
			}else{
				for(int h=0;h<4;h++){
					keyExpansionArray[h][j]=keyExpansionArray[h][j-1]^keyExpansionArray[h][j-8];
				}
			}	
		}
	}

	/**
	 * 
	 */
	public static int getXorResultFrom2Array(int [] array1, int [] array2) {
		int temp1 = 0, temp2 = 0, result = 0;
		int [] miniArray = new int [4];

		for(int j=0;j<4;j++){
			temp1 = array1[j];
			temp2 = array2[j];
			// System.out.print(temp2);
			// System.out.print(" ");
			result = temp1 * temp2;

			if (temp1 != 1 && temp2 != 1 && temp1 != 0 && temp2 != 0) {
				temp1 = lTable[temp1];
				temp2 = lTable[temp2];
				result = temp1 + temp2;
				if (result > 255) {
					result -= 255;
				}
				result = eTable[result];
				// System.out.print(result);
				// System.out.print(" ");
			}
			miniArray[j] = result;
		}
		result = 0;
		for(int i=0;i<4;i++){
			result ^= miniArray[i];
		}
		return result;
	}

	/**
	 * 
	 */
	public int [][] mixColumns(int[][] message) {
		int count = 0;
		int [][] intArray = new int [4][4];
		int [] a = new int [4];
		int [] b = new int [4];

		// 63 FC AC 16 1B EE 28 C3 C4 C1 93 F5 4B 82 33 EA

		// 63 79 E6 D9 F4 67 FB 76 AD 06 3C F4 D2 EB 8A A3

		// char sample[][] = {
		// 	{0x63, 0x1b, 0xc4, 0x4b},
		// 	{0xfc, 0xee, 0xc1, 0x82},
		// 	{0xac, 0x28, 0x93, 0x33},
		// 	{0x16, 0xc3, 0xf5, 0xea}
		// };

		for(int j=0;j<4;j++) {
			a = new int [] {message[0][j], message[1][j], message[2][j], message[3][j]};
			for(int i=0;i<4;i++) {
				b = new int [] {galoisTable[i][0], galoisTable[i][1], galoisTable[i][2], galoisTable[i][3]};
				intArray[i][j] = getXorResultFrom2Array(a, b);

				// System.out.print(" ");
				// System.out.print(intArray[j][i]);
				// System.out.print(" ");
				// System.out.print((int)b[i]);
				// System.out.print((int)sample[0][i]);
				// System.out.print((int)sample[1][i]);
				// System.out.print((int)sample[2][i]);
				// System.out.print((int)sample[3][i]);
				// System.out.print(" ");
				// System.out.print(i + " " + j);
				// System.out.print(" ");
			}
			// System.out.println();
		}

		// for(int j=0;j<4;j++){
		// 	for(int i=0;i<4;i++){
		// 		System.out.print("0x" + Integer.toHexString(intArray[j][i]));
		// 		// System.out.print(j + " " + i);
		// 		System.out.print(",");
		// 	}
		// 	System.out.println();
		// }
		return intArray;
	}

	/**
	 * 
	 */
	public static int[][] addRoundkey(int[][] message, int index) {
		
		int col = 4*index;
		int []temp=new int[16];
		int []temp2=new int[16];
		int count=0;
		int count2=0;
		for(int j=0;j<4;j++){
			for(int i=0;i<4;i++){
				temp[count++]=message[i][j];
			}
		}
		for(int n=col;n<col+4;n++){
			for(int i=0;i<4;i++){
				temp2[count2++]=keyExpansionArray[i][n];
			}
		}
		for(int k=0;k<16;k++){
			temp[k]=temp[k]^temp2[k];
		}
		int count3=0;
		for(int q=0;q<4;q++){
			for(int w=0;w<4;w++){
				message[w][q]=temp[count3++];
			}
		}
		return message;
	}

	/**
	 * 
	 */
	public static char forwardTableLookUp(int a) {
		return forward[a];
	}

	/**
	 * 
	 */
	public static char inverseTableLookUp(int a) {
		return inverse[a];
	}

	/**
	 * 
	 */
	public int [][] convert16BytesToFourByFourArray(int [] inputArray) {
		int a = 0;
		int [][] intArray = new int [4][4];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				try{
					intArray[j][i] = inputArray[a++];
				} catch (IndexOutOfBoundsException e) {
					intArray[j][i] = 0;
				}
			}
		}
		return intArray;
	}

	/**
	 * 
	 */
	public void startEncryption(int [][] intArray) {
		// increment this to check the result
		// int check = 11;
		// int check1 = check - 1;
		String temp = "";
		String debugWholeString = "";

		intArray = addRoundkey(intArray, 0);
		
		// for (int i = 0; i < check; i++) {
		for (int i = 0; i < NUMBEROFROUNDS-1; i++) {

			// if (i == check1) {
			// 	for (int k = 0; k < 4; k++) {
			// 		System.out.print("{");
			// 		for (int j = 0; j < 4; j++) {
			// 			// System.out.print(intArray[k][j]);
			// 			// System.out.print(k + " " + j);
			// 			System.out.print("0x" + Integer.toHexString(intArray[k][j]));
			// 			if (j + 1 != 4) {
			// 				System.out.print(",");
			// 			}else{
			// 				System.out.print("}");
			// 			}
			// 		}
			// 		System.out.println();
			// 	}
			// 	System.out.println();
			// }

			intArray = subBytes(intArray);
			intArray = shiftRows(intArray);
			intArray = mixColumns(intArray);
			intArray = addRoundkey(intArray, i+1);
		}

		intArray = subBytes(intArray);
		intArray = shiftRows(intArray);
		intArray = addRoundkey(intArray, 14);

		for(int j=0;j<4;j++){
			// System.out.print("{");
			for(int i=0;i<4;i++){
				temp = Integer.toHexString(intArray[j][i]);
				if (temp.length() == 1) {
					temp = "0" + temp;
				}
				// System.out.print("0x" + temp);
				temp = Integer.toHexString(intArray[i][j]);
				if (temp.length() == 1) {
					temp = "0" + temp;
				}
				debugWholeString += temp;
				if (i + 1 != 4) {
					// System.out.print(",");
				}else{
					// System.out.print("}");
				}
			}
			// System.out.println();
		}
		contentForWrite += debugWholeString + "\n";
		// System.out.print(debugWholeString + "\n");
	}

	public void prepareToEncrypt() {
		int a = 0;
		int count = 0;
		boolean flag = true;
		// int numberOfFourByFourByteArray = (int) Math.ceil((double)inputText.getBytes().length/32);
		int [][] intArray = new int [4][4];
		int [] inputFileInArray = new int [16];
		char [] keyInByte = key.toCharArray();
		char [] inputTextInByte = inputText.toCharArray();
		String temp = "";
		String sixteenBytesString = "";

		for (int q = 0; q < 64; q++) {
			if (a < 32) {
				temp += keyInByte[q];
				a++;
			}
			if (a == 32) {
				if (flag) {
					readKey(temp, 0);
					flag = false;
				}else{
					readKey(temp, 4);
				}
				temp = "";
				a = 0;
			}
		}
		keyGen();
		// for (int o = 0; o < 60; o++) {
		// 	for (int u = 0; u < 4; u++) {
		// 		// System.out.print(keyExpansionArray[u][o]);
		// 		// System.out.print(Integer.toHexString(keyExpansionArray[u][o]));
		// 		// System.out.print(Integer.toHexString(keyExpansionArray[u][o]));
		// 		// System.out.print("0x" + Integer.toHexString(keyExpansionArray[u][o]));
		// 		// System.out.print(o);
		// 		System.out.print(" ");
		// 		// System.out.print(u);
		// 	}
		// 	System.out.println();
		// 	if ((o+1)%4==0) {
		// 		System.out.println("------------------------------");
		// 	}
		// }
		// a = 0;

		for (int i = 0; i < inputTextInByte.length; i++) {
			if (inputTextInByte[i] == '\n') continue;
			temp += inputTextInByte[i];
			a++;

			if (a == 2) {
				inputFileInArray[count] = Integer.parseInt(temp.trim(), 16);
				a = 0;
				temp = "";
				count++;
			}

			if (count == 16) {
				intArray = convert16BytesToFourByFourArray(inputFileInArray);
				startEncryption(intArray);
				count = 0;
			}
		}
	}

	public void prepareToDecrypt() {
		System.out.println("lets decrypt something!");
	}
	
	public static void main(String[] args) throws Exception {
		AES aes_265 = new AES();
		
		try{
			if (args[MODE].length() > 1) aes_265.printSampleCommandUsage();
			mode = args[MODE].charAt(0);
			keyFileName = args[KEY];
			inputFileName = args[INPUTTEXT];
		}catch(ArrayIndexOutOfBoundsException e) {
			aes_265.printSampleCommandUsage();
		}

		/**
	 	* Making sure if the user inputted parameters are what we expected.
	 	*/
	 	buildSet();
		aes_265.checkEmpty();
		aes_265.checkIfValidMode();

		/**
	 	* Now, we should have the key as String from the key file.
	 	*/
		key = aes_265.readFile(keyFileName, false);
		System.out.println("THE KEY IS");
		System.out.println(key);

		/**
	 	* Now, we should have the message or text from the user's inputted file.
	 	*/
		inputText = aes_265.readFile(inputFileName, true);
		System.out.println("THE INPUT IS");
		System.out.println(inputText);

		if (new Character(mode).compareTo(ENC) == 0) {
			aes_265.prepareToEncrypt();


// {0x02, 0x03, 0x01, 0x01},
		// {0x01, 0x02, 0x03, 0x01},
		// {0x01, 0x01, 0x02, 0x03},
		// {0x03, 0x01, 0x01, 0x02}



			// int [] a = new int [4];
			// int [] b = new int [4];
			// int [] intArray = new int [4];
			// a = new int [] {0x51, 0x0, 0xc5, 0xe0};
			// b = new int [] {0x02, 0x03, 0x01, 0x01};
			// System.out.println("0x" + Integer.toHexString(getXorResultFrom2Array(a, b)));

		}else {
			aes_265.prepareToDecrypt();
		}
		
		aes_265.writeFile(inputFileName);
	}
}