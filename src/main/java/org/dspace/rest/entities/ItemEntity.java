/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Item;
import org.dspace.core.Context;
import java.sql.SQLException;


/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class ItemEntity {

   @EntityId private int id;
   @EntityFieldRequired private String name;
   @EntityFieldRequired private Boolean canEdit;
   private String handle;
   private int type;
  // TODO inspect and add additional fields


   public ItemEntity(String uid, Context context) throws SQLException {
       Item res = Item.find(context, Integer.parseInt(uid));
       this.id = res.getID();
       this.canEdit = res.canEdit();
       this.handle = res.getHandle();
       this.name = res.getName();
       this.type = res.getType();
   }

   public ItemEntity(Item item) throws SQLException {
        this.canEdit = item.canEdit();
        this.handle = item.getHandle();
        this.name = item.getName();
        this.type = item.getType();
        this.id = item.getID();
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


    @Override
    public String toString() {
        return "id:" + this.id + ", stuff.....";
    }



}
