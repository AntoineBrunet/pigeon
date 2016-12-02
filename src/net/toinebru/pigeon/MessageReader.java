package net.toinebru.pigeon;

import java.io.InputStream;
import java.io.DataInputStream;
import java.util.function.Consumer;
import java.io.IOException;
import java.io.EOFException;

public class MessageReader extends Thread {
	private boolean active = true; 
	private final DataInputStream source;
	private final Consumer<Message> dest;

	public MessageReader(InputStream src, Consumer<Message> dst) {
		this.source = new DataInputStream(src);
		this.dest = dst;
	}

	public boolean isRunning() {
		return this.active && this.isAlive();
	}

	public void done() {
		this.active = false;
	}

	@Override
	public void run() {
		while (active) {
			try {
				dest.accept(Message.readFrom(source));
			} catch (IOException e) {
				// Perte de connection ou fermeture a distance
				done();
			}
		}
		try {
			source.close();
		} catch (IOException e) {
			// OK
		}
	}
}
