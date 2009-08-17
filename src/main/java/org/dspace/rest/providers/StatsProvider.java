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
import org.dspace.core.Context;
import org.sakaiproject.entitybus.exception.EntityException;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.dspace.rest.entities.*;

/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class StatsProvider extends AbstractBaseProvider implements  CoreEntityProvider {
    private static Logger log = Logger.getLogger(UserEntityProvider.class);

    public StatsProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "stats";
    }

    public boolean entityExists(String id)  {
        log.info(userInfo()+"entity_exists:" + id);

        // sample entity
        if (id.equals(":ID:"))
            return true;

        return false;
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo()+"get_entity:" + reference.getId());

        // sample entity
        if (reference.getId().equals(":ID:"))
            return new StatsEntity();

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo()+"list_entities");

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        try {
          List<Object> stat = new ArrayList<Object>();
          stat.add(new StatsEntity(context));
          removeConn(context);
        return stat;
        } catch (SQLException ex) {
            throw new EntityException("Internal Server Error", "SQL Problem", 500);
        }
  }

}
