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
import org.dspace.core.Context;
import org.dspace.content.Item;
import org.apache.log4j.Logger;
import org.sakaiproject.entitybus.exception.EntityException;
import org.dspace.content.ItemIterator;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.rest.util.UtilHelper;
import org.dspace.app.webui.components.RecentSubmissionsException;

/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class ItemsProvider extends AbstractBaseProvider implements CoreEntityProvider {
    private static Logger log = Logger.getLogger(UserEntityProvider.class);
    
    public ItemsProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "items";
    }

    @EntityCustomAction(action = "permissions", viewKey = EntityView.VIEW_SHOW)
    public Object permissions(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo()+"permissions_action:"+reference.getId());

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        String id = reference.getId();

        if (entityExists(reference.getId())) {
            return UtilHelper.getObjects(id, context, UtilHelper.ITEM_PERMISSION);
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    @EntityCustomAction(action = "communities", viewKey = EntityView.VIEW_SHOW)
    public Object communities(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo()+"permissions_action:"+reference.getId());

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        String id = reference.getId();

        if (entityExists(reference.getId())) {
            return UtilHelper.getObjects(id, context, UtilHelper.ITEM_IN_COMMUNITIES, idOnly);
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    @EntityCustomAction(action = "collections", viewKey = EntityView.VIEW_SHOW)
    public Object collections(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo()+"collections_action:"+reference.getId());

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        String id = reference.getId();

        if (entityExists(reference.getId())) {
            return UtilHelper.getObjects(id, context, UtilHelper.ITEM_IN_COLLECTIONS, idOnly);
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
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
            Item col = Item.find(context, Integer.parseInt(id));
            if (col != null) {
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
            return new ItemEntity();
        }

        if (entityExists(reference.getId())) {
            try {
                if (idOnly) {
                    return new ItemEntityId(reference.getId(), context);
                } else {
                    return new ItemEntity(reference.getId(), context);
                }
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
            ItemIterator items = Item.findAll(context);

            while (items.hasNext()) {
                entities.add(idOnly ? new ItemEntityId(items.next()) : new ItemEntity(items.next()));
            }
        } catch (SQLException ex) {
            throw new EntityException ("Internal server error", "SQL error", 500);
        }

        /*
        if (search.isEmpty()) {
        // return all
        for (StandardEntity myEntity : myEntities.values()) {
        entities.add( myEntity );
        }
        } else {
        // restrict based on search param
        if (search.getRestrictionByProperty("stuff") != null) {
        for (StandardEntity me : myEntities.values()) {
        String sMatch = search.getRestrictionByProperty("stuff").value.toString();
        if (sMatch.equals(me.getStuff())) {
        entities.add(me);
        }
        }
        }
        }
         */
        removeConn(context);
        return entities;
    }

    public Object getSampleEntity() {
        return new ItemEntity();
    }
}
