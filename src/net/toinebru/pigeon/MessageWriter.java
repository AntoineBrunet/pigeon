package net.toinebru.pigeon;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;

public class MessageWriter {
	private final DataOutputStream os;

	public MessageWriter(OutputStream os) {
		this.os = new DataOutputStream(os);	
	}

	public void send(Message m) throws IOException {
		m.writeTo(this.os);
	}
}
