/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.network;

/**
 * The Class NetworkLayoutOptions.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class NetworkLayoutOptions {
	/**
	 * The Class Hierarchical_Direction.
	 *
	 * @since 1.0.0
	 */
	public static class Hierarchical_Direction {
		private Hierarchical_Direction() {
			super();
		}

		public static final String UP_DOWN = "UD";
		public static final String DOWN_UP = "DU";
		public static final String LEFT_RIGHT = "LR";
		public static final String RIGHT_LEFT = "RL";
	}

	/**
	 * The Class Hierarchical_Sort_Method.
	 *
	 * @since 1.0.0
	 */
	public static class Hierarchical_Sort_Method {
		private Hierarchical_Sort_Method() {
			super();
		}

		public static final String HUBSIZE = "hubsize";
		public static final String DIRECTED = "directed";
	}

	/**
	 * The Class JsObjectName.
	 *
	 * @since 1.0.0
	 */
	protected static class JsObjectName {
		private JsObjectName() {
			super();
		}

		protected static class Hierarchical {
			private Hierarchical() {
				super();
			}

			protected static class Direction {
				private Direction() {
					super();
				}

				protected static final String JS_NAME = "direction";
			}

			protected static class Enabled {
				private Enabled() {
					super();
				}

				protected static final String JS_NAME = "enabled";
			}

			protected static class Level_Separation {
				private Level_Separation() {
					super();
				}

				protected static final String JS_NAME = "levelSeparation";
			}

			protected static class Sort_Method {
				private Sort_Method() {
					super();
				}

				protected static final String JS_NAME = "sortMethod";
			}

			protected static final String JS_NAME = "hierarchical";
		}

		/**
		 * The Class RandomSeed.
		 */
		protected static class RandomSeed {
			private RandomSeed() {
				super();
			}

			protected static final String JS_NAME = "randomSeed";
		}
	}

	private Integer randomSeed;
	private boolean hierarchicalEnabled = true;
	private Integer hierarchicalLevelSeparation = 150;
	private String hierarchicalDirection = Hierarchical_Direction.DOWN_UP;
	private String hierarchicalSortMethod = Hierarchical_Sort_Method.DIRECTED;

	/**
	 * Instantiates a new network layout options.
	 *
	 * @since 1.0.0
	 */
	public NetworkLayoutOptions() {
		super();
	}

	/**
	 * Gets the hierarchical direction.
	 *
	 * @return the hierarchical direction
	 * @since 1.0.0
	 */
	public String getHierarchicalDirection() {
		return this.hierarchicalDirection;
	}

	/**
	 * Gets the hierarchical level separation.
	 *
	 * @return the hierarchical level separation
	 * @since 1.0.0
	 */
	public Integer getHierarchicalLevelSeparation() {
		return this.hierarchicalLevelSeparation;
	}

	/**
	 * Gets the hierarchical sort method.
	 *
	 * @return the hierarchical sort method
	 * @since 1.0.0
	 */
	public String getHierarchicalSortMethod() {
		return this.hierarchicalSortMethod;
	}

	/**
	 * Gets the random seed.
	 *
	 * @return the random seed
	 * @since 1.0.0
	 */
	public Integer getRandomSeed() {
		return this.randomSeed;
	}

	/**
	 * Checks if is hierarchical enabled.
	 *
	 * @return true, if is hierarchical enabled
	 * @since 1.0.0
	 */
	public boolean isHierarchicalEnabled() {
		return this.hierarchicalEnabled;
	}

	/**
	 * Sets the hierarchical direction.
	 *
	 * @param hierarchicalDirection the new hierarchical direction
	 * @since 1.0.0
	 */
	public void setHierarchicalDirection(final String hierarchicalDirection) {
		this.hierarchicalDirection = hierarchicalDirection;
	}

	/**
	 * Sets the hierarchical enabled.
	 *
	 * @param hierarchicalEnabled the new hierarchical enabled
	 * @since 1.0.0
	 */
	public void setHierarchicalEnabled(final boolean hierarchicalEnabled) {
		this.hierarchicalEnabled = hierarchicalEnabled;
	}

	/**
	 * Sets the hierarchical level separation.
	 *
	 * @param hierarchicalLevelSeparation the new hierarchical level separation
	 * @since 1.0.0
	 */
	public void setHierarchicalLevelSeparation(final Integer hierarchicalLevelSeparation) {
		this.hierarchicalLevelSeparation = hierarchicalLevelSeparation;
	}

	/**
	 * Sets the hierarchical sort method.
	 *
	 * @param hierarchicalSortMethod the new hierarchical sort method
	 * @since 1.0.0
	 */
	public void setHierarchicalSortMethod(final String hierarchicalSortMethod) {
		this.hierarchicalSortMethod = hierarchicalSortMethod;
	}

	/**
	 * Sets the random seed.
	 *
	 * @param randomSeed the new random seed
	 * @since 1.0.0
	 */
	public void setRandomSeed(final Integer randomSeed) {
		this.randomSeed = randomSeed;
	}

	/**
	 * Gets the js object.
	 *
	 * @return the js object
	 * @since 1.0.0
	 */
	protected String getJsObject() {
		StringBuilder jsObject = new StringBuilder("{");
		jsObject.append(JsObjectName.RandomSeed.JS_NAME + ": " + this.getRandomSeed() + ", ");
		jsObject.append(JsObjectName.Hierarchical.JS_NAME + ": {");
		jsObject.append(JsObjectName.Hierarchical.Enabled.JS_NAME + ": " + this.isHierarchicalEnabled() + ", ");
		jsObject.append(JsObjectName.Hierarchical.Level_Separation.JS_NAME + ": "
				+ this.getHierarchicalLevelSeparation() + ", ");
		jsObject.append(JsObjectName.Hierarchical.Direction.JS_NAME + ": '" + this.getHierarchicalDirection() + "', ");
		jsObject.append(JsObjectName.Hierarchical.Sort_Method.JS_NAME + ": '" + this.getHierarchicalSortMethod() + "'");
		jsObject.append("}");
		jsObject.append("}");
		return jsObject.toString();
	}

}
