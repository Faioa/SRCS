# TME 3 - Protocole HTTP
___

## Exercice 1 - Contenu des messages

### Question 2
Les informations transmises sont :
	- Le type de requête -> GET
	- La ressource demandée : /index.html
	- La version du protocole utilisé -> HTTP/1.1
	- L'adresse et le port du serveur contacté -> localhost:1234
	- Des informations sur le navigateur -> User-Agent
	- Les types de ressources accepté en retour
	- Des options sur la configuration de la connexion -> keep-alive par exemple.
Le format est textuel (ASCII).
On peut savoir que le client a fini de transmettre ses informations, car il y a toujours une ligne vide à la fin de la transmission.

### Question 3
Le client ne ferme pas automatiquement la connexion après avoir fait sa requête. Sans toucher au code du serveur, fermer le navigateur suffit pour fermer la connexion.

### Question 4
En ignorant les lignes vides on a maintenant un affichage avec une ligne en moins à chaque requête.

