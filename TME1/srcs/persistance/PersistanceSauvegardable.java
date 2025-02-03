package srcs.persistance;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class PersistanceSauvegardable {
	
	public static void save(String f, Sauvegardable s) throws IOException {
		try(DataOutputStream out = new DataOutputStream(new FileOutputStream(f))) {
			out.writeUTF(s.getClass().getCanonicalName());
			s.save(out);
		}
	}
	
	public static Sauvegardable load(String f) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		try(DataInputStream in = new DataInputStream(new FileInputStream(f))) {
			String className = in.readUTF();
			Class<? extends Sauvegardable> c = Class.forName(className).asSubclass(Sauvegardable.class);
			Constructor<? extends Sauvegardable> b = c.getConstructor(InputStream.class);
			return b.newInstance(in);
		}
	}
	
}
