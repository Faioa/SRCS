package srcs.interpretor;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class Cat implements Command {
	
	private String file;
	
	public Cat(String... args) throws IllegalArgumentException {
		if (args.length != 2)
			throw new IllegalArgumentException("Give exactly one file to read to the command.");
		Path p = Path.of(args[1]);
		if (! Files.isRegularFile(p, new LinkOption[0]))
			throw new IllegalArgumentException("File " + args[1] + " does not exist.");
		if (! Files.isReadable(p))
			throw new IllegalArgumentException("You do not have the rights to open the file " + args[1]);
		this.file = args[1];
	}
	
	@Override
	public void execute(PrintStream out) {
		try (FileInputStream in = new FileInputStream(file)){
			int b;
			while ((b = in.read()) != -1)
				out.write(b);
			out.flush();
		} catch(Exception e) {}
	}

}
