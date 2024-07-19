/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.userservice.index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.Request;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.adaptation.RequestSortCriteria;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.UICSSClasses;
import com.orchestranetworks.userservice.UserServicePane;
import com.orchestranetworks.userservice.UserServicePaneContext;
import com.orchestranetworks.userservice.UserServicePaneWriter;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.ui.util.Presales_UIUtils;

/**
 * UserServicePane used by {@link IndexUserService } to render the Index service.
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
class IndexUserServicePane implements UserServicePane {
	private static final String INDEX_CHARACTER_CONTAINER_CLASS = "indexCharacterContainer";
	private static final String CARD_COLOR_BACKGROUND = "#F2F2F2";
	private static final String INDEX_CONTAINER_DIV_ID = "indexContainer";
	private static final String INDEX_SLIDER_DIV_ID = "indexSlider";
	private static final String INDEX_CONTENT_DIV_ID = "indexContent";
	private static final String COLOR_TRANSITION_DURATION = "0.2s";

	private final Path labelPath;
	private final Path descriptionPath;
	private final Request recordRequest;
	private boolean colorIndexTitle = true;
	private boolean colorIndexSlider = true;
	private final LinkedHashMap<String, ArrayList<Adaptation>> data = new LinkedHashMap<>();

	/**
	 * Instanciate IndexUserServicePane with required arguments. {@link Request} is expected as the sorting is then applied.
	 *
	 * @param pRecordRequest   the request to execute to get to records to display (Mandatory).
	 * @param pLabelPath       the path in the record to get the label (Mandatory).
	 * @param pDescriptionPath the path in the record to get a description (Optional).
	 * @throws IllegalArgumentException if the request of the label path is null.
	 * @since 1.8.0
	 */
	public IndexUserServicePane(final Request pRecordRequest, final Path pLabelPath, final Path pDescriptionPath) throws IllegalArgumentException {
		if (pRecordRequest == null) {
			throw new IllegalArgumentException("The reauest argument shall not be null");
		}
		if (pLabelPath == null) {
			throw new IllegalArgumentException("The label path argument shall not be null");
		}

		this.recordRequest = pRecordRequest;
		this.labelPath = pLabelPath;
		this.descriptionPath = pDescriptionPath;
	}

	/**
	 * Getter for the colorIndexSlider parameter defining if the slider index is colored (true) or not (false).
	 *
	 * @return true for colored, false for not.
	 * @since 1.8.0
	 */
	public boolean isColorIndexSlider() {
		return this.colorIndexSlider;
	}

	/**
	 * Getter for the colorIndexTitle parameter defining if the index title is colored (true) or not (false).
	 *
	 * @return true for colored, false for not.
	 * @since 1.8.0
	 */
	public boolean isColorIndexTitle() {
		return this.colorIndexTitle;
	}

	/**
	 * Setter for the colorIndexSlider parameter defining if the slider index is colored (true) or not (false). Default is true.
	 *
	 * @param pColorIndexSlider true for colored, false for not.
	 * @since 1.8.0
	 */
	public void setColorIndexSlider(final boolean pColorIndexSlider) {
		this.colorIndexSlider = pColorIndexSlider;
	}

	/**
	 * Setter for the colorIndexTitle parameter defining if the index title is colored (true) or not (false). Default is true.
	 *
	 * @param pColorIndexTitle true for colored, false for not.
	 * @since 1.8.0
	 */
	public void setColorIndexTitle(final boolean pColorIndexTitle) {
		this.colorIndexTitle = pColorIndexTitle;
	}

	@Override
	public void writePane(final UserServicePaneContext pPaneContext, final UserServicePaneWriter pWriter) {
		// Extract the data
		// Not optimized for very big volumes as data are stored in a map to ease the
		// next steps
		this.extractData(pWriter, this.recordRequest);

		StringBuilder mainContainerDivStyle = new StringBuilder();
		mainContainerDivStyle.append("position:relative;");
		mainContainerDivStyle.append("box-sizing:border-box;");
		mainContainerDivStyle.append("padding:20px 20px 0 20px;");
		mainContainerDivStyle.append("overflow:hidden;");

		// Add div container
		pWriter.add("<div ").addSafeAttribute("id", IndexUserServicePane.INDEX_CONTAINER_DIV_ID).addSafeAttribute("style", mainContainerDivStyle.toString()).add(">");

		// Add the index slider
		IndexUserServicePane.insertHTMLIndexSlider(pWriter, this.data);

		// Add the index content
		this.insertHTMLIndexContent(pWriter, this.data);

		pWriter.add("</div>");

		// Add the javascript code
		this.insertJS(pWriter, this.data);
	}

	/**
	 * Execute the request label-sorted and store the data in a map to be used in next steps of the render.
	 *
	 * @param pWriter        the writer.
	 * @param pRecordRequest the request to execute.
	 * @since 1.8.0
	 */
	private void extractData(final UserServicePaneWriter pWriter, final Request pRecordRequest) {
		// Not optimized for big volumes as data are stored in a map to ease the next
		// steps

		RequestSortCriteria sortCriteria = new RequestSortCriteria();
		if (this.labelPath != null) {
			sortCriteria.add(this.labelPath);
		}
		pRecordRequest.setSortCriteria(sortCriteria);

		RequestResult recordRequestResult = pRecordRequest.execute();

		try {
			Adaptation record = null;
			while ((record = recordRequestResult.nextAdaptation()) != null) {
				String label = record.getString(this.labelPath);

				if (label == null) {
					continue;
				}

				String indexCharacter = label.substring(0, 1).toUpperCase();

				ArrayList<Adaptation> records = this.data.get(indexCharacter);
				if (records == null) {
					records = new ArrayList<>();
					records.add(record);
					this.data.put(indexCharacter, records);
				} else {
					records.add(record);
				}
			}
		} finally {
			recordRequestResult.close();
		}
	}

	/**
	 * Build the HTML id for the index.
	 *
	 * @param pIndex the index character.
	 * @return the id concatenating a prefixe and the index character.
	 * @since 1.8.0
	 */
	private static String getIdForIndex(final String pIndex) {
		return "Index_" + pIndex;
	}

	/**
	 * Insert the HTML for the whole content, ie the record item organized by index character.
	 *
	 * @param pWriter the writer.
	 * @param pData   the data as map.
	 * @since 1.8.0
	 */
	private void insertHTMLIndexContent(final UserServicePaneWriter pWriter, final LinkedHashMap<String, ArrayList<Adaptation>> pData) {
		StringBuilder contentDivStyle = new StringBuilder();
		contentDivStyle.append("overflow:auto;");
		contentDivStyle.append("margin-right:30px;");

		pWriter.add("<div ").addSafeAttribute("id", IndexUserServicePane.INDEX_CONTENT_DIV_ID).addSafeAttribute("style", contentDivStyle.toString()).add(">");

		if (pData.isEmpty()) {
			this.insertHTMLNoContentMessage(pWriter);
			pWriter.add("</div>");
			return;
		}

		StringBuilder contentUlContainer = new StringBuilder();
		contentUlContainer.append("margin:0;");
		contentUlContainer.append("padding:0;");
		contentUlContainer.append("list-style:none;");

		pWriter.add("<ul ").addSafeAttribute("style", contentUlContainer.toString()).add(">");

		Iterator<Entry<String, ArrayList<Adaptation>>> iterator = pData.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, ArrayList<Adaptation>> indexItem = iterator.next();
			String indexCharacter = indexItem.getKey();
			ArrayList<Adaptation> records = indexItem.getValue();

			String indexId = IndexUserServicePane.getIdForIndex(indexCharacter);
			pWriter.add("<li").addSafeAttribute("id", indexId).addSafeAttribute("class", IndexUserServicePane.INDEX_CHARACTER_CONTAINER_CLASS).add(">");

			IndexUserServicePane.insertHTMLIndexTitle(pWriter, indexCharacter);

			StringBuilder indexUlContainer = new StringBuilder();
			indexUlContainer.append("margin:0;");
			indexUlContainer.append("padding:0;");
			indexUlContainer.append("list-style:none;");

			pWriter.add("<ul ").addSafeAttribute("style", indexUlContainer.toString()).add(">");

			for (Adaptation record : records) {
				this.insertHTMLRecordItem(pWriter, record);
			}

			pWriter.add("</ul>");
			pWriter.add("</li>");
		}

		pWriter.add("</ul>");

		// Insert an extra height at the end of the list
		pWriter.add("<div style='height:78px'></div>");
		// Since EBX 5.9, the bottom bar is translucent, the content is scrolled
		// underneath
		// but to ensure all content will be visible while scolling down, an extra
		// height is required
		// EBX 5.9 adds itslef the extra height
		// but the specific implementation of this service require to add it ourselve.

		pWriter.add("</div>");
	}

	/**
	 * Insert the index slider on the side of the service. Allows to redirect to a given index by using
	 *
	 * @param pWriter the writer.
	 * @param pData   the data as map.
	 * @since 1.8.0
	 */
	private static void insertHTMLIndexSlider(final UserServicePaneWriter pWriter, final LinkedHashMap<String, ArrayList<Adaptation>> pData) {
		Set<String> indexes = pData.keySet();

		StringBuilder indexSliderContainerDivStyle = new StringBuilder();
		indexSliderContainerDivStyle.append("position:absolute;");
		indexSliderContainerDivStyle.append("right:20px;");
		indexSliderContainerDivStyle.append("top:20px;");
		indexSliderContainerDivStyle.append("padding:5px;");
		indexSliderContainerDivStyle.append("box-sizing:border-box;");
		indexSliderContainerDivStyle.append("display:flex;");
		indexSliderContainerDivStyle.append("flex-direction:column;");
		indexSliderContainerDivStyle.append("justify-content:space-evenly;");

		pWriter.add("<div ").addSafeAttribute("id", IndexUserServicePane.INDEX_SLIDER_DIV_ID).addSafeAttribute("style", indexSliderContainerDivStyle.toString()).add(">");

		for (String index : indexes) {
			StringBuilder indexDivStyle = new StringBuilder();
			indexDivStyle.append("text-align:center;");
			indexDivStyle.append("line-height:16px;");
			indexDivStyle.append("width:16px;");
			indexDivStyle.append("border-radius:8px;");
			indexDivStyle.append("transition:background-color 0.5s, color 0.5s;");

			pWriter.add("<div").addSafeAttribute("style", indexDivStyle.toString()).add(">");

			String indexId = IndexUserServicePane.getIdForIndex(index);

			StringBuilder indexLinkStyle = new StringBuilder();
			indexLinkStyle.append("font-weight:bold;");
			indexLinkStyle.append("color:inherit;");

			pWriter.add("<a ").addSafeAttribute("href", "#" + indexId).addSafeAttribute("style", indexLinkStyle.toString()).add(">");
			pWriter.addSafeInnerHTML(index);
			pWriter.add("</a>");

			pWriter.add("</div>");
		}

		pWriter.add("</div>");
	}

	/**
	 * Insert a index title.
	 *
	 * @param pWriter the writer.
	 * @param pIndex  the index character.
	 * @since 1.8.0
	 */
	private static void insertHTMLIndexTitle(final UserServicePaneWriter pWriter, final String pIndex) {
		String indexId = IndexUserServicePane.getIdForIndex(pIndex);

		StringBuilder indexTitleStyle = new StringBuilder();
		indexTitleStyle.append("position:-webkit-sticky;");
		indexTitleStyle.append("position:sticky;");
		indexTitleStyle.append("top:0;");
		indexTitleStyle.append("padding:0;");
		indexTitleStyle.append("margin-top:30px;");
		indexTitleStyle.append("box-sizing:border-box;");
		indexTitleStyle.append("width:40px;");
		indexTitleStyle.append("line-height:40px;");
		indexTitleStyle.append("text-align:center;");
		indexTitleStyle.append("border-radius:20px;");
		indexTitleStyle.append("transition:background-color " + IndexUserServicePane.COLOR_TRANSITION_DURATION + ", color " + IndexUserServicePane.COLOR_TRANSITION_DURATION + ";");

		pWriter.add("<h2").addSafeAttribute("id", indexId).addSafeAttribute("style", indexTitleStyle.toString()).add(">");
		pWriter.addSafeInnerHTML(pIndex);
		pWriter.add("</h2>");
	}

	/**
	 * Insert a message in case there is no content to display.
	 *
	 * @param pWriter the writer.
	 * @since 1.8.0
	 */
	private void insertHTMLNoContentMessage(final UserServicePaneWriter pWriter) {
		UserMessage noContentMessage = Messages.getInfo(this.getClass(), "ThereIsNoContentToDisplay");

		pWriter.add("<p>");
		pWriter.addSafeInnerHTML(noContentMessage);
		pWriter.add("</p>");
	}

	/**
	 * Add a card item representing the record by getting its label and description (from the specified paths).
	 *
	 * @param pWriter the writer.
	 * @param pRecord the record to add as item.
	 * @since 1.8.0
	 */
	private void insertHTMLRecordItem(final UserServicePaneWriter pWriter, final Adaptation pRecord) {
		StringBuilder cardContainerStyle = new StringBuilder();
		cardContainerStyle.append("padding:10px 30px 10px 60px;");
		cardContainerStyle.append("box-sizing:border-box;");

		StringBuilder cardStyle = new StringBuilder();
		cardStyle.append("background-color:" + IndexUserServicePane.CARD_COLOR_BACKGROUND + ";");
		cardStyle.append("box-sizing:border-box;");
		cardStyle.append("box-shadow: 0 1px 1px #C0C0C0;");
		cardStyle.append("padding:10px;");
		cardStyle.append("overflow:hidden;");
		cardStyle.append("max-height:5rem;");
		cardStyle.append("border-radius:5px;");

		StringBuilder labelCardStyle = new StringBuilder();
		labelCardStyle.append("max-height:2rem;");
		labelCardStyle.append("overflow:hidden;");

		StringBuilder descriptionCardStyle = new StringBuilder();
		descriptionCardStyle.append("max-height:2rem;");
		descriptionCardStyle.append("overflow:hidden;");
		descriptionCardStyle.append("white-space:nowrap;");
		descriptionCardStyle.append("text-overflow:ellipsis;");
		descriptionCardStyle.append("font-style:italic;");
		descriptionCardStyle.append("margin-top:10px;");

		pWriter.add("<li").addSafeAttribute("style", cardContainerStyle.toString()).add(">");

		pWriter.add("<div ").addSafeAttribute("style", cardStyle.toString()).add(">");

		pWriter.add("<div ").addSafeAttribute("style", labelCardStyle.toString()).add(">");

		String label = null;
		if (this.labelPath == null) {
			label = pRecord.getLabel(pWriter.getLocale());
		} else {
			label = pRecord.getString(this.labelPath);
		}

		Presales_UIUtils.addPopUpLink(pWriter, pRecord, label);

		pWriter.add("</div>");

		if (this.descriptionPath != null) {
			pWriter.add("<div ").addSafeAttribute("style", descriptionCardStyle.toString()).add(">");

			String description = pRecord.getString(this.descriptionPath);
			if (description == null) {
				description = "";
			}
			pWriter.addSafeInnerHTML(description);

			pWriter.add("</div>");
		}

		pWriter.add("</div>");

		pWriter.add("</li>");
	}

	/**
	 * Insert the javascript required for the service.
	 *
	 * @param pWriter the writer.
	 * @param pData   the data as map
	 * @since 1.8.0
	 */
	private void insertJS(final UserServicePaneWriter pWriter, final LinkedHashMap<String, ArrayList<Adaptation>> pData) {
		// Resize the div container to the workspace
		String containerResizeFuncName = IndexUserServicePane.INDEX_CONTAINER_DIV_ID + "SizeSyncToWorkspace";
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + containerResizeFuncName + "(size, sizeToUnderneathBottomBar) {");
		pWriter.addJS_cr("    let height = size.h;");
		pWriter.addJS_cr("    if(sizeToUnderneathBottomBar) {"); // since EBX 5.9.0, translucent bottom bar adds an
		// extra height so provide another argument
		pWriter.addJS_cr("        height = sizeToUnderneathBottomBar.h;");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("    const ele = document.getElementById('" + IndexUserServicePane.INDEX_CONTAINER_DIV_ID + "');");
		pWriter.addJS_cr("    if(ele) {");
		pWriter.addJS_cr("        ele.style.height = height + 'px';");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
		pWriter.addJS_addResizeWorkspaceListener(containerResizeFuncName);
		pWriter.addJS_cr();

		// Resize the div content to the workspace
		String contentResizeFuncName = IndexUserServicePane.INDEX_CONTENT_DIV_ID + "SizeSyncToWorkspace";
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + contentResizeFuncName + "(size, sizeToUnderneathBottomBar) {");
		pWriter.addJS_cr("    let height = size.h;");
		pWriter.addJS_cr("    if(sizeToUnderneathBottomBar) {"); // since EBX 5.9.0, translucent bottom bar adds an
		// extra height so provide another argument
		pWriter.addJS_cr("        height = sizeToUnderneathBottomBar.h;");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("    const ele = document.getElementById('" + IndexUserServicePane.INDEX_CONTENT_DIV_ID + "');");
		pWriter.addJS_cr("    if(ele) {");
		pWriter.addJS_cr("        ele.style.height = height + 'px';");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
		pWriter.addJS_addResizeWorkspaceListener(contentResizeFuncName);
		pWriter.addJS_cr();

		// Resize the div slider to the workspace
		String sliderResizeFuncName = IndexUserServicePane.INDEX_SLIDER_DIV_ID + "SizeSyncToWorkspace";
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + sliderResizeFuncName + "(size) {");
		pWriter.addJS_cr("    let height = size.h;"); // use the height to the top of the bottom bar
		pWriter.addJS_cr("    const ele = document.getElementById('" + IndexUserServicePane.INDEX_SLIDER_DIV_ID + "');");
		pWriter.addJS_cr("    if(ele) {");
		pWriter.addJS_cr("        ele.style.height = (height - 20) + 'px';"); // Take into account the container 20px
		// padding-top
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
		pWriter.addJS_addResizeWorkspaceListener(sliderResizeFuncName);
		pWriter.addJS_cr();

		// Get ride of default spacer on 5.9
		pWriter.addJS_cr();
		pWriter.addJS_cr("const indexContainerElement = document.getElementById('" + IndexUserServicePane.INDEX_CONTAINER_DIV_ID + "');");
		pWriter.addJS_cr("if(indexContainerElement) {");
		pWriter.addJS_cr("    const indexWorkspace = indexContainerElement.parentElement;");
		pWriter.addJS_cr("    if(indexWorkspace) {");
		pWriter.addJS_cr("        indexWorkspace.style.padding = '0';");
		pWriter.addJS_cr("        const indexFormBottomBarSpace = indexWorkspace.querySelector('.ebx_form_bottom_bar_spacer');");
		pWriter.addJS_cr("        if(indexFormBottomBarSpace) {");
		pWriter.addJS_cr("            indexFormBottomBarSpace.style.height = '0';");
		pWriter.addJS_cr("        }");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		// Throttle function to try to avoid too many call
		pWriter.addJS_cr();
		pWriter.addJS_cr("function throttle(callback, delay) {");
		pWriter.addJS_cr("    var last;");
		pWriter.addJS_cr("    var timer;");
		pWriter.addJS_cr("    return function () {");
		pWriter.addJS_cr("        var context = this;");
		pWriter.addJS_cr("        var now = +new Date();");
		pWriter.addJS_cr("        var args = arguments;");
		pWriter.addJS_cr("        if (last && now < last + delay) {");
		pWriter.addJS_cr("            clearTimeout(timer);");
		pWriter.addJS_cr("            timer = setTimeout(function () {");
		pWriter.addJS_cr("                last = now;");
		pWriter.addJS_cr("                callback.apply(context, args);");
		pWriter.addJS_cr("            }, delay);");
		pWriter.addJS_cr("        } else {");
		pWriter.addJS_cr("            last = now;");
		pWriter.addJS_cr("            callback.apply(context, args);");
		pWriter.addJS_cr("        }");
		pWriter.addJS_cr("    };");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		// Function to scroll to a given element
		pWriter.addJS_cr();
		pWriter.addJS_cr("function scrollTo(toElement, duration = 2000) {");
		pWriter.addJS_cr("    const to = toElement.offsetTop;");
		pWriter.addJS_cr("    const element = document.getElementById('" + IndexUserServicePane.INDEX_CONTENT_DIV_ID + "');");
		pWriter.addJS_cr("    const start = element.scrollTop;");
		pWriter.addJS_cr("    const change = to - start;");
		pWriter.addJS_cr("    const increment = 20;");
		pWriter.addJS_cr("    let currentTime = 0;");
		pWriter.addJS_cr();
		pWriter.addJS_cr("    function easeInOut(t, b, c, d) {");
		pWriter.addJS_cr("        t /= d/2;");
		pWriter.addJS_cr("        if (t < 1) return c/2*t*t + b;");
		pWriter.addJS_cr("        t--;");
		pWriter.addJS_cr("        return -c/2 * (t*(t-2)-1) + b;");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr();
		pWriter.addJS_cr("    function animateScroll() {");
		pWriter.addJS_cr("        currentTime += increment;");
		pWriter.addJS_cr("        element.scrollTop = parseInt(easeInOut(currentTime, start, change, duration));");
		pWriter.addJS_cr("        if(currentTime < duration) {");
		pWriter.addJS_cr("            setTimeout(animateScroll, increment);");
		pWriter.addJS_cr("        }");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr();
		pWriter.addJS_cr("    animateScroll();");
		pWriter.addJS_cr();
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		// Listener mouseover on the slider resulting in scrolling to the corresponding
		// element
		pWriter.addJS_cr();
		pWriter.addJS_cr("document.querySelectorAll('#" + IndexUserServicePane.INDEX_SLIDER_DIV_ID + " a').forEach(element => {");
		pWriter.addJS_cr("    element.addEventListener('mouseover', throttle(event => {");
		pWriter.addJS_cr("        const href = event.target.getAttribute('href');");
		pWriter.addJS_cr("        const targetElement = document.querySelector('#" + IndexUserServicePane.INDEX_CONTENT_DIV_ID + " ' + href);");
		pWriter.addJS_cr("        scrollTo(targetElement);");
		pWriter.addJS_cr("    }, 2000));");
		pWriter.addJS_cr("});");
		pWriter.addJS_cr();

		// Part handling the contextual color of the slider and/or the index title
		if (this.colorIndexTitle || this.colorIndexSlider) {
			pWriter.addJS_cr();
			pWriter.addJS_cr("const indexContentDiv = document.getElementById('" + IndexUserServicePane.INDEX_CONTENT_DIV_ID + "');");
			pWriter.addJS_cr("if (indexContentDiv) {");
			pWriter.addJS_cr("    indexContentDiv.addEventListener('scroll', throttle(event => {");
			pWriter.addJS_cr("        const scrollPosition = indexContentDiv.scrollTop;");
			pWriter.addJS_cr("        document.querySelectorAll('#" + IndexUserServicePane.INDEX_CONTENT_DIV_ID + " ." + IndexUserServicePane.INDEX_CHARACTER_CONTAINER_CLASS
					+ "').forEach(indexContainerElem => {");
			pWriter.addJS_cr("            const indexContainerPosition = indexContainerElem.offsetTop;");
			pWriter.addJS_cr("            const indexContainerHeight = indexContainerElem.offsetHeight;");
			pWriter.addJS_cr("            const indexTitleElem = indexContainerElem.querySelector('h2');");

			if (this.colorIndexSlider) {
				pWriter.addJS_cr("            const idIndex = '#' + indexTitleElem.id;");
				pWriter.addJS_cr("            let indexSliderElement = null;");
				pWriter.addJS_cr("            document.querySelectorAll('#" + IndexUserServicePane.INDEX_SLIDER_DIV_ID + " a').forEach(sliderItemElement => {");
				pWriter.addJS_cr("                const href = sliderItemElement.getAttribute('href');");
				pWriter.addJS_cr("                if (idIndex === href) {");
				pWriter.addJS_cr("                    indexSliderElement = sliderItemElement.parentElement;");
				pWriter.addJS_cr("                }");
				pWriter.addJS_cr("            });");
			}

			pWriter.addJS_cr("            if((scrollPosition > indexContainerPosition - 30) && (scrollPosition < (indexContainerPosition + indexContainerHeight))) {");

			if (this.colorIndexSlider) {
				pWriter.addJS_cr("                if (indexSliderElement) indexSliderElement.classList.add('" + UICSSClasses.COLORED_BACKGROUND + "');");
			}
			if (this.colorIndexTitle) {
				pWriter.addJS_cr("                indexTitleElem.classList.add('" + UICSSClasses.COLORED_BACKGROUND + "');");
			}

			pWriter.addJS_cr("            } else {");

			if (this.colorIndexSlider) {
				pWriter.addJS_cr("                if (indexSliderElement) indexSliderElement.classList.remove('" + UICSSClasses.COLORED_BACKGROUND + "');");
			}
			if (this.colorIndexTitle) {
				pWriter.addJS_cr("                indexTitleElem.classList.remove('" + UICSSClasses.COLORED_BACKGROUND + "');");
			}

			pWriter.addJS_cr("            }");
			pWriter.addJS_cr("        });");
			pWriter.addJS_cr("    }, 100));");
			pWriter.addJS_cr("}");
			pWriter.addJS_cr();
		}
	}
}
