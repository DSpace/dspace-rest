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
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.rest.util.CommunityHelper;
import org.dspace.app.webui.components.RecentSubmissionsException;

/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class CollectionsProvider extends AbstractBaseProvider implements  CoreEntityProvider {

    public CollectionsProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "collections";
    }

    @EntityCustomAction(action="communities", viewKey=EntityView.VIEW_SHOW)
    public Object communities(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        refreshParams();

        if (entityExists(reference.getId()))
            return CommunityHelper.getObjects(id, context, CommunityHelper.COMMUNITIES_INVOLVED, idOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    @EntityCustomAction(action="items", viewKey=EntityView.VIEW_SHOW)
    public Object items(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        refreshParams();

        if (entityExists(reference.getId()))
            return CommunityHelper.getObjects(id, context, CommunityHelper.ITEMS_INVOLVED, idOnly, in_archive);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    public boolean entityExists(String id)  {
        // sample entity
        if (id.equals(":ID:"))
            return true;

        boolean result = false;
        try {
            Collection col = Collection.find(context, Integer.parseInt(id));
            if (col != null)
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
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
          refreshParams();
          List<Object> entities = new ArrayList<Object>();

          try {
            Collection[] collections = null;
            collections = Collection.findAll(context);
            for (Collection c : collections)
                entities.add(idOnly ? new CollectionEntityId(c) : new CollectionEntity(c));
            }
     catch (Exception ex) { };
        return entities;
    }

    public Object getSampleEntity() {
        return new CollectionEntity();
    }
}
