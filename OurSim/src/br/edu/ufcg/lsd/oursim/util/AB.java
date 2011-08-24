package br.edu.ufcg.lsd.oursim.util;

import java.util.Arrays;

public class AB {

	public static int[] cv(int initialValue, int finalValue) {
		int[] vector = new int[(finalValue - initialValue) + 1];
		int index = 0;
		for (int i = initialValue; i <= finalValue; i++) {
			vector[index++] = i;
		}
		return vector;
	}

	public static int[] cv(int value) {
		return cv(value, value, value);
	}

	public static int[] cv(int initialValue, int finalValue, int step) {
		Double vectorSizeD = Math.ceil(((finalValue - initialValue) + 1) / (step * 1.0));
		int vectorSize = vectorSizeD.intValue();
		// System.out.println(vectorSize);
		int[] vector = new int[vectorSize];
		int index = 0;
		for (int i = initialValue; i <= finalValue; i += step) {
			vector[index++] = i;
		}
		return vector;
	}

	public static void print(int[] vector) {
		System.out.println(Arrays.toString(vector));
	}

	public static String toString(Object[] vector) {
		return Arrays.toString(vector).replaceAll(" ", "");
	}

	public static String toString(int[] vector) {
		return Arrays.toString(vector).replaceAll(" ", "");
	}

	public static String[] exclude(String[] allSpts, int index) {
		String[] retorno = new String[allSpts.length - 1];
		for (int i = 0; i < allSpts.length; i++) {
			if (i < index) {
				retorno[i] = allSpts[i];
			} else if (i > index) {
				retorno[i - 1] = allSpts[i];
			}
		}
		return retorno;
	}

	public static int[] cv(String seq) {
		String[] split = seq.split(":");
		int[] retorno = null;
		if (split.length == 1) {
			int v1 = Integer.parseInt(split[0]);
			retorno = cv(v1);
		} else if (split.length == 2) {
			int v1 = Integer.parseInt(split[0]);
			int v2 = Integer.parseInt(split[1]);
			retorno = cv(v1, v2);
		} else if (split.length == 2) {
			int v1 = Integer.parseInt(split[0]);
			int v2 = Integer.parseInt(split[1]);
			int v3 = Integer.parseInt(split[1]);
			retorno = cv(v1, v2, v3);
		} else {
			return null;
		}
		return retorno;
	}

	public static int getPositionIn(String string, String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(string)) {
				return i;
			}
		}
		return -1;
	}

}
