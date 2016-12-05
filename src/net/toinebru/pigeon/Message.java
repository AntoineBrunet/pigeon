package net.toinebru.pigeon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.EOFException;

import java.util.Date;

public class Message {
	public final static Charset CHARSET = Charset.forName("UTF-8");

	private String content;
	private String author;
	private Date date;

	protected static String getDefaultAuthor() {
		String s = System.getenv("USER");
		if (s != null) { return s; }
		return "Anonyme";
	}
	
	public Message(String s) {
		this(s, getDefaultAuthor(), new Date());
	}

	public Message(String s, String u) {
		this(s, u, new Date());
	}

	public Message(String s, String u, Date d) {
		this.content = s;
		this.author  = u;
		this.date = d;
	}

	public String toString() {
		return String.format("[%s] %s", author, content);
	}

	public static Message readFrom(DataInputStream source) 
			throws IOException {
		String author = readString(source);
		String conten = readString(source);
		return new Message(conten, author);
	}

	public void writeTo(DataOutputStream os) throws IOException {
		writeString(os, author);
		writeString(os, content);
	}

	protected static String readString(DataInputStream source) 
			throws IOException {
		int length = source.readUnsignedShort();
		byte[] bytes = new byte[length];
		source.readFully(bytes);
		return new String(bytes);
	}

	protected static void writeString(DataOutputStream os, String content)
	   		throws IOException {
		byte[] bytes = content.getBytes(CHARSET);
		int size = bytes.length;
		if (size > 0xffff) { size = 0xffff; }
		os.writeShort(size);
		os.write(bytes, 0, size);
	}
}
