/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.scripting;

import de.hybris.platform.integrationservices.util.Log;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;

/**
 * Contains the information required to execute externally hosted logic.
 * The scheme of this logic and the location will be present.
 * This object is built from a URI.
 */
public final class LogicLocation
{
	private static final Logger LOGGER = Log.getLogger(LogicLocation.class);
	private static final String SCHEME_EXPR = "^(\\w+)";
	private static final Pattern SCHEME_PATTERN = Pattern.compile(SCHEME_EXPR + "://.*");
	private static final Pattern URI_PATTERN = Pattern.compile(SCHEME_EXPR + "://\\S+$");

	private final String location;
	private final LogicLocationScheme scheme;

	private LogicLocation(final LogicLocationScheme scheme, final String location)
	{
		this.scheme = scheme;
		this.location = location;
	}

	/**
	 * Creates a {@link LogicLocation} from the given URI (e.g. model://someModelScript)
	 * @param uri URI to create from
	 * @return LogicLocation
	 * @throws CannotCreateLogicLocationException when the provided URI can't be parsed to create a LogicLocation
	 */
	public static LogicLocation from(final String uri) throws CannotCreateLogicLocationException
	{
		final var matcher = URI_PATTERN.matcher(String.valueOf(uri));
		if (matcher.matches())
		{
			try
			{
				final var scheme = LogicLocationScheme.from(matcher.group(1));
				return new LogicLocation(scheme, uri);
			}
			catch (final UnsupportedLogicLocationSchemeException e)
			{
				LOGGER.trace("Scheme is unsupported", e);
			}
		}
		throw new CannotCreateLogicLocationException(uri);
	}

	/**
	 * Creates a {@link LogicLocation} from the given URI. If the URI does not contain scheme the provided default scheme
	 * will be used.
	 * @param uri a location URI string
	 * @param scheme a default scheme to use for the {@code LogicLocation} being created, if the {@code uri} does not contain
	 *              a scheme. Otherwise, the scheme from the {@code uri} will be used.
	 * @return new {@code LogicLocation} instance
	 * @throws CannotCreateLogicLocationException when the provided {@code uri} does not represent a valid logic location
	 */
	public static LogicLocation fromUri(final String uri, final LogicLocationScheme scheme) throws CannotCreateLogicLocationException
	{
		if (hasScheme(uri))
		{
			return from(uri);
		}
		final String fullUri = uri != null ? (asUriScheme(scheme) + uri) : uri;
		return from(fullUri);
	}

	private static String asUriScheme(final LogicLocationScheme scheme) {
		return scheme != null ? (scheme.name().toLowerCase(Locale.ROOT) + "://") : "";
	}

	/**
	 * Tests whether the given URI is a valid logic location
	 *
	 * @param uri URI to test
	 * @return {@code true} if the URL is valid, else {@code false}
	 */
	public static boolean isValid(final String uri)
	{
		try
		{
			from(uri);
			return true;
		}
		catch (final CannotCreateLogicLocationException e)
		{
			LOGGER.trace("{} is invalid", uri, e);
			return false;
		}
	}

	public @NotNull LogicLocationScheme getScheme()
	{
		return scheme;
	}

	public @NotBlank String getLocation()
	{
		return location;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o != null && getClass() == o.getClass())
		{
			final LogicLocation that = (LogicLocation) o;
			return new EqualsBuilder().append(getLocation(), that.getLocation())
			                          .append(getScheme(), that.getScheme())
			                          .build();
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getLocation(), getScheme());
	}

	@Override
	public String toString()
	{
		return "LogicLocation{" +
				"location='" + location + '\'' +
				", scheme=" + scheme +
				'}';
	}

	private static boolean hasScheme(final String uri) {
		final String nullSafeUri = uri != null ? uri : "";
		return SCHEME_PATTERN.matcher(nullSafeUri).matches();
	}
}
