/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Community;
import org.dspace.core.Context;
import java.sql.SQLException;


/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class CommunityEntity extends CommunityEntityId {

   @EntityId private int id;
   @EntityFieldRequired private String name;
   @EntityFieldRequired private Boolean canEdit;
   private String handle;
   private int type;
   private int countItems;
  // TODO check metadata possibility

   
   public CommunityEntity(String uid, Context context) throws SQLException {
       Community res = Community.find(context, Integer.parseInt(uid));
       this.id = res.getID();
       this.canEdit = res.canEditBoolean();
       this.handle = res.getHandle();
       this.name = res.getName();
       this.type = res.getType();
   }

   public CommunityEntity(Community community) throws SQLException {
        this.canEdit = community.canEditBoolean();
        this.handle = community.getHandle();
        this.name = community.getName();
        this.type = community.getType();
        this.id = community.getID();
        this.countItems = community.countItems();
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

    public int getId() {
       return this.id;
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
