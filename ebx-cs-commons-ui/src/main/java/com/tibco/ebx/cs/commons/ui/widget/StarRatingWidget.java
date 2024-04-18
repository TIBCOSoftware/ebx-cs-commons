/*
 * Copyright Orchestra Networks 2000-2017. All rights reserved.
 */
package com.tibco.ebx.cs.commons.ui.widget;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.info.SchemaFacetBoundaryMaxExclusive;
import com.orchestranetworks.schema.info.SchemaFacetBoundaryMaxInclusive;
import com.orchestranetworks.ui.form.widget.UISimpleCustomWidget;
import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetWriter;

/**
 * Widget to display an integer value as a rating in stars.
 *
 * @see StarRatingWidgetFactory
 * @author Lionel Luquet
 * @since 1.5.0
 */
public class StarRatingWidget extends UISimpleCustomWidget {
	private final String containerStyle;
	private final String fullStarStyle;
	private final String emptyStarStyle;

	/**
	 * Constructor
	 * 
	 * @param context        the context
	 * @param containerStyle the container style
	 * @param fullStarStyle  the full star style
	 * @param emptyStarStyle the empty star style
	 * @since 1.5.0
	 */
	public StarRatingWidget(final WidgetFactoryContext context, final String containerStyle, final String fullStarStyle, final String emptyStarStyle) {
		super(context);
		this.containerStyle = containerStyle;
		this.fullStarStyle = fullStarStyle;
		this.emptyStarStyle = emptyStarStyle;
	}

	@Override
	public void write(final WidgetWriter writer, final WidgetDisplayContext context) {
		boolean isReadOnly = context.getPermission().isReadOnly();
		Integer objectValue = (Integer) context.getValueContext().getValue();
		if (objectValue == null) {
			objectValue = 0;
		}
		final int value = objectValue.intValue();
		SchemaNode node = context.getNode();
		final int max = StarRatingWidget.getMaxValue(node);

		if (isReadOnly || context.isDisplayedInTable()) {
			// Display
			writer.add("<span").addSafeAttribute("style", this.containerStyle).add(">");

			for (int starNumber = 1; starNumber <= max; starNumber++) {
				this.writeStar(writer, value, starNumber);
			}

			writer.add("</span>");
		} else {
			writer.addWidget(Path.SELF);
		}
	}

	/*
	 * Based on node definition to extract max allowed values.
	 */
	private static int getMaxValue(final SchemaNode node) {
		SchemaFacetBoundaryMaxInclusive facetMaxBoundaryInclusive = node.getFacetMaxBoundaryInclusive();
		if (facetMaxBoundaryInclusive != null) {
			Integer inclusiveBound = (Integer) facetMaxBoundaryInclusive.getBound();
			if (inclusiveBound != null) {
				return inclusiveBound.intValue();
			}
		}

		SchemaFacetBoundaryMaxExclusive facetMaxBoundaryExclusive = node.getFacetMaxBoundaryExclusive();
		if (facetMaxBoundaryExclusive != null) {
			Integer exclusiveBound = (Integer) facetMaxBoundaryExclusive.getBound();
			if (exclusiveBound != null) {
				return exclusiveBound.intValue() - 1;
			}
		}

		return 1;
	}

	/*
	 * font-awesome usage.
	 */
	private void writeStar(final WidgetWriter response, final int value, final int starNumber) {
		if (value >= starNumber) {
			response.add("<span").addSafeAttribute("class", "fa-stack").addSafeAttribute("style", this.emptyStarStyle).add(">");
			response.add("<i").addSafeAttribute("class", "fa fa-star fa-stack-1x").addSafeAttribute("style", this.fullStarStyle).add("></i>");
			response.add("<i").addSafeAttribute("class", "fa fa-star-o fa-stack-1x").add("></i>");
			response.add("</span>");
		} else {
			response.add("<i").addSafeAttribute("class", "fa fa-star-o").addSafeAttribute("style", this.emptyStarStyle).add("></i>");
		}
	}
}
