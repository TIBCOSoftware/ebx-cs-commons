/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.timeline;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.UIComponentWriter;
import com.orchestranetworks.ui.form.UIFormWriter;

/**
 * The Class TimelineJS.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
final class TimelineJS {
	/**
	 * Inserts the event on timeline.
	 *
	 * @param pWriter         the writer
	 * @param event           the event
	 * @param functionContent the function content
	 * @since 1.0.0
	 */
	private static void insertEventOnTimeline(final UIComponentWriter pWriter, final String event, final String functionContent) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("    " + Timeline.JS_VARNAME_TIMELINE + ".on('" + event + "', function (properties){");
		pWriter.addJS_cr("        " + functionContent);
		pWriter.addJS_cr("    });");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function asof.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	protected static void insertFunctionAsof(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_MENU_ASOF + "() {");
		pWriter.addJS_cr("    var prefix = '" + Timeline.CUSTOM_TIME_CALENDAR_PREFIX + "';");
		pWriter.addJS_cr("    var customDay = document.getElementById(prefix + '_day').value;");
		pWriter.addJS_cr("    var customMonth = document.getElementById(prefix + '_month').value;");
		pWriter.addJS_cr("    var customYear = document.getElementById(prefix + '_year').value;");
		pWriter.addJS_cr("    " + Timeline.JS_FUNCNAME_MENU_GOTODATE + "(customYear, customMonth, customDay);");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function fit.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	protected static void insertFunctionFit(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_MENU_FIT + "() {");
		pWriter.addJS_cr("    var noEndlessItemIds = " + Timeline.JS_VARNAME_ITEMS + ".getIds({");
		pWriter.addJS_cr("        filter: function(item){");
		pWriter.addJS_cr("            var accepted = true;");
		pWriter.addJS_cr("            if (item." + TimelineItem.JsObjectName.IsEndless.JS_NAME + ") accepted = false;");
		pWriter.addJS_cr("            return accepted;");
		pWriter.addJS_cr("        }");
		pWriter.addJS_cr("    });");
		pWriter.addJS_cr("    if (noEndlessItemIds.length > 0){");
		pWriter.addJS_cr("        console.log(noEndlessItemIds);");
		pWriter.addJS_cr("        " + Timeline.JS_VARNAME_TIMELINE + ".focus(noEndlessItemIds);");
		pWriter.addJS_cr("    } else {");
		pWriter.addJS_cr("        console.log('noEndlessItemIds empty');");
		pWriter.addJS_cr("        var firstItem = " + Timeline.JS_VARNAME_ITEMS + ".min('" + TimelineItem.JsObjectName.Start.JS_NAME + "');");
		pWriter.addJS_cr("        " + Timeline.JS_VARNAME_TIMELINE + ".setWindow({");
		pWriter.addJS_cr("            start: moment(firstItem.start).subtract(7, 'day'),"); // TODO
		pWriter.addJS_cr("            end: " + Timeline.JS_VARNAME_INITIAL_END_DATE);
		pWriter.addJS_cr("        });");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function get timeline info.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	protected static void insertFunctionGetTimelineInfo(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_GETTIMELINEINFO + "() {");
		pWriter.addJS_cr("    var info = JSON.stringify(" + Timeline.JS_VARNAME_TIMELINE_INFO_OUTPUT + ".get());");
		pWriter.addJS_cr("    return info;");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function goto date.
	 *
	 * @param pWriter      the writer
	 * @param startOfEndOf the start of end of
	 * @param type         the type
	 * @since 1.0.0
	 */
	protected static void insertFunctionGotoDate(final UIComponentWriter pWriter, final boolean startOfEndOf, final String type) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_MENU_GOTODATE + "(year, month, day) {");
		pWriter.addJS("    var customDate = moment(year + ' ' + month + ' ' + day, \"YYYY MM DD\")");
		if (startOfEndOf) {
			pWriter.addJS(".startOf('" + type + "')");
		}
		pWriter.addJS_cr(";");
		pWriter.addJS_cr("    var dateEnd = null;");
		pWriter.addJS_cr("    if (customDate > initialEndDate) {");
		pWriter.addJS("        dateEnd = new moment(year + ' ' + month + ' ' + day, \"YYYY MM DD\")");
		if (startOfEndOf) {
			pWriter.addJS(".endOf('" + type + "')");
		}
		pWriter.addJS_cr(";");
		pWriter.addJS_cr("    } else {");
		pWriter.addJS_cr("        dateEnd = " + Timeline.JS_VARNAME_INITIAL_END_DATE + ";");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("    " + Timeline.JS_VARNAME_TIMELINE + ".setWindow(customDate, dateEnd);");
		pWriter.addJS_cr("    " + Timeline.JS_VARNAME_TIMELINE + ".setOptions({");
		pWriter.addJS_cr("        showCustomTime: true");
		pWriter.addJS_cr("    });");
		pWriter.addJS_cr("    " + Timeline.JS_VARNAME_TIMELINE + ".setCustomTime(customDate);");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function initialize.
	 *
	 * @param pWriter          the writer
	 * @param eventDefinitions the event definitions
	 * @since 1.0.0
	 */
	protected static void insertFunctionInitialize(final UIComponentWriter pWriter, final HashMap<String, String> eventDefinitions) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_INITIALIZE + "() {");
		pWriter.addJS_cr("    if (" + Timeline.JS_VARNAME_INITIAL_CUSTOM_TIME + ") {");
		pWriter.addJS_cr("        " + Timeline.JS_VARNAME_TIMELINE + ".setOptions({");
		pWriter.addJS_cr("            showCustomTime: true");
		pWriter.addJS_cr("        });");
		pWriter.addJS_cr("        " + Timeline.JS_VARNAME_TIMELINE + ".setCustomTime(" + Timeline.JS_VARNAME_INITIAL_CUSTOM_TIME + ");");
		pWriter.addJS_cr("    }");
		if (eventDefinitions != null) {
			Iterator<Entry<String, String>> iterator = eventDefinitions.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> eventDefinition = iterator.next();
				String event = eventDefinition.getKey();
				String functionContent = eventDefinition.getValue();
				TimelineJS.insertEventOnTimeline(pWriter, event, functionContent);
			}
		}
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function move.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	protected static void insertFunctionMove(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_MENU_MOVE + "(percentage) {");
		pWriter.addJS_cr("    var range = " + Timeline.JS_VARNAME_TIMELINE + ".getWindow();");
		pWriter.addJS_cr("    var interval = range.end - range.start;");
		pWriter.addJS_cr("    " + Timeline.JS_VARNAME_TIMELINE + ".setWindow({");
		pWriter.addJS_cr("        start: range.start.valueOf() - interval * percentage,");
		pWriter.addJS_cr("        end: range.end.valueOf() - interval * percentage");
		pWriter.addJS_cr("    });");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function on add.
	 *
	 * @param pWriter         the writer
	 * @param functionContent the function content
	 * @since 1.0.0
	 */
	protected static void insertFunctionOnAdd(final UIComponentWriter pWriter, final String functionContent) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_ONADD + "() {");
		pWriter.addJS_cr(functionContent);
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function on move.
	 *
	 * @param pWriter         the writer
	 * @param functionContent the function content
	 * @since 1.0.0
	 */
	protected static void insertFunctionOnMove(final UIComponentWriter pWriter, final String functionContent) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_ONMOVE + "() {");
		pWriter.addJS_cr(functionContent);
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function on moving.
	 *
	 * @param pWriter         the writer
	 * @param functionContent the function content
	 * @since 1.0.0
	 */
	protected static void insertFunctionOnMoving(final UIComponentWriter pWriter, final String functionContent) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_ONMOVING + "() {");
		pWriter.addJS_cr(functionContent);
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function on remove.
	 *
	 * @param pWriter         the writer
	 * @param functionContent the function content
	 * @since 1.0.0
	 */
	protected static void insertFunctionOnRemove(final UIComponentWriter pWriter, final String functionContent) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_ONREMOVE + "() {");
		pWriter.addJS_cr(functionContent);
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function on update.
	 *
	 * @param pWriter         the writer
	 * @param functionContent the function content
	 * @since 1.0.0
	 */
	protected static void insertFunctionOnUpdate(final UIComponentWriter pWriter, final String functionContent) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_ONUPDATE + "() {");
		pWriter.addJS_cr(functionContent);
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function reset.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	protected static void insertFunctionReset(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_MENU_RESET + "() {");
		pWriter.addJS_cr("    " + Timeline.JS_VARNAME_TIMELINE + ".setWindow({");
		pWriter.addJS_cr("        start: " + Timeline.JS_VARNAME_INITIAL_START_DATE + ",");
		pWriter.addJS_cr("        end: " + Timeline.JS_VARNAME_INITIAL_END_DATE);
		pWriter.addJS_cr("    });");
		pWriter.addJS_cr("    if (" + Timeline.JS_VARNAME_INITIAL_CUSTOM_TIME + ") {");
		pWriter.addJS_cr("        " + Timeline.JS_VARNAME_TIMELINE + ".setOptions({");
		pWriter.addJS_cr("            showCustomTime: true");
		pWriter.addJS_cr("        });");
		pWriter.addJS_cr("        " + Timeline.JS_VARNAME_TIMELINE + ".setCustomTime(" + Timeline.JS_VARNAME_INITIAL_START_DATE + ");");
		pWriter.addJS_cr("    } else {");
		pWriter.addJS_cr("        " + Timeline.JS_VARNAME_TIMELINE + ".setOptions({");
		pWriter.addJS_cr("            showCustomTime: false");
		pWriter.addJS_cr("        });");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function update timeline info.
	 *
	 * @param pWriter              the writer
	 * @param timelineInfoNodePath the timeline info node path
	 * @since 1.0.0
	 */
	protected static void insertFunctionUpdateTimelineInfo(final UIComponentWriter pWriter, final Path timelineInfoNodePath) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_UPDATETIMELINEINFO + "() {");
		if (UIFormWriter.class.isInstance(pWriter)) {
			((UIFormWriter) pWriter).addJS_setNodeValue(Timeline.JS_FUNCNAME_GETTIMELINEINFO + "()", timelineInfoNodePath);
		} else {
			pWriter.addJS_cr("    // Error - addJS_setNodeValue can only be used from a UIFormPaneWriter ");
		}
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the function zoom.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	protected static void insertFunctionZoom(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + Timeline.JS_FUNCNAME_MENU_ZOOM + "(percentage) {");
		pWriter.addJS_cr("    var range = " + Timeline.JS_VARNAME_TIMELINE + ".getWindow();");
		pWriter.addJS_cr("    var interval = range.end - range.start;");
		pWriter.addJS_cr("    " + Timeline.JS_VARNAME_TIMELINE + ".setWindow({");
		pWriter.addJS_cr("        start: range.start.valueOf() - interval * percentage,");
		pWriter.addJS_cr("        end: range.end.valueOf() + interval * percentage");
		pWriter.addJS_cr("    });");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
	}

	/**
	 * Inserts the variable declaration.
	 *
	 * @param pWriter                   the writer
	 * @param options                   the options
	 * @param customVariableDeclaration the custom variable declaration
	 * @since 1.0.0
	 */
	protected static void insertVariableDeclaration(final UIComponentWriter pWriter, final TimelineOptions options, final HashMap<String, String> customVariableDeclaration) {
		pWriter.addJS_cr();
		if (options != null) {
			pWriter.addJS_cr(options.getVariableDeclaration());
		}

		if (customVariableDeclaration == null) {
			return;
		}

		Iterator<Entry<String, String>> iterator = customVariableDeclaration.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> variable = iterator.next();
			String key = variable.getKey();
			String value = variable.getValue();
			pWriter.addJS_cr("var " + key + " = " + value + ";");
		}

		pWriter.addJS_cr();
	}

	/**
	 * Inserts the var timeline info output.
	 *
	 * @param pWriter      the writer
	 * @param filterFields the filter fields
	 * @since 1.0.0
	 */
	protected static void insertVarTimelineInfoOutput(final UIComponentWriter pWriter, final List<String> filterFields) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("var " + Timeline.JS_VARNAME_TIMELINE_INFO_OUTPUT + " = new vis.DataView(" + Timeline.JS_VARNAME_ITEMS + ", {");
		pWriter.addJS_cr("    filter: function(item){");
		pWriter.addJS_cr("        var accepted = true;");
		pWriter.addJS_cr("        if (item." + TimelineItem.JsObjectName.Type.JS_NAME + " === '" + TimelineItem.Type.BACKGROUND + "')accepted = false;");
		pWriter.addJS_cr("        if (item.isFixed) accepted = false;");
		pWriter.addJS_cr("        if (item.isVersion) accepted = false;"); // TODO
		pWriter.addJS_cr("        if (item.isVersionId) accepted = false;"); // TODO
		pWriter.addJS_cr("        if (item.isHierarchy) accepted = false;"); // TODO
		pWriter.addJS_cr("        return accepted;");
		pWriter.addJS_cr("    },");
		pWriter.addJS_cr("    fields: [");
		if (filterFields != null && !filterFields.isEmpty()) {
			pWriter.addJS("        ");
			int nbFields = filterFields.size();
			for (int i = 0; i < nbFields; i++) {
				if (i != 0) {
					pWriter.addJS(",");
				}
				String field = filterFields.get(i);
				pWriter.addJS("'" + field + "'");

			}
			pWriter.addJS_cr();
		}
		pWriter.addJS_cr("    ]");
		pWriter.addJS_cr("});");
		pWriter.addJS_cr();
	}

	private TimelineJS() {
	}
}
