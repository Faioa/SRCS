package srcs.banque;

import java.io.*;
import srcs.persistance.Sauvegardable;

public class Compte implements Sauvegardable {
	
	private final String id;
	private double solde;
	

	public Compte(String id) {
		this.id=id;	
		this.solde=0.0;
	}
	
	public Compte(InputStream in) throws IOException {
		DataInputStream in2 = new DataInputStream(in);
		id = in2.readUTF();
		solde = in2.readDouble();
	}
	
	public String getId() {
		return id;
	}

	public double getSolde() {
		return solde;
	}

	public void crediter(double montant) {
		solde += montant;
	}

	public void debiter(double montant) {
		solde -= montant;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o==this) return true;
		if(o==null) return false;
		if(!(o instanceof Compte)) return false;
		Compte other= (Compte) o;
		return other.id.equals(id);
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public void save(OutputStream out) throws IOException {
		DataOutputStream out2 = new DataOutputStream(out);
		out2.writeUTF(id);
		out2.writeDouble(solde);
		out2.flush();
	}
	
}
