/*
 * UtilHelper.java
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
package org.dspace.rest.util;

import org.dspace.rest.entities.*;
import org.dspace.content.Community;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.core.Context;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.sakaiproject.entitybus.exception.EntityException;
import org.dspace.app.webui.components.RecentSubmissionsManager;
import org.dspace.app.webui.components.RecentSubmissions;
import org.dspace.app.webui.components.RecentSubmissionsException;

/**
 * Here are implemented and coupled helper methods used by several providers
 * especially in the custom actions
 * @see GenComparator
 * @see AbstractBaseProvider
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class UtilHelper {

    protected UtilHelper() {
    }
    // methods for getObjects
    public static final int PARENTS = 1;
    public static final int CHILDREN = 2;
    public static final int COLLECTIONS = 3;
    public static final int RECENT_SUBMISSIONS = 4;
    public static final int COMMUNITIES_INVOLVED = 5;
    public static final int ITEMS_INVOLVED = 6;
    public static final int ITEM_PERMISSION = 7;
    public static final int ITEM_IN_COMMUNITIES = 8;
    public static final int ITEM_IN_COLLECTIONS = 9;

    // sort methods for GenComparator
    public static final int SORT_ID = 210;
    public static final int SORT_NAME = 211;
    public static final int SORT_LASTMODIFIED = 212;
    public static final int SORT_SUBMITTER = 213;
    public static final int SORT_COUNT_ITEMS = 214;
    public static final int SORT_LANGUAGE = 215;
    public static final int SORT_LASTNAME = 216;
    public static final int SORT_FULL_NAME = 217;
    public static final int SORT_ID_REV = 310;
    public static final int SORT_NAME_REV = 311;
    public static final int SORT_LASTMODIFIED_REV = 312;
    public static final int SORT_SUBMITTER_REV = 313;
    public static final int SORT_COUNT_ITEMS_REV = 314;
    public static final int SORT_LANGUAGE_REV = 315;
    public static final int SORT_LASTNAME_REV = 316;
    public static final int SORT_FULL_NAME_REV = 317;

    /**
     * Returns objects according to method choosen, used by communities
     * @param uid community/collection/item id
     * @param context 
     * @param method method choosen
     * @param idOnly should only ids (basic entities) be returned
     * @param immediateOnly returns only immediate subcommunities
     * @return list containing entities (e.g. CommunityEntity, ItemEntity etc)
     * @throws java.sql.SQLException
     * @throws org.dspace.app.webui.components.RecentSubmissionsException
     */
    public static List<Object> getObjects(String uid, Context context, int method, boolean idOnly, boolean immediateOnly) throws SQLException, RecentSubmissionsException {
        List<Object> entities = new ArrayList<Object>();

        // Reflect method resolution could be used here but this causes perfomance penalty

        switch (method) {

            // returns parent entities of community
            case PARENTS:
                 {
                    if (uid.equals(":ID:")) {
                        entities.add(new CommunityEntity());
                    } else {
                        Community res = Community.find(context, Integer.parseInt(uid));
                        if (immediateOnly) {
                            for (Community o : res.getAllParents()) {
                                entities.add(idOnly ? new CommunityEntityId(o) : new CommunityEntity(o));
                            }
                        } else {
                            entities.add(idOnly ? new CommunityEntityId(res.getParentCommunity()) : new CommunityEntity(res.getParentCommunity()));
                        }
                    }
                }
                break;

            // returns children entities of community
            case CHILDREN:
                 {
                    if (uid.equals(":ID:")) {
                        entities.add(new CommunityEntity());
                    } else {
                        Community res = Community.find(context, Integer.parseInt(uid));
                        for (Community o : res.getSubcommunities()) {
                            entities.add(idOnly ? new CommunityEntityId(o) : new CommunityEntity(o));
                        }

                        for (Collection o : res.getCollections()) { // added later for test purposes
                            entities.add(idOnly ? new CollectionEntityId(o) : new CollectionEntity(o));
                        }


                    }
                }
                break;
            
            // returns collections in community
            case COLLECTIONS:
                 {
                    if (uid.equals(":ID:")) {
                        entities.add(new CollectionEntity());
                    } else {
                        Community res = Community.find(context, Integer.parseInt(uid));
                        for (Collection o : res.getCollections()) {
                            entities.add(idOnly ? new CollectionEntityId(o) : new CollectionEntity(o));
                        }
                    }
                }
                break;

            // returns recent submissions in community
            case RECENT_SUBMISSIONS:
                 {
                    if (uid.equals(":ID:")) {
                        entities.add(new ItemEntity());
                    } else {
                        Community res = Community.find(context, Integer.parseInt(uid));
                        RecentSubmissionsManager rsm = new RecentSubmissionsManager(context);
                        RecentSubmissions recent = rsm.getRecentSubmissions(res);
                        for (Item i : recent.getRecentSubmissions()) {
                            entities.add(idOnly ? new ItemEntityId(i) : new ItemEntity(i));
                        }
                    }
                }
                break;

            // returns communities involved in collection
            case COMMUNITIES_INVOLVED:
                 {
                    if (uid.equals(":ID:")) {
                        entities.add(new CommunityEntity());
                    } else {
                        Collection col = Collection.find(context, Integer.parseInt(uid));
                        for (Community o : col.getCommunities()) {
                            entities.add(idOnly ? new CommunityEntityId(o) : new CommunityEntity(o));
                        }
                    }
                }
                break;

            // returns items in relation with collectin
            case ITEMS_INVOLVED:
                 {
                    if (uid.equals(":ID:")) {
                        entities.add(new ItemEntity());
                    } else {
                        Collection col = Collection.find(context, Integer.parseInt(uid));
                        ItemIterator i = immediateOnly ? col.getItems() : col.getAllItems();
                        while (i.hasNext()) {
                            entities.add(idOnly ? new ItemEntityId(i.next()) : new ItemEntity(i.next()));
                        }
                    }
                }
                break;

            // returns current user permissions on item
            case ITEM_PERMISSION:
                 {
                    if (uid.equals(":ID:")) {
                        entities.add(true);
                    } else {
                        Item i = Item.find(context, Integer.parseInt(uid));
                        entities.add(i.canEdit());
                    }
                }
                break;

            // returns communities in which item is member
            case ITEM_IN_COMMUNITIES:
                 {
                    if (uid.equals(":ID:")) {
                        entities.add(new CommunityEntity());
                    } else {
                        Item i = Item.find(context, Integer.parseInt(uid));
                        for (Community o : i.getCommunities()) {
                            entities.add(idOnly ? new CommunityEntityId(o) : new CommunityEntity(o));
                        }
                    }
                }
                break;

            // returns collections in which item is contained
            case ITEM_IN_COLLECTIONS: {
                if (uid.equals(":ID:")) {
                    entities.add(new CollectionEntity());
                } else {
                    Item i = Item.find(context, Integer.parseInt(uid));
                    for (Collection o : i.getCollections()) {
                        entities.add(idOnly ? new CollectionEntityId(o) : new CollectionEntity(o));
                    }
                }
            }


        }

        if (entities.size() == 0) {
            throw new EntityException("No members", uid, 204);
        }
        return entities;

    }

    /**
     * This method forwards to basic getObjects method, used by some other providers
     */
    public static List<Object> getObjects(String uid, Context context, int method, boolean idOnly) throws SQLException, RecentSubmissionsException {
        return getObjects(uid, context, method, idOnly, false);
    }

    /**
     * This method forwards to basic getObjects method, used by some other providers
     */
    public static List<Object> getObjects(String uid, Context context, int method) throws SQLException, RecentSubmissionsException {
        return getObjects(uid, context, method, false, false);
    }
}
