/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.directory.server.core.api.txn.logedit;


import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import org.apache.directory.server.core.api.log.LogAnchor;
import org.apache.directory.server.core.api.log.UserLogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractLogEdit implements LogEdit
{
    /** A logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractLogEdit.class );

    /** Position in the wal */
    private transient LogAnchor logAnchor = new LogAnchor();

    /** Transaction under which the change is done */
    protected long txnID;


    /**
     * Create a new instance of AbstractLogEdit
     * 
     * @param txnID The transaction ID to store
     */
    protected AbstractLogEdit( long txnID )
    {
        this.txnID = txnID;
    }


    /**
     * {@inheritDoc}
     */
    public LogAnchor getLogAnchor()
    {
        return logAnchor;
    }


    /**
     * {@inheritDoc}
     */
    public void apply( boolean recovery ) throws Exception
    {
        // do nothing by default
    }


    public long getTxnID()
    {
        return txnID;
    }


    public void setTxnID( long id )
    {
        txnID = id;
    }


    /**
     * {@inheritDoc}
     */
    public void injectData( UserLogRecord logRecord, UserLogRecord.LogEditType type ) throws Exception
    {
        logRecord.setType( type );
        LOG.debug( "Serializing : " + this );

        ObjectOutputStream out = null;
        ByteArrayOutputStream bout = null;
        byte[] data;

        try
        {
            bout = new ByteArrayOutputStream();
            out = new ObjectOutputStream( bout );

            // The LogEdit instance
            writeExternal( out );

            out.flush();
            data = bout.toByteArray();
        }
        finally
        {
            if ( bout != null )
            {
                bout.close();
            }

            if ( out != null )
            {
                out.close();
            }
        }

        logRecord.setData( data, data.length );
    }
}
