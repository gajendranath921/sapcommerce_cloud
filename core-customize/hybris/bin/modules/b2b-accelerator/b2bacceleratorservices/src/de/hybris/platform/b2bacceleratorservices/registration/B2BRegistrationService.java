/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.registration;

import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;

import java.util.List;


/**
 * Service methods that are used by the B2B registration process
 */
public interface B2BRegistrationService
{

	/**
	 * Gets the list of employees that are part of a given user group
	 * 
	 * @param userGroup
	 *           The name of the user group
	 * @return Employees within the user group
	 */
	public List<EmployeeModel> getEmployeesInUserGroup(String userGroup);

	/**
	 * Gets the contact email address of the specified list of employees
	 * 
	 * @param employees
	 *           List of employees to get email address from
	 * @return List of email addresses. It is possible that the list is empty since employees are not required to have an
	 *         email nor a contact address
	 */
	public List<EmailAddressModel> getEmailAddressesOfEmployees(List<EmployeeModel> employees);

}
