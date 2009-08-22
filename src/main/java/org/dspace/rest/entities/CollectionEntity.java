/*
 * CollectionEntity.java
 *
 * Version: $Revision$
 *
 * Date: $Date$
 *
 * Copyright (c) 2002-2009, The DSpace Foundation.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the DSpace Foundation nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
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
 * Entity describing collection
 * @see CollectionEntityId
 * @see Collection
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
