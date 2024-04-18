package com.tibco.ebx.cs.commons.beans.generator.template;

import java.util.ArrayList;
import java.util.List;

import com.orchestranetworks.addon.dama.models.MediaType;

/**
 * Bean to DTO mapper for MediaType<br>
 *
 * @author Fabien Bontemps
 * @since 2.1.0
 *
 */
public class MediaTypeToMediaTypeDTOMapper {
	private static final MediaTypeToMediaTypeDTOMapper instance = new MediaTypeToMediaTypeDTOMapper();

	public static MediaTypeToMediaTypeDTOMapper getInstance() {
		return instance;
	}

	/**
	 * Get a MediaType bean from a MediaTypeDTO
	 * 
	 * @param pDTO MediaTypeDTO
	 * @return MediaType
	 */
	public MediaType getBean(final MediaTypeDTO pDTO) {
		MediaType mediaType = null;
		if (pDTO != null) {
			mediaType = new MediaType();
			mediaType.setAttachment(pDTO.getAttachment());
		}
		return mediaType;
	}

	/**
	 * Get a MediaType bean from a MediaTypeDTO
	 * 
	 * @param pDTOs List of MediaTypeDTO
	 * @return List of MediaType
	 */
	public List<MediaType> getBean(final List<MediaTypeDTO> pDTOs) {
		List<MediaType> beans = new ArrayList<>();
		if (pDTOs != null) {
			for (MediaTypeDTO dto : pDTOs) {
				beans.add(getBean(dto));
			}
		}
		return beans;
	}

	/**
	 * Get a MediaTypeDTO bean from a MediaType
	 * 
	 * @param pBean MediaType
	 * @return MediaTypeDTO
	 */
	public MediaTypeDTO getDTO(final MediaType pBean) {
		MediaTypeDTO mediaTypeDTO = null;
		if (pBean != null) {
			mediaTypeDTO = new MediaTypeDTO();
			mediaTypeDTO.setAttachment(pBean.getAttachment());
		}
		return mediaTypeDTO;
	}

	/**
	 * Get a MediaTypeDTO bean from a MediaType
	 * 
	 * @param pBeans List of MediaType
	 * @return List of MediaTypeDTO
	 */
	public List<MediaTypeDTO> getDTO(final List<MediaType> pBeans) {
		List<MediaTypeDTO> dtos = new ArrayList<>();
		if (pBeans != null) {
			for (MediaType bean : pBeans) {
				dtos.add(getDTO(bean));
			}
		}
		return dtos;
	}
}
