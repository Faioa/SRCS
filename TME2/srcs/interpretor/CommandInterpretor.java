package srcs.interpretor;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class CommandInterpretor {
	
	private Map<String, Class<? extends Command>> cmds;
	private Map<String, String> cmds_paths;
	
	public CommandInterpretor() {
		cmds = new HashMap<String,Class<? extends Command>>();
		cmds_paths = new HashMap<String, String>();
		cmds.put("echo", Echo.class);
		cmds.put("cat", Cat.class);
		cmds.put("deploy", Deploy.class);
		cmds.put("undeploy", Undeploy.class);
		cmds.put("save", Save.class);
	}

	public CommandInterpretor(String file) throws IOException, ClassNotFoundException {
		CustomObjectInputStream in = new CustomObjectInputStream(new FileInputStream(file));

		// Getting the paths associated with external commands
		cmds_paths = (Map<String, String>) in.readObject();

		// Now that we know where is each class located from, we update the ClassLoader and continue reading
		in.setClassLoader(new CustomClassLoader(cmds_paths, Thread.currentThread().getContextClassLoader()));
		cmds = (Map<String, Class<? extends Command>>) in.readObject();
	}
	
	public Class<? extends Command> getClassOf(String cmd) {
		return cmds.get(cmd);
	}
	
	public void perform(String cmd_line, OutputStream out) throws Exception {
		if (cmd_line.isEmpty())
			return;
		StringTokenizer st = new StringTokenizer(cmd_line);
		List<String> tokens = new LinkedList<>();
		while (st.hasMoreTokens()) {
			tokens.add(st.nextToken());
		}

		Class<? extends Command> cmd_class = cmds.get(tokens.getFirst());
		if (cmd_class == null)
			throw new CommandNotFoundException("Command " + tokens.getFirst() + " does not exist.");

		Constructor<? extends Command> cmd_cons;
		Command cmd;
		if (cmd_class.isMemberClass() && cmd_class.getDeclaringClass() == CommandInterpretor.class) {
			cmd_cons = cmd_class.getDeclaredConstructor(CommandInterpretor.class, String[].class);
			try {
				cmd = cmd_cons.newInstance(this, (Object) tokens.toArray(new String[0]));
			} catch (Exception e) {
				if (e.getCause() instanceof IllegalArgumentException)
					throw (IllegalArgumentException) e.getCause();
				else
					throw e;
			}
		} else {
			// Double check if the constructor exist but this is already done in the Deploy command. It should only select the right one between String[] and List<String> without throwing anything.
			try {
				cmd_cons = cmd_class.getDeclaredConstructor(String[].class);
				cmd = cmd_cons.newInstance((Object) tokens.toArray(new String[0]));
			} catch(NoSuchMethodException e) {
                cmd_cons = cmd_class.getDeclaredConstructor(List.class);
                cmd = cmd_cons.newInstance(tokens);
            }
		}
		cmd.execute(new PrintStream(out));
	}

	public class Undeploy implements Command {

		private String cmd_name;

		public Undeploy(String... args) throws IllegalArgumentException {
			if (!cmds.containsKey(args[0]))
				throw new IllegalArgumentException("Command " + cmd_name + " does not exist.");
			cmd_name = args[1];
		}

		@Override
		public void execute(PrintStream out) {
			cmds.remove(cmd_name);
			cmds_paths.remove(cmd_name);
		}

	}

	public class Deploy implements Command {

		private String cmd_name;
		private Class<? extends Command> new_cmd;

		public Deploy(String... args) throws IllegalArgumentException {
			cmd_name = args[1];
			String cmd_path = args[2];
			String cmd_absolute_name = args[3];
			if (cmds.containsKey(cmd_name))
				throw new IllegalArgumentException("Command " + cmd_name + " already exists.");
			File f = new File(cmd_path);
			if (!f.exists())
				throw new IllegalArgumentException("Directory " + cmd_path + " does not exist.");
			if (!f.isDirectory())
				throw new IllegalArgumentException(cmd_path + " is not a directory.");
			try {
				URL url = f.toURI().toURL();
				URLClassLoader cl = new URLClassLoader(new URL[]{url});
				new_cmd = cl.loadClass(cmd_absolute_name).asSubclass(Command.class);
				cmds_paths.put(cmd_absolute_name, cmd_path);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}

			// Verifying if the correct constructor is available
			try {
				new_cmd.getDeclaredConstructor(String[].class);
			} catch (NoSuchMethodException e) {
				try {
					new_cmd.getDeclaredConstructor(List.class);
				} catch (NoSuchMethodException e2) {
					throw new IllegalArgumentException("Command " + cmd_absolute_name + " does not implement the Command interface correctly. It should implement a constructor with a single String[] or List<String> as parameter.");
				}
			}
		}

		@Override
		public void execute(PrintStream out) {
			cmds.put(cmd_name, new_cmd);
		}

	}

	public class Save implements Command {

		String file;

		public Save(String... args) throws IllegalArgumentException {
			if (args.length != 2)
				throw new IllegalArgumentException("Give exactly one file to write to the command.");
			File f = new File(args[1]);
			if (f.isDirectory())
				throw new IllegalArgumentException("File " + args[1] + " is a directory.");
			if (f.exists() && !f.canWrite())
				throw new IllegalArgumentException("File " + args[1] + " is not writable.");
			file = args[1];
		}

		@Override
		public void execute(PrintStream out) {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
				oos.writeObject(cmds_paths);
				oos.writeObject(cmds);
			} catch (Exception ignored) {}
		}

	}

}
