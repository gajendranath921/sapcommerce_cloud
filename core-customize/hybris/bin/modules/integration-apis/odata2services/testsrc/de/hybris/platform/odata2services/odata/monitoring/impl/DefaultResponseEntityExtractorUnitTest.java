/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.odata.monitoring.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.inboundservices.model.InboundRequestErrorModel;
import de.hybris.platform.integrationservices.monitoring.MonitoredRequestErrorParser;
import de.hybris.platform.odata2services.odata.RuntimeIOException;
import de.hybris.platform.odata2services.odata.monitoring.IntegrationKeyExtractor;
import de.hybris.platform.odata2services.odata.monitoring.ResponseChangeSetEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultResponseEntityExtractorUnitTest
{
	@InjectMocks
	private final DefaultResponseEntityExtractor responseEntityExtractor = new DefaultResponseEntityExtractor();
	@Mock(lenient = true)
	private IntegrationKeyExtractor integrationKeyExtractor;
	@Mock(lenient = true)
	private ODataResponse response;
	@Mock(lenient = true)
	private MonitoredRequestErrorParser<InboundRequestErrorModel> errorParser;

	private static final String SUCCESS_INTEGRATION_KEY_VALUE = "success|key";
	private static final String ERROR_INTEGRATION_KEY_VALUE = "error|key";

	@Before
	public void setUp()
	{
		responseEntityExtractor.setIntegrationKeyExtractors(Collections.singletonList(integrationKeyExtractor));
		responseEntityExtractor.setErrorParsers(Collections.singletonList(errorParser));
		mockIntegrationKeyExtractor();
	}

	@Test
	public void testSingleBatchSingleChangesetSuccessResponse() throws ODataException
	{
		givenSuccessfulBatchResponse();

		final List<ResponseChangeSetEntity> responseEntities = responseEntityExtractor.extractFrom(response);

		assertThat(responseEntities).hasSize(1);
		final ResponseChangeSetEntity responseEntity = responseEntities.get(0);
		assertThat(responseEntity.isSuccessful()).isTrue();
		assertThat(responseEntity.getIntegrationKey()).isEqualTo(SUCCESS_INTEGRATION_KEY_VALUE);
		assertThat(responseEntity.getRequestError()).isEmpty();
	}

	@Test
	public void testSingleSuccessResponse() throws ODataException
	{
		givenSingleSuccessResponse();

		final List<ResponseChangeSetEntity> responseEntities = responseEntityExtractor.extractFrom(response);

		assertThat(responseEntities).hasSize(1);
		final ResponseChangeSetEntity responseEntity = responseEntities.get(0);
		assertThat(responseEntity.isSuccessful()).isTrue();
		assertThat(responseEntity.getIntegrationKey()).isEqualTo(SUCCESS_INTEGRATION_KEY_VALUE);
		assertThat(responseEntity.getRequestError()).isEmpty();
	}

	@Test
	public void testExtractSingleResponseThrowsIOExceptionWhenClosingStream() throws ODataException, IOException
	{
		givenSingleSuccessResponseWithIoExceptionOnStreamClosing();

		assertThatThrownBy(() -> responseEntityExtractor.extractFrom(response))
				.isInstanceOf(RuntimeIOException.class)
				.hasCauseInstanceOf(IOException.class);
	}

	@Test
	public void testSingleErrorResponse() throws ODataException
	{
		mockSingleErrorResponse();
		mockErrorParsing();
		mockIntegrationKeyExtractor();

		final List<ResponseChangeSetEntity> responseEntities = responseEntityExtractor.extractFrom(response);

		assertThat(responseEntities).hasSize(1);
		final ResponseChangeSetEntity responseEntity = responseEntities.get(0);
		assertThat(responseEntity.isSuccessful()).isFalse();
		assertThat(responseEntity.getIntegrationKey()).isEqualTo(ERROR_INTEGRATION_KEY_VALUE);
		assertThat(responseEntity.getRequestError()).isNotEmpty();
	}

	@Test
	public void testBatchResponseWithOneBatchPartErrorAndOneSuccess() throws ODataException
	{
		givenBatchResponseWithOnePartErrorAndOnePartSuccess();
		mockErrorParsing();

		final List<ResponseChangeSetEntity> responseEntities = responseEntityExtractor.extractFrom(response);

		assertThat(responseEntities).hasSize(2);
		final ResponseChangeSetEntity errorResponseEntity = responseEntities.get(0);
		assertThat(errorResponseEntity.isSuccessful()).isFalse();
		assertThat(errorResponseEntity.getIntegrationKey()).isEqualTo(ERROR_INTEGRATION_KEY_VALUE);
		assertThat(errorResponseEntity.getRequestError()).isNotEmpty();

		final ResponseChangeSetEntity successResponseEntity = responseEntities.get(1);
		assertThat(successResponseEntity.isSuccessful()).isTrue();
		assertThat(successResponseEntity.getIntegrationKey()).isEqualTo(SUCCESS_INTEGRATION_KEY_VALUE);
		assertThat(successResponseEntity.getRequestError()).isEmpty();
	}

	@Test
	public void testSingleBatchTwoChangesets() throws ODataException
	{
		givenSuccessfulBatchResponseWithTwoChangesets();

		final List<ResponseChangeSetEntity> responseEntities = responseEntityExtractor.extractFrom(response);

		assertThat(responseEntities).hasSize(2);
		final ResponseChangeSetEntity firstChangesetResponse = responseEntities.get(0);
		assertThat(firstChangesetResponse.isSuccessful()).isTrue();
		assertThat(firstChangesetResponse.getIntegrationKey()).isEqualTo(SUCCESS_INTEGRATION_KEY_VALUE);
		assertThat(firstChangesetResponse.getRequestError()).isEmpty();

		final ResponseChangeSetEntity secondChangesetResponse = responseEntities.get(1);
		assertThat(secondChangesetResponse.isSuccessful()).isTrue();
		assertThat(secondChangesetResponse.getIntegrationKey()).isEqualTo(SUCCESS_INTEGRATION_KEY_VALUE);
		assertThat(secondChangesetResponse.getRequestError()).isEmpty();
	}

	@Test
	public void testSetErrorParsersNoLeakReference()
	{
		final List<MonitoredRequestErrorParser<InboundRequestErrorModel>> errorParsers = new ArrayList<>(
				Arrays.asList(errorParser));
		responseEntityExtractor.setErrorParsers(errorParsers);
		errorParsers.clear();
		assertThat(responseEntityExtractor.getErrorParsers()).isNotEmpty();
	}

	@Test
	public void testSetErrorParsersToNullReturnEmptyList()
	{
		responseEntityExtractor.setErrorParsers(null);
		assertThat(responseEntityExtractor.getErrorParsers()).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testSetIntegrationKeyExtractorsNoLeakReference()
	{
		final List<IntegrationKeyExtractor> integrationKeyExtractors = new ArrayList<>(Arrays.asList(integrationKeyExtractor));
		responseEntityExtractor.setIntegrationKeyExtractors(integrationKeyExtractors);
		integrationKeyExtractors.clear();
		assertThat(responseEntityExtractor.getIntegrationKeyExtractors()).isNotEmpty();
	}

	@Test
	public void testSetIntegrationKeyExtractorsToNullReturnEmptyList()
	{
		responseEntityExtractor.setIntegrationKeyExtractors(null);
		assertThat(responseEntityExtractor.getIntegrationKeyExtractors()).isEqualTo(Collections.emptyList());
	}

	private void givenSuccessfulBatchResponseWithTwoChangesets() throws ODataException
	{
		mockBatchResponse();
		lenient().when(response.getEntityAsStream()).thenReturn(IOUtils.toInputStream(singleBatchTwoChangesetsResponseBody(SUCCESS_INTEGRATION_KEY_VALUE)));
	}

	private void givenSingleSuccessResponse() throws ODataException
	{
		lenient().when(response.getStatus()).thenReturn(HttpStatusCodes.CREATED);
		lenient().when(response.getContentHeader()).thenReturn(APPLICATION_JSON_VALUE);
		lenient().when(response.getEntityAsStream()).thenReturn(toStream(singleSuccessResponseBody(SUCCESS_INTEGRATION_KEY_VALUE)));
	}

	private void givenSingleSuccessResponseWithIoExceptionOnStreamClosing() throws ODataException, IOException
	{
		final InputStream inputStream = mockNonEmptyInputStream();
		doThrow(new IOException()).when(inputStream).close();
		lenient().when(response.getStatus()).thenReturn(HttpStatusCodes.CREATED);
		lenient().when(response.getContentHeader()).thenReturn(APPLICATION_JSON_VALUE);
		lenient().when(response.getEntityAsStream()).thenReturn(inputStream);
	}

	private InputStream mockNonEmptyInputStream() throws IOException
	{
		final InputStream inputStream = mock(InputStream.class);
		lenient().when(inputStream.read(any(), anyInt(), anyInt())).thenReturn(123).thenReturn(-1);
		lenient().when(inputStream.read(any())).thenReturn(-1);
		return inputStream;
	}

	private void mockSingleErrorResponse() throws ODataException
	{
		lenient().when(response.getStatus()).thenReturn(HttpStatusCodes.INTERNAL_SERVER_ERROR);
		lenient().when(response.getContentHeader()).thenReturn(APPLICATION_JSON_VALUE);
		lenient().when(response.getEntityAsStream()).thenReturn(toStream(singleErrorResponseBody()));
	}

	private void mockErrorParsing()
	{
		lenient().when(errorParser.isApplicable("application/json", 201)).thenReturn(false);
		lenient().when(errorParser.isApplicable("application/json", 500)).thenReturn(true);
		lenient().when(errorParser.parseErrorFrom(any(), anyInt(), anyString())).thenReturn(mock(InboundRequestErrorModel.class));
	}

	private void mockIntegrationKeyExtractor()
	{
		lenient().when(integrationKeyExtractor.isApplicable(anyString())).thenReturn(true);
		lenient().when(integrationKeyExtractor.extractIntegrationKey(singleSuccessResponseBody(SUCCESS_INTEGRATION_KEY_VALUE), HttpStatusCodes.CREATED.getStatusCode())).thenReturn(SUCCESS_INTEGRATION_KEY_VALUE);
		lenient().when(integrationKeyExtractor.extractIntegrationKey(singleErrorResponseBody(), 500)).thenReturn(ERROR_INTEGRATION_KEY_VALUE);
	}

	private void givenBatchResponseWithOnePartErrorAndOnePartSuccess() throws ODataException
	{
		mockBatchResponse();
		lenient().when(response.getEntityAsStream()).thenReturn(IOUtils.toInputStream(twoBatchMultipartResponseBody()));
	}

	private void givenSuccessfulBatchResponse() throws ODataException
	{
		mockBatchResponse();
		lenient().when(response.getEntityAsStream()).thenReturn(IOUtils.toInputStream(singleMultipartResponseBody(SUCCESS_INTEGRATION_KEY_VALUE)));
	}

	private void mockBatchResponse()
	{
		lenient().when(response.getContentHeader()).thenReturn("multipart/mixed; boundary=batch_123;charset=UTF-8");
		lenient().when(response.getStatus()).thenReturn(HttpStatusCodes.ACCEPTED);
	}

	private static InputStream toStream(final String str)
	{
		return IOUtils.toInputStream(str);
	}

	private static String singleSuccessResponseBody(final String integrationKey)
	{
		return "{\"d\":" +
				"{\"__metadata\":" +
				"{\"id\":\"https://localhost:9002/odata2webservices/InboundProduct/Products('" + integrationKey + "')\"," +
				"\"uri\":\"https://localhost:9002/odata2webservices/InboundProduct/Products('" + integrationKey + "')\"," +
				"\"type\":\"HybrisCommerceOData.Product\"}," +
				"\"code\":\"1\"," +
				"\"name\":\"product name\"," +
				"\"integrationKey\":\"" + integrationKey + "\"," +
				"\"catalogVersion\":{\"__deferred\":{\"uri\":\"https://localhost:9002/odata2webservices/InboundProduct/Products('" + integrationKey + "')/catalogVersion\"}}," +
				"\"unit\":{\"__deferred\":{\"uri\":\"https://localhost:9002/odata2webservices/InboundProduct/Products('" + integrationKey + "')/unit\"}}," +
				"\"supercategories\":{\"__deferred\":{\"uri\":\"https://localhost:9002/odata2webservices/InboundProduct/Products('" + integrationKey + "')/supercategories\"}}}}\n";
	}

	private static String singleErrorResponseBody()
	{
		return "{\n" +
				"    \"error\": {\n" +
				"        \"code\": \"internal_error\",\n" +
				"        \"message\": {\n" +
				"            \"lang\": \"en\",\n" +
				"            \"value\": \"There was an error encountered during the processing of the integration object.\"\n" +
				"        }\n" +
				"    }\n" +
				"}\n";
	}

	private static String singleMultipartResponseBody(final String integrationKey)
	{
		return "--batch_123\n" +
				"Content-Type: multipart/mixed; boundary=changeset_abc\n" +
				successChangeset(integrationKey) +
				"--changeset_abc--\n" +
				"--batch_123--";
	}

	private static String singleBatchTwoChangesetsResponseBody(final String integrationKey)
	{
		return "--batch_123\n" +
				"Content-Type: multipart/mixed; boundary=changeset_abc\n" +
				successChangeset(integrationKey) +
				successChangeset(integrationKey) +
				"--changeset_abc--\n" +
				"--batch_123--";
	}

	private String twoBatchMultipartResponseBody()
	{
		return "--batch_123\n" +
				"Content-Type: multipart/mixed; boundary=changeset_def\n" +
				errorChangeset(ERROR_INTEGRATION_KEY_VALUE) +
				"--changeset_def--\n" +
				"--batch_123\n" +
				"Content-Type: multipart/mixed; boundary=changeset_abc\n" +
				successChangeset(SUCCESS_INTEGRATION_KEY_VALUE) +
				"--changeset_abc--\n" +
				"--batch_123--";
	}

	private static String successChangeset(final String integrationKey)
	{
		return "\r\n" +
				"--changeset_abc\n" +
				"Content-Type: application/http\n" +
				"Content-Transfer-Encoding: binary\n" +
				"\r\n" +
				"HTTP/1.1 201 Created\n" +
				"DataServiceVersion: 2.0\n" +
				"Location: https://localhost:9002/odata2webservices/InboundProduct/Products('" + integrationKey + "')\n" +
				"Content-Type: application/json\n" +
				"Content-Length: 784\n" +
				"\r\n" +
				singleSuccessResponseBody(integrationKey);
	}

	private static String errorChangeset(final String integrationKey)
	{
		return "\r\n" +
				"--changeset_def\n" +
				"Content-Type: application/http\n" +
				"Content-Transfer-Encoding: binary\n" +
				"\r\n" +
				"HTTP/1.1 500 Internal Error\n" +
				"DataServiceVersion: 2.0\n" +
				"Location: https://localhost:9002/odata2webservices/InboundProduct/Products('" + integrationKey + "')\n" +
				"Content-Type: application/json\n" +
				"\r\n" +
				singleErrorResponseBody() +
				"--changeset_def--\n";
	}
}
