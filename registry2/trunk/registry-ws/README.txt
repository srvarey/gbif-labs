Registry v2 
-----------

This project provides the WS, and JS based admin console for the registry (v2) which operates against PostgreSQL.

********
Warnings: 
********
  a) This project makes use of liquibase to manage schemas, AND WILL TRUNCATE TABLES in tests.
  b) Tests are run against the database in JUnit during the maven verify
  c) Grizzly is used for tests, and port conflicts can be avoided by using -D


To use this project you would typically do one of the following:
  i) mvn clean verify package -Pregistry2-local
  ii) mvn jetty:run -Pregistry2-local

 
With a maven profile similar to:

    <!-- A local profile for the registry2 postgres development -->
    <profile>
      <id>registry2-local</id>
      <properties>
        <registry2.db.url>jdbc:postgresql://localhost/registry</registry2.db.url>
        <registry2.db.username>postgres</registry2.db.username>
        <registry2.db.password>postgres</registry2.db.password>
        <registry2-it.db.url>jdbc:postgresql://localhost/registry</registry2-it.db.url>
        <registry2-it.db.username>postgres</registry2-it.db.username>
        <registry2-it.db.password>postgres</registry2-it.db.password>
      </properties>
    </profile>    

