




<b>INSTALL Maven Dependencies </b>
	
1) Checkout the project and locate the "libs" directory 

2) Download the file <a href="http://bit.ly/1O7BozJ">matoll-0.0.1.jar</a> and put under "libs" in the project folder

3) run the following codes inside "libs" directory (installs maven dependencies)

<code>  mvn install:install-file -DgroupId=de.citec.sc -DartifactId=matoll -Dversion=1.0 -Dpackaging=jar -Dfile=matoll-0.0.1.jar </code>

<code>    mvn install:install-file -DgroupId=edu.smu.tspell -DartifactId=wordNet -Dversion=1.0 -Dpackaging=jar -Dfile=jaws-bin.jar  </code>



<b>DOWNLOAD Project Files </b>

1) Download the file <a href="http://bit.ly/1OV4m76">wikipedia_anchors.ttl</a> and put under "anchorFiles" in the project folder

2) Download the file <a href="http://bit.ly/1I3tGjs">lexicon.ttl</a> and put under "matollFiles" in the project folder OR look in <a href="http://dblexipedia.org/download">official website of MATOLL</a> to download "TTL" files.

3) Download the following datasets, uncompress them to folder "dbpediaFiles", (the folder already includes "dbpedia_2015-04.nt") :


<a href="http://downloads.dbpedia.org/2015-04/core-i18n/en/labels_en.nt.bz2" >labels_en.nt.bz2</a> </p>
<a href="http://downloads.dbpedia.org/2015-04/core-i18n/en/mappingbased-properties_en.nt.bz2">mappingbased-properties_en.nt.bz2</a> </p>
<a href="http://downloads.dbpedia.org/2015-04/core-i18n/en/infobox-property-definitions_en.nt.bz2">infobox-property-definitions_en.nt.bz2</a> </p>
<a href="http://downloads.dbpedia.org/2015-04/core-i18n/en/persondata_en.nt.bz2">persondata_en.nt.bz2</a> </p>
<a href="http://downloads.dbpedia.org/2015-04/core-i18n/en/redirects_en.nt.bz2">redirects_en.nt.bz2</a> </p>
<a href="http://downloads.dbpedia.org/2015-04/core-i18n/en/infobox-properties_en.nt.bz2">infobox-properties_en.nt.bz2</a> </p>



<b>
CREATE Index Files </b>

Run  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/CreateIndexes.java">CreateIndexes</a> to create indexes




<b>
RETRIEVAL of Data from Indexes </b>

Check  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/TestSearch.java">TestSearch</a> for searching over created indexes




