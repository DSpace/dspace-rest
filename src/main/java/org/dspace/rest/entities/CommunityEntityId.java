/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Community;
import org.dspace.core.Context;
import java.sql.SQLException;


/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class CommunityEntityId {
   @EntityId private int id;

   protected CommunityEntityId() {
       
   }

   public CommunityEntityId (String uid, Context context) throws SQLException {
       Community res = Community.find(context, Integer.parseInt(uid));
       this.id = res.getID();
   }

    public CommunityEntityId(Community community) throws SQLException {
        this.id = community.getID();
   }

   public int getId() {
       return this.id;
   }

   @Override
   public boolean equals(Object obj) {
      if (null == obj)
         return false;
      if (!(obj instanceof CommunityEntityId))
         return false;
      else {
         CommunityEntityId castObj = (CommunityEntityId) obj;
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
