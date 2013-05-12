/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.persist.location;

import java.io.InputStream;
import java.io.OutputStream;

import org.encog.persist.PersistError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class allows Encog persistence to use a location that is based on an
 * InputStream.
 * 
 */
public class InputStreamPersistence implements PersistenceLocation {

	/**
	 * The input stream this class is based on.
	 */
	private final InputStream istream;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory
			.getLogger(InputStreamPersistence.class);

	/**
	 * Construct this location.
	 * 
	 * @param istream
	 *            The input stream to use.
	 */
	public InputStreamPersistence(final InputStream istream) {
		this.istream = istream;
	}

	/**
	 * Create the input stream.
	 * 
	 * @return The input stream.
	 */
	public InputStream createInputStream() {
		return this.istream;
	}

	/**
	 * Try to create an output stream. This will fail, as output streams are not
	 * supported by this location type.
	 * 
	 * @return Not used.
	 */
	public OutputStream createOutputStream() {
		final String str = "The InputStreamPersistence location does not support output streams.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PersistError(str);
	}

	/**
	 * Delete operations are not supported for resource persistence.
	 */
	public void delete() {
		final String str = "The InputStreamPersistence location does not support delete operations.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PersistError(str);

	}

	/**
	 * Because this is based on a stream, it always exists, so return true.
	 * @return True.
	 */
	public boolean exists() {
		return true;
	}

	/**
	 * Renames are not allowed on this type of location.
	 * 
	 * @param toLocation
	 *            Not used.
	 */
	public void renameTo(final PersistenceLocation toLocation) {
		final String str = "The InputStreamPersistence location does not support rename operations.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PersistError(str);

	}

}
