# TME 2 - Interpréteur interactif de commandes
___
### Exercice 1 - Question 5
L'exception *InvocationTargetException* est levée lors d'un mauvais usage de la réflexivité en Java. Elle enveloppe
toute exception levée par *Method.invoke* ou *Constructor.newInstance*. Ainsi, l'exception qui est testée est bien une
*InvocationTargetException*, mais celle-ci contient bien une *IllegalArgumentException*.

### Exercice 2 - Question 1
Il n'est pas possible d'utiliser la fonction *Class.forName* car l'arborescence de *ClassLoaders* ne connait pas
forcément la classe que l'on souhaite ajouter à l'interpréteur.

### Exercice 3 - Question 1
Nous allons rencontrer un problème lors du chargement des commandes externes, car celles-ci ne seront pas connues du
*ClassLoader*s et ne pourront donc pas être chargées, faute de fichier.class sur lequel se baser.

### Exercice 3 - Question 2
J'ai choisi d'implémenter un CustomClassLoader qui permet de charger toutes les commandes externes ajoutées avant la
sauvegarde persistante. Il prend un paramètre une HashMap<String, String> associant les chemins absolus des dossiers
d'où l'on doit charger les classes externes (les chemins peuvent être différents). Ainsi, j'ai ajouté une gestion d'une
telle HashMap dans le CommandInterpretor au niveau du déploiement et de la suppression de commandes. Si la Class que
l'on veut charger n'est pas une commande externe, alors le ClassLoader délègue au ClassLoader du contexte d'exécution.\
De plus, j'ai implémenté un CustomObjectInputStream pour changer dynamiquement le ClassLoader utilisé par
l'ObjectInputStream.