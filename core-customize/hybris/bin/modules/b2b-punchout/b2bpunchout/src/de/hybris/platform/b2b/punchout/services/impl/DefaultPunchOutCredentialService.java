/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.daos.PunchOutCredentialDao;
import de.hybris.platform.b2b.punchout.model.B2BCustomerPunchOutCredentialMappingModel;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;

import org.apache.commons.lang.StringUtils;
import org.cxml.Credential;
import org.cxml.SharedSecret;


/**
 * Default implementation of {@link PunchOutCredentialService}.
 */
public class DefaultPunchOutCredentialService implements PunchOutCredentialService
{
	private PunchOutCredentialDao credentialDao;

	@Override
	public PunchOutCredentialModel getPunchOutCredential(final String domain, final String identity)
	{
		return getCredentialDao().getPunchOutCredential(domain, identity);
	}

	protected B2BCustomerModel getCustomerForCredential(final Credential credential, final boolean verifySharedSecret)
	{
		B2BCustomerModel customer = null;
		final String domain = credential.getDomain();
		final String identity = extractIdentity(credential);

		final PunchOutCredentialModel credentialModel = getPunchOutCredential(domain, identity);
		if (credentialModel != null)
		{
			final B2BCustomerPunchOutCredentialMappingModel mappingModel = credentialModel.getB2BCustomerPunchOutCredentialMapping();
			boolean authenticated = true;

			if (verifySharedSecret)
			{
				final String sharedSecret = extractSharedSecret(credential);
				authenticated = StringUtils.equals(sharedSecret, credentialModel.getSharedsecret());
			}

			if (mappingModel != null && authenticated)
			{
				customer = mappingModel.getB2bCustomer();
			}
		}

		return customer;
	}

	@Override
	public B2BCustomerModel getCustomerForCredential(final Credential credential)
	{
		return getCustomerForCredential(credential, true);
	}

	@Override
	public B2BCustomerModel getCustomerForCredentialNoAuth(final Credential credential)
	{
		return getCustomerForCredential(credential, false);
	}

	protected String extractIdentity(final Credential credential)
	{
		String login = null;
		for (final Object obj : credential.getIdentity().getContent())
		{
			if (obj instanceof String)
			{
				login = (String) obj;
			}
		}
		return login;
	}

	protected String extractSharedSecret(final Credential credential)
	{
		String sharedSecretValue = null;
		for (final Object obj : credential.getSharedSecretOrDigitalSignatureOrCredentialMac())
		{
			if (obj instanceof SharedSecret)
			{
				final SharedSecret sharedSecret = (SharedSecret) obj;
				sharedSecretValue = (String) sharedSecret.getContent().get(0);
			}
		}
		return sharedSecretValue;
	}

	protected PunchOutCredentialDao getCredentialDao()
	{
		return credentialDao;
	}

	public void setCredentialDao(final PunchOutCredentialDao credentialDao)
	{
		this.credentialDao = credentialDao;
	}

}
