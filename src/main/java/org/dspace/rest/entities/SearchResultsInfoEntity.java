/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;


import java.util.List;
import java.util.ArrayList;

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
