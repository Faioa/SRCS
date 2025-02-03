package srcs.persistance;

import java.io.*;

public abstract class PersistanceArray {
	
	public static void saveArrayInt(String f, int[] tab) throws FileNotFoundException, IOException {
		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(f))) {
			out.writeInt(tab.length);
			for (int n : tab)
				out.writeInt(n);
			out.flush();
		}
	}
	
	public static int[] loadArrayInt(String f) throws IOException {
		try(DataInputStream in = new DataInputStream(new FileInputStream(f))) {
			int len = in.readInt();
			int[] ret = new int[len];
			int cpt = 0;
			
			while (cpt < len)
				ret[cpt++] = in.readInt();
			return ret;
		}
	}
	
}
