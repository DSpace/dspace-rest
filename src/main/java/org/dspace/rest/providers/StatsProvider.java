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
import java.sql.SQLException;
import org.dspace.rest.entities.*;

/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class StatsProvider extends AbstractBaseProvider implements  CoreEntityProvider {


    public StatsProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "stats";
    }

    public boolean entityExists(String id)  {
        // sample entity
        if (id.equals(":ID:"))
            return true;

        return false;
    }

    public Object getEntity(EntityReference reference) {
         refreshParams();
        // sample entity
        if (reference.getId().equals(":ID:"))
            return new StatsEntity();

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        refreshParams();
        try {
          List<Object> stat = new ArrayList<Object>();
          stat.add(new StatsEntity(context));
        return stat;
        } catch (Exception ex) { System.out.println(ex.getMessage() + " greeska "); };
        return null;
    }

}
