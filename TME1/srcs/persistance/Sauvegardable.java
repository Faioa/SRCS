package srcs.persistance;

import java.io.*;

public interface Sauvegardable {
	/*
	 On suppose que les classes implementant cette interface implementent egalement un constructeur prenant simplement
	 un InputStream en parametre pour de-serialiser l'objet.
	 */

	public void save(OutputStream out) throws IOException;
	
}
