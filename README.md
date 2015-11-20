

INSTALL MAVEN LIBRARIES :

1) Checkout the project and locate the "libs" folder under the Project, change into that directory with jar files (matoll-0.0.1.jar, jaws-bin.jar)
	
2) run the following codes to install jars into local machine:

  mvn install:install-file -DgroupId=de.citec.sc -DartifactId=matoll -Dversion=1.0 -Dpackaging=jar -Dfile=matoll-0.0.1.jar

  mvn install:install-file -DgroupId=edu.smu.tspell -DartifactId=wordNet -Dversion=1.0 -Dpackaging=jar -Dfile=jaws-bin.jar 


DOWNLOAD PROJECT FILES

1) Download the file from <a href="http://bit.ly/1OV4m76">wikipedia_anchors.ttl</a> and put under "anchorFiles" in the project folder

2) Download the file from <a href="http://bit.ly/1I3tGjs">lexicon.ttl</a> and put under "matollFiles" in the project folder


CREATE INDEX FILES:

Check  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/CreateIndexes.java">CreateIndexes</a> for creating indexes


RETRIEVAL OF DATA FROM INDEXES

Check  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/TestIndexSearch.java">TestIndexSearch</a> for searching over created indexes




