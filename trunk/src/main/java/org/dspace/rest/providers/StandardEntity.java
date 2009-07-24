/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.providers;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityDateCreated;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityLastModified;

/**
 * This is a sample entity object for testing, it is a bean with no default values and comparison
 * overrides
 *
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class StandardEntity {

    @EntityId private String id;
    @EntityFieldRequired private String stuff;
    private int number;
    public String extra;
    @EntityDateCreated protected long dateCreated;
    @EntityLastModified protected long lastModified;

    /**
     * Basic empty constructor
     */
    protected StandardEntity() { }

    public StandardEntity(String id, String stuff) {
        this(id, stuff, 0);
    }

    public StandardEntity(String stuff, int number) {
        this(null, stuff, number);
    }

    public StandardEntity(String id, String stuff, int number) {
        this.id = id;
        this.stuff = stuff;
        this.number = number;
        this.dateCreated = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStuff() {
        return stuff;
    }

    public void setStuff(String stuff) {
        this.stuff = stuff;
        this.lastModified = System.currentTimeMillis();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        this.lastModified = System.currentTimeMillis();
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public long getLastModified() {
        return lastModified;
    }

    /**
     * @return a copy of this object
     */
    public StandardEntity copy() {
        return copy(this);
    }

    /**
     * @return a copy of the supplied object
     */
    public static StandardEntity copy(StandardEntity me) {
        if (me == null) {
            throw new IllegalArgumentException("entity to copy must not be null");
        }
        StandardEntity togo = new StandardEntity(me.id, me.stuff, me.number);
        togo.extra = me.extra;
        return togo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StandardEntity other = (StandardEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "id:" + this.id + ", stuff:" + this.stuff + ", number:" + number;
    }

}
