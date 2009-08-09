/*
 * NOTICE GOES HERE
 * 
 */

package org.dspace.rest.servlet;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.entitybus.EntityBrokerManager;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.impl.EntityBrokerCoreServiceManager;
import org.sakaiproject.entitybus.providers.EntityRequestHandler;
import org.sakaiproject.entitybus.rest.EntityBrokerRESTServiceManager;
import org.sakaiproject.entitybus.util.servlet.DirectServlet;
import org.dspace.rest.providers.AbstractRESTProvider;
import org.dspace.rest.providers.CommunitiesProvider;
import org.dspace.rest.providers.StandardEntityProvider;
import org.dspace.rest.providers.TestEntityProvider;
import org.dspace.rest.providers.CollectionsProvider;
import org.dspace.rest.providers.BitstreamProvider;
import org.dspace.rest.providers.ItemsProvider;
import org.dspace.rest.providers.StatsProvider;
import org.dspace.rest.providers.UserEntityProvider;
import org.dspace.rest.providers.AbstractBaseProvider;
import org.dspace.content.*;
import org.dspace.core.*;



public class DS16DirectServlet extends DirectServlet {


    private static final long serialVersionUID = 2L;

    private transient EntityBrokerCoreServiceManager entityBrokerCoreServiceManager;
    private transient EntityBrokerRESTServiceManager entityRESTServiceManager;

    private transient List<AbstractBaseProvider> entityProviders;

    /**
     * Starts up all the entity providers and places them into the list
     * @param entityProviderManager the provider manager
     */
    protected void startProviders(EntityProviderManager entityProviderManager) throws java.sql.SQLException {
        String config = getServletContext().getInitParameter("dspace-config");

        // for dev testing only
        if (config.contains("dspace.dir")) {
            config = "/dspace/config/dspace.cfg";
        }
    
        ConfigurationManager.loadConfig(config);
//        this.entityProviders = new Vector<AbstractRESTProvider>();
        this.entityProviders = new Vector<AbstractBaseProvider>();
/*        this.entityProviders.add( new CommunitiesProvider(entityProviderManager) );
        this.entityProviders.add( new StandardEntityProvider(entityProviderManager) );
        this.entityProviders.add( new TestEntityProvider(entityProviderManager) );
        this.entityProviders.add( new CollectionsProvider(entityProviderManager) );
        this.entityProviders.add( new ItemsProvider(entityProviderManager) );
        this.entityProviders.add( new BitstreamProvider(entityProviderManager) );
        this.entityProviders.add( new StatsProvider(entityProviderManager) );
  */      this.entityProviders.add( new UserEntityProvider(entityProviderManager) );
    }

    @Override
    public EntityRequestHandler initializeEntityRequestHandler() {
        EntityRequestHandler erh;
        try {
            // fire up the EB services
            this.entityBrokerCoreServiceManager = new EntityBrokerCoreServiceManager();
            EntityBrokerManager ebm = this.entityBrokerCoreServiceManager.getEntityBrokerManager();
            // create the EB REST services
            this.entityRESTServiceManager = new EntityBrokerRESTServiceManager(ebm);
            erh = this.entityRESTServiceManager.getEntityRequestHandler();
            if (erh == null) {
                throw new RuntimeException("FAILED to load EB EntityRequestHandler");
            }

            EntityProviderManager epm = this.entityBrokerCoreServiceManager.getEntityProviderManager();
            

            // fire up the providers
            startProviders(epm);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("FAILURE during init of direct servlet: " + e, e);
        }
        return erh;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.entityProviders != null) {
//            for (AbstractRESTProvider provider : entityProviders) {
            for (AbstractBaseProvider provider : entityProviders) {
                if (provider != null) {
                    try {
                        provider.destroy();
                    } catch (Exception e) {
                        System.err.println("WARN Could not clean up provider ("+provider+") on destroy: " + e);
                    }
                }
            }
            this.entityProviders.clear();
            this.entityProviders = null;
        }
        if (this.entityRESTServiceManager != null) {
            this.entityRESTServiceManager.destroy();
            this.entityRESTServiceManager = null;
        }
        if (this.entityBrokerCoreServiceManager != null) {
            this.entityBrokerCoreServiceManager.destroy();
            this.entityBrokerCoreServiceManager = null;
        }
    }

    @Override
    public String getCurrentLoggedInUserId() {
        return "tester";
    }

    @Override
    public void handleUserLogin(HttpServletRequest req, HttpServletResponse res, String path) {
        // attempt basic auth first?
        throw new SecurityException("Not able to handle login redirects yet");
    }

}
