/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.query;

import java.util.Objects;

/**
 *
 * @author sherzod
 */
public class Instance implements Comparable<Instance> {

    private String uri;
    private int freq;
    private String pos;
    private String onProperty;

    public Instance(String uri, int freq) {
        this.uri = uri;
        this.freq = freq;
    }

    @Override
    public int compareTo(Instance o) {
        if (freq > o.freq) {
            return -1;
        } else if (freq < o.freq) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Instance{" + "uri=" + uri + ", freq=" + freq + ", pos=" + pos + ", onProperty=" + onProperty + '}';
    }

    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.uri);
        hash = 29 * hash + this.freq;
        hash = 29 * hash + Objects.hashCode(this.pos);
        hash = 29 * hash + Objects.hashCode(this.onProperty);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Instance other = (Instance) obj;
        if (!Objects.equals(this.uri, other.uri)) {
            return false;
        }
        if (this.freq != other.freq) {
            return false;
        }
        if (!Objects.equals(this.pos, other.pos)) {
            return false;
        }
        if (!Objects.equals(this.onProperty, other.onProperty)) {
            return false;
        }
        return true;
    }

    

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getOnProperty() {
        return onProperty;
    }

    public void setOnProperty(String onProperty) {
        this.onProperty = onProperty;
    }
    
    

}
