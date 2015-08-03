# Simple transactions REST API

## Running The Application

To test the application run the following commands.

* To package the app run.

        mvn package

* To setup the h2 database run.

        java -jar target/main-1.0-SNAPSHOT.jar db migrate bank.yml

* To run the server run.

        java -jar target/main-1.0-SNAPSHOT.jar server bank.yml

* To view the Swagger API explorer:

		http://localhost:8080/swagger
