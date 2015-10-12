Creates DBpedia Linking  on Lucene Index.

1) Download DBpedia files  in NTriple format (e.g.dbpedia_ontology.nt) and put them under directory "dbpediaFiles"

2) Put wikipedia anchor file under "anchorFiles"

	Wikipedia anchor file is formatted e.g. tab seperated line (Label, URI, frequency)

		Ethiopia	http://dbpedia.org/resource/Ethiopia	200
3) Put MATOLL lexicon file in Turtle (*.ttl) format under "matollFiles"

4) Check  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/TestIndexCreator.java">TestIndexCreator</a> for creating indexes

4) Check  <a href="https://github.com/ag-sc/DBpediaLinking/blob/master/src/main/java/test/TestIndexSearch.java">TestIndexSearch</a> for searching over created indexes
