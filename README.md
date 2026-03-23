# Coders

En la terminal 1: 
* iniciar docker

```bash 
sudo systemctl start docker  
```
* iniciar mysql y rabbitmq
```bash
#Ejecutar desde la carpeta raiz 
docker compose up mysql rabbitmq -d
```

En la terminal 2:
* iniciar el monolito

```bash
#Ejecutar desde uamiShop
mvn spring-boot:run -Dspring-boot.run.profiles=mysql -Dmaven.test.skip=true
```
* inicializar el microservicio de catalogo
  
```bash
#Ejecutar desde uamishop-catalogo
mvn spring-boot:run
```