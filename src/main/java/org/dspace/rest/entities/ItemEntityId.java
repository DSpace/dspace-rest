/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */


package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Item;
import org.dspace.core.Context;
import java.sql.SQLException;

/**
 * Entity describing item, basic version
 * @see ItemEntityId
 * @see Item
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class ItemEntityId {

    @EntityId
    private int id;

    protected ItemEntityId() {
    }

    public ItemEntityId(String uid, Context context) throws SQLException {
        Item res = Item.find(context, Integer.parseInt(uid));
        this.id = res.getID();
        //context.complete();
    }

    public ItemEntityId(Item item) throws SQLException {
        this.id = item.getID();
    }

    public int getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof ItemEntityId)) {
            return false;
        } else {
            ItemEntityId castObj = (ItemEntityId) obj;
            return (this.id == castObj.id);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "id:" + this.id;
    }
}
