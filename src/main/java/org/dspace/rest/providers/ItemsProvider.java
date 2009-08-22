/*
 * ItemsProvider.java
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
import org.sakaiproject.entitybus.entityprovider.search.Order;
import org.sakaiproject.entitybus.entityprovider.search.Restriction;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityCustomAction;
import org.dspace.core.Context;
import org.dspace.content.Item;
import org.apache.log4j.Logger;
import org.sakaiproject.entitybus.exception.EntityException;
import org.dspace.content.ItemIterator;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.rest.util.UtilHelper;
import org.dspace.app.webui.components.RecentSubmissionsException;
import java.util.Collections;
import org.dspace.rest.util.GenComparator;

/**
 * Provides interface for access to item entities
 * @see ItemEntityId
 * @see ItemEntity
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class ItemsProvider extends AbstractBaseProvider implements CoreEntityProvider {

    private static Logger log = Logger.getLogger(UserProvider.class);

    /**
     * Constructor handles registration of provider
     * @param entityProviderManager
     * @throws java.sql.SQLException
     */
    public ItemsProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    // this is the prefix where provider is registered (URL path)
    public String getEntityPrefix() {
        return "items";
    }

    /**
     * Items provider, handles listing of all items in system and
     * particular items. Provide info on these entities.
     * @see ItemEntity
     * @see ItemEntityId
     * @param reference
     * @param view
     * @param params
     * @return
     * @throws java.sql.SQLException
     * @throws org.dspace.app.webui.components.RecentSubmissionsException
     */
    @EntityCustomAction(action = "permissions", viewKey = EntityView.VIEW_SHOW)
    public Object permissions(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo() + "permissions_action:" + reference.getId());

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        // refresh request parameters
        refreshParams(context);
        String id = reference.getId();

        if (entityExists(reference.getId())) 
            return UtilHelper.getObjects(id, context, UtilHelper.ITEM_PERMISSION);

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    /**
     * Custom action, returns communities in which item is member
     * @param reference
     * @param view
     * @param params
     * @return
     * @throws java.sql.SQLException
     * @throws org.dspace.app.webui.components.RecentSubmissionsException
     */
    @EntityCustomAction(action = "communities", viewKey = EntityView.VIEW_SHOW)
    public Object communities(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo() + "permissions_action:" + reference.getId());
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
            entities = UtilHelper.getObjects(id, context, UtilHelper.ITEM_IN_COMMUNITIES, idOnly);
        } else {
            throw new IllegalArgumentException("Invalid id:" + reference.getId());
        }

        removeConn(context);

        // provide sorting capability if it is required
        if (!idOnly && sortOptions.size() > 0) {
            Collections.sort(entities, new GenComparator(sortOptions));
        }

        // format results according to requirements in query
        removeTrailing(entities);

        return entities;
    }

    /**
     * Custom action, returns collections in which item is member
     * @param reference
     * @param view
     * @param params
     * @return
     * @throws java.sql.SQLException
     * @throws org.dspace.app.webui.components.RecentSubmissionsException
     */
    @EntityCustomAction(action = "collections", viewKey = EntityView.VIEW_SHOW)
    public Object collections(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo() + "collections_action:" + reference.getId());
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
            entities = UtilHelper.getObjects(id, context, UtilHelper.ITEM_IN_COLLECTIONS, idOnly);
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

        // search for existence for particular item
        try {
            Item col = Item.find(context, Integer.parseInt(id));
            if (col != null) {
                result = true;
            }
        } catch (SQLException ex) {
            result = false;
        }

        // handles manual deregistration by sql server to lower load
        removeConn(context);
        return result;
    }

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
            return new ItemEntity();
        }

        if (entityExists(reference.getId())) {
            try {

                // return basic or full info, according to requirements
                if (idOnly) {
                    return new ItemEntityId(reference.getId(), context);
                } else {
                    return new ItemEntity(reference.getId(), context);
                }
            } catch (SQLException ex) {
                throw new IllegalArgumentException("Invalid id:" + reference.getId());
            }
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

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
            ItemIterator items = Item.findAll(context);
            while (items.hasNext())
                entities.add(idOnly ? new ItemEntityId(items.next()) : new ItemEntity(items.next()));
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        removeConn(context);
        if (!idOnly && sortOptions.size() > 0) {
            Collections.sort(entities, new GenComparator(sortOptions));
        }

        removeTrailing(entities);
        return entities;
    }

    /**
     * Return sample entity
     * @return
     */
    public Object getSampleEntity() {
        return new ItemEntity();
    }
}
