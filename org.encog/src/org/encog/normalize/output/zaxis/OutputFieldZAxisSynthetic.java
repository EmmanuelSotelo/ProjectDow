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
package org.encog.normalize.output.zaxis;

import org.encog.normalize.NormalizationError;
import org.encog.normalize.output.OutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;

/**
 * This field represents the synthetic value used in Z-Axis normalization.
 * For more information see the OutputFieldZAxis class.
 */
public class OutputFieldZAxisSynthetic extends OutputFieldGrouped {

	/**
	 * Construct a synthetic output field for Z-Axis. 
	 * @param group The Z-Axis group that this belongs to.
	 */
	public OutputFieldZAxisSynthetic(final OutputFieldGroup group) {
		super(group, null);
		if (!(group instanceof ZAxisGroup)) {
			throw new NormalizationError(
					"Must use ZAxisGroup with OutputFieldZAxisSynthetic.");
		}
	}

	/**
	 * Calculate the synthetic value for this Z-Axis normalization.
	 * @param subfield Not used.
	 * @return The calculated value.
	 */
	public double calculate(final int subfield) {
		final double l = ((ZAxisGroup) getGroup()).getLength();
		final double f = ((ZAxisGroup) getGroup()).getMultiplier();
		final double n = getGroup().getGroupedFields().size();
		final double result = f * Math.sqrt(n - (l * l));
		if (Double.isInfinite(result) || Double.isNaN(result)) {
			return 0;
		} else {
			return result;
		}
	}

	/**
	 * @return The subfield count, which is one, as this field type does not
	 *         have subfields.
	 */
	public int getSubfieldCount() {
		return 1;
	}

	/**
	 * Not needed for this sort of output field.
	 */
	public void rowInit() {
	}

}
