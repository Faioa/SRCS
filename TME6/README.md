# TME 6 - Messagerie instantanée en ligne

## Question 1
Cette commande est à exécuter à la racine du projet :
```
protoc --proto_path=proto --plugin=lib/protoc-gen-grpc-java --grpc-java_out=srcs/chat/generated --java_out=srcs/chat/generated app.proto
```