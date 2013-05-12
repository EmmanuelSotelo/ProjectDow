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
package org.encog.bot.browse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.encog.bot.browse.range.Div;
import org.encog.bot.browse.range.DocumentRange;
import org.encog.bot.browse.range.Form;
import org.encog.bot.browse.range.Input;
import org.encog.bot.browse.range.Link;
import org.encog.bot.browse.range.Span;
import org.encog.bot.dataunit.CodeDataUnit;
import org.encog.bot.dataunit.DataUnit;
import org.encog.bot.dataunit.TagDataUnit;
import org.encog.bot.dataunit.TextDataUnit;
import org.encog.parse.tags.Tag;
import org.encog.parse.tags.read.ReadHTML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Called to actually load a web page. This will read the HTML on a web page and
 * generate the DocumentRange classes.
 * 
 * @author jheaton
 * 
 */
public class LoadWebPage {

	/**
	 * The loaded webpage.
	 */
	private WebPage page;

	/**
	 * The base URL for the page being loaded.
	 */
	private final URL base;

	/**
	 * The last form that was processed.
	 */
	private Form lastForm;

	/**
	 * The last hierarchy element that was processed.
	 */
	private DocumentRange lastHierarchyElement;

	/**
	 * The logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a web page loader with the specified base URL.
	 * 
	 * @param base
	 *            The base URL to use when loading.
	 */
	public LoadWebPage(final URL base) {
		this.base = base;
	}

	/**
	 * Add the specified hierarchy element.
	 * 
	 * @param element
	 *            The hierarchy element to add.
	 */
	private void addHierarchyElement(final DocumentRange element) {
		if (this.lastHierarchyElement == null) {
			this.page.addContent(element);
		} else {
			this.lastHierarchyElement.addElement(element);
		}
		this.lastHierarchyElement = element;
	}

	/**
	 * Create a dataunit to hode the code HTML tag.
	 * 
	 * @param str
	 *            The code to create the data unit with.
	 */
	private void createCodeDataUnit(final String str) {
		if (str.trim().length() > 0) {
			final CodeDataUnit d = new CodeDataUnit();
			d.setCode(str);
			this.page.addDataUnit(d);
		}
	}

	/**
	 * Create a tag data unit.
	 * 
	 * @param tag
	 *            The tag name to create the data unit for.
	 */
	private void createTagDataUnit(final Tag tag) {
		final TagDataUnit d = new TagDataUnit();
		d.setTag(tag.clone());

		this.page.addDataUnit(d);
	}

	/**
	 * Create a text data unit.
	 * 
	 * @param str
	 *            The text.
	 */
	private void createTextDataUnit(final String str) {
		if (str.trim().length() > 0) {
			final TextDataUnit d = new TextDataUnit();
			d.setText(str);
			this.page.addDataUnit(d);
		}
	}

	/**
	 * Find the end tag that lines up to the beginning tag.
	 * 
	 * @param index
	 *            The index to start the search on. This specifies the starting
	 *            data unit.
	 * @param tag
	 *            The beginning tag that we are seeking the end tag for.
	 * @return The index that the ending tag was found at. Returns -1 if not
	 *         found.
	 */
	protected int findEndTag(final int index, final Tag tag) {
		int depth = 0;
		int count = index;

		while (count < this.page.getDataSize()) {
			final DataUnit du = this.page.getDataUnit(count);

			if (du instanceof TagDataUnit) {
				final Tag nextTag = ((TagDataUnit) du).getTag();
				if (tag.getName().equalsIgnoreCase(nextTag.getName())) {
					if (nextTag.getType() == Tag.Type.END) {
						if (depth == 0) {
							return count;
						} else {
							depth--;
						}
					} else if (nextTag.getType() == Tag.Type.BEGIN) {
						depth++;
					}
				}
			}
			count++;
		}
		return -1;

	}

	/**
	 * Load a web page from the specified stream.
	 * 
	 * @param is
	 *            The input stream to load from.
	 * @return The loaded web page.
	 */
	public WebPage load(final InputStream is) {
		this.page = new WebPage();

		loadDataUnits(is);
		loadContents();

		return this.page;
	}

	/**
	 * Load the web page from a string that contains HTML.
	 * 
	 * @param str
	 *            A string containing HTML.
	 * @return The loaded WebPage.
	 */
	public WebPage load(final String str) {
		try {
			final ByteArrayInputStream bis = new ByteArrayInputStream(str
					.getBytes());
			final WebPage result = load(bis);
			bis.close();
			return result;
		} catch (final IOException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Exception", e);
			}
			throw new BrowseError(e);
		}
	}

	/**
	 * Using the data units, which should have already been loaded by this time,
	 * load the contents of the web page. This includes the title, any links and
	 * forms. Div tags and spans are also processed.
	 */
	protected void loadContents() {
		for (int index = 0; index < this.page.getDataSize(); index++) {
			final DataUnit du = this.page.getDataUnit(index);
			if (du instanceof TagDataUnit) {
				final Tag tag = ((TagDataUnit) du).getTag();

				if (tag.getType() != Tag.Type.END) {
					if (tag.getName().equalsIgnoreCase("a")) {
						loadLink(index, tag);
					} else if (tag.getName().equalsIgnoreCase("title")) {
						loadTitle(index, tag);
					} else if (tag.getName().equalsIgnoreCase("form")) {
						loadForm(index, tag);
					} else if (tag.getName().equalsIgnoreCase("input")) {
						loadInput(index, tag);
					}

				}

				if (tag.getType() == Tag.Type.BEGIN) {
					if (tag.getName().equalsIgnoreCase("div")) {
						loadDiv(index, tag);
					} else if (tag.getName().equalsIgnoreCase("span")) {
						loadSpan(index, tag);
					}
				}

				if (tag.getType() == Tag.Type.END) {
					if (tag.getName().equalsIgnoreCase("div")) {
						if (this.lastHierarchyElement != null) {
							this.lastHierarchyElement = this.lastHierarchyElement
									.getParent();
						}
					} else if (tag.getName().equalsIgnoreCase("span")) {
						if (this.lastHierarchyElement != null) {
							this.lastHierarchyElement = this.lastHierarchyElement
									.getParent();
						}
					}
				}
			}
		}
	}

	/**
	 * Load the data units. Once the lower level data units have been loaded,
	 * the contents can be loaded.
	 * 
	 * @param is
	 *            The input stream that the data units are loaded from.
	 */
	protected void loadDataUnits(final InputStream is) {
		final StringBuilder text = new StringBuilder();
		int ch;
		final ReadHTML parse = new ReadHTML(is);
		boolean style = false;
		boolean script = false;

		while ((ch = parse.read()) != -1) {
			if (ch == 0) {

				if (style) {
					createCodeDataUnit(text.toString());
				} else if (script) {
					createCodeDataUnit(text.toString());
				} else {
					createTextDataUnit(text.toString());
				}
				style = false;
				script = false;

				text.setLength(0);
				createTagDataUnit(parse.getTag());
				if (parse.getTag().getName().equalsIgnoreCase("style")) {
					style = true;
				} else if (parse.getTag().getName().equalsIgnoreCase("script")) {
					script = true;
				}
			} else {
				text.append((char) ch);
			}
		}

		createTextDataUnit(text.toString());

	}

	/**
	 * Called by loadContents to load a div tag.
	 * 
	 * @param index
	 *            The index to begin at.
	 * @param tag
	 *            The beginning div tag.
	 */
	private void loadDiv(final int index, final Tag tag) {
		final Div div = new Div(this.page);
		final String classAttribute = tag.getAttributeValue("class");
		final String idAttribute = tag.getAttributeValue("id");

		div.setIdAttribute(idAttribute);
		div.setClassAttribute(classAttribute);
		div.setBegin(index);
		div.setEnd(findEndTag(index + 1, tag));
		addHierarchyElement(div);
	}

	/**
	 * Called by loadContents to load a form on the page.
	 * 
	 * @param index
	 *            The index to begin loading at.
	 * @param tag
	 *            The beginning tag.
	 */
	protected void loadForm(final int index, final Tag tag) {
		final String method = tag.getAttributeValue("method");
		final String action = tag.getAttributeValue("action");

		final Form form = new Form(this.page);
		form.setBegin(index);
		form.setEnd(findEndTag(index + 1, tag));

		if ((method == null) || method.equalsIgnoreCase("GET")) {
			form.setMethod(Form.Method.GET);
		} else {
			form.setMethod(Form.Method.POST);
		}

		if (action == null) {
			form.setAction(new Address(this.base));
		} else {
			form.setAction(new Address(this.base, action));
		}

		this.page.addContent(form);
		this.lastForm = form;
	}

	/**
	 * Called by loadContents to load an input tag on the form.
	 * 
	 * @param index
	 *            The index to begin loading at.
	 * @param tag
	 *            The beginning tag.
	 */
	protected void loadInput(final int index, final Tag tag) {
		final String type = tag.getAttributeValue("type");
		final String name = tag.getAttributeValue("name");
		final String value = tag.getAttributeValue("value");

		final Input input = new Input(this.page);
		input.setType(type);
		input.setName(name);
		input.setValue(value);

		if (this.lastForm != null) {
			this.lastForm.addElement(input);
		} else {
			this.page.addContent(input);
		}
	}

	/**
	 * Called by loadContents to load a link on the page.
	 * 
	 * @param index
	 *            The index to begin loading at.
	 * @param tag
	 *            The beginning tag.
	 */

	protected void loadLink(final int index, final Tag tag) {
		final Link link = new Link(this.page);
		final String href = tag.getAttributeValue("href");

		if (href != null) {
			link.setTarget(new Address(this.base, href));
			link.setBegin(index);
			link.setEnd(findEndTag(index + 1, tag));
			this.page.addContent(link);
		}
	}

	/**
	 * Called by loadContents to load a span.
	 * 
	 * @param index
	 *            The index to begin loading at.
	 * @param tag
	 *            The beginning tag.
	 */

	private void loadSpan(final int index, final Tag tag) {
		final Span span = new Span(this.page);
		final String classAttribute = tag.getAttributeValue("class");
		final String idAttribute = tag.getAttributeValue("id");

		span.setIdAttribute(idAttribute);
		span.setClassAttribute(classAttribute);
		span.setBegin(index);
		span.setEnd(findEndTag(index + 1, tag));
		addHierarchyElement(span);
	}

	/**
	 * Called by loadContents to load the title of the page.
	 * 
	 * @param index
	 *            The index to begin loading at.
	 * @param tag
	 *            The beginning tag.
	 */
	protected void loadTitle(final int index, final Tag tag) {
		final DocumentRange title = new DocumentRange(this.page);
		title.setBegin(index);
		title.setEnd(findEndTag(index + 1, tag));
		this.page.setTitle(title);
	}

}
