/*
 * CollectionsProvider.java
 *
 * Version: $Revision$
 *
 * Date: $Date$
 *
 * Copyright (c) 2002-2009, The DSpace Foundation.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the DSpace Foundation nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
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
import org.apache.log4j.Logger;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.exception.EntityException;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.rest.util.UtilHelper;
import org.dspace.app.webui.components.RecentSubmissionsException;
import org.dspace.rest.util.GenComparator;
import java.util.Collections;

/**
 * Provides interface for access to collections entities
 * @see CollectionEntity
 * @see CollectionEntityId
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class CollectionsProvider extends AbstractBaseProvider implements CoreEntityProvider {

    private static Logger log = Logger.getLogger(UserProvider.class);

    public CollectionsProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "collections";
    }

    /**
     * Custom action, returns list with communities in which collection is member
     * @param reference
     * @param view
     * @param params
     * @return
     * @throws java.sql.SQLException
     * @throws org.dspace.app.webui.components.RecentSubmissionsException
     */
    @EntityCustomAction(action = "communities", viewKey = EntityView.VIEW_SHOW)
    public Object communities(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo() + "communities_action:" + reference.getId());
        List<Object> entities = new ArrayList<Object>();

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        String id = reference.getId();

        if (entityExists(reference.getId())) {
            entities = UtilHelper.getObjects(id, context, UtilHelper.COMMUNITIES_INVOLVED, idOnly);
        } else {
            throw new IllegalArgumentException("Invalid id:" + reference.getId());
        }

        removeConn(context);

        // apply sorting if necessary and requested
        if (!idOnly && sortOptions.size() > 0) {
            Collections.sort(entities, new GenComparator(sortOptions));
        }

        // format output according to _limit and other parameters
        removeTrailing(entities);
        return entities;

    }

    /**
     * Provides items which are a part of the collection
     * @param reference
     * @param view
     * @param params
     * @return Items 
     * @throws java.sql.SQLException
     * @throws org.dspace.app.webui.components.RecentSubmissionsException
     */
    @EntityCustomAction(action = "items", viewKey = EntityView.VIEW_SHOW)
    public Object items(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo() + "items_action:" + reference.getId());
        List<Object> entities = new ArrayList<Object>();

        String id = reference.getId();
        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        if (entityExists(reference.getId())) {
            entities = UtilHelper.getObjects(id, context, UtilHelper.ITEMS_INVOLVED, idOnly, in_archive);
        } else {
            throw new IllegalArgumentException("Invalid id:" + reference.getId());
        }

        removeConn(context);
        if (!idOnly && sortOptions.size() > 0) {
            Collections.sort(entities, new GenComparator(sortOptions));
        }
        removeTrailing(entities);
        return entities;
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "entity_exists:" + id);

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
            Collection col = Collection.find(context, Integer.parseInt(id));
            if (col != null) {
                result = true;
            }
        } catch (SQLException ex) {
            result = false;
        }

        // close connection to prevent connection problems
        removeConn(context);
        return result;
    }

    /**
     * Returns information about particular entity
     * @param reference
     * @return
     */
    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_entity:" + reference.getId());

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        // sample entity
        if (reference.getId().equals(":ID:")) {
            return new CollectionEntity();
        }

        if (reference.getId() == null) {
            return new CollectionEntity();
        }

        if (entityExists(reference.getId())) {
            try {
                // return basic entity or full info
                if (idOnly) {
                    return new CollectionEntityId(reference.getId(), context);
                } else {
                    return new CollectionEntity(reference.getId(), context);
                }
            } catch (SQLException ex) {
                throw new IllegalArgumentException("Invalid id:" + reference.getId());
            }
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    /**
     * List all collection in the system, sort and format if requested
     * @param ref
     * @param search
     * @return
     */
    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_entities");

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        List<Object> entities = new ArrayList<Object>();

        try {
            Collection[] collections = null;
            collections = Collection.findAll(context);
            for (Collection c : collections) {
                entities.add(idOnly ? new CollectionEntityId(c) : new CollectionEntity(c));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ;

        removeConn(context);
        if (!idOnly && sortOptions.size() > 0) {
            Collections.sort(entities, new GenComparator(sortOptions));
        }

        removeTrailing(entities);

        return entities;
    }

    /*
     * Here is sample collection entity defined
     */
    public Object getSampleEntity() {
        return new CollectionEntity();
    }
}
