Registry v2 WS Client 
---------------------

This project provides the WS client to the registry (v2) web services.

To use this project you need to provide the WS URL which one would normally include in a maven profile along the lines of:
    <profile>
      <id>some_application<_using_the_registry_client/id>
      <properties>
        <registry2.ws.url>http://localhost:8080/registry-ws</registry2.ws.url>
      </properties>
    </profile>    

