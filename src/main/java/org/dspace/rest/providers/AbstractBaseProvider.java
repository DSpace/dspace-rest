/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

import java.sql.SQLException;

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
    protected boolean idOnly, topLevelOnly, in_archive, immediateOnly, withdrawn;
    protected String query,  user,  pass,  userc,  passc,  _order,  _sort,  
            loggedUser, _sdate, _edate;
    protected int _start,  _page,  _perpage,  _limit,  sort;
    protected Collection _collection = null;
    protected Community _community = null;
    private static Logger log = Logger.getLogger(UserEntityProvider.class);

    public AbstractBaseProvider(EntityProviderManager entityProviderManager) throws SQLException {
        this.entityProviderManager = entityProviderManager;
        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException("Unable to register the provider (" + this + "): " + e, e);
        }
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

    public String userInfo() {
        String ipaddr = "";
        try {
            ipaddr = this.entityProviderManager.getRequestGetter().getRequest().getRemoteAddr();
        } catch (NullPointerException ex) {
        }
        return "user:" + loggedUser + ":ip_addr=" + ipaddr + ":";
    }

    // checking request headers and applying requested format and login data
    // note that header based request has precedence over query one
    public void before(EntityView view, HttpServletRequest req, HttpServletResponse res) {
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

    public void after(EntityView view, HttpServletRequest req, HttpServletResponse res) {

    }

    // extract parameters from query and do basic authentication
    public void refreshParams(Context context) {

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

        if (!(userc.isEmpty() && passc.isEmpty())) {
            user = userc;
            pass = passc;
        }

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


        // defining sort fields and values for UserEntityProvder
        if (this.getClass().getName().equalsIgnoreCase("org.dspace.rest.providers.UserEntityProvider")) {
            if (_sort.equalsIgnoreCase("id")) {
                sort = EPerson.ID;
            } else if (_sort.equalsIgnoreCase("language")) {
                sort = EPerson.LANGUAGE;
            } else if (_sort.equalsIgnoreCase("netid")) {
                sort = EPerson.NETID;
            } else {
                sort = EPerson.LASTNAME;
            }
        }

        int intcommunity = 0;
        int intcollection = 0;

        try {
            intcommunity = Integer.parseInt(reqStor.getStoredValue("community").toString());
        } catch (NullPointerException nul) { }

        try {
            _community = Community.find(context, intcommunity);
        } catch (NullPointerException nul) {
        } catch (SQLException sql) {  }

        try {
            intcollection = Integer.parseInt(reqStor.getStoredValue("collection").toString());
        } catch (NullPointerException nul) {  }

        try {
            _collection = Collection.find(context, intcollection);
        } catch (NullPointerException nul) {
        } catch (SQLException sql) { }

        if ((intcommunity > 0 ) && (intcollection > 0))
            throw new EntityException("Bad request", "Community and collection selected", 400);
        
        if ((intcommunity >0) && (_community == null))
            throw new EntityException("Bad request", "Unknown community", 400);

        if ((intcollection >0) && (_collection == null))
            throw new EntityException("Bad request", "Unknown collection", 400);

    }

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
