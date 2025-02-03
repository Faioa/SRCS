package srcs.banque;

import srcs.persistance.Sauvegardable;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Banque implements Sauvegardable {

	private final Set<Client> clients;
	
	public Banque() {
		clients=new HashSet<>();
	}

	public Banque(InputStream in) throws IOException{
		DataInputStream in2 = new DataInputStream(in);
		clients = new HashSet<>();
		HashMap<Compte, HashSet<Client>> tmp = new HashMap<>();

		int len = in2.readInt();
		for(int i=0; i<len; i++) {
			Client c = new Client(in2);
			// Verifier si le Compte est deja cree
			if (!tmp.containsKey(c.getCompte())) {
				HashSet<Client> set = new HashSet<>();
				set.add(c);
				tmp.put(c.getCompte(), set);
			} else {
				// Recherche du Compte deja cree
				for (Map.Entry<Compte, HashSet<Client>> entry : tmp.entrySet()) {
					if (entry.getKey().equals(c.getCompte())) {
						c = new Client(c.getNom(), entry.getKey());
						break;
					}
				}
			}
			clients.add(c);
		}
	}
		
	public int nbClients() {
		return clients.size();
	}
	
	public int nbComptes() {
		Set<srcs.banque.Compte> comptes = new HashSet<>();
		for(srcs.banque.Client c : clients) {
			comptes.add(c.getCompte());
		}
		return comptes.size();
	}
	
	public srcs.banque.Client getClient(String nom) {
		for(srcs.banque.Client c : clients) {
			if(c.getNom().equals(nom)) return c;
		}
		return null;
	}
	
	public boolean addNewClient(srcs.banque.Client c) {
		return clients.add(c);
	}

	@Override
	public void save(OutputStream out) throws IOException {
		DataOutputStream out2 = new DataOutputStream(out);
		out2.writeInt(nbClients());
		for(srcs.banque.Client c : clients) {
			c.save(out2);
		}
	}

}
