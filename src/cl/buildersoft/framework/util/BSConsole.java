package cl.buildersoft.framework.util;

import java.util.Scanner;

public class BSConsole {
	public static void println(String m) {
		System.out.println(m);
	}

	public static void print(String m) {
		System.out.print(m);
	}

	public static String readString() {
		return readString(null);
	}

	public static String readString(String msg) {
		Scanner inputReader = new Scanner(System.in);
		if (msg != null) {
			print(msg);
		}
		String input = inputReader.nextLine();
		return input;
	}
}
