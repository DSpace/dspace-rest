/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Bitstream;
import org.dspace.core.Context;
import java.sql.SQLException;


/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class BitstreamEntityId {
   @EntityId private int id;

   protected BitstreamEntityId() {

   }

   public BitstreamEntityId (String uid, Context context) throws SQLException {
       Bitstream res = Bitstream.find(context, Integer.parseInt(uid));
       this.id = res.getID();
       context.complete();
   }

    public BitstreamEntityId(Bitstream bitstream) throws SQLException {
        this.id = bitstream.getID();
   }

   public int getId() {
       return this.id;
   }

   @Override
   public boolean equals(Object obj) {
      if (null == obj)
         return false;
      if (!(obj instanceof BitstreamEntityId))
         return false;
      else {
         BitstreamEntityId castObj = (BitstreamEntityId) obj;
            return (this.id == castObj.id);
      }
   }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "id:" + this.id;
    }

}
