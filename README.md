# HotRod client: Use server side Filters to retrieve entries
HotRod client: Use server side Filters to retrieve entries
=================================================================
Author: Wolf-Dieter Fink
Level: Advanced
Technologies: Infinispan, Hot Rod


What is it?
-----------

Examples how to use retrieveEntries method for a HotRod remote client with server side filtering.
A simple server side filter which filter all (String) keys from a cache by using a substrin pattern.
A complex example which need to deploy the classes used inside of the cache and the filter will use criteria past by the client.

Hot Rod is a binary TCP client-server protocol. The Hot Rod protocol facilitates faster client and server interactions in comparison to other text based protocols and allows clients to make decisions about load balancing, failover and data location operations.


Prepare a server instance
-------------
Simple start a Infinispan or JDG server with the default configuration.

Build and Run the example
-------------------------
1. Type this command to build and deploy the archive:

        mvn clean package

2. Copy the necessary jar to the server and start the server

        cp domain/target/HotRodRetrieveWithFilter-domain.jar /path/to/server/standalone/deployments
        cp serverSide/target/HotRodRetrieveWithFilter-server.jar /path/to/server/standalone/deployments
        /path/to/server/bin/standalone.sh

   Consider the -domain.jar is only for the second example to provide the Custom key/Value classes
   It can be removed if this is not used together with the jboss-deployment-structure descriptor

3. Use java command or an IDE to start one of the following standalone clients for demonstration

       RetrieveHotRodClient
         This example use a simple String key
         One example is a Hardcoded filter at server side.
         The other example use a filter that can set a substring from the client side
 
       RetrieveCustomObjectHotRodClient
         This example use more complicated filter parameter and also a custom class for the key/values within the cache.
         To get this working the classes need to be known for serialization, otherwise an Exception is thrown.
         The classes can be passed by the start command like this

              bin/standalone.sh -Dinfinispan.deserialization.whitelist.classes=org.infinispan.wfink.hotrod.batch.domain.MySpecialKey,org.infinispan.wfink.hotrod.batch.domain.MySpecialValue


         Other option is to use the system-properties element in the server configuration.

