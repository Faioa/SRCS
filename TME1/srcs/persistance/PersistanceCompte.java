package srcs.persistance;

import java.io.*;
import srcs.banque.Compte;

public abstract class PersistanceCompte {
	
	public static void saveCompte(String f, Compte e) throws IOException {
		try (OutputStream out = new FileOutputStream(f)) {
			e.save(out);
		}
	}
	
	public static Compte loadCompte(String f) throws IOException {
		try (InputStream in = new FileInputStream(f)) {
			return new Compte(in);
		}
	}
	
}
