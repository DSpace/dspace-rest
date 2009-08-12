/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.providers;

import java.util.ArrayList;
import java.util.List;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.dspace.eperson.EPerson;
import java.sql.SQLException;
import org.dspace.rest.entities.*;

/**
 * 
 */
public class UserEntityProvider extends AbstractBaseProvider implements CoreEntityProvider {

    public UserEntityProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "users";
    }


    public boolean entityExists(String id)  {
        // sample entity
        if (id.equals(":ID:"))
            return true;
        
        boolean result = false;
        try {
            EPerson eperson = EPerson.find(context, Integer.parseInt(id));
            if (eperson != null)
                result = true;
        } catch (SQLException ex) {
            result = false;
        }
        return result;
    }


    public Object getEntity(EntityReference reference) {
         refreshParams();
        // sample entity
        if (reference.getId().equals(":ID:"))
            return new UserEntity();


        if (reference.getId() == null) {
            System.out.println(" it is null ");
            return new UserEntity();
        }
        if (entityExists(reference.getId())) {
            try {
                if (idOnly)
                    return new UserEntityId(reference.getId(), context);
                else
                    return new UserEntity(reference.getId(), context);
            } catch (SQLException ex) {
                throw new IllegalArgumentException("Invalid id:" + reference.getId());
            }
        }
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
          refreshParams();
          List<Object> entities = new ArrayList<Object>();

          try {
            EPerson[] epersons = null;
            if (!(query.equals(""))||(_start != 0)||(_limit !=0))
                epersons = EPerson.search(context, query, _start, _limit);
            else
                epersons = EPerson.findAll(context, sort);
            for (int x=0; x<epersons.length; x++)
                entities.add(idOnly ? new UserEntityId(epersons[x]) : new UserEntity(epersons[x]));
            }
          catch (Exception ex) {  };

        return entities;
    }


    /**
     * Returns a Entity object with sample data
     */

    public Object getSampleEntity() {
        return new UserEntity();
    }


}
