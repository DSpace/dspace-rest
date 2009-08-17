/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.content.Community;
import org.dspace.core.Context;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;


/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class CollectionEntity {

   @EntityId private int id;
   @EntityFieldRequired private String name;
   @EntityFieldRequired private Boolean canEdit;
   private String handle, licence;
   private int type;
   private int countItems;
   List<Object> items = new ArrayList<Object>();
   List<Object> communities = new ArrayList<Object>();

   public CollectionEntity(String uid, Context context) throws SQLException {
       Collection res = Collection.find(context, Integer.parseInt(uid));
       this.id = res.getID();
       this.canEdit = res.canEditBoolean();
       this.handle = res.getHandle();
       this.name = res.getName();
       this.type = res.getType();
       this.licence = res.getLicense();

       ItemIterator i = Item.findAll(context);
       while (i.hasNext()) {
           items.add(new ItemEntity(i.next()));
           }

       for (Community c : res.getCommunities())
           communities.add(new CommunityEntityId(c));
       context.complete();
    }

   public CollectionEntity(Collection collection) throws SQLException {
        // check calling package/class in order to prevent chaining
        boolean includeFull = false;
        try {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            if ((ste.length > 1) && (ste[1].getClassName().contains("org.dspace.rest.providers")))
                includeFull = true;
        } catch (Exception ex) { System.out.println(ex.getMessage()); }

        this.canEdit = collection.canEditBoolean();
        this.handle = collection.getHandle();
        this.name = collection.getName();
        this.type = collection.getType();
        this.id = collection.getID();
        this.countItems = collection.countItems();
        this.licence = collection.getLicense();

        ItemIterator i = collection.getAllItems();
        while (i.hasNext())
            items.add(includeFull ? new ItemEntity(i.next()) : new ItemEntityId(i.next()) );

        for (Community c : collection.getCommunities())
            communities.add(includeFull ? new CommunityEntity(c) : new CommunityEntityId(c));
   }

   public CollectionEntity() {
        // check calling package/class in order to prevent chaining
        boolean includeFull = false;
        try {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            if ((ste.length > 1) && (ste[1].getClassName().contains("org.dspace.rest.providers")))
                includeFull = true;
        } catch (Exception ex) { System.out.println(ex.getMessage()); }

        this.canEdit = false;
        this.handle = "123456789/0";
        this.name = "Sample collection";
        this.type = 4;
        this.id = 92;
        this.countItems = 10921;
        this.licence = "Example licence";
        this.items.add(new ItemEntity());
        this.communities.add(includeFull ? new CommunityEntity() : new CommunityEntityId());
   }

   public String getLicence() {
       return this.licence;
   }

   public List<?> getItems() {
       return this.items;
   }

   public List<?> getCommunities() {
       return this.communities;
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
