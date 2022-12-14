/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.populators;

import com.hybris.yprofile.dto.Address;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import org.apache.commons.lang.StringUtils;

public class AddressPopulator implements Populator<AddressModel, Address> {

    @Override
    public void populate(AddressModel addressModel, Address address) {

        if (addressModel == null) {
            return;
        }

        address.setTitle(addressModel.getTitle() != null ? addressModel.getTitle().getCode() : StringUtils.EMPTY);
        address.setFirstName(addressModel.getFirstname() != null ? addressModel.getFirstname() : StringUtils.EMPTY);
        address.setLastName(addressModel.getLastname() != null? addressModel.getLastname() : StringUtils.EMPTY);

        setStreetNameAndNumber(addressModel, address);

        address.setAddition(addressModel.getLine2() != null ? addressModel.getLine2() : StringUtils.EMPTY);

        address.setZip(addressModel.getPostalcode() != null ? addressModel.getPostalcode() : StringUtils.EMPTY);
        address.setCity(addressModel.getTown() != null ? addressModel.getTown() : StringUtils.EMPTY);
        address.setCountry((addressModel.getCountry() != null && addressModel.getCountry().getIsocode() != null) ? addressModel.getCountry().getIsocode() : StringUtils.EMPTY);
    }

    protected void setStreetNameAndNumber(AddressModel addressModel, Address address) {
        address.setStreet(addressModel.getStreetname());
        address.setNumber("0");

        if (addressModel.getStreetname() != null) {
            final int lastIndexOf = addressModel.getStreetname().lastIndexOf(' ');
            if (lastIndexOf > 0) {
                address.setStreet(addressModel.getStreetname().substring(0, lastIndexOf));
                address.setNumber(addressModel.getStreetname().substring(lastIndexOf));
            }
        }
    }
}
