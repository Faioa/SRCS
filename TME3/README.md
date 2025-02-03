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
En ignorant les lignes vides, on a maintenant un affichage avec une ligne en moins à chaque requête.

### Question 5
La réponse du serveur est du format :
```
<Version HTTP> <Code de statut> <Message de statut>\r\n
<Headers HTTP>\r\n
\r\n
<Corps de la réponse>
```
Dans notre cas, le serveur nous retourne le contenu d'une page HTML avec le code de retour ***200 OK***.

### Question 6
Le serveur retourne quand même une page html pour avoir un affichage de retour pour les navigateurs, mais le code de
retour est ***404 Not Found***. Le serveur nous signale donc que cette ressource n'existe pas avec une erreur.

### Question 7
La encore, une erreur est retournée par le serveur : le message ne respecte pas le protocole HTTP, et le serveur ne peut
pas répondre à la requête. Le code de retour est ***400 Bad Request***.