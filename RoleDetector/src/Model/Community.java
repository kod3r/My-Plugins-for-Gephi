/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/*
 Copyright 2013 DOREMUS
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net
*/

public class Community implements Comparable<Community> {

    private Integer size;
    private String label;
    private Integer id;
    private String localStar;

    public Community() {
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void incrementSize() {
        if (size == null) {
            size = 1;
        } else {
            size++;
        }
    }

    public String getLocalStar() {
        return localStar;
    }

    public void setLocalStar(String localStar) {
        this.localStar = localStar;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final Community other = (Community) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Community o) {
        if (this.size > o.size) {
            return 1;
        } else {
            return 0;
        }
    }
}
