package br.edu.ufcg.lsd.oursim.ui;

import java.util.ArrayList;
import java.util.List;

public class WraperTask {

	String cmd;

	List<String> inputs = new ArrayList<String>();

	List<String> outputs = new ArrayList<String>();

	@Override
	public String toString() {
		return cmd;
	}

}
