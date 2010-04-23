package oursim.entities;

public class File {

	private String name;

	/**
	 * Size in bytes of this File
	 */
	private long size;

	public File(String name, long size) {
		this.name = name;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

}
