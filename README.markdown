Scorm2Fedora
============

A web application that accepts a SCORM package and deposits it into a Fedora 
repository. The new Fedora object contains the original SCORM package as a 
datastream and updates the Fedora object's DC datastream with a crosswalk of the
SCORM package's IMS Metadata.

Requirements
------------

* SCORM 1.2
* Fedora 3.3
* Java 6

Installation
------------

1. Download the source, e.g.

        git clone git://github.com/mediashelf/scorm2fedora.git

2. Build the project

        cd scorm2fedora
        mvn package -DskipTests

3. Deploy the war to a servlet container, e.g.:

        cp scorm2fedora-webapp/target/scorm2fedora.war /opt/tomcat/webapps
        
4. Assuming the scorm2fedora is now available at http://example.org/scorm2fedora,
   SCORM packages can be POSTed to http://example.org/scorm2fedora

Configuration
-------------

Scorm2Fedora supports configuration by web.xml init-params and Java properties files.
The following configuration parameters are supported:

* username
    * The username Scorm2Fedora should use to connect to the Fedora Repository, 
      e.g. fedoraAdmin
* password
    * The password Scorm2Fedora should use to connect to the Fedora Repository,
      e.g. fedoraAdmin
* baseUrl
    * The baseUrl of the Fedora Repository, e.g. http://example.org:8080/fedora
* namespace
    * pid namespace for newly created Fedora objects, e.g. scorm
* scorm.dsid
    * The datastream id for the original SCORM package
* cmodel
    * The URI of the content model new objects subscribe to, e.g. info:fedora/foo:bar

If the web.xml init-params are not present, configuration will fall back to 
the scorm2fedora.properties file located in the scorm2fedora-core jar file.

Copyright
---------

Copyright &copy; 2010 MediaShelf
