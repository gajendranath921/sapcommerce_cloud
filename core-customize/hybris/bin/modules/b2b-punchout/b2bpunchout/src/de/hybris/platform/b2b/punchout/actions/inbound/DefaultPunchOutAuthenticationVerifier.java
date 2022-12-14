/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseCode;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.Header;
import org.cxml.Sender;


/**
 * Authenticates information in the cXML.
 */
public class DefaultPunchOutAuthenticationVerifier
{
	private PunchOutCredentialService punchOutCredentialService;

	/**
	 * Extracts the header information and matches the sender details with the configuration.
	 *
	 * @param request
	 *           the CXML request message
	 */
	public void verify(final CXML request)
	{
		final CXMLElementBrowser cXmlBrowser = new CXMLElementBrowser(request);

		final Header header = cXmlBrowser.findHeader();
		Validate.notNull(header, "PunchOut cXML request incomplete. Missing Header node.");

		final Sender sender = header.getSender();
		Validate.notNull(sender, "PunchOut cXML request incomplete. Missing Sender node.");

		final boolean success = authenticateSender(sender.getCredential());
		if (!success)
		{
			final String message = String
					.format("Authentication failed, please check if the credential is mapped to an hybris user and the Shared "
							+ "Secret matches the configuration.");
			throw new PunchOutException(PunchOutResponseCode.ERROR_CODE_AUTH_FAILED, message);
		}
	}

	/**
	 * Matches the given credentials against the configured ones in the system. It is necessary for at least one of the
	 * credentials to match.
	 *
	 * @param credentials
	 *           the credentials to check
	 * @return true if at least one matches
	 */
	protected boolean authenticateSender(final List<Credential> credentials)
	{
		boolean authenticated = false;
		for (final Credential credential : credentials)
		{

			final B2BCustomerModel customer = getPunchOutCredentialService().getCustomerForCredential(credential);
			if (customer != null)
			{
				authenticated = true;
				break;
			}
		}
		return authenticated;
	}


	protected PunchOutCredentialService getPunchOutCredentialService()
	{
		return punchOutCredentialService;
	}

	public void setPunchOutCredentialService(final PunchOutCredentialService punchOutCredentialService)
	{
		this.punchOutCredentialService = punchOutCredentialService;
	}

}
