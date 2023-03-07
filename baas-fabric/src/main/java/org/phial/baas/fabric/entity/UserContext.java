/****************************************************** 
 *  Copyright 2018 IBM Corporation 
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License.
 */ 
package org.phial.baas.fabric.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.phial.baas.api.constant.CryptoEnum;
import org.phial.baas.api.domain.entity.SysUser;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

/**
 * fabric sdk client User
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserContext extends SysUser implements User, Serializable {
	private static final long serialVersionUID = 1L;

	protected String name;
	protected Set<String> roles;
	protected String account;
	protected String affiliation;
	protected Enrollment enrollment;
	protected String mspId;
	private String cert;
	private String privateKey;
	private PublicKey publicKey;
	private String caId;

	private String secret;

	private String orgDomain;

	private Map<String, Object> attr;

	public void setName(String name) {
		this.name = name;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public void setEnrollment(Enrollment enrollment) {
		this.enrollment = enrollment;
	}

	public void setMspId(String mspId) {
		this.mspId = mspId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<String> getRoles() {
		return roles;
	}

	@Override
	public String getAccount() {
		return account;
	}

	@Override
	public String getAffiliation() {
		return affiliation;
	}

	@Override
	public Enrollment getEnrollment() {
		return enrollment;
	}

	@Override
	public String getMspId() {
		return mspId;
	}


	public X509Certificate parseCertificate() {
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public PrivateKey parsePrivateKey() {
		try {
			return Identities.readPrivateKey(this.privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public UserContext(String cert, String privateKey, String caId, String secret,
					   String orgDomain, CryptoEnum.CryptoUserType userType,
					   Map<String, Object> attr) {
		this.cert = cert;
		this.privateKey = privateKey;
		this.caId = caId;
		this.secret = secret;
		this.orgDomain = orgDomain;
		this.attr = attr;
		this.publicKey = parseCertificate().getPublicKey();
		this.userType = userType;
	}
}
