

INSTALL Maven Dependencies
	
Checkout the project and locate the "libs" directory , run the following codes inside "libs" directory

<code>  mvn install:install-file -DgroupId=de.citec.sc -DartifactId=matoll -Dversion=1.0 -Dpackaging=jar -Dfile=matoll-0.0.1.jar </code>

<code>    mvn install:install-file -DgroupId=edu.smu.tspell -DartifactId=wordNet -Dversion=1.0 -Dpackaging=jar -Dfile=jaws-bin.jar  </code>



DOWNLOAD Project Files

1) Download the file <a href="http://bit.ly/1OV4m76">wikipedia_anchors.ttl</a> and put under "anchorFiles" in the project folder

2) Download the file <a href="http://bit.ly/1I3tGjs">lexicon.ttl</a> and put under "matollFiles" in the project folder





CREATE Index Files

Check  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/CreateIndexes.java">CreateIndexes</a> for creating indexes





RETRIEVAL of Data from Indexes

Check  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/TestIndexSearch.java">TestIndexSearch</a> for searching over created indexes




