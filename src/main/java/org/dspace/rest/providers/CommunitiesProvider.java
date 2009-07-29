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
import org.sakaiproject.entitybus.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybus.entityprovider.extension.Formats;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityCustomAction;
import org.dspace.content.Community;
import org.dspace.core.Context;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.rest.util.CommunityHelper;
import org.sakaiproject.entitybus.exception.EntityException;
import org.sakaiproject.entitybus.exception.EntityEncodingException;
import org.dspace.app.webui.components.RecentSubmissionsException;

/**
 *
 */
public class CommunitiesProvider extends AbstractRESTProvider implements  CoreEntityProvider, RESTful {
    
    private Context context;

    public CommunitiesProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        context = new Context();
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "communities";
    }


    @EntityCustomAction(action="parents", viewKey=EntityView.VIEW_SHOW)
    public Object parents(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        boolean idOnly = false;
        boolean immediateOnly = true;

        if (params.containsKey("immediateOnly") && params.get("immediateOnly").equals("false"))
            immediateOnly = false;

        if (params.containsKey("idOnly") && params.get("idOnly").equals("true"))
            idOnly = true;

        if (entityExists(reference.getId())) 
            return CommunityHelper.getObjects(id, context, CommunityHelper.PARENTS, idOnly, immediateOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    @EntityCustomAction(action="children", viewKey=EntityView.VIEW_SHOW)
    public Object children(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        boolean idOnly = false;
        boolean immediateOnly = true;

        if (params.containsKey("immediateOnly") && params.get("immediateOnly").equals("false"))
            immediateOnly = false;

        if (params.containsKey("idOnly") && params.get("idOnly").equals("true"))
            idOnly = true;

        if (entityExists(reference.getId()))
            return CommunityHelper.getObjects(id, context, CommunityHelper.CHILDREN, idOnly, immediateOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    @EntityCustomAction(action="collections", viewKey=EntityView.VIEW_SHOW)
    public Object collections(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        boolean idOnly = false;

        if (params.containsKey("idOnly") && params.get("idOnly").equals("true"))
            idOnly = true;

        if (entityExists(reference.getId())) 
            return CommunityHelper.getObjects(id, context, CommunityHelper.COLLECTIONS, idOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    @EntityCustomAction(action="recent", viewKey=EntityView.VIEW_SHOW)
    public Object recentSubmissions(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        boolean idOnly = false;

        if (params.containsKey("idOnly") && params.get("idOnly").equals("true"))
            idOnly = true;

        if (entityExists(reference.getId()))
            return CommunityHelper.getObjects(id, context, CommunityHelper.RECENT_SUBMISSIONS, idOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    public boolean entityExists(String id)  {

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
        if (reference.getId() == null) {
            return new StandardEntity();
        }
        if (entityExists(reference.getId())) {
            try {
                // TODO Figure out how to get query parameters here
    //            if (params.get("topLevelOnly").equals("true"))
//                    return new CommunityEntityId(reference.getId(), context);
      //          else
                    return new CommunityEntity(reference.getId(), context);
            } catch (SQLException ex) {
                throw new IllegalArgumentException("Invalid id:" + reference.getId());
            }
        }
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
          List<CommunityEntity> entities = new ArrayList<CommunityEntity>();

          try {
            Community[] communities = null;
            Context context = new Context();
            communities = Community.findAll(context);

            for (int x=0; x<communities.length; x++) {
                entities.add(new CommunityEntity(communities[x]));
                }
            }
     catch (Exception ex) { };

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
        return entities;
    }


    /**
     * Returns {@link StandardEntity} objects with no id, default number to 10
     * {@inheritDoc}
     */

    public Object getSampleEntity() {
        return new StandardEntity(null, 10);
    //    return new Object();
    }

    /**
     * Expects {@link StandardEntity} objects
     * {@inheritDoc}
     */
    public String createEntity(EntityReference ref, Object entity, Map<String, Object> params) {
        return "none";
    }

    /**
     * Expects {@link StandardEntity} objects
     * {@inheritDoc}
     */

    public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params) {

    }

    public void deleteEntity(EntityReference ref, Map<String, Object> params) {

    }

    public String[] getHandledOutputFormats() {
        return new String[] {Formats.HTML, Formats.JSON, Formats.XML, Formats.FORM};
     }

     public String[] getHandledInputFormats() {
        return new String[] {Formats.HTML, Formats.JSON, Formats.XML};
     }

}
