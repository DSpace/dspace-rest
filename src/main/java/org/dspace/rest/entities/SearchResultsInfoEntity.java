/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Item;
import org.dspace.content.Bundle;
import org.dspace.content.Bitstream;
import org.dspace.core.Context;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

/**
 *
 * @author Administrator
 */
public class SearchResultsInfoEntity {
    int resultsCount;
    List<Object> resultTypes = new ArrayList<Object>();
    List<Object> resultHandles = new ArrayList<Object>();
    List<Object> resultsIDs = new ArrayList<Object>();

    public SearchResultsInfoEntity (int res, List types, List handles, List ids) {
        this.resultsCount = res;
        this.resultTypes = types;
        this.resultHandles = handles;
        this.resultsIDs = ids;
    }

    public int getResultsCount() {
        return this.resultsCount;
    }

    public List<?> getResultTypes() {
        return this.resultTypes;
    }

    public List<?> getResultHandles() {
        return this.resultHandles;
    }

    public List<?> getResultIDs() {
        return this.resultsIDs;
    }

}
