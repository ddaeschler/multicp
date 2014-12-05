package multicp;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class multicp {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: multicp source_dir dest_dir [dest_dir dest_dir ...]");
			return;
		}
		
		FileUtilities fu = new FileUtilities();
		
		System.out.println("Copying " + args[0] + " to ");
		
		File[] dests = new File[args.length - 1];
		for (int i = 1; i < args.length; i++) {
			System.out.println("--> " + args[i]);
			dests[i - 1] = new File(args[i]);
		}
		
		try {
			fu.copyDirectory(new File(args[0]), dests);
		} catch (IOException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
