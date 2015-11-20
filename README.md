




<b>INSTALL Maven Dependencies </b>
	
1) Checkout the project and locate the "libs" directory 

2) Download the file <a href="http://bit.ly/1O7BozJ">matoll-0.0.1.jar</a> and put under "libs" in the project folder

3) run the following codes inside "libs" directory (installs maven dependencies)

<code>  mvn install:install-file -DgroupId=de.citec.sc -DartifactId=matoll -Dversion=1.0 -Dpackaging=jar -Dfile=matoll-0.0.1.jar </code>

<code>    mvn install:install-file -DgroupId=edu.smu.tspell -DartifactId=wordNet -Dversion=1.0 -Dpackaging=jar -Dfile=jaws-bin.jar  </code>



<b>DOWNLOAD Project Files </b>

1) Download the file <a href="http://bit.ly/1OV4m76">wikipedia_anchors.ttl</a> and put under "anchorFiles" in the project folder

2) Download the file <a href="http://bit.ly/1I3tGjs">lexicon.ttl</a> and put under "matollFiles" in the project folder




<b>
CREATE Index Files </b>

Check  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/CreateIndexes.java">CreateIndexes</a> for creating indexes




<b>
RETRIEVAL of Data from Indexes </b>

Check  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/TestIndexSearch.java">TestIndexSearch</a> for searching over created indexes




