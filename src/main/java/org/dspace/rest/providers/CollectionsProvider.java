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
import org.sakaiproject.entitybus.entityprovider.extension.RequestStorage;
import org.sakaiproject.entitybus.entityprovider.extension.Formats;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityCustomAction;
import org.dspace.content.Community;
import org.dspace.content.Collection;
import org.dspace.core.Context;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.rest.util.CommunityHelper;
import org.sakaiproject.entitybus.exception.EntityException;
import org.sakaiproject.entitybus.exception.EntityEncodingException;
import org.dspace.app.webui.components.RecentSubmissionsException;

/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class CollectionsProvider extends AbstractRESTProvider implements  CoreEntityProvider, RESTful {

    private Context context;
    private RequestStorage reqStor;

    public CollectionsProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        context = new Context();
        entityProviderManager.registerEntityProvider(this);
        this.reqStor = entityProviderManager.getRequestStorage();
        System.out.println("registered col");
    }

    public String getEntityPrefix() {
        return "collections";
    }


    @EntityCustomAction(action="communities", viewKey=EntityView.VIEW_SHOW)
    public Object communities(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        boolean idOnly = false;

        if (params.containsKey("idOnly") && params.get("idOnly").equals("true"))
            idOnly = true;

        if (entityExists(reference.getId()))
            return CommunityHelper.getObjects(id, context, CommunityHelper.COMMUNITIES_INVOLVED, idOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    // TODO Move some initializations from methods to class

    // TODO Think about refactoring all entity providers, ie making one basic class
    // TODO Think about refactoring all entity classes in similar sense

    @EntityCustomAction(action="items", viewKey=EntityView.VIEW_SHOW)
    public Object items(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        boolean idOnly = false;
        boolean in_archive = false;
        
        if (params.containsKey("idOnly") && params.get("idOnly").equals("true"))
            idOnly = true;
        if (params.containsKey("in_archive") && params.get("in_archive").equals("true"))
            in_archive = true;


        if (entityExists(reference.getId()))
            return CommunityHelper.getObjects(id, context, CommunityHelper.ITEMS_INVOLVED, idOnly, in_archive);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    public boolean entityExists(String id)  {

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
          boolean idOnly;
          try {
          idOnly = reqStor.getStoredValue("idOnly").equals("true");
          } catch (NullPointerException ex) { idOnly = false; };

        if (reference.getId() == null) {
            return new StandardEntity();
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
          boolean idOnly;
          try {
          idOnly = reqStor.getStoredValue("idOnly").equals("true");
          } catch (NullPointerException ex) { idOnly = false; };

          List<Object> entities = new ArrayList<Object>();

          System.out.println ("trying...");
          try {
            Collection[] collections = null;
            Context context = new Context();
            collections = Collection.findAll(context);
System.out.println(" num col " + collections.length);
            for (int x=0; x<collections.length; x++) {
                entities.add(idOnly ? new CollectionEntityId(collections[x]) : new CollectionEntity(collections[x]));
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
