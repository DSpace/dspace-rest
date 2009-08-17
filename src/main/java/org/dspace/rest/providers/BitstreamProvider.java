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
import org.dspace.core.Context;
import org.apache.log4j.Logger;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.app.webui.components.RecentSubmissionsException;
import javax.servlet.http.HttpServletResponse;
import org.dspace.authorize.AuthorizeException;
import javax.servlet.ServletOutputStream;
import java.io.BufferedInputStream;
import org.sakaiproject.entitybus.exception.EntityException;
import java.io.IOException;

/**
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class BitstreamProvider extends AbstractBaseProvider implements  CoreEntityProvider {

    EntityProviderManager locEPM;
    private static Logger log = Logger.getLogger(UserEntityProvider.class);
    
    public BitstreamProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        locEPM = entityProviderManager;
    }

    public String getEntityPrefix() {
        return "bitstream";
    }


    @EntityCustomAction(action="receive", viewKey=EntityView.VIEW_SHOW)
    public Object receive(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo()+"receive_action:"+reference.getId());
        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        Bitstream bst = Bitstream.find(context, Integer.parseInt(reference.getId()));

        HttpServletResponse response = this.entityProviderManager.getRequestGetter().getResponse();
        try {
            ServletOutputStream stream = response.getOutputStream();
            response.setContentType(bst.getFormat().getMIMEType());
            response.addHeader("Content-Disposition", "attachment; filename=" + bst.getName());
            response.setContentLength((int)bst.getSize());
            BufferedInputStream buf = new BufferedInputStream(bst.retrieve());

            int readBytes = 0;
            while ((readBytes = buf.read()) != -1)
                stream.write(readBytes);

            if (stream != null)
                stream.close();
            if (buf != null)
                buf.close();
        } catch (IOException ex) {
            throw new EntityException("Internal Server error", "Unable to open file", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "The resource is not available for current user", 403);
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public boolean entityExists(String id)  {
        log.info(userInfo()+"entity_exists:" + id);

        // sample entity
        if (id.equals(":ID:"))
            return true;

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        boolean result = false;
        try {
            Bitstream bst = Bitstream.find(context, Integer.parseInt(id));
            if (bst != null)
                result = true;
        } catch (SQLException ex) {
            result = false;
        }

        removeConn(context);
        return result;
    }


    public Object getEntity(EntityReference reference) {
        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) { 
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        log.info(userInfo()+"get_entity:" + reference.getId());
        System.out.println("xxxx" + userInfo());
System.out.println("ip " + this.entityProviderManager.getRequestGetter().getRequest().getRemoteAddr());

        // sample entity
        if (reference.getId().equals(":ID:"))
            return new CommunityEntity();

        if (reference.getId() == null) {
            return new BitstreamEntity();
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

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo()+"list_entities:");

        return null;
}

    public Object getSampleEntity() {
        return new BitstreamEntity();
    }
}
