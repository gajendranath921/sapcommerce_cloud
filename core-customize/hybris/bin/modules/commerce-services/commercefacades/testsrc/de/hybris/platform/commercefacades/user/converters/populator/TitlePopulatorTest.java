/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.user.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.TitleModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TitlePopulatorTest
{
    @Mock
    private TitleModel titleModel;

    private Populator<TitleModel, TitleData> populator = new TitlePopulator();

    @Test
    public void testPopulate()
    {
        final TitleData titleData = new TitleData();
        given(titleModel.getCode()).willReturn("titleCode");
        given(titleModel.getName()).willReturn("titleName");

        populator.populate(titleModel, titleData);

        Assert.assertEquals("titleCode", titleData.getCode());
        Assert.assertEquals("titleName", titleData.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateWhenSourceIsNull()
    {
        populator.populate(null, new TitleData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateWhenTargetIsNull()
    {
        populator.populate(titleModel, null);
    }
}
