/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.cmsfacades.data.SyncJobData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class SyncItemJobToSyncJobDataPopulatorTest
{

    @InjectMocks
    private SyncItemJobToSyncJobDataPopulator populator;

    @Test
    public void populatorShouldSetCatalogVersionsIfSourceIsProvided()
    {
        // Arrange
        String sourceVersion = "someSourceVersion";
        CatalogVersionModel sourceCatalogVersion = new CatalogVersionModel();
        sourceCatalogVersion.setVersion(sourceVersion);

        String targetVersion = "someTargetVersion";
        CatalogVersionModel targetCatalogVersion = new CatalogVersionModel();
        targetCatalogVersion.setVersion( targetVersion );

        SyncItemJobModel syncItemJobModel = new SyncItemJobModel();
        syncItemJobModel.setSourceVersion(sourceCatalogVersion);
        syncItemJobModel.setTargetVersion(targetCatalogVersion);

        Optional<SyncItemJobModel> source = Optional.of( syncItemJobModel );
        SyncJobData target = new SyncJobData();

        // Act
        populator.populate(source, target);

        // Assert
        assertEquals( target.getSourceCatalogVersion(), sourceVersion );
        assertEquals( target.getTargetCatalogVersion(), targetVersion );
    }

    @Test
    public void populatorShouldNotFailIfSourceIsEmpty()
    {
        // Arrange
        Optional<SyncItemJobModel> source = Optional.empty();
        SyncJobData target = new SyncJobData();

        // Act
        populator.populate( source, target );

        // Assert
        assertNull( target.getSourceCatalogVersion() );
        assertNull( target.getTargetCatalogVersion() );
    }

}
