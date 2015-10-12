package de.citec.sc.query;

import java.util.List;

public interface QueryProcessor {
    
	public List<String> getMatches(String label);
        
        public List<Instance> getTopMatches(String label, int k);
        
	public void initIndexDirectory(String indexDirectory);
}
