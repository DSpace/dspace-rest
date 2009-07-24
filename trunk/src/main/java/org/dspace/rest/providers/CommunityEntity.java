/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.providers;


import org.sakaiproject.entitybus.entityprovider.annotations.EntityDateCreated;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityLastModified;

/**
 *
 * @author bojan
 */
public class CommunityEntity {
   @EntityId private String id;
   private String name;
   

}
