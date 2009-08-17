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
import org.dspace.eperson.EPerson;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.apache.log4j.Logger;

/**
 * 
 */
public class UserEntityProvider extends AbstractBaseProvider implements CoreEntityProvider {

    private static Logger log = Logger.getLogger(UserEntityProvider.class);

    public UserEntityProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "users";
    }

    public boolean entityExists(String id) {
        log.info(userInfo()+"entity_exists:" + id);

        // sample entity
        if (id.equals(":ID:")) {
            return true;
        }

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        boolean result = false;
        try {
            EPerson eperson = EPerson.find(context, Integer.parseInt(id));
            if (eperson != null) {
                result = true;
            }
        } catch (SQLException ex) {
            result = false;
        }

        removeConn(context);
        return result;
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo()+"get_entity:" + reference.getId());

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        // sample entity
        if (reference.getId().equals(":ID:")) {
            return new UserEntity();
        }


        if (reference.getId() == null) {
            return new UserEntity();
        }

        if (entityExists(reference.getId())) {
            try {
                if (idOnly) {
                    return new UserEntityId(reference.getId(), context);
                } else {
                    return new UserEntity(reference.getId(), context);
                }
            } catch (SQLException ex) {
                throw new IllegalArgumentException("Invalid id:" + reference.getId());
            }
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo()+"list_entities:");

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        List<Object> entities = new ArrayList<Object>();

        try {
            EPerson[] epersons = null;
            if (!(query.equals("")) || (_start != 0) || (_limit != 0)) {
                epersons = EPerson.search(context, query, _start, _limit);
            } else {
                epersons = EPerson.findAll(context, sort);
            }
            for (int x = 0; x < epersons.length; x++) {
                entities.add(idOnly ? new UserEntityId(epersons[x]) : new UserEntity(epersons[x]));
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL erorr", 500);
        }

        removeConn(context);
        return entities;
    }

    /**
     * Returns a Entity object with sample data
     */
    public Object getSampleEntity() {
        return new UserEntity();
    }
}
