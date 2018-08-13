Camel Spring Boot Project kub
===========================

To build this project use

    mvn install

To run this project with Maven use

    mvn spring-boot:run

For more help see the Apache Camel documentation

    http://camel.apache.org/

For deploying login to your oc cluster with your oc client and run :
	
	mvn -P ocp fabric8:deploy

For testing
    curl http://localhost:8080/camel/ping

	Grant view role to service account on openshift
    
    	oc policy add-role-to-user view --serviceaccount=default
    
    If the above permission is not granted, your pod may throw a message similar to the following:

	"Forbidden!Configured service account doesn't have access. Service account may have been revoked"
    