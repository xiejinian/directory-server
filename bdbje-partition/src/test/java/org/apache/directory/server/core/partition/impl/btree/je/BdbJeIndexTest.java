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

package org.apache.directory.server.core.partition.impl.btree.je;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.cursor.Cursor;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.schema.extractor.SchemaLdifExtractor;
import org.apache.directory.api.ldap.schema.extractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.api.ldap.schema.loader.LdifSchemaLoader;
import org.apache.directory.api.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.api.util.FileUtils;
import org.apache.directory.api.util.Strings;
import org.apache.directory.api.util.exception.Exceptions;
import org.apache.directory.server.core.api.partition.PartitionTxn;
import org.apache.directory.server.xdbm.Index;
import org.apache.directory.server.xdbm.IndexEntry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BdbJeIndexTest
{
    private Index<String, String> idx;
    private static SchemaManager schemaManager;
    private PartitionTxn partitionTxn;

    /** The temporary directory the files will be created in */
    private static Path tempDir;

    static BdbJePartitionEnviroment env;


    @BeforeClass
    public static void init() throws Exception
    {
        tempDir = Files.createTempDirectory( BdbJeIndexTest.class.getSimpleName() );

        File schemaRepository = new File( tempDir.toFile(), "schema" );
        SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor( tempDir.toFile() );
        extractor.extractOrCopy( true );
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        schemaManager = new DefaultSchemaManager( loader );

        boolean loaded = schemaManager.loadAllEnabled();

        if ( !loaded )
        {
            fail( "Schema load failed : " + Exceptions.printErrors( schemaManager.getErrors() ) );
        }

        env = new BdbJePartitionEnviroment( schemaManager, tempDir.toFile() );
    }


    @Before
    public void setup() throws IOException
    {
        renewTxn();
    }


    private void renewTxn()
    {
        if ( partitionTxn != null )
        {
            try
            {
                partitionTxn.commit();
            }
            catch ( IOException e )
            {
                throw new RuntimeException( e );
            }
        }
        partitionTxn = new JeTransaction( env.createTxn() );
    }


    @After
    public void teardown() throws Exception
    {
        destroyIndex();
    }


    @AfterClass
    public static void cleanup() throws Exception
    {
        FileUtils.deleteDirectory( tempDir.toFile() );
    }


    void destroyIndex() throws Exception
    {
        if ( idx != null )
        {
            idx.close( partitionTxn );
            env.deleteIndexDb( idx.getAttribute().getOid() );
        }

        idx = null;
    }


    void initIndex() throws Exception
    {
        AttributeType attributeType = schemaManager.lookupAttributeTypeRegistry( SchemaConstants.OU_AT );
        BdbJeIndex<String> index = new BdbJeIndex<String>( attributeType.getName(), false );
        initIndex( index );
    }


    void initIndex( BdbJeIndex<String> bdbJeIdx ) throws Exception
    {
        AttributeType attributeType = schemaManager.lookupAttributeTypeRegistry( SchemaConstants.OU_AT );

        if ( bdbJeIdx == null )
        {
            bdbJeIdx = new BdbJeIndex<String>( attributeType.getName(), false );
        }

        bdbJeIdx.init( schemaManager, env );
        this.idx = bdbJeIdx;
    }


    // -----------------------------------------------------------------------
    // Property Test Methods
    // -----------------------------------------------------------------------

    @Test(expected = LdapException.class)
    public void testAttributeId() throws Exception
    {
        // uninitialized index
        BdbJeIndex<Object> jdbmIndex1 = new BdbJeIndex<Object>( "foo", false );
        assertEquals( "foo", jdbmIndex1.getAttributeId() );

        BdbJeIndex<Object> jdbmIndex2 = new BdbJeIndex<Object>( "bar", false );
        assertEquals( "bar", jdbmIndex2.getAttributeId() );

        // initialized index
        initIndex();

        try
        {
            idx.setAttributeId( "foo" );
            fail( "Should not be able to set attributeId after initialization." );
        }
        catch ( Exception e )
        {
        }

        assertEquals( "ou", idx.getAttributeId() );

        destroyIndex();
        BdbJeIndex<String> index = new BdbJeIndex<String>( "foo", false );
        initIndex( index );
        assertEquals( "foo", idx.getAttributeId() );
    }


    @Test
    public void testGetAttribute() throws Exception
    {
        // uninitialized index
        BdbJeIndex<Object> jdbmIndex = new BdbJeIndex<Object>( "ou", false );
        assertNull( jdbmIndex.getAttribute() );

        initIndex();
        assertEquals( schemaManager.lookupAttributeTypeRegistry( "ou" ), idx.getAttribute() );
    }


    // -----------------------------------------------------------------------
    // Count Test Methods
    // -----------------------------------------------------------------------

    @Test
    public void testCount() throws Exception
    {
        initIndex();
        assertEquals( 0, idx.count( partitionTxn ) );

        idx.add( partitionTxn, "foo", Strings.getUUID( 1234L ) );
        assertEquals( 1, idx.count( partitionTxn ) );

        idx.add( partitionTxn, "foo", Strings.getUUID( 333L ) );
        assertEquals( 2, idx.count( partitionTxn ) );

        idx.add( partitionTxn, "bar", Strings.getUUID( 555L ) );
        assertEquals( 3, idx.count( partitionTxn ) );
    }


    @Test
    public void testCountOneArg() throws Exception
    {
        initIndex();
        assertEquals( 0, idx.count( partitionTxn, " foo " ) );

        idx.add( partitionTxn, "bar", Strings.getUUID( 1234L ) );
        assertEquals( 0, idx.count( partitionTxn, " foo " ) );

        idx.add( partitionTxn, " foo ", Strings.getUUID( 1234L ) );
        assertEquals( 1, idx.count( partitionTxn, " foo " ) );

        idx.add( partitionTxn, " foo ", Strings.getUUID( 333L ) );
        assertEquals( 2, idx.count( partitionTxn, " foo " ) );
    }


    @Test
    public void testGreaterThanCount() throws Exception
    {
        initIndex();
        assertEquals( 0, idx.greaterThanCount( partitionTxn, "a" ) );

        for ( char ch = 'a'; ch <= 'z'; ch++ )
        {
            idx.add( partitionTxn, String.valueOf( ch ), Strings.getUUID( ch ) );
        }

        // We should not go above the magic limit of 10
        assertEquals( 10, idx.greaterThanCount( partitionTxn, "a" ) );
    }


    @Test
    public void testLessThanCount() throws Exception
    {
        initIndex();
        assertEquals( 0, idx.lessThanCount( partitionTxn, "z" ) );

        for ( char ch = 'a'; ch <= 'z'; ch++ )
        {
            idx.add( partitionTxn, String.valueOf( ch ), Strings.getUUID( ch ) );
        }

        // We should not go above the magic limit of 10
        assertEquals( 10, idx.lessThanCount( partitionTxn, "z" ) );
    }


    // -----------------------------------------------------------------------
    // Add, Drop and Lookup Test Methods
    // -----------------------------------------------------------------------

    @Test
    public void testLookupsToo() throws Exception
    {
        AttributeType attributeType = schemaManager.lookupAttributeTypeRegistry( "seeAlso" );
        BdbJeIndex<String> index = new BdbJeIndex<String>( attributeType.getName(), false );
        index.init( schemaManager, env );
        this.idx = index;

        String foobarDn = "uid=foo,ou=bar";
        String bazbarDn = "uid=baz,ou=bar";

        assertNull( idx.forwardLookup( partitionTxn, foobarDn ) );
        assertNull( idx.forwardLookup( partitionTxn, bazbarDn ) );
        idx.add( partitionTxn, foobarDn, Strings.getUUID( 0L ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, foobarDn ) );
        assertNull( idx.forwardLookup( partitionTxn, bazbarDn ) );
        idx.add( partitionTxn, bazbarDn, Strings.getUUID( 24L ) );
        assertEquals( Strings.getUUID( 24L ), idx.forwardLookup( partitionTxn, bazbarDn ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, foobarDn ) );
    }


    @Test
    public void testLookups() throws Exception
    {
        initIndex();
        assertNull( idx.forwardLookup( partitionTxn, " foo " ) );
        assertNull( idx.forwardLookup( partitionTxn, " bar " ) );

        idx.add( partitionTxn, " foo ", Strings.getUUID( 0L ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, " foo " ) );
        assertTrue( idx.forward( partitionTxn, " foo ", Strings.getUUID( 0L ) ) );

        idx.add( partitionTxn, " foo ", Strings.getUUID( 1L ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, " foo " ) );
        assertTrue( idx.forward( partitionTxn, " foo ", Strings.getUUID( 0L ) ) );
        assertTrue( idx.forward( partitionTxn, " foo ", Strings.getUUID( 1L ) ) );

        idx.add( partitionTxn, " bar ", Strings.getUUID( 0L ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, " bar " ) );
        assertTrue( idx.forward( partitionTxn, " bar ", Strings.getUUID( 0L ) ) );
        assertTrue( idx.forward( partitionTxn, " foo ", Strings.getUUID( 0L ) ) );
        assertTrue( idx.forward( partitionTxn, " foo ", Strings.getUUID( 1L ) ) );
    }


    @Test
    public void testAddDropById() throws Exception
    {
        initIndex();
        assertNull( idx.forwardLookup( partitionTxn, " foo " ) );
        assertNull( idx.forwardLookup( partitionTxn, " bar " ) );

        // test add/drop without adding any duplicates
        idx.add( partitionTxn, " foo ", Strings.getUUID( 0L ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, " foo " ) );

        idx.drop( partitionTxn, " foo ", Strings.getUUID( 0L ) );
        assertNull( idx.forwardLookup( partitionTxn, " foo " ) );

        // test add/drop with duplicates in bulk
        idx.add( partitionTxn, " foo ", Strings.getUUID( 0L ) );
        idx.add( partitionTxn, " foo ", Strings.getUUID( 1L ) );
        idx.add( partitionTxn, " bar ", Strings.getUUID( 0L ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, " foo " ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, " bar " ) );

        idx.drop( partitionTxn, " foo ", Strings.getUUID( 0L ) );
        idx.drop( partitionTxn, " bar ", Strings.getUUID( 0L ) );
        assertFalse( idx.forward( partitionTxn, " bar ", Strings.getUUID( 0L ) ) );
        assertFalse( idx.forward( partitionTxn, " foo ", Strings.getUUID( 0L ) ) );

        idx.drop( partitionTxn, " bar ", Strings.getUUID( 1L ) );
        idx.drop( partitionTxn, " foo ", Strings.getUUID( 1L ) );
        assertNull( idx.forwardLookup( partitionTxn, " foo " ) );
        assertNull( idx.forwardLookup( partitionTxn, " bar " ) );
        assertEquals( 0, idx.count( partitionTxn ) );
    }


    @Test
    public void testAddDropOneByOne() throws Exception
    {
        initIndex();
        assertNull( idx.forwardLookup( partitionTxn, " foo " ) );
        assertNull( idx.forwardLookup( partitionTxn, " bar " ) );

        // test add/drop without adding any duplicates
        idx.add( partitionTxn, " foo ", Strings.getUUID( 0L ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, " foo " ) );

        idx.drop( partitionTxn, " foo ", Strings.getUUID( 0L ) );
        assertNull( idx.forwardLookup( partitionTxn, " foo " ) );

        // test add/drop with duplicates but one at a time
        idx.add( partitionTxn, " foo ", Strings.getUUID( 0L ) );
        idx.add( partitionTxn, " foo ", Strings.getUUID( 1L ) );
        idx.add( partitionTxn, " bar ", Strings.getUUID( 0L ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, " foo " ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, " bar " ) );

        idx.drop( partitionTxn, " bar ", Strings.getUUID( 0L ) );
        assertEquals( Strings.getUUID( 0L ), idx.forwardLookup( partitionTxn, " foo " ) );
        assertFalse( idx.forward( partitionTxn, " bar ", Strings.getUUID( 0L ) ) );

        idx.drop( partitionTxn, " foo ", Strings.getUUID( 0L ) );
        assertEquals( Strings.getUUID( 1L ), idx.forwardLookup( partitionTxn, " foo " ) );
        assertFalse( idx.forward( partitionTxn, " foo ", Strings.getUUID( 0L ) ) );

        idx.drop( partitionTxn, " foo ", Strings.getUUID( 1L ) );
        assertNull( idx.forwardLookup( partitionTxn, " foo " ) );
        assertNull( idx.forwardLookup( partitionTxn, " bar " ) );
        assertEquals( 0, idx.count( partitionTxn ) );
    }


    // -----------------------------------------------------------------------
    // Miscellaneous Test Methods
    // -----------------------------------------------------------------------

    @Test
    public void testCursors() throws Exception
    {
        initIndex();
        assertEquals( 0, idx.count( partitionTxn ) );

        idx.add( partitionTxn, " foo ", Strings.getUUID( 1234L ) );
        assertEquals( 1, idx.count( partitionTxn ) );

        idx.add( partitionTxn, " foo ", Strings.getUUID( 333L ) );
        assertEquals( 2, idx.count( partitionTxn ) );

        idx.add( partitionTxn, "bar", Strings.getUUID( 555L ) );
        assertEquals( 3, idx.count( partitionTxn ) );

        // use forward index's cursor
        Cursor<IndexEntry<String, String>> cursor = idx.forwardCursor( partitionTxn );
        cursor.beforeFirst();

        assertEquals( 3, idx.count( partitionTxn ) );

        cursor.next();
        IndexEntry<String, String> e1 = cursor.get();
        assertEquals( Strings.getUUID( 555L ), e1.getId() );
        assertEquals( "bar", e1.getKey() );

        cursor.next();
        IndexEntry<String, String> e2 = cursor.get();
        assertEquals( Strings.getUUID( 333L ), e2.getId() );
        assertEquals( " foo ", e2.getKey() );

        cursor.next();
        IndexEntry<String, String> e3 = cursor.get();
        assertEquals( Strings.getUUID( 1234L ), e3.getId() );
        assertEquals( " foo ", e3.getKey() );

        cursor.close();
    }


    @Test(expected = LdapException.class)
    public void testNoEqualityMatching() throws Exception
    {
        BdbJeIndex<Object> jdbmIndex = new BdbJeIndex<Object>( "1.1", false );

        AttributeType noEqMatchAttribute = new AttributeType( "1.1" );
        jdbmIndex.init( schemaManager, env );
        fail( "should not get here" );
    }


    // -----------------------------------------------------------------------
    // Failing Tests
    // -----------------------------------------------------------------------

    @Test
    public void testSingleValuedAttribute() throws Exception
    {
        BdbJeIndex<Object> jdbmIndex = new BdbJeIndex<Object>( SchemaConstants.CREATORS_NAME_AT, false );
        jdbmIndex.init( schemaManager, env );
        jdbmIndex.close( partitionTxn );
    }
}