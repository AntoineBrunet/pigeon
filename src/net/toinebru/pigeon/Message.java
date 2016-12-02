package net.toinebru.pigeon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.EOFException;

public class Message {
	public final static Charset CHARSET = Charset.forName("UTF-8");

	private String content;

	public Message(String s) {
		this.content = s;
	}

	public String toString() {
		return content;
	}

	public static Message readFrom(DataInputStream source) 
			throws EOFException, IOException {
		int length = source.readUnsignedShort();
		byte[] bytes = new byte[length];
		source.readFully(bytes);
		return new Message(new String(bytes));
	}

	public void writeTo(DataOutputStream os) throws IOException {
		byte[] bytes = content.getBytes(CHARSET);
		int size = bytes.length;
		if (size > 0xffff) { size = 0xffff; }
		os.writeShort(size);
		os.write(bytes, 0, size);
	}
}
