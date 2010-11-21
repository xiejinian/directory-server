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
package org.apache.directory.shared.kerberos;

/**
 * An cass to define Kerberos constants
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class KerberosConstants
{
    /** The Kerberos version 5 */
    public static final int KERBEROS_V5 = 5;

    //-------------------------------------------------------------------------
    // Messages
    //-------------------------------------------------------------------------
    /** Ticket message's tags */
    public static final int TICKET_TAG = 0x61;
    public static final int TICKET_TKT_VNO_TAG = 0xA0;
    public static final int TICKET_REALM_TAG = 0xA1;
    public static final int TICKET_SNAME_TAG = 0xA2;
    public static final int TICKET_ENC_PART_TAG = 0xA3;
    
    /** Authenticator tags */
    public static final int AUTHENTICATOR_TAG = 0x62;
    public static final int AUTHENTICATOR_AUTHENTICATOR_VNO_TAG = 0xA0;
    public static final int AUTHENTICATOR_CREALM_TAG = 0xA1;
    public static final int AUTHENTICATOR_CNAME_TAG = 0xA2;
    public static final int AUTHENTICATOR_CKSUM_TAG = 0xA3;
    public static final int AUTHENTICATOR_CUSEC_TAG = 0xA4;
    public static final int AUTHENTICATOR_CTIME_TAG = 0xA5;
    public static final int AUTHENTICATOR_SUBKEY_TAG = 0xA6;
    public static final int AUTHENTICATOR_SEQ_NUMBER_TAG = 0xA7;
    public static final int AUTHENTICATOR_AUTHORIZATION_DATA_TAG = 0xA8;
    
    /** AS-REQ's tags */
    public static final int AS_REQ_TAG = 0x6A;
    
    /** AS-REP's tags */
    public static final int AS_REP_TAG = 0x6B;
    
    /** TGS-REQ's tags */
    public static final int TGS_REQ_TAG = 0x6C;
    
    /** TGS-REP's tags */
    public static final int TGS_REP_TAG = 0x6D;
    
    /** AP-REQ tags */
    public static final int AP_REQ_TAG = 0x6E;
    public static final int AP_REQ_PVNO_TAG = 0xA0;
    public static final int AP_REQ_MSG_TYPE_TAG = 0xA1;
    public static final int AP_REQ_AP_OPTIONS_TAG = 0xA2;
    public static final int AP_REQ_TICKET_TAG = 0xA3;
    public static final int AP_REQ_AUTHENTICATOR_TAG = 0xA4;
    
    /** AP-REP tags */
    public static final int AP_REP_TAG = 0x6F;
    public static final int AP_REP_PVNO_TAG = 0xA0;
    public static final int AP_REP_MSG_TYPE_TAG = 0xA1;
    public static final int AP_REP_ENC_PART_TAG = 0xA2;
    
    /** KRB-ERROR tags */
    public static final int KRB_ERR_TAG = 0x7E;
    public static final int KRB_ERR_PVNO_TAG = 0xA0;
    public static final int KRB_ERR_MSGTYPE_TAG = 0xA1;
    public static final int KRB_ERR_CTIME_TAG = 0xA2;
    public static final int KRB_ERR_CUSEC_TAG = 0xA3;
    public static final int KRB_ERR_STIME_TAG = 0xA4;
    public static final int KRB_ERR_SUSEC_TAG = 0xA5;
    public static final int KRB_ERR_ERROR_CODE_TAG = 0xA6;
    public static final int KRB_ERR_CREALM_TAG = 0xA7;
    public static final int KRB_ERR_CNAME_TAG = 0xA8;
    public static final int KRB_ERR_REALM_TAG = 0xA9;
    public static final int KRB_ERR_SNAME_TAG = 0xAA;
    public static final int KRB_ERR_ETEXT_TAG = 0xAB;
    public static final int KRB_ERR_EDATA_TAG = 0xAC;

    //-------------------------------------------------------------------------
    // Components
    //-------------------------------------------------------------------------
    /** AuthorizationData tags */
    public static final int AUTHORIZATION_DATA_ADTYPE_TAG = 0xA0;
    public static final int AUTHORIZATION_DATA_ADDATA_TAG = 0xA1;

    /** Checksum tags */
    public static final int CHECKSUM_TYPE_TAG = 0xA0;
    public static final int CHECKSUM_CHECKSUM_TAG = 0xA1;

    /** Encrypteddata's tags */
    public static final int ENCRYPTED_DATA_ETYPE_TAG = 0xA0;
    public static final int ENCRYPTED_DATA_KVNO_TAG = 0xA1;
    public static final int ENCRYPTED_DATA_CIPHER_TAG = 0xA2;
    
    /** EncryptionKey tags */
    public static final int ENCRYPTION_KEY_TYPE_TAG = 0xA0;
    public static final int ENCRYPTION_KEY_VALUE_TAG = 0xA1;
    
    /** ETYPE-INFO-ENTRY tags */
    public static final int ETYPE_INFO_ENTRY_ETYPE_TAG = 0xA0;
    public static final int ETYPE_INFO_ENTRY_SALT_TAG = 0xA1;
    

    /** HostAddress' tags */
    public static final int HOST_ADDRESS_ADDR_TYPE_TAG = 0xA0;
    public static final int HOST_ADDRESS_ADDRESS_TAG = 0xA1;
    
    /** KRB-REP's tags */
    public static final int KDC_REP_PVNO_TAG = 0xA0;
    public static final int KDC_REP_MSG_TYPE_TAG = 0xA1;
    public static final int KDC_REP_PA_DATA_TAG = 0xA2;
    public static final int KDC_REP_CREALM_TAG = 0xA3;
    public static final int KDC_REP_CNAME_TAG = 0xA4;
    public static final int KDC_REP_TICKET_TAG = 0xA5;
    public static final int KDC_REP_ENC_PART_TAG = 0xA6;
    
    /** KRB-REQ's tags */
    public static final int KDC_REQ_PVNO_TAG = 0xA1;
    public static final int KDC_REQ_MSG_TYPE_TAG = 0xA2;
    public static final int KDC_REQ_PA_DATA_TAG = 0xA3;
    public static final int KDC_REQ_KDC_REQ_BODY_TAG = 0xA4;

    /** KRB-REQ-BODY's tags */
    public static final int KDC_REQ_BODY_KDC_OPTIONS_TAG = 0xA0;
    public static final int KDC_REQ_BODY_CNAME_TAG = 0xA1;
    public static final int KDC_REQ_BODY_REALM_TAG = 0xA2;
    public static final int KDC_REQ_BODY_SNAME_TAG = 0xA3;
    public static final int KDC_REQ_BODY_FROM_TAG = 0xA4;
    public static final int KDC_REQ_BODY_TILL_TAG = 0xA5;
    public static final int KDC_REQ_BODY_RTIME_TAG = 0xA6;
    public static final int KDC_REQ_BODY_NONCE_TAG = 0xA7;
    public static final int KDC_REQ_BODY_ETYPE_TAG = 0xA8;
    public static final int KDC_REQ_BODY_ADDRESSES_TAG = 0xA9;
    public static final int KDC_REQ_BODY_ENC_AUTHZ_DATA_TAG = 0xAA;
    public static final int KDC_REQ_BODY_ADDITIONAL_TICKETS_TAG = 0xAB;

    /** PaData tags */
    public static final int PADATA_TYPE_TAG = 0xA1;
    public static final int PADATA_VALUE_TAG = 0xA2;

    /** PrincipalName's tags */
    public static final int PRINCIPAL_NAME_NAME_TYPE_TAG = 0xA0;
    public static final int PRINCIPAL_NAME_NAME_STRING_TAG = 0xA1;

    /** TransitedEncoding tags */
    public static final int TRANSITED_ENCODING_TR_TYPE_TAG = 0xA0;
    public static final int TRANSITED_ENCODING_CONTENTS_TAG = 0xA1;
    
    /** KrbSafeBody tags */
    public static final int KRB_SAFE_BODY_USER_DATA_TAG = 0xA0;
    public static final int KRB_SAFE_BODY_TIMESTAMP_TAG = 0xA1;
    public static final int KRB_SAFE_BODY_USEC_TAG = 0xA2;
    public static final int KRB_SAFE_BODY_SEQ_NUMBER_TAG = 0xA3;
    public static final int KRB_SAFE_BODY_SENDER_ADDRESS_TAG = 0xA4;
    public static final int KRB_SAFE_BODY_RECIPIENT_ADDRESS_TAG = 0xA5;
    
    /** KrbSafe tags */
    public static final int KRB_SAFE_TAG = 0x14;
    public static final int KRB_SAFE_PVNO_TAG = 0xA0;
    public static final int KRB_SAFE_MSGTYPE_TAG = 0xA1;
    public static final int KRB_SAFE_SAFE_BODY_TAG = 0xA2;
    public static final int KRB_SAFE_CKSUM_TAG = 0xA3;
}