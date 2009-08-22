/*
 * AbstractBaseProvider.java
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

import org.sakaiproject.entitybus.entityprovider.EntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.capabilities.*;
import org.sakaiproject.entitybus.entityprovider.extension.RequestStorage;
import org.sakaiproject.entitybus.entityprovider.extension.Formats;
import org.dspace.core.Context;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.eperson.EPerson;
import org.dspace.authorize.AuthorizeException;
import org.sakaiproject.entitybus.exception.EntityException;
import org.apache.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sakaiproject.entitybus.EntityView;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import org.dspace.rest.util.UtilHelper;

/**
 * Base abstract class for Entity Providers. Takes care about general
 * operations like extracting url parameters, registration, unregistration <br/>
 * and other stuff. The Entity Provider should extend this class and implement
 * CoreEntityProvider. This class implements capabilities as it is currently
 * planed for REST support in DSpace, meaning, there is no Inputable capability
 * implemented but could be easily extended later if necessary.
 *
 * @author Bojan Suzic(bojan.suzic@gmail.com)
 */
public abstract class AbstractBaseProvider implements EntityProvider, Resolvable, CollectionResolvable, Outputable, Describeable, ActionsExecutable, Redirectable, RequestInterceptor {

    // query parameters used in subclasses
    protected RequestStorage reqStor;
    protected boolean idOnly,  topLevelOnly,  in_archive,  immediateOnly,  withdrawn;
    protected String query,  user,  pass,  userc,  passc,  _order,  _sort,  loggedUser,  _sdate,  _edate;
    protected int _start,  _page,  _perpage,  _limit,  sort;
    protected List<Integer> sortOptions = new ArrayList<Integer>();
    protected Collection _collection = null;
    protected Community _community = null;
    private static Logger log = Logger.getLogger(UserProvider.class);

    /**
     * Handle registration of EntityProvider
     * @param entityProviderManager
     * @throws java.sql.SQLException
     */
    public AbstractBaseProvider(EntityProviderManager entityProviderManager) throws SQLException {
        this.entityProviderManager = entityProviderManager;
        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException("Unable to register the provider (" + this + "): " + e, e);
        }

        // get request info for later parsing of parameters
        this.reqStor = entityProviderManager.getRequestStorage();
    }
    protected EntityProviderManager entityProviderManager;

    public void setEntityProviderManager(EntityProviderManager entityProviderManager) {
        this.entityProviderManager = entityProviderManager;
    }

    public void init() throws Exception {
        entityProviderManager.registerEntityProvider(this);
    }

    public void destroy() throws Exception {
        entityProviderManager.unregisterEntityProvider(this);
    }

    /**
     * Extracts and returns information about current session user, for logging
     * @return
     */
    public String userInfo() {
        String ipaddr = "";
        try {
            ipaddr = this.entityProviderManager.getRequestGetter().getRequest().getRemoteAddr();
        } catch (NullPointerException ex) {
        }
        return "user:" + loggedUser + ":ip_addr=" + ipaddr + ":";
    }

    /**
     * Checks request headers and applying requested format and login data
     * note that header based request has precedence over query one
     * This method is called before other methods processing request
     * so we can change some properties of response
     * @param view
     * @param req
     * @param res
     */
    public void before(EntityView view, HttpServletRequest req, HttpServletResponse res) {
        // json by default if nothing is requested
        try {
            if (req.getContentType().equals("application/json")) {
                view.setExtension("json");
            } else if (req.getContentType().equals("application/xml")) {
                view.setExtension("xml");
            } else {
                view.setExtension("json");
            }
        } catch (Exception ex) {
            if (view.getFormat().equals("xml")) {
                view.setExtension("xml");
            } else {
                view.setExtension("json");
            }
        }

        /**
         * Check user/login data in header and apply if present
         */
        try {
            if (!(req.getHeader("user").isEmpty() && req.getHeader("pass").isEmpty())) {
                userc = req.getHeader("user");
                passc = req.getHeader("pass");
            }
        } catch (NullPointerException nu) {
            userc = "";
            passc = "";
        }
    }

    /**
     * Called after processing of request
     * Not relevant in this case but implementation must be available
     * @param view
     * @param req
     * @param res
     */
    public void after(EntityView view, HttpServletRequest req, HttpServletResponse res) {
    }

    /**
     * Extract parameters from query and do basic authentication, analyze
     * and prepare sorting and other fields
     * @param context current database context locally (in subclass method)
     * defined but used here for loging and other purposes
     */
    public void refreshParams(Context context) {

        /**
         * now check user login info and try to register
         */
        try {
            user = reqStor.getStoredValue("user").toString();
        } catch (NullPointerException ex) {
            user = "";
        }

        try {
            pass = reqStor.getStoredValue("pass").toString();
        } catch (NullPointerException ex) {
            pass = "";
        }

        // these are from header - have priority
        if (!(userc.isEmpty() && passc.isEmpty())) {
            user = userc;
            pass = passc;
        }

        // now try to login user
        loggedUser = "anonymous";
        try {
            EPerson eUser = EPerson.findByEmail(context, user);
            if ((eUser.canLogIn()) && (eUser.checkPassword(pass))) {
                context.setCurrentUser(eUser);
                loggedUser = eUser.getName();
            } else {
                throw new EntityException("Bad username or password", user, 403);
            }
        } catch (SQLException sql) {
            System.out.println(sql.toString());
        } catch (AuthorizeException auth) {
            throw new EntityException("Unauthorised", user, 401);
        } catch (NullPointerException ne) {
            if (!(user.equals("") && pass.equals(""))) {
                throw new EntityException("Bad username or password", user, 403);
            }
        }

        try {
            this.idOnly = reqStor.getStoredValue("idOnly").equals("true");
        } catch (NullPointerException ex) {
            idOnly = false;
        }

        try {
            this.immediateOnly = reqStor.getStoredValue("immediateOnly").equals("false");
        } catch (NullPointerException ex) {
            immediateOnly = true;
        }

        try {
            this.topLevelOnly = !(reqStor.getStoredValue("topLevelOnly").equals("false"));
        } catch (NullPointerException ex) {
            topLevelOnly = true;
        }

        try {
            query = reqStor.getStoredValue("query").toString();
        } catch (NullPointerException ex) {
            query = "";
        }

        try {
            in_archive = reqStor.getStoredValue("in_archive").toString().equalsIgnoreCase("true");
        } catch (NullPointerException ex) {
            in_archive = false;
        }

        /**
         * these are fields based on RoR conventions
         */
        try {
            _order = reqStor.getStoredValue("_order").toString();
        } catch (NullPointerException ex) {
            _order = "";
        }

        try {
            _sort = reqStor.getStoredValue("_sort").toString();
        } catch (NullPointerException ex) {
            _sort = "";
        }

        // both parameters are used according to requirements
        if (_order.length() > 0 && _sort.equals("")) {
            _sort = _order;
        }

        try {
            _start = Integer.parseInt(reqStor.getStoredValue("_start").toString());
        } catch (NullPointerException ex) {
            _start = 0;
        }

        try {
            _page = Integer.parseInt(reqStor.getStoredValue("_page").toString());
        } catch (NullPointerException ex) {
            _page = 0;
        }

        try {
            _perpage = Integer.parseInt(reqStor.getStoredValue("_perpage").toString());
        } catch (NullPointerException ex) {
            _perpage = 0;
        }

        try {
            _limit = Integer.parseInt(reqStor.getStoredValue("_limit").toString());
        } catch (NullPointerException ex) {
            _limit = 0;
        }


        // some checking for invalid values
        if (_page < 0) {
            _page = 0;
        }
        if (_perpage < 0) {
            _perpage = 0;
        }
        if (_limit < 0) {
            _limit = 0;
        }


        try {
            _sdate = reqStor.getStoredValue("startdate").toString();
        } catch (NullPointerException ex) {
            _sdate = null;
        }

        try {
            _edate = reqStor.getStoredValue("enddate").toString();
        } catch (NullPointerException ex) {
            _edate = null;
        }

        try {
            withdrawn = reqStor.getStoredValue("withdrawn").toString().equalsIgnoreCase("true");
        } catch (NullPointerException ex) {
            withdrawn = false;
        }


        // defining sort fields and values
        _sort = _sort.toLowerCase();
        String[] sort_arr = _sort.split(",");
        for (String option : sort_arr) {
            if (option.startsWith("submitter")) {
                sortOptions.add(UtilHelper.SORT_SUBMITTER);
            } else if (option.startsWith("lastname")) {
                sortOptions.add(UtilHelper.SORT_LASTNAME);
            } else if (option.startsWith("fullname")) {
                sortOptions.add(UtilHelper.SORT_FULL_NAME);
            } else if (option.startsWith("language")) {
                sortOptions.add(UtilHelper.SORT_LANGUAGE);
            } else if (option.startsWith("lastmodified")) {
                sortOptions.add(UtilHelper.SORT_LASTMODIFIED);
            } else if (option.startsWith("countitems")) {
                sortOptions.add(UtilHelper.SORT_COUNT_ITEMS);
            } else if (option.startsWith("name")) {
                sortOptions.add(UtilHelper.SORT_NAME);
            } else {
                sortOptions.add(UtilHelper.SORT_ID);
            }
            if ((option.endsWith("_desc") || option.endsWith("_reverse"))) {
                int i = sortOptions.get(sortOptions.size() - 1);
                sortOptions.remove(sortOptions.size() - 1);
                i += 100;
                sortOptions.add(i);
            }

        }

        int intcommunity = 0;
        int intcollection = 0;

        // integer values used in some parts
        try {
            intcommunity = Integer.parseInt(reqStor.getStoredValue("community").toString());
        } catch (NullPointerException nul) {
        }

        try {
            _community = Community.find(context, intcommunity);
        } catch (NullPointerException nul) {
        } catch (SQLException sql) {
        }

        try {
            intcollection = Integer.parseInt(reqStor.getStoredValue("collection").toString());
        } catch (NullPointerException nul) {
        }

        try {
            _collection = Collection.find(context, intcollection);
        } catch (NullPointerException nul) {
        } catch (SQLException sql) {
        }

        if ((intcommunity > 0) && (intcollection > 0)) {
            throw new EntityException("Bad request", "Community and collection selected", 400);
        }

        if ((intcommunity > 0) && (_community == null)) {
            throw new EntityException("Bad request", "Unknown community", 400);
        }

        if ((intcollection > 0) && (_collection == null)) {
            throw new EntityException("Bad request", "Unknown collection", 400);
        }

    }

    /**
     * Remove items from list in order to display only requested items
     * (according to _start, _limit etc.)
     * @param entities
     */
    public void removeTrailing(List<?> entities) {
        if ((_start > 0) && (_start < entities.size())) {
            for (int x = 0; x < _start; x++) {
                entities.remove(x);
            }
        }
        if (_perpage > 0) {
            entities.subList(0, _page * _perpage).clear();
        }
        if ((_limit > 0) && entities.size() > _limit) {
            entities.subList(_limit, entities.size()).clear();
        }
    }

    /**
     * Complete connection in order to lower load of sql server
     * this way it goes faster and prevents droppings with higher load
     * @param context
     */
    public void removeConn(Context context) {
        // close connection to prevent connection problems
        try {
            context.complete();
        } catch (SQLException ex) {
        }
    }

    public String[] getHandledOutputFormats() {
          return new String[]{Formats.JSON, Formats.XML, Formats.FORM};
    }
}
