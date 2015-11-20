

INSTALL MAVEN LIBRARIES :

	<p>
	1) Checkout the project and locate the "libs" folder under the Project, change into that directory with jar files (matoll-0.0.1.jar, jaws-bin.jar)
	</p>
	<p>
	2) run the following codes to install jar into local machine:
	<p>	mvn install:install-file -DgroupId=de.citec.sc -DartifactId=matoll -Dversion=1.0 -Dpackaging=jar -Dfile=matoll-0.0.1.jar	</p>
	<p>	mvn install:install-file -DgroupId=edu.smu.tspell -DartifactId=wordNet -Dversion=1.0 -Dpackaging=jar -Dfile=jaws-bin.jar </p>


CREATE INDEX FILES:
<p>
Check  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/CreateIndexes.java">CreateIndexes</a> for creating indexes
</p>

RETRIEVAL OF DATA FROM INDEXES
<p>
Check  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/TestIndexSearch.java">TestIndexSearch</a> for searching over created indexes
</p>



