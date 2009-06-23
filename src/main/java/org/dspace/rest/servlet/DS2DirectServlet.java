/**
 * $Id$
 * $URL$
 * DS2DirectServlet.java - entity-broker - 31 May 2007 7:01:11 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2007, 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dspace.rest.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dspace.kernel.DSpaceKernel;
import org.dspace.kernel.DSpaceKernelManager;
import org.dspace.kernel.ServiceManager;
import org.dspace.rest.providers.AbstractDS2RESTProvider;
import org.dspace.services.RequestService;
import org.dspace.services.SessionService;
import org.sakaiproject.entitybus.EntityBrokerManager;
import org.sakaiproject.entitybus.providers.EntityRequestHandler;
import org.sakaiproject.entitybus.rest.EntityBrokerRESTServiceManager;
import org.sakaiproject.entitybus.util.servlet.DirectServlet;

/**
 * Direct servlet allows unfettered access to entity URLs within DSpace 2
 * and provides the EB REST access
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
public class DS2DirectServlet extends DirectServlet {

    private static final long serialVersionUID = 2L;

    private transient EntityBrokerRESTServiceManager entityRESTServiceManager;
    private transient SessionService sessionService;
    private transient RequestService requestService;

    private transient List<AbstractDS2RESTProvider> entityProviders;

    @Override
    public EntityRequestHandler initializeEntityRequestHandler() {
        EntityRequestHandler erh;
        try {
            DSpaceKernel kernel = new DSpaceKernelManager().getKernel();
            if (kernel == null) {
                throw new IllegalStateException("Could not get the DSpace Kernel");
            }
            if (! kernel.isRunning()) {
                throw new IllegalStateException("DSpace Kernel is not running, cannot startup the DirectServlet");
            }

            ServiceManager serviceManager = kernel.getServiceManager();

            sessionService = serviceManager.getServiceByName(SessionService.class.getName(), SessionService.class);
            if (sessionService == null) {
                throw new IllegalStateException("Could not get the DSpace SessionService");
            }
            requestService = serviceManager.getServiceByName(RequestService.class.getName(), RequestService.class);
            if (requestService == null) {
                throw new IllegalStateException("Could not get the DSpace RequestService");
            }

            // fire up the EB rest services
            EntityBrokerManager ebm = serviceManager.getServiceByName(EntityBrokerManager.class.getName(), EntityBrokerManager.class);
            // create the EB REST services
            this.entityRESTServiceManager = new EntityBrokerRESTServiceManager(ebm);
            erh = this.entityRESTServiceManager.getEntityRequestHandler();
            if (erh == null) {
                throw new RuntimeException("FAILED to load EB EntityRequestHandler");
            }

            // fire up the providers
            // TODO
        } catch (Exception e) {
            throw new IllegalStateException("FAILURE during init of direct servlet: " + e.getMessage(), e);
        }
        return erh;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.entityProviders != null) {
            for (AbstractDS2RESTProvider provider : entityProviders) {
                if (provider != null) {
                    try {
                        provider.destroy();
                    } catch (Exception e) {
                        System.err.println("Could not clean up provider ("+provider+") on destroy: " + e);
                    }
                }
            }
        }
    }

    @Override
    public String getCurrentLoggedInUserId() {
        String userId = this.sessionService.getCurrentUserId();
        return userId;
    }

    @Override
    public void handleUserLogin(HttpServletRequest req, HttpServletResponse res, String path) {
        // attempt basic auth first?
        throw new SecurityException("Not able to handle login redirects yet");
    }

}
