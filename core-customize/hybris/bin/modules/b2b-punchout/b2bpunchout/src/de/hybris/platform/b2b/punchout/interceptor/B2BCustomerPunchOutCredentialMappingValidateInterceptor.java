/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.interceptor;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.punchout.jalo.B2BCustomerPunchOutCredentialMapping;
import de.hybris.platform.b2b.punchout.model.B2BCustomerPunchOutCredentialMappingModel;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.apache.commons.lang.StringUtils;

/**
 * Validator for entity {@link B2BCustomerPunchOutCredentialMapping}.
 */
public class B2BCustomerPunchOutCredentialMappingValidateInterceptor implements ValidateInterceptor
{
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> customerService;
	private PunchOutCredentialService punchOutCredentialService;
	private L10NService l10NService;

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof B2BCustomerPunchOutCredentialMappingModel)
		{
			final B2BCustomerPunchOutCredentialMappingModel mapping = (B2BCustomerPunchOutCredentialMappingModel) model;
			isCustomerExistent(mapping);
			hasCredentials(mapping);
			isCredentialAlreadyExistent(mapping);
		}

	}

	/**
	 * Checks if the B2B Customer on the mapping exists on the system.
	 *
	 * @param mapping
	 *           The mapping that contains the customer.
	 * @throws InterceptorException
	 *            If B2B Customer does not exists.
	 */
	protected void isCustomerExistent(final B2BCustomerPunchOutCredentialMappingModel mapping) throws InterceptorException
	{
		final B2BCustomerModel customer = mapping.getB2bCustomer();
		final B2BCustomerModel existent = getCustomerService().getUserForUID(customer.getUid());
		if (existent == null)
		{
			throw new InterceptorException(localizeForKey("error.b2bcustomerpunchoutcredentialmapping.inexistentcustomer"));
		}
	}

	/**
	 * Checks if there are credentials in the mapping.
	 *
	 * @param mapping
	 *           The mapping that should contain a set of credentials.
	 * @throws InterceptorException
	 *            If the mapping does not contain any credentials.
	 */
	protected void hasCredentials(final B2BCustomerPunchOutCredentialMappingModel mapping) throws InterceptorException
	{
		if (mapping.getCredentials() == null || mapping.getCredentials().isEmpty())
		{
			throw new InterceptorException(localizeForKey("error.b2bcustomerpunchoutcredentialmapping.nocredentials"));
		}
	}

	/**
	 * Checks if the credentials in the mapping are not already set to another mapping.
	 *
	 * @param mapping
	 *           The current mapping to be verified.
	 * @throws InterceptorException
	 *            If at least one of the credentials on the mapping is already in use by another mapping.
	 */
	protected void isCredentialAlreadyExistent(final B2BCustomerPunchOutCredentialMappingModel mapping) throws InterceptorException
	{
		for (final PunchOutCredentialModel credential : mapping.getCredentials())
		{
			final PunchOutCredentialModel idFound = getPunchOutCredentialService().getPunchOutCredential(credential.getDomain(),
					credential.getIdentity());
			if (idFound != null && idFound.getB2BCustomerPunchOutCredentialMapping() != null)
			{
				final B2BCustomerModel customerFound = idFound.getB2BCustomerPunchOutCredentialMapping().getB2bCustomer();
				final B2BCustomerModel customer = mapping.getB2bCustomer();
				// check if credential is mapped to another customer
				if ((customerFound != null) && (customer != null) && (!StringUtils.equals(customerFound.getUid(), customer.getUid())))
				{
					throw new InterceptorException(
							localizeForKey("error.b2bcustomerpunchoutcredentialmapping.credentialalreadymapped"));
				}
			}
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

	protected B2BCustomerService<B2BCustomerModel, B2BUnitModel> getCustomerService()
	{
		return customerService;
	}

	public void setCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> customerService)
	{
		this.customerService = customerService;
	}
}
