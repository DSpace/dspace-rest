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
import org.dspace.content.Bitstream;
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
public class BitstreamProvider extends AbstractRESTProvider implements  CoreEntityProvider, RESTful {

    private Context context;
    private RequestStorage reqStor;

    public BitstreamProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        context = new Context();
        entityProviderManager.registerEntityProvider(this);
        this.reqStor = entityProviderManager.getRequestStorage();
    }

    public String getEntityPrefix() {
        return "bitstream";
    }


    @EntityCustomAction(action="receive", viewKey=EntityView.VIEW_SHOW)
    public Object receive(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        // TODO implement sending full bitstream
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public boolean entityExists(String id)  {

        boolean result = false;
        try {
            Bitstream bst = Bitstream.find(context, Integer.parseInt(id));
            if (bst != null)
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
                    return new BitstreamEntityId(reference.getId(), context);
                else
                    return new BitstreamEntity(reference.getId(), context);
            } catch (SQLException ex) {
                throw new IllegalArgumentException("Invalid id:" + reference.getId());
            }
        }
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        return null;
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
