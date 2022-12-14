/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.interceptor;

import de.hybris.platform.b2b.punchout.jalo.PunchOutCredential;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * Validator for entity {@link PunchOutCredential}.
 */
public class PunchOutCredentialValidateInterceptor implements ValidateInterceptor
{
	private PunchOutCredentialService punchOutCredentialService;
	private L10NService l10NService;

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof PunchOutCredentialModel)
		{
			final PunchOutCredentialModel mapping = (PunchOutCredentialModel) model;
			existentCredential(mapping);
		}
	}

	/**
	 * Check if there is already a credential for the same domain and identity in the system.
	 *
	 * @param credential
	 *           The credential to be checked.
	 * @throws InterceptorException
	 *            If there is already an existent credential for the same main values.
	 */
	protected void existentCredential(final PunchOutCredentialModel credential) throws InterceptorException
	{
		final PunchOutCredentialModel idFound = getPunchOutCredentialService().getPunchOutCredential(credential.getDomain(),
				credential.getIdentity());
		if (idFound != null && !credential.equals(idFound))
		{
			throw new InterceptorException(localizeForKey("error.punchoutcredential.existentcredential"));
		}
	}

	protected String localizeForKey(final String key)
	{
		return getL10NService().getLocalizedString(key);
	}

	protected L10NService getL10NService()
	{
		return l10NService;
	}

	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
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
