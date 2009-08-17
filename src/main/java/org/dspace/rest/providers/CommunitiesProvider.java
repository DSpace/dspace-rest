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
import org.dspace.content.Community;
import org.dspace.core.Context;
import org.apache.log4j.Logger;
import org.sakaiproject.entitybus.exception.EntityException;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.rest.util.UtilHelper;
import org.dspace.app.webui.components.RecentSubmissionsException;

/**
 *
 */
public class CommunitiesProvider extends AbstractBaseProvider implements CoreEntityProvider {

    private static Logger log = Logger.getLogger(UserEntityProvider.class);

    public CommunitiesProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "communities";
    }

    @EntityCustomAction(action = "parents", viewKey = EntityView.VIEW_SHOW)
    public Object parents(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo()+"parents_action:"+reference.getId());

        String id = reference.getId();
        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        if (entityExists(reference.getId())) {
            return UtilHelper.getObjects(id, context, UtilHelper.PARENTS, idOnly, immediateOnly);
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    @EntityCustomAction(action = "children", viewKey = EntityView.VIEW_SHOW)
    public Object children(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo()+"children_action:"+reference.getId());

        String id = reference.getId();
        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        if (entityExists(reference.getId())) {
            return UtilHelper.getObjects(id, context, UtilHelper.CHILDREN, idOnly, immediateOnly);
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    @EntityCustomAction(action = "collections", viewKey = EntityView.VIEW_SHOW)
    public Object collections(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo()+"collections_action:"+reference.getId());

        String id = reference.getId();
        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        if (entityExists(reference.getId())) {
            return UtilHelper.getObjects(id, context, UtilHelper.COLLECTIONS, idOnly);
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    @EntityCustomAction(action = "recent", viewKey = EntityView.VIEW_SHOW)
    public Object recentSubmissions(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo()+"recentsubmissions_action:"+reference.getId());

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        String id = reference.getId();

        if (entityExists(reference.getId())) {
            return UtilHelper.getObjects(id, context, UtilHelper.RECENT_SUBMISSIONS, idOnly);
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
            Community comm = Community.find(context, Integer.parseInt(id));
            if (comm != null) {
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

        // sample entity
        if (reference.getId().equals(":ID:")) {
            return new CommunityEntity();
        }

        if (reference.getId() == null) {
            return new CommunityEntity();
        }

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        if (entityExists(reference.getId())) {
            try {
                if (idOnly) {
                    return new CommunityEntityId(reference.getId(), context);
                } else {
                    return new CommunityEntity(reference.getId(), context);
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
            Community[] communities = null;
            communities = topLevelOnly ? Community.findAllTop(context) : Community.findAll(context);

            for (Community c : communities) {
                entities.add(idOnly ? new CommunityEntityId(c) : new CommunityEntity(c));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        removeConn(context);
        return entities;
    }

    public Object getSampleEntity() {
        return new CollectionEntity();
    }
}
