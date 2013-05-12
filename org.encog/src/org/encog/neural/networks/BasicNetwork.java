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
package org.encog.neural.networks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.Encog;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.logic.NeuralLogic;
import org.encog.neural.networks.logic.SimpleRecurrentLogic;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.BasicNetworkPersistor;
import org.encog.util.ErrorCalculation;
import org.encog.util.ObjectCloner;
import org.encog.util.randomize.RangeRandomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements a neural network. This class works in conjunction the
 * Layer classes. Layers are added to the BasicNetwork to specify the structure
 * of the neural network.
 * 
 * The first layer added is the input layer, the final layer added is the output
 * layer. Any layers added between these two layers are the hidden layers.
 * 
 * The network structure is stored in the structure member. It is important to
 * call:
 * 
 * network.getStructure().finalizeStructure();
 * 
 * Once the neural network has been completely constructed.
 * 
 */
public class BasicNetwork implements Serializable, Network, ContextClearable {

	/**
	 * Tag used for the input layer.
	 */
	public static final String TAG_INPUT = "INPUT";

	/**
	 * Tag used for the output layer.
	 */
	public static final String TAG_OUTPUT = "OUTPUT";

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -136440631687066461L;

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory.getLogger(BasicNetwork.class);

	/**
	 * Determine which member of the output is the winning neuron.
	 * 
	 * @param output
	 *            The output from the neural network.
	 * @return The winning neuron.
	 */
	public static int determineWinner(final NeuralData output) {

		int win = 0;

		double biggest = Double.MIN_VALUE;
		for (int i = 0; i < output.size(); i++) {

			if (output.getData(i) > biggest) {
				biggest = output.getData(i);
				win = i;
			}
		}

		return win;
	}

	/**
	 * The description of this object.
	 */
	private String description;

	/**
	 * The name of this object.
	 */
	private String name;

	/**
	 * Holds the structure of the network. This keeps the network from having to
	 * constantly lookup layers and synapses.
	 */
	private final NeuralStructure structure;

	/**
	 * This class tells the network how to calculate the output for each of the
	 * layers.
	 */
	private NeuralLogic logic;

	/**
	 * Properties about the neural network. Some NeuralLogic classes require
	 * certain properties to be set.
	 */
	private final Map<String, String> properties = new HashMap<String, String>();

	/**
	 * The tags for the layers.
	 */
	private final Map<String, Layer> layerTags = new HashMap<String, Layer>();

	/**
	 * Construct an empty neural network.
	 */
	public BasicNetwork() {
		this.structure = new NeuralStructure(this);
		this.logic = new SimpleRecurrentLogic();
	}

	/**
	 * Construct a basic network using the specified logic.
	 * 
	 * @param logic
	 *            The logic to use with the neural network.
	 */
	public BasicNetwork(final NeuralLogic logic) {
		this.structure = new NeuralStructure(this);
		this.logic = logic;
	}

	/**
	 * Add a layer to the neural network. The first layer added is the input
	 * layer, the last layer added is the output layer. This layer is added with
	 * a weighted synapse.
	 * 
	 * @param layer
	 *            The layer to be added.
	 */
	public void addLayer(final Layer layer) {
		addLayer(layer, SynapseType.Weighted);
	}

	/**
	 * Add a layer to the neural network. If there are no layers added this
	 * layer will become the input layer. This function automatically updates
	 * both the input and output layer references.
	 * 
	 * @param layer
	 *            The layer to be added to the network.
	 * @param type
	 *            What sort of synapse should connect this layer to the last.
	 */
	public void addLayer(final Layer layer, final SynapseType type) {

		// is this the first layer? If so, mark as the input layer.
		if (this.layerTags.size() == 0) {
			tagLayer(BasicNetwork.TAG_INPUT, layer);
			tagLayer(BasicNetwork.TAG_OUTPUT, layer);
		} else {
			// add the layer to any previous layers
			final Layer outputLayer = getLayer(BasicNetwork.TAG_OUTPUT);
			outputLayer.addNext(layer, type);
			tagLayer(BasicNetwork.TAG_OUTPUT, layer);
		}
	}

	/**
	 * Calculate the error for this neural network. The error is calculated
	 * using root-mean-square(RMS).
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	public double calculateError(final NeuralDataSet data) {
		final ErrorCalculation errorCalculation = new ErrorCalculation();

		for (final NeuralDataPair pair : data) {
			final NeuralData actual = compute(pair.getInput());
			errorCalculation.updateError(actual, pair.getIdeal());
		}
		return errorCalculation.calculateRMS();
	}

	/**
	 * Calculate the total number of neurons in the network across all layers.
	 * 
	 * @return The neuron count.
	 */
	public int calculateNeuronCount() {
		int result = 0;
		for (final Layer layer : this.structure.getLayers()) {
			result += layer.getNeuronCount();
		}
		return result;
	}

	/**
	 * Check that the input size is acceptable, if it does not match the input
	 * layer, then throw an error.
	 * 
	 * @param input
	 *            The input data.
	 */
	public void checkInputSize(final NeuralData input) {

		final Layer inputLayer = getLayer(BasicNetwork.TAG_INPUT);

		if (input.size() != inputLayer.getNeuronCount()) {

			final String str = "Size mismatch: Can't compute outputs for input size="
					+ input.size()
					+ " for input layer size="
					+ inputLayer.getNeuronCount();

			if (BasicNetwork.LOGGER.isErrorEnabled()) {
				BasicNetwork.LOGGER.error(str);
			}

			throw new NeuralNetworkError(str);
		}
	}

	/**
	 * Clear any data from any context layers.
	 */
	public void clearContext() {
		for (final Layer layer : this.structure.getLayers()) {
			if (layer instanceof ContextClearable) {
				((ContextClearable) layer).clearContext();
			}
		}

	}

	/**
	 * Remove all layer tags.
	 */
	public void clearLayerTags() {
		this.layerTags.clear();
	}

	/**
	 * Return a clone of this neural network. Including structure, weights and
	 * threshold values. This is a deep copy.
	 * 
	 * @return A cloned copy of the neural network.
	 */
	@Override
	public Object clone() {
		return ObjectCloner.deepCopy(this);
	}

	/**
	 * Compute the output for a given input to the neural network.
	 * 
	 * @param input
	 *            The input to the neural network.
	 * @return The output from the neural network.
	 */
	public NeuralData compute(final NeuralData input) {
		return this.logic.compute(input, null);
	}

	/**
	 * Compute the output for a given input to the neural network. This method
	 * provides a parameter to specify an output holder to use. This holder
	 * allows propagation training to track the output from each layer. If you
	 * do not need this holder pass null, or use the other compare method.
	 * 
	 * @param input
	 *            The input provide to the neural network.
	 * @param useHolder
	 *            Allows a holder to be specified, this allows propagation
	 *            training to check the output of each layer.
	 * @return The results from the output neurons.
	 */
	public NeuralData compute(final NeuralData input,
			final NeuralOutputHolder useHolder) {
		return this.logic.compute(input, useHolder);
	}

	/**
	 * Create a persistor for this object.
	 * 
	 * @return The newly created persistor.
	 */
	public Persistor createPersistor() {
		return new BasicNetworkPersistor();
	}

	/**
	 * Compare the two neural networks. For them to be equal they must be of the
	 * same structure, and have the same matrix values.
	 * 
	 * @param other
	 *            The other neural network.
	 * @return True if the two networks are equal.
	 */
	public boolean equals(final BasicNetwork other) {
		return equals(other, Encog.DEFAULT_PRECISION);
	}

	/**
	 * Determine if this neural network is equal to another. Equal neural
	 * networks have the same weight matrix and threshold values, within a
	 * specified precision.
	 * 
	 * @param other
	 *            The other neural network.
	 * @param precision
	 *            The number of decimal places to compare to.
	 * @return True if the two neural networks are equal.
	 */
	public boolean equals(final BasicNetwork other, final int precision) {
		return NetworkCODEC.equals(this, other, precision);
	}

	/**
	 * @return The description for this object.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Get the layer specified by the tag.
	 * 
	 * @param tag
	 *            The tag.
	 * @return The layer associated with that tag.
	 */
	public Layer getLayer(final String tag) {
		return this.layerTags.get(tag);
	}

	/**
	 * @return The map of all layer tags.
	 */
	public Map<String, Layer> getLayerTags() {
		return this.layerTags;
	}

	/**
	 * @return The logic used by this network.
	 */
	public NeuralLogic getLogic() {
		return this.logic;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return A map of all properties.
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * Get the specified property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property as a double.
	 */
	public double getPropertyDouble(final String name) {
		return Double.parseDouble(this.properties.get(name));
	}

	/**
	 * Get the specified property as a long.
	 * 
	 * @param name
	 *            The name of the specified property.
	 * @return The value of the specified property.
	 */
	public long getPropertyLong(final String name) {
		return Long.parseLong(this.properties.get(name));
	}

	/**
	 * Get the specified property as a string.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The value of the property.
	 */
	public String getPropertyString(final String name) {
		return this.properties.get(name);
	}

	/**
	 * @return Get the structure of the neural network. The structure allows you
	 *         to quickly obtain synapses and layers without traversing the
	 *         network.
	 */
	public NeuralStructure getStructure() {
		return this.structure;
	}

	/**
	 * Get a list of all of the tags on a specific layer.
	 * 
	 * @param layer
	 *            The layer to check.
	 * @return A collection of the layer tags.
	 */
	public Collection<String> getTags(final Layer layer) {
		final Collection<String> result = new ArrayList<String>();

		for (final Entry<String, Layer> entry : this.layerTags.entrySet()) {
			if (entry.getValue() == layer) {
				result.add(entry.getKey());
			}
		}

		return result;
	}

	/**
	 * @return The size of the matrix.
	 */
	public int getWeightMatrixSize() {
		int result = 0;
		for (final Synapse synapse : this.structure.getSynapses()) {
			result += synapse.getMatrixSize();
		}
		return result;
	}

	/**
	 * Generate a hash code.
	 * 
	 * @return THe hash code.
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Reset the weight matrix and the thresholds.
	 * 
	 */
	public void reset() {
		(new RangeRandomizer(-1, 1)).randomize(this);
	}

	/**
	 * Set the description for this object.
	 * 
	 * @param theDescription
	 *            The description.
	 */
	public void setDescription(final String theDescription) {
		this.description = theDescription;
	}

	/**
	 * Set the type of logic this network should use.
	 * 
	 * @param logic
	 *            The logic used by the network.
	 */
	public void setLogic(final NeuralLogic logic) {
		this.logic = logic;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Set a property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param d
	 *            The value of the property.
	 */
	public void setProperty(final String name, final double d) {
		this.properties.put(name, "" + d);
	}

	/**
	 * Set a property as a long.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param l
	 *            The value of the property.
	 */
	public void setProperty(final String name, final long l) {
		this.properties.put(name, "" + l);
	}

	/**
	 * Set a property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param value
	 *            The value of the property.
	 */
	public void setProperty(final String name, final String value) {
		this.properties.put(name, value);
	}

	/**
	 * Tag a layer.
	 * 
	 * @param tag
	 *            The tag name.
	 * @param layer
	 *            THe layer to tag.
	 */
	public void tagLayer(final String tag, final Layer layer) {
		this.layerTags.put(tag, layer);
	}

	/**
	 * @return Convert this object to a string.
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[BasicNetwork: Layers=");
		final int layers = this.structure.getLayers().size();
		builder.append(layers);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * 
	 * @param input
	 *            The input patter to present to the neural network.
	 * @return The winning neuron.
	 */
	public int winner(final NeuralData input) {

		final NeuralData output = compute(input);
		return BasicNetwork.determineWinner(output);
	}

}
