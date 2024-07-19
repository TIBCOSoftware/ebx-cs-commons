/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.generator.template;

import java.util.Optional;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.service.SessionPermissions;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;

/**
 * Bean to DTO mapper abstract class<br>
 *
 * @author Mickaël Chevalier
 * @since 1.1.6
 *
 * @param <B> TableBean type
 * @param <T> DTO type
 */
public abstract class BeanToDTOMapper<B extends Object, T extends Object> {

	protected abstract B getBean(Adaptation pDataset, T pDTO, Optional<SessionPermissions> pPermissions) throws EBXCommonsException;

	protected abstract T getDTO(B pBean) throws EBXCommonsException;
}
