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
package org.encog.util.randomize;

import org.encog.EncogError;
import org.encog.matrix.Matrix;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A randomizer that attempts to create starting weight values that are
 * conducive to back propagation training.
 * 
 * From:
 * 
 * Neural Networks - A Comprehensive Foundation, Haykin, chapter 6.7
 * 
 * @author jheaton
 * 
 */
public class FanInRandomizer extends BasicRandomizer {

	/**
	 * Error message. Can't use fan-in on a single number.
	 */
	static final String ERROR = "To use FanInRandomizer you must "
			+ "present a Matrix or 2D array type value.";

	/**
	 * The default boundary.
	 */
	private static final double DEFAULT_BOUNDARY = 2.4;

	/** The lower bound. */
	private final double lowerBound;

	/** The upper bound. */
	private final double upperBound;

	/**
	 * Should the square root of the number of rows be used?
	 */
	private final boolean sqrt;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Create a fan-in randomizer with default values.
	 */
	public FanInRandomizer() {
		this(-FanInRandomizer.DEFAULT_BOUNDARY,
				FanInRandomizer.DEFAULT_BOUNDARY, false);
	}

	/**
	 * Construct a fan-in randomizer along the specified boundary. The min will
	 * be -boundary and the max will be boundary.
	 * 
	 * @param boundary
	 *            The boundary for the fan-in.
	 * @param sqrt
	 *            Should the square root of the rows to be used in the
	 *            calculation.
	 */
	public FanInRandomizer(final double boundary, final boolean sqrt) {
		this(-boundary, boundary, sqrt);

	}

	/**
	 * Construct a fan-in randomizer. Use the specified bounds.
	 * 
	 * @param aLowerBound
	 *            The lower bound.
	 * @param anUpperBound
	 *            The upper bound.
	 * @param sqrt
	 *            True if the square root of the rows should be used in the
	 *            calculation.
	 */
	public FanInRandomizer(final double aLowerBound, final double anUpperBound,
			final boolean sqrt) {
		this.lowerBound = aLowerBound;
		this.upperBound = anUpperBound;
		this.sqrt = sqrt;
	}

	/**
	 * Calculate the fan-in value.
	 * 
	 * @param rows
	 *            The number of rows.
	 * @return The fan-in value.
	 */
	private double calculateValue(final int rows) {
		double rowValue;

		if (this.sqrt) {
			rowValue = Math.sqrt(rows);
		} else {
			rowValue = rows;
		}

		return (this.lowerBound / rowValue) + Math.random()
				* ((this.upperBound - this.lowerBound) / rowValue);
	}

	/**
	 * Throw an error if this class is used improperly.
	 */
	private void causeError() {
		if (this.logger.isErrorEnabled()) {
			this.logger.error(FanInRandomizer.ERROR);
		}
		throw new EncogError(FanInRandomizer.ERROR);
	}

	/**
	 * Randomize the synapses and thresholds in the basic network based on an
	 * array, modify the array. Previous values may be used, or they may be
	 * discarded, depending on the randomizer.
	 * 
	 * @param network
	 *            A network to randomize.
	 */
	@Override
	public void randomize(final BasicNetwork network) {

		// randomize the weight matrix
		for (final Synapse synapse : network.getStructure().getSynapses()) {
			if (synapse.getMatrix() != null) {
				randomize(synapse.getMatrix());
			}
		}

		// clear the thresholds
		for (final Layer layer : network.getStructure().getLayers()) {
			for (int i = 0; i < layer.getNeuronCount(); i++) {
				layer.setThreshold(i, 0);
			}
		}
	}

	/**
	 * Starting with the specified number, randomize it to the degree specified
	 * by this randomizer. This could be a totally new random number, or it
	 * could be based on the specified number.
	 * 
	 * @param d
	 *            The number to randomize.
	 * @return A randomized number.
	 */
	public double randomize(final double d) {
		causeError();
		return 0;
	}

	/**
	 * Randomize the array based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	@Override
	public void randomize(final double[] d) {
		causeError();
	}

	/**
	 * Randomize the array based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	@Override
	public void randomize(final Double[] d) {
		causeError();
	}

	/**
	 * Randomize the 2d array based on an array, modify the array. Previous
	 * values may be used, or they may be discarded, depending on the
	 * randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	@Override
	public void randomize(final double[][] d) {
		for (int row = 0; row < d.length; row++) {
			for (int col = 0; col < d[0].length; col++) {
				d[row][col] = calculateValue(d.length);
			}
		}
	}

	/**
	 * Randomize the 2d array based on an array, modify the array. Previous
	 * values may be used, or they may be discarded, depending on the
	 * randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	@Override
	public void randomize(final Double[][] d) {
		for (int row = 0; row < d.length; row++) {
			for (int col = 0; col < d[0].length; col++) {
				d[row][col] = calculateValue(d.length);
			}
		}
	}

	/**
	 * Randomize the matrix based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param m
	 *            A matrix to randomize.
	 */
	@Override
	public void randomize(final Matrix m) {
		for (int row = 0; row < m.getRows(); row++) {
			for (int col = 0; col < m.getCols(); col++) {
				m.set(row, col, calculateValue(m.getRows()));
			}
		}
	}

}
