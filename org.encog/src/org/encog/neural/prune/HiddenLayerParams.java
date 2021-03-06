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
package org.encog.neural.prune;

/**
 * Specifies the minimum and maximum neuron counts for a layer.
 * 
 * @author jheaton
 * 
 */
public class HiddenLayerParams {

	/**
	 * The minimum number of neurons on this layer.
	 */
	private final int min;

	/**
	 * The maximum number of neurons on this layer.
	 */
	private final int max;

	/**
	 * Construct a hidden layer param object with the specified min and max
	 * values.
	 * 
	 * @param min
	 *            The minimum number of neurons.
	 * @param max
	 *            The maximum number of neurons.
	 */
	public HiddenLayerParams(final int min, final int max) {
		super();
		this.min = min;
		this.max = max;
	}

	/**
	 * @return The maximum number of neurons.
	 */
	public int getMax() {
		return this.max;
	}

	/**
	 * @return The minimum number of neurons.
	 */
	public int getMin() {
		return this.min;
	}

}
