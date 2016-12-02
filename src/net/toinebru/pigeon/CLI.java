package net.toinebru.pigeon;

import java.io.IOException;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CLI {
	public static void write(Message m) {
		System.out.println(m);
	}

	public static void main(String [] args) {
		if (args.length < 1) {
			System.out.println("USAGE: pigeon <addresse> [local_port [dist_port]]");
		}
		try {
			int lp = Pigeon.DEFAULT_PORT;
			int dp = Pigeon.DEFAULT_PORT;
			if (args.length > 1) {
				lp = Integer.parseInt(args[1]);
			} 
			if (args.length > 2) {
				dp = Integer.parseInt(args[2]);
			}
			Pigeon pigeon = new Pigeon(InetAddress.getByName(args[0]), CLI::write, lp, dp);
			BufferedReader in
				= new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				Message m = new Message(in.readLine());
				pigeon.send(m);
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
