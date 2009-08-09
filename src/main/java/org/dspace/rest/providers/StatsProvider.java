/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
public class StatsProvider extends AbstractRESTProvider implements  CoreEntityProvider, RESTful {

    private Context context;
    private RequestStorage reqStor;

    public StatsProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        context = new Context();
        entityProviderManager.registerEntityProvider(this);
        this.reqStor = entityProviderManager.getRequestStorage();
    }

    public String getEntityPrefix() {
        return "stats";
    }


    public boolean entityExists(String id)  {
        return false;
    }


    public Object getEntity(EntityReference reference) {
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        try {
          List<Object> stat = new ArrayList<Object>();
          stat.add(new StatsEntity(context));
        return stat;
        } catch (Exception ex) { System.out.println(ex.getMessage() + " greeska "); };
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
