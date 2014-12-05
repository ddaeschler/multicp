package multicp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileUtilities {

	public FileUtilities() {
	}

	private ExecutorService _exec;
	
	public final void copyDirectory(File source, File[] destinations)
			throws IOException, InterruptedException, ExecutionException {
		
		if (_exec != null) {
			_exec.shutdown();
		}
		
		_exec = Executors.newFixedThreadPool(destinations.length);
		
		doCopyDirectory(source, destinations);
		
		_exec.shutdown();
		_exec = null;
	}
	
	private final void doCopyDirectory(File source, File[] destinations)
			throws IOException, InterruptedException, ExecutionException {
		
		if (!source.isDirectory()) {
			throw new IllegalArgumentException("Source (" + source.getPath()
					+ ") must be a directory.");
		}

		if (!source.exists()) {
			throw new IllegalArgumentException("Source directory ("
					+ source.getPath() + ") doesn't exist.");
		}

		for (File dest : destinations) {
			if (dest.exists()) {
				throw new IllegalArgumentException("Destination ("
						+ dest.getPath() + ") exists.");
			}
			
			dest.mkdirs();
		}
		
		File[] files = source.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				java.util.ArrayList<File> destDirs = new java.util.ArrayList<File>();
				for (File dest : destinations) {
					destDirs.add(new File(dest, file.getName()));
				}
				
				doCopyDirectory(file, destDirs.toArray(new File[destDirs.size()]));
				
			} else {
				java.util.ArrayList<File> destFiles = new java.util.ArrayList<File>();
				for (File dest : destinations) {
					destFiles.add(new File(dest, file.getName()));
				}
				
				copyFile(file, destFiles);
			}
		}
	}
	
	public final void copyFile(File source, java.util.ArrayList<File> destinations)
			throws IOException, InterruptedException, ExecutionException {

		System.out.println("Copying: " + source.getAbsolutePath());
		
		final int BUFFER_SZ = 524288;
		byte[] fbuf = new byte[BUFFER_SZ];
		FileInputStream inStream = new FileInputStream(source);
		FileOutputStream[] outstreams = new FileOutputStream[destinations.size()];
		
		for (int i = 0; i < destinations.size(); i++) {
			outstreams[i] = new FileOutputStream(destinations.get(i));
		}
		
		int sz = -1;
		Future<?>[] tasks = new Future<?>[destinations.size()];
		
		while ((sz = inStream.read(fbuf)) != -1) {
			int i = 0;
			for (final FileOutputStream o : outstreams) {
				final int msz = sz;
				tasks[i++] = 
					_exec.submit(new Runnable() {
			            @Override
			            public void run() {
			            	try {
								o.write(fbuf, 0, msz);
							} catch (IOException e) {
								e.printStackTrace();
								System.exit(-1);
							}
			            }
			        });
		    }
			
			for (Future<?> f : tasks) {
				f.get();
			}
		}
		
		for (FileOutputStream os : outstreams) {
			os.close();
		}
		
		inStream.close();
	}
}