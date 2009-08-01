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
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
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
public class ItemsProvider extends AbstractRESTProvider implements  CoreEntityProvider, RESTful {

    private Context context;
    private RequestStorage reqStor;

    public ItemsProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        context = new Context();
        entityProviderManager.registerEntityProvider(this);
        this.reqStor = entityProviderManager.getRequestStorage();
    }

    public String getEntityPrefix() {
        return "items";
    }


    @EntityCustomAction(action="permissions", viewKey=EntityView.VIEW_SHOW)
    public Object permissions(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        boolean idOnly = false;

        if (params.containsKey("idOnly") && params.get("idOnly").equals("true"))
            idOnly = true;

        if (entityExists(reference.getId()))
            return CommunityHelper.getObjects(id, context, CommunityHelper.ITEM_PERMISSION);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    @EntityCustomAction(action="communities", viewKey=EntityView.VIEW_SHOW)
    public Object communities(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        boolean idOnly = false;
        boolean in_archive = false;

        if (params.containsKey("idOnly") && params.get("idOnly").equals("true"))
            idOnly = true;

        if (entityExists(reference.getId()))
            return CommunityHelper.getObjects(id, context, CommunityHelper.ITEM_IN_COMMUNITIES, idOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    @EntityCustomAction(action="collections", viewKey=EntityView.VIEW_SHOW)
    public Object collections(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        String id = reference.getId();
        boolean idOnly = false;
        boolean in_archive = false;

        if (params.containsKey("idOnly") && params.get("idOnly").equals("true"))
            idOnly = true;

        if (entityExists(reference.getId()))
            return CommunityHelper.getObjects(id, context, CommunityHelper.ITEM_IN_COLLECTIONS, idOnly);

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }


    public boolean entityExists(String id)  {

        boolean result = false;
        try {
            Item col = Item.find(context, Integer.parseInt(id));
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
                    return new ItemEntityId(reference.getId(), context);
                else
                    return new ItemEntity(reference.getId(), context);
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
            Context context = new Context();
            ItemIterator items = Item.findAll(context);


            while (items.hasNext()) {
                entities.add(idOnly ? new ItemEntityId(items.next()) : new ItemEntity(items.next()));
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
