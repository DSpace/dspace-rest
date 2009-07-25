/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.providers;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybus.entityprovider.extension.Formats;
import org.sakaiproject.entitybus.entityprovider.search.Search;


/**
 * A more typical example of an entity provider,
 * stores everything in memory
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class StandardEntityProvider extends AbstractRESTProvider implements CoreEntityProvider, RESTful {

    public Map<String, StandardEntity> myEntities = new LinkedHashMap<String, StandardEntity>(4);

    public StandardEntityProvider(EntityProviderManager entityProviderManager) {
        super(entityProviderManager);
        init( new String[] {"aaa","bbb","ccc"} );
    }

    public void init(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            myEntities.put(ids[i], new StandardEntity(ids[i], "aaron" + i, i) );
        }
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entitybus.entityprovider.EntityProvider#getEntityPrefix()
     */
    public String getEntityPrefix() {
        return "standard";
    }

    public boolean entityExists(String id) {
        return myEntities.containsKey(id);
    }

    public Object getEntity(EntityReference reference) {
        if (reference.getId() == null) {
            return new StandardEntity();
        }
        if (myEntities.containsKey(reference.getId())) {
            return myEntities.get( reference.getId() );
        }
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        List<StandardEntity> entities = new ArrayList<StandardEntity>();
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
        return entities;
    }

    /**
     * Returns {@link StandardEntity} objects with no id, default number to 10
     * {@inheritDoc}
     */
    public Object getSampleEntity() {
        return new StandardEntity(null, 10);
    }

    /**
     * Expects {@link StandardEntity} objects
     * {@inheritDoc}
     */
    public String createEntity(EntityReference ref, Object entity, Map<String, Object> params) {
        StandardEntity me = (StandardEntity) entity;
        if (me.getStuff() == null || "".equals(me.getStuff())) {
            throw new IllegalArgumentException("stuff is not set, it is required");
        }
        String newId = me.getId();
        int counter = 0;
        if (newId == null || "".equals(newId)) {
            newId = null;
            while (newId == null) {
                String id = "my"+counter++;
                if (! myEntities.containsKey(id)) {
                    newId = id;
                }
            }
            me.setId( newId );
        }
        myEntities.put(newId, me);
        return newId;
    }

    /**
     * Expects {@link StandardEntity} objects
     * {@inheritDoc}
     */
    public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params) {
        StandardEntity me = (StandardEntity) entity;
        if (me.getStuff() == null) {
            throw new IllegalArgumentException("stuff is not set, it is required");
        }
        StandardEntity current = myEntities.get(ref.getId());
        if (current == null) {
            throw new IllegalArgumentException("Invalid update, cannot find entity");
        }
        // update the fields
        current.setStuff( me.getStuff() );
        current.setNumber( me.getNumber() );
        current.extra = me.extra;
    }

    public void deleteEntity(EntityReference ref, Map<String, Object> params) {
        if (myEntities.remove(ref.getId()) == null) {
            throw new IllegalArgumentException("Invalid entity id, cannot find entity to remove: " + ref);
        }
    }

    public String[] getHandledOutputFormats() {
        return new String[] {Formats.HTML, Formats.JSON, Formats.XML, Formats.FORM};
     }

     public String[] getHandledInputFormats() {
        return new String[] {Formats.HTML, Formats.JSON, Formats.XML};
     }

}
