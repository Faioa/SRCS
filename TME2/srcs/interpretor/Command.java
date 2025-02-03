package srcs.interpretor;

import java.io.PrintStream;
import java.io.Serializable;

public interface Command extends Serializable {
	
	public void execute(PrintStream out);
	
}
