/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.EntityView;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityCustomAction;
import org.dspace.content.Collection;
import org.apache.log4j.Logger;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.exception.EntityException;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.rest.util.UtilHelper;
import org.dspace.app.webui.components.RecentSubmissionsException;

/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class CollectionsProvider extends AbstractBaseProvider implements  CoreEntityProvider {

    private static Logger log = Logger.getLogger(UserEntityProvider.class);

    public CollectionsProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "collections";
    }

    @EntityCustomAction(action="communities", viewKey=EntityView.VIEW_SHOW)
    public Object communities(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo()+"communities_action:"+reference.getId());

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        String id = reference.getId();        

        if (entityExists(reference.getId()))
            return UtilHelper.getObjects(id, context, UtilHelper.COMMUNITIES_INVOLVED, idOnly);

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    @EntityCustomAction(action="items", viewKey=EntityView.VIEW_SHOW)
    public Object items(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo()+"items_action:"+reference.getId());

        String id = reference.getId();
        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        if (entityExists(reference.getId()))
            return UtilHelper.getObjects(id, context, UtilHelper.ITEMS_INVOLVED, idOnly, in_archive);

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    public boolean entityExists(String id)  {
        log.info(userInfo()+"entity_exists:" + id);

        // sample entity
        if (id.equals(":ID:"))
            return true;
        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        boolean result = false;
        try {
            Collection col = Collection.find(context, Integer.parseInt(id));
            if (col != null)
                result = true;
        } catch (SQLException ex) {
            result = false;
        }

        // close connection to prevent connection problems
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
        if (reference.getId().equals(":ID:"))
            return new CollectionEntity();

        if (reference.getId() == null) {
            return new CollectionEntity();
        }

         if (entityExists(reference.getId())) {
            try {
                if (idOnly)
                    return new CollectionEntityId(reference.getId(), context);
                else
                    return new CollectionEntity(reference.getId(), context);
            } catch (SQLException ex) {
                throw new IllegalArgumentException("Invalid id:" + reference.getId());
            }
        }

        removeConn(context);
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

        List<Object> entities = new ArrayList<Object>();

          try {
            Collection[] collections = null;
            collections = Collection.findAll(context);
            for (Collection c : collections)
                entities.add(idOnly ? new CollectionEntityId(c) : new CollectionEntity(c));
            }
            catch (Exception ex) { ex.printStackTrace(); };

        removeConn(context);
        return entities;
    }

    public Object getSampleEntity() {
        return new CollectionEntity();
    }
}
