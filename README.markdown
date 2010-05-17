Scorm2Fedora
============

A web application that accepts a SCORM package and deposits it into a Fedora repository.

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

3. Deploy the war (scorm2fedora-webapp/target/scorm2fedora.war) to a servlet container

Configuration
-------------

Scorm2Fedora supports configuration by web.xml init-params and Java properties files.
The web.xml init-params take precedence. The following configuration parameters 
are supported:

* username
    * The username Scorm2Fedora should use to connect to the Fedora Repository, 
      e.g. fedoraAdmin
* password
    * The password Scorm2Fedora should use to connect to the Fedora Repository,
      e.g. fedoraAdmin
* baseUrl
    * The baseUrl of the Fedora Repository, e.g. http://example.org:8080/fedora
* namespace
    *
* scorm.dsid
    * The datastream id for the original SCORM package
* cmodel
    * The URI of the content model new objects subscribe to, e.g. info:fedora/foo:bar


Copyright
---------

Copyright &copy; 2010 MediaShelf
