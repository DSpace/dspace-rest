/**
 * $Id: AbstractDS2RESTProvider.java 3405 2009-01-29 17:39:45Z azeckoski $
 * $URL: http://scm.dspace.org/svn/repo/modules/rest/trunk/src/main/java/org/dspace/rest/providers/AbstractDS2RESTProvider.java $
 * AbstractEntityProvider.java - entity-broker - Apr 30, 2008 7:26:11 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Sakai Foundation
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

package org.dspace.rest.providers;

import org.sakaiproject.entitybus.DeveloperHelperService;
import org.sakaiproject.entitybus.entityprovider.EntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;

/**
 * Makes it easier to write {@link EntityProvider}s in webapps <br/>
 * A class to extend that gets rid of some of the redundant code that has
 * to be written over and over, causes this provider to be registered when it
 * is created and unregistered when it is destroyed, also includes the
 * {@link DeveloperHelperService} as a protected variable
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
public abstract class AbstractDS2RESTProvider implements EntityProvider {

    public AbstractDS2RESTProvider() { }

    public AbstractDS2RESTProvider(EntityProviderManager entityProviderManager,
            DeveloperHelperService developerHelperService) {
        this.entityProviderManager = entityProviderManager;
        this.developerHelperService = developerHelperService;
        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException("Unable to register the provider ("+this+"): " + e, e);
        }
    }

    private EntityProviderManager entityProviderManager;
    public void setEntityProviderManager(EntityProviderManager entityProviderManager) {
        this.entityProviderManager = entityProviderManager;
    }

    protected DeveloperHelperService developerHelperService;
    public void setDeveloperHelperService(DeveloperHelperService developerHelperService) {
        this.developerHelperService = developerHelperService;
    }

    public void init() throws Exception {
        entityProviderManager.registerEntityProvider(this);
    }

    public void destroy() throws Exception {
        entityProviderManager.unregisterEntityProvider(this);
    }

}
