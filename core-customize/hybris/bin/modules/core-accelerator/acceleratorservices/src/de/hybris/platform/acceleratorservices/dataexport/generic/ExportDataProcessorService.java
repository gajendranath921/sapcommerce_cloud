/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataexport.generic;

import de.hybris.platform.acceleratorservices.dataexport.generic.config.PipelineConfig;
import de.hybris.platform.acceleratorservices.dataexport.generic.event.ExportDataEvent;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;

import org.springframework.messaging.Message;


/**
 * ExportDataProcessorService. Main service that queries, looks up and converts data.
 */
public interface ExportDataProcessorService
{
	/**
	 * Lookup the bean with the name specified
	 * 
	 * @param beanName
	 *           the bean name
	 * @return the bean
	 */
	Object lookupBean(String beanName);

	/**
	 * Compute the filename of the generated file for the export event.
	 * 
	 * @param message message containing the event
	 * @param pipelineConfig
	 *           the configuration used in this export
	 * @return the filename
	 */
	String computeFilename(Message<?> message, PipelineConfig pipelineConfig);

	/**
	 * Search for a list of items.
	 * 
	 * @param message message containing the event
	 * @param pipelineConfig
	 *           object that holds the export configuration
	 * @return List of item PK's
	 * @throws Throwable throwable
	 */
	List<PK> search(Message<ExportDataEvent> message, PipelineConfig pipelineConfig) throws Throwable;

	/**
	 * Get the Item model for the specified PK
	 * 
	 * @param pk pk to look up
	 * @return Item model for the specified PK
	 */
	ItemModel lookupItemForPk(PK pk);

	/**
	 * Convert the Item model to a POJO
	 * 
	 * @param message
	 *           message that contains the Item model
	 * @param pipelineConfig
	 *           object that holds the export configuration. Should hold the converter
	 * @return the POJO representing the Item model
	 */
	Object convertItem(Message<? extends ItemModel> message, PipelineConfig pipelineConfig);

	/**
	 * Convert a POJO to an object that could be outputted to file.
	 * 
	 * @param records
	 *           List of POJO's
	 * @param pipelineConfig
	 *           pipelineConfig object that holds the export configuration. Should hold the output converter
	 * @return an object that can be sent to a writer
	 */
	Object convertOutput(List<Object> records, PipelineConfig pipelineConfig);
}
