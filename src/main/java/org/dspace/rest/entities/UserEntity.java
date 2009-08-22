/*
 * UserEntity.java
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
import org.dspace.eperson.EPerson;
import org.dspace.core.Context;
import java.sql.SQLException;

/**
 * Entity describing users registered on the system
 * @see UserEntityId
 * @see EPerson
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
//       context.complete();
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

   @Override
   public int getId() {
       return this.id;
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

    @Override
    public int compareTo(Object o1){
        return ((UserEntity)(o1)).getName().compareTo(this.getName()) * -1;
    }

}
