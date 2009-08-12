/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Item;
import org.dspace.content.Bundle;
import org.dspace.content.Bitstream;
import org.dspace.content.Collection;
import org.dspace.content.Community;

import org.dspace.core.Context;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.Date;


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
   List<Object> bundles = new ArrayList<Object>();
   List<Object> bitstreams = new ArrayList<Object>();
   List<Object> collections = new ArrayList<Object>();
   List<Object> communities = new ArrayList<Object>();
   Date lastModified;
   Collection owningCollection;
   boolean isArchived, isWithdrawn;
   UserEntity submitter;



   // TODO inspect and add additional fields


   public ItemEntity(String uid, Context context) throws SQLException {
       Item res = Item.find(context, Integer.parseInt(uid));
       this.id = res.getID();
       this.canEdit = res.canEdit();
       this.handle = res.getHandle();
       this.name = res.getName();
       this.type = res.getType();
       this.lastModified = res.getLastModified();
       this.owningCollection = res.getOwningCollection();
       this.isArchived = res.isArchived();
       this.isArchived = res.isWithdrawn();
       this.submitter = new UserEntity(res.getSubmitter());
       Bundle[] bun = res.getBundles();
       Bitstream[] bst = res.getNonInternalBitstreams();
       Collection[] col = res.getCollections();
       Community[] com = res.getCommunities();
       for (Bundle b : bun)
           this.bundles.add(new BundleEntity(b));
       for (Bitstream b : bst)
           this.bitstreams.add(new BitstreamEntity(b));
       for (Collection c : col)
           this.collections.add(new CollectionEntity(c));
       for (Community c : com)
           this.communities.add(new CommunityEntity(c));
   }

   public ItemEntity(Item item) throws SQLException {
       // check calling package/class in order to prevent chaining
       boolean includeFull = false;
       try {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            if ((ste.length > 1) && (ste[1].getClassName().contains("org.dspace.rest.providers")))
                includeFull = true;
        } catch (Exception ex) { System.out.println(ex.getMessage()); }

        this.canEdit = item.canEdit();
        this.handle = item.getHandle();
        this.name = item.getName();
        this.type = item.getType();
        this.id = item.getID();
        this.lastModified = item.getLastModified();
        this.owningCollection = item.getOwningCollection();
        this.isArchived = item.isArchived();
        this.isWithdrawn = item.isWithdrawn();
        this.submitter = new UserEntity(item.getSubmitter());
        Bundle[] bun = item.getBundles();
        Bitstream[] bst = item.getNonInternalBitstreams();
        Collection[] col = item.getCollections();
        Community[] com = item.getCommunities();
        for (Bundle b : bun)
            this.bundles.add(includeFull ? new BundleEntity(b) : new BundleEntityId(b));
        for (Bitstream b : bst)
            this.bitstreams.add(includeFull ? new BitstreamEntity(b) : new BitstreamEntity(b));
        for (Collection c : col)
            this.collections.add(includeFull ? new CollectionEntity(c) : new CollectionEntityId(c));
        for (Community c : com)
            this.communities.add(includeFull ? new CommunityEntity(c) : new CommunityEntityId(c));
   }

   public ItemEntity() {
        this.canEdit = false;
        this.handle = "123456789/0";
        this.name = "Item";
        this.type = 3;
        this.id = 22;
        this.bundles.add(new BundleEntity());
        this.bitstreams.add(new BitstreamEntity());
        this.collections.add(new CollectionEntity());
        this.communities.add(new CommunityEntity());
   }

   public UserEntity getSubmitter() {
       return this.submitter;
   }
   public boolean getIsArchived() {
       return this.isArchived;
   }

   public boolean getIsWithdrawn() {
       return this.isWithdrawn;
   }

   public Collection getOwningCollection() {
       return this.getOwningCollection();
   }

   public Date getLastModified() {
       return this.lastModified;
   }

   public List getCollections() {
       return this.collections;
   }

   public List getCommunities() {
       return this.communities;
   }
   
   public String getName() {
       return this.name;
   }

   public List getBitstreams() {
       return this.bitstreams;
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

   public List getBundles() {
       return this.bundles;
   }

    @Override
    public String toString() {
        return "id:" + this.id + ", stuff.....";
    }

}
