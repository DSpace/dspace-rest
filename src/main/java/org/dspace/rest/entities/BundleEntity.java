/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Bundle;
import org.dspace.content.Bitstream;
import org.dspace.core.Context;
import org.dspace.content.Item;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;


/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class BundleEntity extends BundleEntityId {

   @EntityId private int id;
   @EntityFieldRequired private String name;
   private String handle;
   private int type, pid;
   List<Object> bitstreams = new ArrayList<Object>();
   List<Object> items = new ArrayList<Object>();


   public BundleEntity(String uid, Context context) throws SQLException {
       Bundle res = Bundle.find(context, Integer.parseInt(uid));
       Bitstream[] bst = res.getBitstreams();
       for (Bitstream b : bst)
           this.bitstreams.add(new BitstreamEntity(b));
       this.pid = res.getPrimaryBitstreamID();
       this.id = res.getID();
       this.handle = res.getHandle();
       this.name = res.getName();
       this.type = res.getType();
       Item[] itm = res.getItems();
       for (Item i : itm)
           this.items.add(new ItemEntity(i));
       context.complete();
   }

   public BundleEntity(Bundle bundle) throws SQLException {
        // check calling package/class in order to prevent chaining
        boolean includeFull = false;
        try {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            if ((ste.length > 1) && (ste[1].getClassName().contains("org.dspace.rest.providers")))
                includeFull = true;
        } catch (Exception ex) { System.out.println(ex.getMessage()); }

        this.handle = bundle.getHandle();
        this.name = bundle.getName();
        this.type = bundle.getType();
        this.id = bundle.getID();
        this.pid = bundle.getPrimaryBitstreamID();
        Bitstream[] bst = bundle.getBitstreams();
        Item[] itm = bundle.getItems();
        for (Bitstream b : bst)
            this.bitstreams.add(includeFull ? new BitstreamEntity(b) : new BitstreamEntityId(b));
        for (Item i : itm)
            this.items.add(includeFull ? new ItemEntity(i) : new ItemEntityId(i));
   }

   public BundleEntity() {
        // check calling package/class in order to prevent chaining
        boolean includeFull = false;
        try {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            if ((ste.length > 1) && (ste[1].getClassName().contains("org.dspace.rest.providers")))
                includeFull = true;
        } catch (Exception ex) { System.out.println(ex.getMessage()); }

        this.handle = "123456789/0";
        this.name = "Sample bundle";
        this.type = 1;
        this.pid = 10;
        this.id = 2;
        this.bitstreams.add(includeFull ? new BitstreamEntity() : new BitstreamEntityId());
        this.items.add(includeFull ? new ItemEntity() : new ItemEntityId());
   }

   public List<?> getItems() {
       return this.items;
   }
   
   public int getPrimaryBitstreamId() {
       return this.pid;
   }
   
   public String getName() {
       return this.name;
   }

   public String getHandle() {
       return this.handle;
   }

   @Override
   public int getId() {
       return this.id;
   }

   public int getType() {
      return this.type;
   }

   public List getBitstreams() {
       return this.bitstreams;
   }

   @Override
   public String toString() {
       return "id:" + this.id + ", stuff.....";
   }

}
