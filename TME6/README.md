# TME 6 - Messagerie instantanée en ligne

Il faut ajouter le répertoire **lib** au classpath et faire en sorte que **plugin/protoc-gen-grpc-java** soit exécutable.
Celui présent dans le projet est pour un système Linux x86_64 et est en version 1.26.0 : changez le selon votre système.
De même, pour assurer la compatibilité entre les fichiers générés par **protobuf-compiler** et les librairies fournies,
il faut utiliser une version plus ancienne de **protobuf-compiler** (la version 3.11.0 est compatible). Une version
compilée du programme est disponible à **bin/protoc**.

## Question 1
Cette commande est à exécuter à la racine du projet pour générer les classes gRPC :
```
$ bin/protoc --proto_path=proto\
    --plugin=./plugin/protoc-gen-grpc-java\
    --grpc-java_out=.\
    --java_out=.\
    app.proto
```