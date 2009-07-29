/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Collection;
import org.dspace.core.Context;
import java.sql.SQLException;


/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class CollectionEntity {

   @EntityId private int id;
   @EntityFieldRequired private String name;
   @EntityFieldRequired private Boolean canEdit;
   private String handle;
   private int type;
   private int countItems;
  // TODO inspect and add additional fields


   public CollectionEntity(String uid, Context context) throws SQLException {
       Collection res = Collection.find(context, Integer.parseInt(uid));
       this.id = res.getID();
       this.canEdit = res.canEditBoolean();
       this.handle = res.getHandle();
       this.name = res.getName();
       this.type = res.getType();
   }

   public CollectionEntity(Collection collection) throws SQLException {
        this.canEdit = collection.canEditBoolean();
        this.handle = collection.getHandle();
        this.name = collection.getName();
        this.type = collection.getType();
        this.id = collection.getID();
        this.countItems = collection.countItems();
   }


   public String getName() {
       return this.name;
   }

   public String getHandle() {
       return this.handle;
   }

   public boolean canEdit() {
       return this.canEdit;
   }


   public int getType() {
      return this.type;
   }

   public int getCountItems() {
       return this.countItems;
   }

    @Override
    public String toString() {
        return "id:" + this.id + ", stuff.....";
    }


}
