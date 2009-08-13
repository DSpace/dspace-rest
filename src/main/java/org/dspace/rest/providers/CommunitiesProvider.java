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
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.rest.util.CommunityHelper;
import org.dspace.app.webui.components.RecentSubmissionsException;

/**
 *
 */
public class CommunitiesProvider extends AbstractBaseProvider implements  CoreEntityProvider  {

    public CommunitiesProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "communities";
    }


    @EntityCustomAction(action="parents", viewKey=EntityView.VIEW_SHOW)
    public Object parents(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        refreshParams();

        if (entityExists(reference.getId())) 
            return CommunityHelper.getObjects(id, context, CommunityHelper.PARENTS, idOnly, immediateOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    @EntityCustomAction(action="children", viewKey=EntityView.VIEW_SHOW)
    public Object children(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        refreshParams();

        if (entityExists(reference.getId()))
            return CommunityHelper.getObjects(id, context, CommunityHelper.CHILDREN, idOnly, immediateOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    @EntityCustomAction(action="collections", viewKey=EntityView.VIEW_SHOW)
    public Object collections(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        refreshParams();

        if (entityExists(reference.getId())) 
            return CommunityHelper.getObjects(id, context, CommunityHelper.COLLECTIONS, idOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    @EntityCustomAction(action="recent", viewKey=EntityView.VIEW_SHOW)
    public Object recentSubmissions(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        refreshParams();

        if (entityExists(reference.getId()))
            return CommunityHelper.getObjects(id, context, CommunityHelper.RECENT_SUBMISSIONS, idOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    public boolean entityExists(String id)  {
        // sample entity
        if (id.equals(":ID:"))
            return true;

        boolean result = false;
        try {
            Community comm = Community.find(context, Integer.parseInt(id));
            if (comm != null)
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
            return new CommunityEntity();

        if (reference.getId() == null) {
            return new CommunityEntity();
        }
        if (entityExists(reference.getId())) {
            try {
                if (idOnly)
                    return new CommunityEntityId(reference.getId(), context);
                else
                    return new CommunityEntity(reference.getId(), context);
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
            Community[] communities = null;
            communities = topLevelOnly ? Community.findAllTop(context) : Community.findAll(context);

            for (Community c: communities)
                entities.add(idOnly ? new CommunityEntityId(c) : new CommunityEntity(c));
            }
     catch (Exception ex) { };

        return entities;
    }

    public Object getSampleEntity() {
        return new CollectionEntity();
    }

}
