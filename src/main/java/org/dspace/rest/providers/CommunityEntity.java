/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.providers;


import org.sakaiproject.entitybus.entityprovider.annotations.EntityDateCreated;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityLastModified;
import org.dspace.content.Community;
import org.dspace.core.Context;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

/**
 *
 * @author bojan
 */
public class CommunityEntity  {
   @EntityId private String id;
   @EntityFieldRequired private String name;
   @EntityFieldRequired private Boolean canEdit;
   private String handle;
   private int type;
   private int countItems;
  // TODO check metadata possibility

   protected CommunityEntity() {};

   public CommunityEntity(String uid, Context context) throws SQLException {
       this.id = uid;

       Community res = Community.find(context, Integer.parseInt(uid));

       this.canEdit = res.canEditBoolean();
       this.handle = res.getHandle();
       this.name = res.getName();
       this.type = res.getType();
       this.countItems = res.countItems();
   }

   public CommunityEntity(Community community) throws SQLException {
        this.canEdit = community.canEditBoolean();
        this.handle = community.getHandle();
        this.name = community.getName();
        this.type = community.getType();
        this.id = Integer.toString(community.getID());
        this.countItems = community.countItems();
   }


   public String getName() {
       return this.name;
   }

   public String getHandle() {
       return this.handle;
   }

   public String getId() {
       return this.id;
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
