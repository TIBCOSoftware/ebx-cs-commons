package com.tibco.ebx.cs.commons.ui.network;

/**
 * The Class NetworkOptions.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class NetworkOptions {
	/**
	 * The Class Unit.
	 *
	 * @since 1.0.0
	 */
	public static class Unit {
		private Unit() {
			super();
		}

		public static final String PIXEL = "px";
		public static final String PERCENTAGE = "%";
	}

	protected static class JsObjectName {

		private JsObjectName() {
			super();
		}

		protected static class AutoResize {
			private AutoResize() {
				super();
			}

			protected static final String JS_NAME = "autoResize";
		}

		protected static class ClickToUse {
			private ClickToUse() {
				super();
			}

			protected static final String JS_NAME = "clickToUse";
		}

		protected static class Configure {
			private Configure() {
				super();
			}

			protected static final String JS_NAME = "configure";
		}

		protected static class Edges {
			private Edges() {
				super();
			}

			protected static final String JS_NAME = "edges";
		}

		protected static class Groups {
			private Groups() {
				super();
			}

			protected static final String JS_NAME = "groups";
		}

		protected static class Height {
			private Height() {
				super();
			}

			protected static final String JS_NAME = "height";
		}

		protected static class Interaction {
			private Interaction() {
				super();
			}

			protected static final String JS_NAME = "interaction";
		}

		protected static class Layout {
			private Layout() {
				super();
			}

			protected static final String JS_NAME = "layout";
		}

		protected static class Locale {
			private Locale() {
				super();
			}

			protected static final String JS_NAME = "locale";
		}

		protected static class Locales {
			private Locales() {
				super();
			}

			protected static final String JS_NAME = "locales";
		}

		protected static class Manipulation {
			private Manipulation() {
				super();
			}

			protected static final String JS_NAME = "manipulation";
		}

		protected static class Nodes {
			private Nodes() {
				super();
			}

			protected static final String JS_NAME = "nodes";
		}

		protected static class Physics {
			private Physics() {
				super();
			}

			protected static final String JS_NAME = "physics";
		}

		protected static class Width {
			private Width() {
				super();
			}

			protected static final String JS_NAME = "width";
		}
	}

	private boolean autoResize = true;
	private Integer width = 100;
	private String widthUnit = Unit.PERCENTAGE;
	private Integer height = 100;
	private String heightUnit = Unit.PERCENTAGE;
	private boolean clickToUse = false;
	private NetworkEdgeOptions edgesOptions;
	private NetworkNodeOptions nodesOptions;
	private NetworkLayoutOptions layoutOptions;
	private NetworkInteractionOptions interactionOptions;
	private NetworkManipulationOptions manipulationOptions;
	private NetworkPhysicsOptions physicsOptions;

	/**
	 * Instantiates a new network options.
	 *
	 * @since 1.0.0
	 */
	public NetworkOptions() {
		super();
	}

	/**
	 * Gets the edges options.
	 *
	 * @return the edges options
	 * @since 1.0.0
	 */
	public NetworkEdgeOptions getEdgesOptions() {
		if (this.edgesOptions == null) {
			return new NetworkEdgeOptions();
		} else {
			return this.edgesOptions;
		}
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 * @since 1.0.0
	 */
	public Integer getHeight() {
		return this.height;
	}

	/**
	 * Gets the height unit.
	 *
	 * @return the height unit
	 * @since 1.0.0
	 */
	public String getHeightUnit() {
		return this.heightUnit;
	}

	/**
	 * Gets the interaction options.
	 *
	 * @return the interaction options
	 * @since 1.0.0
	 */
	public NetworkInteractionOptions getInteractionOptions() {
		if (this.interactionOptions == null) {
			return new NetworkInteractionOptions();
		} else {
			return this.interactionOptions;
		}
	}

	/**
	 * Gets the layout options.
	 *
	 * @return the layout options
	 * @since 1.0.0
	 */
	public NetworkLayoutOptions getLayoutOptions() {
		if (this.layoutOptions == null) {
			return new NetworkLayoutOptions();
		} else {
			return this.layoutOptions;
		}
	}

	/**
	 * Gets the manipulation options.
	 *
	 * @return the manipulation options
	 * @since 1.0.0
	 */
	public NetworkManipulationOptions getManipulationOptions() {
		if (this.manipulationOptions == null) {
			return new NetworkManipulationOptions();
		} else {
			return this.manipulationOptions;
		}
	}

	/**
	 * Gets the nodes options.
	 *
	 * @return the nodes options
	 * @since 1.0.0
	 */
	public NetworkNodeOptions getNodesOptions() {
		if (this.nodesOptions == null) {
			return new NetworkNodeOptions();
		} else {
			return this.nodesOptions;
		}
	}

	/**
	 * Gets the physics options.
	 *
	 * @return the physics options
	 * @since 1.0.0
	 */
	public NetworkPhysicsOptions getPhysicsOptions() {
		if (this.physicsOptions == null) {
			return new NetworkPhysicsOptions();
		} else {
			return this.physicsOptions;
		}
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 * @since 1.0.0
	 */
	public Integer getWidth() {
		return this.width;
	}

	/**
	 * Gets the width unit.
	 *
	 * @return the width unit
	 * @since 1.0.0
	 */
	public String getWidthUnit() {
		return this.widthUnit;
	}

	/**
	 * Checks if is auto resize.
	 *
	 * @return true, if is auto resize
	 * @since 1.0.0
	 */
	public boolean isAutoResize() {
		return this.autoResize;
	}

	/**
	 * Checks if is click to use.
	 *
	 * @return true, if is click to use
	 * @since 1.0.0
	 */
	public boolean isClickToUse() {
		return this.clickToUse;
	}

	/**
	 * Sets the auto resize.
	 *
	 * @param autoResize the new auto resize
	 * @since 1.0.0
	 */
	public void setAutoResize(final boolean autoResize) {
		this.autoResize = autoResize;
	}

	/**
	 * Sets the click to use.
	 *
	 * @param clickToUse the new click to use
	 * @since 1.0.0
	 */
	public void setClickToUse(final boolean clickToUse) {
		this.clickToUse = clickToUse;
	}

	/**
	 * Sets the edges options.
	 *
	 * @param edgesOptions the new edges options
	 * @since 1.0.0
	 */
	public void setEdgesOptions(final NetworkEdgeOptions edgesOptions) {
		this.edgesOptions = edgesOptions;
	}

	/**
	 * Sets the height.
	 *
	 * @param height the new height
	 * @since 1.0.0
	 */
	public void setHeight(final Integer height) {
		this.height = height;
	}

	/**
	 * Sets the height unit.
	 *
	 * @param heightUnit the new height unit
	 * @since 1.0.0
	 */
	public void setHeightUnit(final String heightUnit) {
		this.heightUnit = heightUnit;
	}

	/**
	 * Sets the interaction options.
	 *
	 * @param interactionOptions the new interaction options
	 * @since 1.0.0
	 */
	public void setInteractionOptions(final NetworkInteractionOptions interactionOptions) {
		this.interactionOptions = interactionOptions;
	}

	/**
	 * Sets the layout options.
	 *
	 * @param layoutOptions the new layout options
	 * @since 1.0.0
	 */
	public void setLayoutOptions(final NetworkLayoutOptions layoutOptions) {
		this.layoutOptions = layoutOptions;
	}

	/**
	 * Sets the manipulation options.
	 *
	 * @param manipulationOptions the new manipulation options
	 * @since 1.0.0
	 */
	public void setManipulationOptions(final NetworkManipulationOptions manipulationOptions) {
		this.manipulationOptions = manipulationOptions;
	}

	/**
	 * Sets the nodes options.
	 *
	 * @param nodesOptions the new nodes options
	 * @since 1.0.0
	 */
	public void setNodesOptions(final NetworkNodeOptions nodesOptions) {
		this.nodesOptions = nodesOptions;
	}

	/**
	 * Sets the physics options.
	 *
	 * @param physicsOptions the new physics options
	 * @since 1.0.0
	 */
	public void setPhysicsOptions(final NetworkPhysicsOptions physicsOptions) {
		this.physicsOptions = physicsOptions;
	}

	/**
	 * Sets the width.
	 *
	 * @param width the new width
	 * @since 1.0.0
	 */
	public void setWidth(final Integer width) {
		this.width = width;
	}

	/**
	 * Sets the width unit.
	 *
	 * @param widthUnit the new width unit
	 * @since 1.0.0
	 */
	public void setWidthUnit(final String widthUnit) {
		this.widthUnit = widthUnit;
	}

	/**
	 * Gets the js object.
	 *
	 * @return the js object
	 * @since 1.0.0
	 */
	protected String getJsObject() {
		StringBuilder jsObject = new StringBuilder("{");
		jsObject.append(JsObjectName.AutoResize.JS_NAME + ": " + this.isAutoResize() + ", ");
		jsObject.append(JsObjectName.Height.JS_NAME + ": '" + this.getHeight() + this.getHeightUnit() + "', ");
		jsObject.append(JsObjectName.Width.JS_NAME + ": '" + this.getWidth() + this.getWidthUnit() + "', ");
		jsObject.append(JsObjectName.ClickToUse.JS_NAME + ": " + this.isClickToUse() + ", ");
		jsObject.append(JsObjectName.Edges.JS_NAME + ": " + this.getEdgesOptions().getJsObject() + ", ");
		jsObject.append(JsObjectName.Nodes.JS_NAME + ": " + this.getNodesOptions().getJsObject() + ", ");
		jsObject.append(JsObjectName.Layout.JS_NAME + ": " + this.getLayoutOptions().getJsObject() + ", ");
		jsObject.append(JsObjectName.Interaction.JS_NAME + ": " + this.getInteractionOptions().getJsObject() + ", ");
		jsObject.append(JsObjectName.Manipulation.JS_NAME + ": " + this.getManipulationOptions().getJsObject() + ", ");
		jsObject.append(JsObjectName.Physics.JS_NAME + ": " + this.getPhysicsOptions().getJsObject());
		jsObject.append("}");
		return jsObject.toString();
	}

}
