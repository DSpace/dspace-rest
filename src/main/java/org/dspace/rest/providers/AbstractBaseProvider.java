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
import org.dspace.eperson.EPerson;
import org.dspace.authorize.AuthorizeException;
import org.sakaiproject.entitybus.exception.EntityException;

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
public abstract class AbstractBaseProvider implements EntityProvider, Resolvable, CollectionResolvable, Outputable,  Describeable, ActionsExecutable, Redirectable {
    protected Context context;

    // query parameters used in subclasses
    protected RequestStorage reqStor;
    protected boolean idOnly, topLevelOnly, in_archive, immediateOnly;
    protected String query, user, pass, _order, _sort;
    protected int _start, _page, _perpage, _limit, sort;

    public AbstractBaseProvider(EntityProviderManager entityProviderManager) throws SQLException {
        this.entityProviderManager = entityProviderManager;
        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException("Unable to register the provider ("+this+"): " + e, e);
        }
        this.reqStor = entityProviderManager.getRequestStorage();
        context = new Context();
    }

    private EntityProviderManager entityProviderManager;
    public void setEntityProviderManager(EntityProviderManager entityProviderManager) {
        this.entityProviderManager = entityProviderManager;
    }

    public void init() throws Exception {
        entityProviderManager.registerEntityProvider(this);
    }

    public void destroy() throws Exception {
        entityProviderManager.unregisterEntityProvider(this);
    }

    // extract parameters from query and do basic authentication
    public void refreshParams() {

        try {
            context = new Context();
        } catch (SQLException ex) { throw new EntityException("Internal server error", "SQL error", 500); }

        try {
            this.idOnly = reqStor.getStoredValue("idOnly").equals("true");
        } catch (NullPointerException ex) { idOnly = false; };

        try {
            this.immediateOnly = reqStor.getStoredValue("immediateOnly").equals("false");
        } catch (NullPointerException ex) { immediateOnly = true; };

        try {
            this.topLevelOnly = reqStor.getStoredValue("topLevelOnly").equals("false");
        } catch (NullPointerException ex) { topLevelOnly = true; };


        try {
            query = reqStor.getStoredValue("query").toString();
        } catch (NullPointerException ex) { query = ""; };

        try {
            user = reqStor.getStoredValue("user").toString();
        } catch (NullPointerException ex) { user = ""; };

        try {
            pass = reqStor.getStoredValue("pass").toString();
        } catch (NullPointerException ex) { pass = ""; };

        try {
            EPerson eUser = EPerson.findByEmail(context, user);
            if ((eUser.canLogIn()) && (eUser.checkPassword(pass))) 
                context.setCurrentUser(eUser);
            else
                throw new EntityException("Bad username or password", user, 403);
        } 
        catch (SQLException sql) { System.out.println (sql.toString()); }
        catch (AuthorizeException auth) { throw new EntityException("Unauthorised", user, 401); }
        catch (NullPointerException ne) { 
            if (!(user.equals("") && pass.equals("")))
                    throw new EntityException("Bad username or password", user, 403);
        }

        try {
            in_archive = reqStor.getStoredValue("in_archive").toString().equalsIgnoreCase("true");
        } catch (NullPointerException ex) { in_archive = false; };


        try {
            _order = reqStor.getStoredValue("_order").toString();
        } catch (NullPointerException ex) { _order = ""; };

        try {
            _sort = reqStor.getStoredValue("_sort").toString();
        } catch (NullPointerException ex) { _sort = ""; };

        try {
            _start = Integer.parseInt(reqStor.getStoredValue("_start").toString());
        } catch (NullPointerException ex) { _start = 0; };

        try {
            _page = Integer.parseInt(reqStor.getStoredValue("_page").toString());
        } catch (NullPointerException ex) { _page = 0; };

        try {
            _perpage = Integer.parseInt(reqStor.getStoredValue("_perpage").toString());
        } catch (NullPointerException ex) { _perpage = 0; };

        try {
            _limit = Integer.parseInt(reqStor.getStoredValue("_limit").toString());
        } catch (NullPointerException ex) { _limit = 0; };

        // defining sort fields and values for UserEntityProvder
        if (this.getClass().getName().equalsIgnoreCase("org.dspace.rest.providers.UserEntityProvider")) {
              if (_sort.equalsIgnoreCase("id"))
                      sort = EPerson.ID; else
              if (_sort.equalsIgnoreCase("language"))
                      sort = EPerson.LANGUAGE; else
              if (_sort.equalsIgnoreCase("netid"))
                      sort = EPerson.NETID; else
                          sort = EPerson.LASTNAME;

        }

    }

    public String[] getHandledOutputFormats() {
        return new String[] {Formats.HTML, Formats.JSON, Formats.XML, Formats.FORM};
     }
}
