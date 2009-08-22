/*
 * SearchResultsInfoEntity.java
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
package org.dspace.rest.entities;

import java.util.List;
import java.util.ArrayList;

/**
 * Entity decribing search results
 * @see SearchProvider
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class SearchResultsInfoEntity {

    int resultsCount;
    List<Object> resultTypes = new ArrayList<Object>();
    List<Object> resultHandles = new ArrayList<Object>();
    List<Object> resultsIDs = new ArrayList<Object>();

    /**
     * Constructs SearchResultsInfoEntity, which should contain basic info
     * on results of search performed
     * 
     * @param res number of results
     * @param types list including types (DAO entity type) of results
     * @param handles list including handles (DAO handle) of results
     * @param ids list including ids of results
     */
    public SearchResultsInfoEntity(int res, List types, List handles, List ids) {
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

    // these are added for sorting management
    public String getName() {
        return "";
    }

    public int getId() {
        return 0;
    }
}
