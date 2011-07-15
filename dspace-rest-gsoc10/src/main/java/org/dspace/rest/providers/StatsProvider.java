/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
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
 * Provides interface for access to basic statistic data
 * @see StatsEntity
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class StatsProvider extends AbstractBaseProvider implements CoreEntityProvider {

    private static Logger log = Logger.getLogger(UserProvider.class);

    public StatsProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "stats";
    }

    /**
     * By default in this provider there is no particular entity
     * @param id
     * @return
     */
    public boolean entityExists(String id) {
        log.info(userInfo() + "entity_exists:" + id);

        // sample entity
        if (id.equals(":ID:")) {
            return true;
        }

        return false;
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_entity:" + reference.getId());

        // sample entity
        if (reference.getId().equals(":ID:")) {
            return new StatsEntity();
        }

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    /**
     * Here are statistical data extracted and returned as the list
     * StatsEntity is used here to format and present data
     * It could be done using HashMap for now it is not functioning, updates
     * of related software needed
     * @see StatsEntity
     * @param ref
     * @param search
     * @return
     */
    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_entities");

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


    // TODO CHANGE
    public Object getSampleEntity() {
        return new CollectionEntity();
    }
}
