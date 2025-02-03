package srcs.banque;

import srcs.persistance.Sauvegardable;

import java.io.*;

public class Client implements Sauvegardable {
	
	private final String nom;
	private final srcs.banque.Compte compte;

	public Client(String nom, srcs.banque.Compte compte) {
		this.nom=nom;
		this.compte=compte;
	}

	public Client(InputStream in) throws IOException {
		DataInputStream in2 = new DataInputStream(in);
		nom = in2.readUTF();
		compte = new Compte(in2);
	}
		
	public String getNom() {
		return nom;
	}

	public srcs.banque.Compte getCompte() {
		return compte;
	}

	@Override
	public boolean equals(Object o) {
		if(o==this) return true;
		if(o==null) return false;
		if(!(o instanceof srcs.banque.Compte)) return false;
		Client other= (Client) o;
		return other.nom.equals(nom);
	}

	public void save(OutputStream out) throws IOException {
		DataOutputStream out2 = new DataOutputStream(out);
		out2.writeUTF(nom);
		compte.save(out2);
	}

}
