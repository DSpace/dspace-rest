/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dspace.rest.entities;

/**
 *
 * @author Administrator
 */
public class HarvestResultsInfoEntity {
    int resultsCount;

    public HarvestResultsInfoEntity (int res) {
        this.resultsCount = res;
    }

    public int getResultsCount() {
        return this.resultsCount;
    }

}
