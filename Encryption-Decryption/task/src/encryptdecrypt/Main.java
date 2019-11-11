package encryptdecrypt;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

	static class Cryptography {

		private CryptographyStrategy cryptographyStrategy;

		public Cryptography(CryptographyStrategy cryptographyStrategy) {

			this.cryptographyStrategy = cryptographyStrategy;
		}

		public String cryptoOperation(String targetOperation, String text, int key) {

			if (targetOperation.equals("enc")) {
				return this.cryptographyStrategy.encryption(text, key);
			} else if (targetOperation.equals("dec")) {
				return this.cryptographyStrategy.decryption(text, key);
			}

			return null;
		}

	}

	interface CryptographyStrategy {

		String decryption(String text, int key);

		String encryption(String text, int key);
	}

	static class UncodeTableStrategy implements CryptographyStrategy {

		public String decryption(String text, int key) {

			StringBuilder decrypted = new StringBuilder();

			for (char c : text.toCharArray()) {
				int newChar = c - key;
				if (newChar < 0) {
					newChar = 256 + newChar;
				}
				decrypted.append((char) newChar);
			}

			return decrypted.toString();
		}

		public String encryption(String text, int key) {

			StringBuilder encrypted = new StringBuilder();

			for (char c : text.toCharArray()) {
				int newChar = c + key;
				if (newChar > 255) {
					newChar = newChar - 255;
				}

				encrypted.append((char) newChar);
			}

			return encrypted.toString();
		}
	}

	static class ShiftingStrategy implements CryptographyStrategy {

		@Override
		public String decryption(String text, int key) {

			char ch;
			StringBuilder decryptedMessage = new StringBuilder();

			for (int i = 0; i < text.length(); ++i) {
				ch = text.charAt(i);

				if (ch >= 'a' && ch <= 'z') {
					ch = (char) (ch - key);

					if (ch < 'a') {
						ch = (char) (ch + 'z' - 'a' + 1);
					}

					decryptedMessage.append(ch);
				} else if (ch >= 'A' && ch <= 'Z') {
					ch = (char) (ch - key);

					if (ch < 'A') {
						ch = (char) (ch + 'Z' - 'A' + 1);
					}

					decryptedMessage.append(ch);
				} else {
					decryptedMessage.append(ch);
				}
			}
			return decryptedMessage.toString();
		}

		@Override
		public String encryption(String text, int key) {

			char ch;
			StringBuilder encryptedMessage = new StringBuilder();

			for (int i = 0; i < text.length(); ++i) {
				ch = text.charAt(i);

				if (ch >= 'a' && ch <= 'z') {
					ch = (char) (ch + key);

					if (ch > 'z') {
						ch = (char) (ch - 'z' + 'a' - 1);
					}

					encryptedMessage.append(ch);
				} else if (ch >= 'A' && ch <= 'Z') {
					ch = (char) (ch + key);

					if (ch > 'Z') {
						ch = (char) (ch - 'Z' + 'A' - 1);
					}

					encryptedMessage.append(ch);
				} else {
					encryptedMessage.append(ch);
				}
			}
			return encryptedMessage.toString();
		}
	}

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		String targetOperation = null;  // enc || dec
		String text = null;
		Integer key = null;
		String in = null;
		String out = null;
		String alg = null;

		for (int i = 0, j = 1; j < args.length; i += 2, j += 2) {
			if (args[i].equals("-mode")) {
				targetOperation = args[j];
			}
			if (args[i].equals("-key")) {
				key = Integer.valueOf(args[j]);
			}

			if (args[i].equals("-data")) {
				text = args[j];
			}
			if (args[i].equals("-in")) {
				in = args[j];
			}

			if (args[i].equals("-out")) {
				out = args[j];
			}

			if (args[i].equals("-alg")) {
				alg = args[j];
			}

		}

		if (targetOperation == null) {
			targetOperation = "enc";
		}

		if (text == null && in == null) {
			String input = scanner.next();
			if (input.contains(".txt")) {
				in = input;
			} else {
				text = input;
			}
		}
		if (key == null) {
			key = scanner.nextInt();
		}


		try {
			if (text == null) {
				text = new String(Files.readAllBytes(Paths.get(in)));

			}

			Cryptography cryptography = null;

			switch (alg){
				case "shift":
					cryptography = new Cryptography(new ShiftingStrategy());
					break;
				case "unicode" :
					cryptography = new Cryptography(new UncodeTableStrategy());
					default:
						break;
			}

			if (cryptography == null) {
				throw new RuntimeException(
						"Unknown algorithm type passed.");
			}

		String result = cryptography.cryptoOperation(targetOperation, text, key);

			if (out == null) {
				System.out.println(result);
			} else {
				try (PrintWriter printWriter = new PrintWriter(out)) {
					printWriter.print(result);
				}
			}


		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}


	}
}

