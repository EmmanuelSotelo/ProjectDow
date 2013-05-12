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
package org.encog.normalize.target;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;

/**
 * Store the normalized data to a neural data set.
 */
public class NormalizationStorageNeuralDataSet implements NormalizationStorage {

	/**
	 * The input count.
	 */
	private int inputCount;

	/**
	 * The ideal count.
	 */
	private int idealCount;

	/**
	 * The data set to add to.
	 */
	private NeuralDataSet dataset;

	/**
	 * Construct a new NeuralDataSet based on the parameters specified.
	 * 
	 * @param inputCount The input count.
 	 * @param idealCount The output count.
	 */
	public NormalizationStorageNeuralDataSet(final int inputCount,
			final int idealCount) {
		this.inputCount = inputCount;
		this.idealCount = idealCount;
		this.dataset = new BasicNeuralDataSet();
	}

	/**
	 * Construct a normalized neural storage class to hold data.
	 * 
	 * @param dataset
	 *            The data set to store to. This uses an existing data set.
	 */
	public NormalizationStorageNeuralDataSet(final NeuralDataSet dataset) {
		this.dataset = dataset;
		this.inputCount = this.dataset.getInputSize();
		this.idealCount = this.dataset.getIdealSize();
	}

	/**
	 * Not needed for this storage type.
	 */
	public void close() {
	}

	/**
	 * Not needed for this storage type.
	 */
	public void open() {
	}

	/**
	 * Write an array.
	 * 
	 * @param data
	 *            The data to write.
	 * @param inputCount
	 *            How much of the data is input.
	 */
	public void write(final double[] data, final int inputCount) {

		if (this.idealCount == 0) {
			final BasicNeuralData inputData = new BasicNeuralData(data);
			this.dataset.add(inputData);
		} else {
			final BasicNeuralData inputData = new BasicNeuralData(
					this.inputCount);
			final BasicNeuralData idealData = new BasicNeuralData(
					this.idealCount);

			int index = 0;
			for (int i = 0; i < this.inputCount; i++) {
				inputData.setData(i, data[index++]);
			}

			for (int i = 0; i < this.idealCount; i++) {
				idealData.setData(i, data[index++]);
			}

			this.dataset.add(inputData, idealData);
		}

	}

}
