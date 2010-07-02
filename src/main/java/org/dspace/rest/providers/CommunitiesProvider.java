/*
 * CommunitiesProvider.java
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
import org.dspace.content.Community;
import org.dspace.core.Context;
import org.apache.log4j.Logger;
import org.sakaiproject.entitybus.exception.EntityException;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.rest.util.UtilHelper;
import org.dspace.app.webui.components.RecentSubmissionsException;
import java.util.Collections;
import org.dspace.rest.util.GenComparator;

/**
 * Provides interface for access to community entities
 * @see CommunityEntityId
 * @see CommunityEntity
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class CommunitiesProvider extends AbstractBaseProvider implements CoreEntityProvider {

    private static Logger log = Logger.getLogger(UserProvider.class);

    public CommunitiesProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    /** Defines url path for this provider
     * @see CommunityEntity
     * @return
     */
    public String getEntityPrefix() {
        return "communities";
    }

    /**
     * Custom action, returns list containing parents of referenced community
     * @param reference
     * @param view
     * @param params
     * @return
     * @throws java.sql.SQLException
     * @throws org.dspace.app.webui.components.RecentSubmissionsException
     */
    @EntityCustomAction(action = "parents", viewKey = EntityView.VIEW_SHOW)
    public Object parents(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo() + "parents_action:" + reference.getId());
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
            entities = UtilHelper.getObjects(id, context, UtilHelper.PARENTS, idOnly, immediateOnly);
        } else {
            throw new IllegalArgumentException("Invalid id:" + reference.getId());
        }

        // do sorting and formating/limiting if requested
        removeConn(context);
        if (!idOnly && sortOptions.size() > 0) {
            Collections.sort(entities, new GenComparator(sortOptions));
        }
        removeTrailing(entities);

        return entities;
    }

    /**
     * Custom action, returns list containing children of community
     * @param reference
     * @param view
     * @param params
     * @return
     * @throws java.sql.SQLException
     * @throws org.dspace.app.webui.components.RecentSubmissionsException
     */
    @EntityCustomAction(action = "children", viewKey = EntityView.VIEW_SHOW)
    public Object children(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo() + "children_action:" + reference.getId());
        List<Object> entities = new ArrayList<Object>();

        System.out.println("-- reference " + reference.prefix + " id " + reference.getId() + " get reference " + reference.getId());

        String id = reference.getId();
        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        if (entityExists(reference.getId())) {
            entities = UtilHelper.getObjects(id, context, UtilHelper.CHILDREN, idOnly, immediateOnly);
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


      /**
     * Custom action, returns list containingchildren of community
     * @param reference
     * @param view
     * @param params
     * @return
     * @throws java.sql.SQLException
     * @throws org.dspace.app.webui.components.RecentSubmissionsException
     */
    @EntityCustomAction(action = "query", viewKey = EntityView.VIEW_SHOW)
    public Object children2(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo() + "children_action:" + reference.getId());
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
            entities = UtilHelper.getObjects(id, context, UtilHelper.CHILDREN, idOnly, immediateOnly);
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



    /**
     * Custom action, returns list containing collections in this community
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

        String id = reference.getId();
        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);

        if (entityExists(reference.getId())) {
            entities = UtilHelper.getObjects(id, context, UtilHelper.COLLECTIONS, idOnly);
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

    /**
     * Custom action, returns list containing recent submissions (items)
     * to the referenced community
     * @param reference
     * @param view
     * @param params
     * @return
     * @throws java.sql.SQLException
     * @throws org.dspace.app.webui.components.RecentSubmissionsException
     */
    @EntityCustomAction(action = "recent", viewKey = EntityView.VIEW_SHOW)
    public Object recentSubmissions(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo() + "recentsubmissions_action:" + reference.getId());

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        String id = reference.getId();

        if (entityExists(reference.getId())) {
            return UtilHelper.getObjects(id, context, UtilHelper.RECENT_SUBMISSIONS, idOnly);
        }

        removeConn(context);
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
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

        // extract query parameters
        refreshParams(context);
        boolean result = false;
        try {
            Community comm = Community.find(context, Integer.parseInt(id));
            if (comm != null) {
                result = true;
            }
        } catch (SQLException ex) {
            result = false;
        }

        removeConn(context);
        return result;
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_entity:" + reference.getId());

        // sample entity
        if (reference.getId().equals(":ID:")) {
            return new CommunityEntity();
        }

        if (reference.getId() == null) {
            return new CommunityEntity();
        }

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        refreshParams(context);
        if (entityExists(reference.getId())) {
            try {
                // return just entity containg id or full info
                if (idOnly) {
                    return new CommunityEntityId(reference.getId(), context);
                } else {
                    return new CommunityEntity(reference.getId(), context);
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
            Community[] communities = null;
            communities = topLevelOnly ? Community.findAllTop(context) : Community.findAll(context);

            for (Community c : communities) {
                entities.add(idOnly ? new CommunityEntityId(c) : new CommunityEntity(c));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        removeConn(context);

        // sort and limit if necessary
        if (!idOnly && sortOptions.size() > 0) {
            Collections.sort(entities, new GenComparator(sortOptions));
        }

        removeTrailing(entities);
        return entities;
    }

    /**
     * Prepare sample entity
     * @return
     */
    public Object getSampleEntity() {
        return new CollectionEntity();
    }
}
