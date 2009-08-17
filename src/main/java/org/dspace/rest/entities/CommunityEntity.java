/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Community;
import org.dspace.content.Collection;
import org.dspace.core.Context;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;


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
   List<Object> collections = new ArrayList<Object>();
   List<Object> subCommunities = new ArrayList<Object>();
   Object parent;
   
   public CommunityEntity(String uid, Context context) throws SQLException {
       Community res = Community.find(context, Integer.parseInt(uid));
       this.id = res.getID();
       this.canEdit = res.canEditBoolean();
       this.handle = res.getHandle();
       this.name = res.getName();
       this.type = res.getType();
       Collection[] cols = res.getCollections();
       for (Collection c : cols)
           collections.add(new CollectionEntity(c));
        Community[] coms = res.getSubcommunities();
        for (Community c : coms)
            this.subCommunities.add(new CommunityEntity(c));
        try {
            this.parent = new CommunityEntity(res.getParentCommunity());
        } catch (NullPointerException ex) { this.parent = null; };
       context.complete();
   }

   public CommunityEntity(Community community) throws SQLException {
        // check calling package/class in order to prevent chaining
        boolean includeFull = false;
        try {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            if ((ste.length > 1) && (ste[1].getClassName().contains("org.dspace.rest.providers")))
                includeFull = true;
        } catch (Exception ex) { System.out.println(ex.getMessage()); }

        this.canEdit = community.canEditBoolean();
        this.handle = community.getHandle();
        this.name = community.getName();
        this.type = community.getType();
        this.id = community.getID();
        this.countItems = community.countItems();
        Collection[] cols = community.getCollections();
        for (Collection c : cols)
            collections.add(includeFull ? new CollectionEntity(c) : new CollectionEntityId(c));
        Community[] coms = community.getSubcommunities();
        for (Community c : coms)
            subCommunities.add(includeFull ? new CommunityEntity(c) : new CommunityEntityId(c));
        try {
            this.parent = includeFull ? new CommunityEntity(community.getParentCommunity()) : new CommunityEntityId(community.getParentCommunity());
        } catch (NullPointerException ne) { this.parent = null; }
   }

   public CommunityEntity() {
        // check calling package/class in order to prevent chaining
        boolean includeFull = false;
        try {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            if ((ste.length > 1) && (ste[1].getClassName().contains("org.dspace.rest.providers")))
                includeFull = true;
        } catch (Exception ex) { System.out.println(ex.getMessage()); }

        this.canEdit = true;
        this.handle = "123456789/0";
        this.name = "Community Name";
        this.type = 5;
        this.id = 6;
        this.countItems = 1001;
        this.collections.add(includeFull ? new CollectionEntity() : new CollectionEntityId());
        this.subCommunities.add(includeFull ? new CommunityEntity() : new CommunityEntityId());
        this.parent = includeFull ? new CommunityEntity() : new CommunityEntityId();
   }


   public List<?> getCollections() {
       return this.collections;
   }
   
   public List<?> getSubCommunities() {
       return this.subCommunities;
   }
   
   public Object getParentCommunity() {
       return this.parent;
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

   @Override
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
