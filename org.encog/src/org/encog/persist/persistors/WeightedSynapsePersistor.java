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
package org.encog.persist.persistors;

import org.encog.neural.networks.synapse.WeightedSynapse;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

/**
 * The Encog persistor used to persist the ActivationBiPolar class.
 * 
 * @author jheaton
 */
public class WeightedSynapsePersistor implements Persistor {

	/**
	 * The weights tag.
	 */
	public static final String TAG_WEIGHTS = "weights";

	/**
	 * Load the specified Encog object from an XML reader.
	 * 
	 * @param in
	 *            The XML reader to use.
	 * @return The loaded object.
	 */
	public EncogPersistedObject load(final ReadXML in) {
		final WeightedSynapse synapse = new WeightedSynapse();

		final String end = in.getTag().getName();

		while (in.readToTag()) {

			if (in.is(WeightedSynapsePersistor.TAG_WEIGHTS, true)) {
				in.readToTag();
				synapse.setMatrix(PersistorUtil.loadMatrix(in));
			}
			if (in.is(end, false)) {
				break;
			}
		}

		return synapse;
	}

	/**
	 * Save the specified Encog object to an XML writer.
	 * 
	 * @param obj
	 *            The object to save.
	 * @param out
	 *            The XML writer to save to.
	 */
	public void save(final EncogPersistedObject obj, final WriteXML out) {
		PersistorUtil
				.beginEncogObject(
						EncogPersistedCollection.TYPE_WEIGHTED_SYNAPSE, out,
						obj, false);
		final WeightedSynapse synapse = (WeightedSynapse) obj;

		out.beginTag(WeightedSynapsePersistor.TAG_WEIGHTS);
		PersistorUtil.saveMatrix(synapse.getMatrix(), out);
		out.endTag();

		out.endTag();
	}

}
