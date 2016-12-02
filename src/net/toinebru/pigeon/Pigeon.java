package net.toinebru.pigeon;

import java.util.function.Consumer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.Socket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class Pigeon {
	public final static int DEFAULT_PORT = 9090;
	public final static int LATENCY_MS = 100;
	private ServerSocket server;
	private Connection connection;
	private BlockingQueue<Message> toSend;
	private Consumer<Message> onRcv;
	private int local_port;
	private int dist_port;

	public Pigeon(InetAddress other, Consumer<Message> onRcv) throws IOException {
		this(other, onRcv, DEFAULT_PORT, DEFAULT_PORT);
	}

	public Pigeon(InetAddress other, Consumer<Message> onRcv, int local, int dist)
		throws IOException {
		this.local_port = local;
		this.dist_port  = dist;
		this.toSend = new LinkedBlockingQueue<Message>();
		this.onRcv = onRcv;
		this.server = new ServerSocket(this.local_port);
		this.connection = new Connection(other);
		this.connection.start();
	}

	public void kill() throws InterruptedException {
		this.connection.active = false;
		this.connection.join();
	}

	private class Connection extends Thread {
		public boolean active = true;
		private InetAddress other;

		public Connection(InetAddress other) {
			this.other = other;
		}

		public void run() {
			while (active) {
				try {
					System.out.println("Tentative de connection...");
					Socket sock = this.connect();
					System.out.println("OK c'est parti!");

					MessageReader mr = new MessageReader(sock.getInputStream(), onRcv);
					MessageWriter mw = new MessageWriter(sock.getOutputStream());
					mr.start();
					while (mr.isRunning() && active) {
						Message outbound = toSend.poll(LATENCY_MS, TimeUnit.MILLISECONDS);
						if (outbound != null) {
							try {
								mw.send(outbound);
							} catch (IOException e) {
								mr.done();
							}
						}
					}
					mr.done();
					sock.close();
				} catch (Exception e) {
					System.err.println(e);
					active = false;
				}

			}
		}

		private Socket connect() throws IOException {
			try {
				return new Socket(other, dist_port);
			} catch (IOException ioe) {
				System.out.println("Pas de niche chez le correspondant, on attend au chaud ici.");
				return server.accept();
			}
		}
	}

	public void send(Message m) {
		try {
			this.toSend.put(m);
		} catch (Exception e) {}
	}
}
