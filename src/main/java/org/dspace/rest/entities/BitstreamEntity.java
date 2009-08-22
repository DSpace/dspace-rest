/*
 * BitstreamEntity.java
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

import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.dspace.content.Bundle;
import org.dspace.content.Bitstream;
import org.dspace.core.Context;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

/**
 * Entity describing Bitstreams
 * @see BitstreamEntityId
 * @see BitstreamProvider
 * @see Bitstream
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class BitstreamEntity extends BitstreamEntityId {

    @EntityId
    private int id;
    @EntityFieldRequired
    private String name;
    private String handle;
    private int type,  storeNumber;
    private long sequenceId,  size;
    private String checkSumAlgorithm,  description,  checkSum,  formatDescription,  source,  userFormatDescription,  mimeType;
    List<Object> bundles = new ArrayList<Object>();

    public BitstreamEntity(String uid, Context context) throws SQLException {
        Bitstream res = Bitstream.find(context, Integer.parseInt(uid));
        Bundle[] bnd = res.getBundles();
        this.id = res.getID();
        this.handle = res.getHandle();
        this.name = res.getName();
        this.type = res.getType();
        this.checkSum = res.getChecksum();
        this.checkSumAlgorithm = res.getChecksumAlgorithm();
        this.description = res.getDescription();
        this.formatDescription = res.getFormatDescription();
        this.sequenceId = res.getSequenceID();
        this.size = res.getSize();
        this.source = res.getSource();
        this.storeNumber = res.getStoreNumber();
        this.userFormatDescription = res.getUserFormatDescription();
        for (Bundle b : bnd) {
            this.bundles.add(new BundleEntity(b));
        }
        this.mimeType = res.getFormat().getMIMEType();
        context.complete();
    }

    public BitstreamEntity(Bitstream bitstream) throws SQLException {
        // check calling package/class in order to prevent chaining
        boolean includeFull = false;
        try {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            if ((ste.length > 1) && (ste[1].getClassName().contains("org.dspace.rest.providers"))) {
                includeFull = true;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        this.handle = bitstream.getHandle();
        this.name = bitstream.getName();
        this.type = bitstream.getType();
        this.id = bitstream.getID();
        this.checkSum = bitstream.getChecksum();
        this.checkSumAlgorithm = bitstream.getChecksumAlgorithm();
        this.description = bitstream.getDescription();
        this.formatDescription = bitstream.getFormatDescription();
        this.sequenceId = bitstream.getSequenceID();
        this.size = bitstream.getSize();
        this.source = bitstream.getSource();
        this.storeNumber = bitstream.getStoreNumber();
        this.userFormatDescription = bitstream.getUserFormatDescription();
        Bundle[] bnd = bitstream.getBundles();
        for (Bundle b : bnd) {
            this.bundles.add(includeFull ? new BundleEntity(b) : new BundleEntityId(b));
        }
        this.mimeType = bitstream.getFormat().getMIMEType();
    }

    public BitstreamEntity() {
        // check calling package/class in order to prevent chaining
        boolean includeFull = false;
        try {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            if ((ste.length > 1) && (ste[1].getClassName().contains("org.dspace.rest.providers"))) {
                includeFull = true;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        this.handle = null;
        this.name = "The name of the file";
        this.type = 0;
        this.id = 4;
        this.checkSum = "b74c368660979349459fe30c93e2d9c7";
        this.checkSumAlgorithm = "MD5";
        this.description = "Sample description";
        this.formatDescription = "Adobe PDF";
        this.sequenceId = 1;
        this.size = 123456789;
        this.source = "/dspace/upload/file.pdf";
        this.storeNumber = 0;
        this.userFormatDescription = null;
        this.bundles.add(includeFull ? new BundleEntity() : new BundleEntityId());
        this.mimeType = "application/pdf";
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public List<?> getBundles() {
        return this.bundles;
    }

    public String getCheckSum() {
        return this.checkSum;
    }

    public String getCheckSumAlgorithm() {
        return this.checkSumAlgorithm;
    }

    public String getDescription() {
        return this.description;
    }

    public String getFormatDescription() {
        return this.formatDescription;
    }

    public long getSequenceId() {
        return this.sequenceId;
    }

    public long getSize() {
        return this.size;
    }

    public String getSource() {
        return this.source;
    }

    public int getStoreNumber() {
        return this.storeNumber;
    }

    public String getUserFormatDescription() {
        return this.userFormatDescription;
    }

    public String getName() {
        return this.name;
    }

    public String getHandle() {
        return this.handle;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "id:" + this.id + ", stuff.....";
    }
}
