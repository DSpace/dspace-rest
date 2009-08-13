/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class CommunityHelper {

    protected CommunityHelper() {
        
    }

    public static final int PARENTS = 1;

    public static final int CHILDREN = 2;

    public static final int COLLECTIONS = 3;

    public static final int RECENT_SUBMISSIONS = 4;

    public static final int COMMUNITIES_INVOLVED = 5;

    public static final int ITEMS_INVOLVED = 6;

    public static final int ITEM_PERMISSION = 7;

    public static final int ITEM_IN_COMMUNITIES = 8;

    public static final int ITEM_IN_COLLECTIONS = 9;
    

    public static List<Object> getObjects(String uid, Context context, int method, boolean idOnly, boolean immediateOnly) throws SQLException, RecentSubmissionsException {
        List<Object> entities = new ArrayList<Object>();

        // Reflect method resolution could be used here but this causes perfomance penalty

        switch(method) {

            case PARENTS :
            {
                if (uid.equals(":ID:"))
                    entities.add(new CommunityEntity());
                else {
                    Community res = Community.find(context, Integer.parseInt(uid));
                    if (immediateOnly)
                        for (Community o : res.getAllParents())
                            entities.add(idOnly ? new CommunityEntityId(o) : new CommunityEntity(o));
                        else
                            entities.add(idOnly ? new CommunityEntityId(res.getParentCommunity()) : new CommunityEntity(res.getParentCommunity()));
                }
            } break;
            case CHILDREN :
            {
                if (uid.equals(":ID:"))
                    entities.add(new CommunityEntity());
                else {
                    Community res = Community.find(context, Integer.parseInt(uid));
                    for (Community o : res.getSubcommunities())
                        entities.add(idOnly ? new CommunityEntityId(o) : new CommunityEntity(o));
                }
            } break;
            case COLLECTIONS :
            {
                if (uid.equals(":ID:"))
                    entities.add(new CollectionEntity());
                else {
                    Community res = Community.find(context, Integer.parseInt(uid));
                    for (Collection o: res.getCollections())
                        entities.add(idOnly ? new CollectionEntityId(o) : new CollectionEntity(o));
                }
            } break;
            case RECENT_SUBMISSIONS :
            {
                if (uid.equals(":ID:"))
                    entities.add(new ItemEntity());
                else {
                    Community res = Community.find(context, Integer.parseInt(uid));
        			RecentSubmissionsManager rsm = new RecentSubmissionsManager(context);
            		RecentSubmissions recent = rsm.getRecentSubmissions(res);
                    for (Item i: recent.getRecentSubmissions())
                        entities.add(idOnly ? new ItemEntityId(i) : new ItemEntity(i));
                }
            } break;
            case COMMUNITIES_INVOLVED :
            {
                if (uid.equals(":ID:"))
                    entities.add(new CommunityEntity());
                else {
                    Collection col = Collection.find(context, Integer.parseInt(uid));
                    for (Community o : col.getCommunities())
                        entities.add(idOnly ? new CommunityEntityId(o) : new CommunityEntity(o));
                }
            } break;
            case ITEMS_INVOLVED :
            {
                if (uid.equals(":ID:"))
                    entities.add(new ItemEntity());
                else {
                    Collection col = Collection.find(context, Integer.parseInt(uid));
                    ItemIterator i = immediateOnly ? col.getItems() : col.getAllItems();
                    while (i.hasNext())
                        entities.add(idOnly ? new ItemEntityId(i.next()) : new ItemEntity(i.next()));
                }
            } break;
            case ITEM_PERMISSION :
            {
                if (uid.equals(":ID:"))
                    entities.add(true);
                else {
                    Item i = Item.find(context, Integer.parseInt(uid));
                    entities.add(i.canEdit());
                }
            } break;
            case ITEM_IN_COMMUNITIES :
            {
                if (uid.equals(":ID:"))
                    entities.add(new CommunityEntity());
                else {
                    Item i = Item.find(context, Integer.parseInt(uid));
                    for (Community o : i.getCommunities())
                        entities.add(idOnly ? new CommunityEntityId(o) : new CommunityEntity(o));
                }
            } break;
            case ITEM_IN_COLLECTIONS :
            {
                if (uid.equals(":ID:"))
                    entities.add(new CollectionEntity());
                else {
                    Item i = Item.find(context, Integer.parseInt(uid));
                    for (Collection o : i.getCollections())
                        entities.add(idOnly ? new CollectionEntityId(o) : new CollectionEntity(o));
                }
            }


        }

        if (entities.size() == 0)
            throw new EntityException("No members", uid, 204);
        return entities;

    }

    public static List<Object> getObjects(String uid, Context context, int method, boolean idOnly) throws SQLException, RecentSubmissionsException {
        return getObjects(uid, context, method, idOnly, false);
    }

    public static List<Object> getObjects(String uid, Context context, int method) throws SQLException, RecentSubmissionsException {
        return getObjects(uid, context, method, false, false);
    }


}
