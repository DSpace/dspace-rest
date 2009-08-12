/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.providers;

import java.util.List;
import java.util.Map;

import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.EntityView;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityCustomAction;
import org.dspace.content.Bitstream;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.app.webui.components.RecentSubmissionsException;

/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class BitstreamProvider extends AbstractBaseProvider implements  CoreEntityProvider {

    public BitstreamProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
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
        // sample entity
        if (id.equals(":ID:"))
            return true;

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
         refreshParams();
        // sample entity
        if (reference.getId().equals(":ID:"))
            return new CommunityEntity();

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

    public Object getSampleEntity() {
        return new BitstreamEntity();
    }
}
