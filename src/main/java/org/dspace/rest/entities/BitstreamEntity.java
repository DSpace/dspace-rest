/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Item;
import org.dspace.content.Bundle;
import org.dspace.content.Bitstream;
import org.dspace.core.Context;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;


/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class BitstreamEntity extends BitstreamEntityId {

   @EntityId private int id;
   @EntityFieldRequired private String name;
   private String handle;
   private int type, storeNumber;
   private long sequenceId, size;
   List<String> bundles = new ArrayList<String>();
   private String checkSumAlgorithm, description, checkSum,
           formatDescription, source, userFormatDescription;

   

   // TODO inspect and add additional fields


   public BitstreamEntity(String uid, Context context) throws SQLException {
       Bitstream res = Bitstream.find(context, Integer.parseInt(uid));
       Bundle[] bun = res.getBundles();
       this.id = res.getID();
       this.handle = res.getHandle();
       this.name = res.getName();
       this.type = res.getType();
       for (Bundle b : bun)
           this.bundles.add(b.getName());
       Bundle b;
       this.checkSum = res.getChecksum();
       this.checkSumAlgorithm = res.getChecksumAlgorithm();
       this.description = res.getDescription();
       this.formatDescription = res.getFormatDescription();
       this.sequenceId = res.getSequenceID();
       this.size = res.getSize();
       this.source = res.getSource();
       this.storeNumber = res.getStoreNumber();
       this.userFormatDescription = res.getUserFormatDescription();

   }

   public BitstreamEntity(Bitstream item) throws SQLException {
        this.handle = item.getHandle();
        this.name = item.getName();
        this.type = item.getType();
        this.id = item.getID();
        Bundle[] bun = item.getBundles();
        for (Bundle b : bun)
            this.bundles.add(b.getName());
       this.checkSum = item.getChecksum();
       this.checkSumAlgorithm = item.getChecksumAlgorithm();
       this.description = item.getDescription();
       this.formatDescription = item.getFormatDescription();
       this.sequenceId = item.getSequenceID();
       this.size = item.getSize();
       this.source = item.getSource();
       this.storeNumber = item.getStoreNumber();
       this.userFormatDescription = item.getUserFormatDescription();

   }

   public String getCheckSum() {
       return this.checkSum;
   }

   public String getCheckSumAlgorithm() {
       return this.checkSumAlgorithm;
   }

   public String getDescription() {
       return this.description;
   }

   public String getFormatDescription() {
       return this.formatDescription;
   }

   public long getSequenceId() {
       return this.sequenceId;
   }

   public long getSize() {
       return this.size;
   }

   public String getSource() {
       return this.source;
   }

   public int getStoreNumber() {
       return this.storeNumber;
   }

   public String getUserFormatDescription() {
       return this.userFormatDescription;
   }

   public String getName() {
       return this.name;
   }

   public String getHandle() {
       return this.handle;
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
