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
   @EntityFieldRequired private String metadata;
   

   //protected CommunityEntity() { };
   
   public CommunityEntity(String uid) //throws java.sql.SQLException {
   {
       this.id = uid;
       Community res = null;
       try {
       Context context = new Context();
       res = Community.find(context, Integer.parseInt(uid)); }
       catch (Exception ex) { };
       //this.name = res.getName();
       this.name = "ime";
       this.canEdit = true;
       this.handle="hendl;";

       if (res != null) {
        try {
            this.canEdit = res.canEditBoolean();
        } catch (Exception ex) { };
        this.handle = res.getHandle();
        this.name = res.getName();
        this.type = res.getType();
        //this.metadata = res.getMetadata("");
       };
       
   }


   public CommunityEntity (boolean onlyTop) {

   
   }

   public String getName() {
       return this.name;
   }

   public String getHandle() {
       return this.handle;
   }

    @Override
    public String toString() {
        return "id:" + this.id + ", stuff.....";
    }


}
