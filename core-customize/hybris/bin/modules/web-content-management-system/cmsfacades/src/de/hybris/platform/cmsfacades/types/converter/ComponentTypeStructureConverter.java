/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.converter;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.TYPE_READONLY_MODE;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.TYPE_REQUIRED_FIELDS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.hybris.platform.cms2.common.exceptions.PermissionExceptionUtils;
import de.hybris.platform.cms2.exceptions.AttributePermissionException;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.common.service.StringDecapitalizer;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.types.service.CMSPermissionChecker;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Streams;


/**
 * Converter use to convert a <code>ComponentTypeStructure</code> to a <code>ComponentTypeData</code>.
 */
public class ComponentTypeStructureConverter implements Converter<ComponentTypeStructure, ComponentTypeData>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ComponentTypeStructureConverter.class);

	private StringDecapitalizer stringDecapitalizer;
	private PermissionCRUDService permissionCRUDService;
	private CMSPermissionChecker cmsPermissionChecker;
	private ObjectFactory<ComponentTypeAttributeData> componentTypeAttributeDataFactory;
	private TypeService typeService;
	private CMSAdminSiteService cmsAdminSiteService;

	@Override
	public ComponentTypeData convert(final ComponentTypeStructure source) throws ConversionException
	{
		return convert(source, new ComponentTypeData());
	}

	@Override
	public ComponentTypeData convert(final ComponentTypeStructure source, final ComponentTypeData target)
			throws ConversionException
	{
		if (!getPermissionCRUDService().canReadType(source.getTypecode()))
		{
			throwTypePermissionException(PermissionsConstants.READ, source.getTypecode());
		}

		try
		{
			// Get structure type
			final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(source.getTypecode());

			// populates the category, but if there is a populator that populates it again, it will be overridden by the populator.
			target.setCategory(source.getCategory().name());

			// Populate component type properties
			source.getPopulators().forEach(populator -> populator.populate(composedType, target));

			// Convert attributes
			if (isFieldRequired("attributes"))
			{
				target.setAttributes(source.getAttributes().stream() //
					.map(attribute -> convertAttributeAndEvaluatePermissions(composedType, attribute)) //
					.filter(Objects::nonNull)
					.filter(componentTypeAttributeData -> isNotBlank(componentTypeAttributeData.getCmsStructureType()))
					.collect(Collectors.toList()));
			}
			else
			{
				target.setAttributes(Collections.emptyList());
			}
		}
		catch (final AttributePermissionException | TypePermissionException ex)
		{
			/*
			 * When user does not have read permission to a required attribute for a given type, then he does not have
			 * permission to create and update any items of that type
			 */
			LOGGER.info(String.format("User has insufficient attribute permissions for type %s", source.getTypecode()));
			target.setAttributes(new LinkedList<>());
		}

		getStringDecapitalizer() //
				.decapitalize(source.getTypeDataClass()) //
				.ifPresent(target::setType);

		return target;
	}

	/**
	 * Throws {@link TypePermissionException}.
	 *
	 * @param permissionName
	 *           permission name
	 * @param typeCode
	 *           type code
	 */
	protected void throwTypePermissionException(final String permissionName, final String typeCode)
	{
		throw PermissionExceptionUtils.createTypePermissionException(permissionName, typeCode);
	}

	/**
	 * Creates a new <code>AttributeDescriptor</code> with basic data copied from the provided attribute.
	 *
	 * @param composedTypeModel
	 *           - the composed type model containing the provided attribute
	 * @param attribute
	 *           - the attribute from where to copy the new descriptor data
	 * @return the newly created attribute descriptor
	 */
	protected AttributeDescriptorModel buildAttributeDescriptorModel(final ComposedTypeModel composedTypeModel,
			final ComponentTypeAttributeStructure attribute)
	{
		final ComposedTypeModel attributeType = getTypeService().getComposedTypeForCode(attribute.getTypecode());

		final AttributeDescriptorModel attributeDescriptorModel = new AttributeDescriptorModel();
		attributeDescriptorModel.setQualifier(attribute.getQualifier());
		attributeDescriptorModel.setEnclosingType(composedTypeModel);
		attributeDescriptorModel.setAttributeType(attributeType);

		return attributeDescriptorModel;
	}

	/**
	 * Get <code>AttributeDescriptor</code> matching with the qualifier of the attribute for the type provided.
	 *
	 * @param type
	 *           - the composed type model in which to search for the descriptor
	 * @param originalAttribute
	 *           - the attribute whose descriptor to search for.
	 * @return the attribute descriptor matching the given criteria
	 */
	protected AttributeDescriptorModel getAttributeDescriptor(final ComposedTypeModel type,
			final ComponentTypeAttributeStructure originalAttribute)
	{
		return Stream.of(type.getDeclaredattributedescriptors(), type.getInheritedattributedescriptors())
				.flatMap(Collection::stream)
				.filter(attributeDescriptor -> attributeDescriptor.getQualifier().equals(originalAttribute.getQualifier())).findAny()
				.orElseGet(() -> buildAttributeDescriptorModel(type, originalAttribute));
	}

	/**
	 * Convert the attribute descriptor to a POJO using the structure attribute's populators.
	 *
	 * @param attribute
	 *           - the structure type attribute
	 * @param attributeDescriptor
	 *           - the attribute descriptor
	 * @return the component type attribute POJO
	 */
	protected ComponentTypeAttributeData convertAttribute(final ComponentTypeAttributeStructure attribute,
			final AttributeDescriptorModel attributeDescriptor)
	{
		final ComponentTypeAttributeData target = getComponentTypeAttributeDataFactory().getObject();
		target.setQualifier(attribute.getQualifier());
		attribute.getPopulators().forEach(populator -> populator.populate(attributeDescriptor, target));

		return target;
	}

	/**
	 * This method converts the attribute attribute descriptor to a POJO using the structure attribute's populators and
	 * evaluates its permissions.
	 *
	 * @param composedType
	 *           - the composed type model containing the attribute to convert.
	 * @param attributeStructure
	 *           - the attribute to convert
	 * @return the component type attribute POJO. Will be null if user has no read-permissions.
	 */
	protected ComponentTypeAttributeData convertAttributeAndEvaluatePermissions(final ComposedTypeModel composedType,
			final ComponentTypeAttributeStructure attributeStructure)
	{
		final AttributeDescriptorModel attributeDescriptorModel = getAttributeDescriptor(composedType, attributeStructure);
		final ComponentTypeAttributeData target = convertAttribute(attributeStructure, attributeDescriptorModel);

		/*
		 * If the attribute was not in the enclosing type then it was added "manually" by one of the populators. This
		 * means that its permission cannot be controlled in BackOffice. By default, the permission is granted then.
		 */
		if (enclosingTypeHasAttribute(composedType, attributeDescriptorModel.getQualifier()))
		{
			/*
			 * If user has no read permission for the attribute or for the type containing in the attribute, then the
			 * attribute will be skipped.
			 */
			final boolean canReadContainingType = getCmsPermissionChecker() //
					.hasPermissionForContainedType(attributeDescriptorModel, PermissionsConstants.READ);
			final boolean canReadAttribute = hasReadPermissionOnAttribute(attributeDescriptorModel);
			if (!canReadAttribute || !canReadContainingType)
			{
				return null;
			}

			/*
			 * Check if the user has change permission on the type. If yes, continue with checking change permission on
			 * attribute level. Otherwise, regardless of other populators, the field should be read-only.
			 */
			final boolean canChangeType = getPermissionCRUDService().canChangeType(composedType);
			target.setEditable(canChangeType ?
					target.isEditable() && getPermissionCRUDService().canChangeAttribute(attributeDescriptorModel) : false);
		}

		if (isReadOnlyMode())
		{
			target.setEditable(false);
		}

		return target;
	}

	/**
	 * Checks whether the structure api should return fields in read only mode (editable = false)
	 * @return <tt>TRUE</tt> if the read only mode is used, <tt>FALSE</tt> otherwise.
	 */
	protected boolean isReadOnlyMode()
	{
		final Map<String, Object> structureTypeContext = getCmsAdminSiteService().getTypeContext();
		final Boolean readOnly = (Boolean) structureTypeContext.get(TYPE_READONLY_MODE);
		return readOnly != null && readOnly;
	}

	/**
	 * Checks whether the field is required to be calculated and returned in the final component type structure.
	 *
	 * @param fieldToCheck Name of the field to check whether it has to be included in the final structure.
	 * @return <tt>TRUE</tt> if the field must be included in the final structure, <tt>FALSE</tt> otherwise.
	 */
	protected boolean isFieldRequired(final String fieldToCheck)
	{
		final List<String> requiredFields = getRequiredFields();
		return requiredFields.isEmpty() || requiredFields.contains(fieldToCheck);
	}

	/**
	 * Gets the name of the fields that must be returned in the final component type structure.
	 *
	 * @return The list of fields that must be returned. The list will be empty if all fields must be included.
	 */
	protected List<String> getRequiredFields()
	{
		final Map<String, Object> structureTypeContext = getCmsAdminSiteService().getTypeContext();
		return (List<String>) structureTypeContext.getOrDefault(TYPE_REQUIRED_FIELDS, Collections.emptyList());
	}

	/**
	 * Checks whether the current principal has read permission on the attribute provided.
	 *
	 * @param attributeDescriptorModel
	 *           - The descriptor that specifies the attribute whose read permission to check.
	 * @return true if the current principal has been granted read permission on the attribute; false otherwise.
	 * @throws AttributePermissionException
	 *            - When principal does not have read permission for a required attribute.
	 */
	protected boolean hasReadPermissionOnAttribute(final AttributeDescriptorModel attributeDescriptorModel)
	{
		final boolean canReadAttribute = getPermissionCRUDService().canReadAttribute(attributeDescriptorModel);

		if (!attributeDescriptorModel.getOptional() && !canReadAttribute)
		{
			final String errorMessage = String.format("Current principal cannot read required attribute %s of type %s.",
					attributeDescriptorModel.getQualifier(), attributeDescriptorModel.getEnclosingType().getCode());

			LOGGER.info(errorMessage);
			throw new AttributePermissionException(errorMessage);
		}

		return canReadAttribute;
	}

	/**
	 * Checks whether the component type contains the attribute identified by the qualifier provided.
	 *
	 * @param enclosingType
	 *           - The item where to look for the expected attribute
	 * @param qualifier
	 *           - The qualifier of the attribute to find
	 * @return true if the attribute is found; false otherwise.
	 */
	protected boolean enclosingTypeHasAttribute(final ComposedTypeModel enclosingType, final String qualifier)
	{
		return Streams
				.concat(enclosingType.getDeclaredattributedescriptors().stream(),
						enclosingType.getInheritedattributedescriptors().stream())
				.anyMatch(attributeDescriptorModel -> attributeDescriptorModel.getQualifier().equals(qualifier));
	}

	protected StringDecapitalizer getStringDecapitalizer()
	{
		return stringDecapitalizer;
	}

	@Required
	public void setStringDecapitalizer(final StringDecapitalizer stringDecapitalizer)
	{
		this.stringDecapitalizer = stringDecapitalizer;
	}

	protected ObjectFactory<ComponentTypeAttributeData> getComponentTypeAttributeDataFactory()
	{
		return componentTypeAttributeDataFactory;
	}

	@Required
	public void setComponentTypeAttributeDataFactory(
			final ObjectFactory<ComponentTypeAttributeData> componentTypeAttributeDataFactory)
	{
		this.componentTypeAttributeDataFactory = componentTypeAttributeDataFactory;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected PermissionCRUDService getPermissionCRUDService()
	{
		return permissionCRUDService;
	}

	@Required
	public void setPermissionCRUDService(final PermissionCRUDService permissionCRUDService)
	{
		this.permissionCRUDService = permissionCRUDService;
	}

	protected CMSPermissionChecker getCmsPermissionChecker()
	{
		return cmsPermissionChecker;
	}

	@Required
	public void setCmsPermissionChecker(final CMSPermissionChecker cmsPermissionChecker)
	{
		this.cmsPermissionChecker = cmsPermissionChecker;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}
}
