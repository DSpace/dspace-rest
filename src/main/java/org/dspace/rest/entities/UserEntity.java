/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.eperson.EPerson;
import org.dspace.core.Context;
import java.sql.SQLException;

/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class UserEntity extends UserEntityId {

   @EntityFieldRequired private String name;
   private Boolean requireCertificate, selfRegistered;
   private String handle, email, firstName, lastName, fullName,
           language, netId;
   private int type;

   public UserEntity(String uid, Context context) throws SQLException {
       super(uid, context);
       this.handle = res.getHandle();
       this.name = res.getName();
       this.type = res.getType();
       this.email = res.getEmail();
       this.firstName = res.getFirstName();
       this.fullName = res.getFullName();
       this.requireCertificate = res.getRequireCertificate();
       this.selfRegistered = res.getSelfRegistered();
       this.language = res.getLanguage();
       this.lastName = res.getLastName();
       this.netId = res.getNetid();
   }    

   public UserEntity(EPerson eperson) throws SQLException {
        super(eperson);
        try {
            this.handle = eperson.getHandle();
            this.name = eperson.getName();
            this.type = eperson.getType();
            this.email = eperson.getEmail();
            this.firstName = eperson.getFirstName();
            this.fullName = eperson.getFullName();
            this.requireCertificate = eperson.getRequireCertificate();
            this.selfRegistered = eperson.getSelfRegistered();
            this.language = eperson.getLanguage();
            this.lastName = eperson.getLastName();
            this.netId = eperson.getNetid();
        }
        catch (Exception ex) { }
   }

   public UserEntity() {
       this.id = 111;
       this.handle = "123456789/0";
       this.name = "John";
       this.type = 7;
       this.email = "john.smith@johnsemail.com";
       this.firstName = "John";
       this.fullName = "John Smith";
       this.requireCertificate = false;
       this.selfRegistered = true;
       this.language = "en";
       this.lastName = "Smith";
       this.netId = "1";
   }

   public String getName() {
       return this.name;
   }

   public String getHandle() {
       return this.handle;
   }

   public String getEmail() {
       return this.email;
   }

   public String getFirstName() {
       return this.firstName;
   }

   public String getFullName() {
       return this.fullName;
   }
   
   public int getType() {
      return this.type;
   }
   
   public String getLastName() {
       return this.lastName;
   }

   public String getLanguage() {
       return this.language;
   }

   public String getNetId() {
       return this.netId;
   }

   public boolean getRequireCertificate() {
       return this.requireCertificate;
   }

   public boolean getSelfRegistered() {
       return this.selfRegistered;
   }

    @Override
    public String toString() {
        return "id:" + this.id + ", full_name:" + this.fullName;
    }

}
