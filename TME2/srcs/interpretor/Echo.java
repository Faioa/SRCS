package srcs.interpretor;

import java.io.PrintStream;

public class Echo implements Command {
	
	private String[] args;
	
	public Echo(String... args) {
		this.args = args;
	}
	
	@Override
	public void execute(PrintStream out) {
		for (int i = 1; i < args.length; i++) {
			out.print(args[i]);
			if (i < args.length - 1)
				out.print(" ");
			out.flush();
		}
	}

}
