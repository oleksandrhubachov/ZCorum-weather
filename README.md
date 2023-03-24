## README

This README file provides instructions on how to run the Spring Boot application using JDK 8 and optional environment variables.

### Requirements
To run the Spring Boot application, you need to have the following tools installed on your machine:

* Java (JDK) 8 or later
* MySQL database

### Installation
Clone the repository to your local machine.
Open a command prompt or terminal window and navigate to the root directory of the project.
Run the following command to build the project:


`mvn clean install`


### Configuration
The Spring Boot application can be configured using the following environment variables:

* `MYSQL_HOST`: the hostname or IP address of the MySQL server. Default value is `localhost`.
* `MYSQL_PORT`: the port number of the MySQL server. Default value is `3306`.
* `MYSQL_USERNAME`: the username to use when connecting to the MySQL server. Default value is `root`.
* `MYSQL_PASSWORD`: the password to use when connecting to the MySQL server. Default value is `root`.
* `MYSQL_DB`: the name of the MySQL database to use. Default value is `weather_db`.


### Running the Application
To run the Spring Boot application with the default configuration, follow the installation instructions above and then run the following command from the root directory of the project:


`java -jar target/zcorum-weather-0.0.1-SNAPSHOT.jar`

To run the Spring Boot application with custom configuration using environment variables, run the following command:

`java -jar target/zcorum-weather-0.0.1-SNAPSHOT.jar MYSQL_HOST=myhost MYSQL_PORT=3307 MYSQL_USERNAME=myuser MYSQL_PASSWORD=mypassword MYSQL_DB=mydatabase`

This will start the application with the specified configuration. Default port value is `8080`