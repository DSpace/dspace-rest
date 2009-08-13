/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.providers;

import java.util.ArrayList;
import java.util.List;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import java.sql.SQLException;
import org.dspace.rest.entities.*;
import org.dspace.search.*;
import org.dspace.sort.SortOption;
import org.dspace.core.Constants;
import java.util.HashMap;

/**
 *
 */
public class SearchProvider extends AbstractBaseProvider implements CoreEntityProvider {

    public SearchProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "search";
    }


    public boolean entityExists(String id)  {
        return false;
    }


    public Object getEntity(EntityReference reference) {
        throw new IllegalArgumentException("Not supported");
    }

    public List<?> getEntities(EntityReference ref, Search search) {
          refreshParams();
          List<Object> entities = new ArrayList<Object>();

          try {
              QueryArgs arg = new QueryArgs();
              arg.setQuery(query);

              if (_perpage > 0)
                  arg.setPageSize(_perpage);
              arg.setStart(_start);

              if ((_order.equalsIgnoreCase("descending"))||(_order.equalsIgnoreCase("desc")))
                  arg.setSortOrder(SortOption.DESCENDING);
              else
                  arg.setSortOrder(SortOption.ASCENDING);

              
              
              QueryResults qre;
              
              if (_community != null)
                  qre = DSQuery.doQuery(context, arg, _community);
              else
                  if (_collection != null)
                      qre = DSQuery.doQuery(context, arg, _collection);
                  else
                      qre = DSQuery.doQuery(context, arg);
              entities.add(new SearchResultsInfoEntity(qre.getHitCount()-1, qre.getHitTypes(), qre.getHitHandles(), qre.getHitIds()));
              
              for (int x=0; x<qre.getHitTypes().size(); x++) {
                  switch ((Integer)(qre.getHitTypes().get(x))) {
                          case Constants.ITEM: {
                              entities.add(idOnly ?
                                  new ItemEntityId(qre.getHitIds().get(x).toString(), context) :
                                  new ItemEntity(qre.getHitIds().get(x).toString(), context) );
                          }; break;

                          case Constants.COMMUNITY: {
                              entities.add(idOnly ?
                                  new CommunityEntityId(qre.getHitIds().get(x).toString(), context) :
                                  new CommunityEntity(qre.getHitIds().get(x).toString(), context) );
                          }; break;

                          case Constants.COLLECTION: {
                              entities.add(idOnly ?
                                  new CollectionEntityId(qre.getHitIds().get(x).toString(), context) :
                                  new CollectionEntity(qre.getHitIds().get(x).toString(), context) );
                          }; break;

                          case Constants.BITSTREAM: {
                              entities.add(idOnly ?
                                  new BitstreamEntityId(qre.getHitIds().get(x).toString(), context) :
                                  new BitstreamEntity(qre.getHitIds().get(x).toString(), context) );
                          }; break;

                          case Constants.BUNDLE: {
                              entities.add(idOnly ?
                                  new BundleEntityId(qre.getHitIds().get(x).toString(), context) :
                                  new BundleEntity(qre.getHitIds().get(x).toString(), context) );
                          }; break;

                          case Constants.EPERSON: {
                              entities.add(idOnly ?
                                  new UserEntityId(qre.getHitIds().get(x).toString(), context) :
                                  new UserEntity(qre.getHitIds().get(x).toString(), context) );
                          }; break;

                      }
                  }
                          
          }
          catch (Exception ex) {  };

        return entities;
    }


    /**
     * Returns a Entity object with sample data
     */

    public Object getSampleEntity() {
        return null;
    }


}
