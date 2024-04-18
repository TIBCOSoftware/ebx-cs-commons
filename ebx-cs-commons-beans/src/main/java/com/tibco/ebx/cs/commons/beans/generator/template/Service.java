package com.tibco.ebx.cs.commons.beans.generator.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Procedure;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ProcedureResult;
import com.orchestranetworks.service.ProgrammaticService;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.beans.generator.exception.BeansTechnicalException;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;

/**
 * Set of operations on a given EBX table.
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 * 
 * @param <B> TableBean type
 * @param <D> TableDAO type
 * @param <T> TableDTO Type
 */
public abstract class Service<B extends TableBean, D extends TableDAO<B>, T extends TableDTO> {

	private static final String NOT_DTO_RETURNED_FROM_SUCCESSFUL_PROCEDURE = "Not DTO returned from successful procedure.";

	/**
	 * Create a table occurrence in EBX settings values from a {@link TableDTO}
	 *
	 * @param pDTO     The object transferring values to be set for creation.
	 * @param pSession An EBX user {@link Session}.
	 * @param pDataset The dataset in which the table occurrence must be created.
	 * @throws BeansTechnicalException BeansTechnicalException
	 * @return TableDTO created
	 */
	public T create(final T pDTO, final Session pSession, final Adaptation pDataset) throws BeansTechnicalException {
		ProgrammaticService srv = ProgrammaticService.createForSession(pSession, pDataset.getHome());
		final List<B> bean = new ArrayList<>();
		ProcedureResult result = srv.execute(new Procedure() {
			@Override
			public void execute(final ProcedureContext pContext) throws Exception {
				bean.add(Service.this.getDAO().create(pContext, pDataset, Service.this.getMapper().getBean(pDataset, pDTO, Optional.of(pSession.getPermissions())),
						Optional.of(pSession.getPermissions())));
			}
		});
		if (result.hasFailed()) {
			throw new BeansTechnicalException(result.getException());
		} else {
			return this.createDTO(bean.get(0));
		}
	}

	/**
	 * Create a collection of table occurrences in EBX settings values from instances of a {@link TableDTO}
	 *
	 * @param pDTOs    The list of objects transferring values to be set for creation.
	 * @param pSession An EBX user {@link Session}.
	 * @param pDataset The dataset in which the table occurrences must be created.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public void create(final List<T> pDTOs, final Session pSession, final Adaptation pDataset) throws BeansTechnicalException {
		ProgrammaticService srv = ProgrammaticService.createForSession(pSession, pDataset.getHome());
		ProcedureResult result = srv.execute(new Procedure() {
			@Override
			public void execute(final ProcedureContext pContext) throws Exception {
				for (T dto : pDTOs) {
					Service.this.getDAO().create(pContext, pDataset, Service.this.getMapper().getBean(pDataset, dto, Optional.of(pSession.getPermissions())), Optional.of(pSession.getPermissions()));
				}
			}
		});
		if (result.hasFailed()) {
			throw new BeansTechnicalException(result.getException());
		}
	}

	/**
	 * Retrieve a table occurrence from EBX, given its primary key as a String.
	 *
	 * @param pPK      The string representation of an EBX Primary Key discriminating the record to retrieve.
	 * @param pSession An EBX user {@link Session}.
	 * @param pDataset The dataset in which the table occurrences must be created.
	 *
	 * @return {@link TableDTO}
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public Optional<T> read(final String pPK, final Session pSession, final Adaptation pDataset) throws BeansTechnicalException {
		Optional<B> bean = this.getDAO().read(pPK, pDataset, Optional.of(pSession.getPermissions()));
		if (!bean.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(this.createDTO(bean.get()));
	}

	T createDTO(final B pBean) throws BeansTechnicalException {
		if (pBean == null) {
			return null;
		}
		try {
			return this.getMapper().getDTO(pBean);
		} catch (EBXCommonsException ex) {
			throw new BeansTechnicalException(ex);
		}
	}

	/**
	 * Retrieve a collection of table occurrences from EBX given an XPath predicate.
	 *
	 * @param pSession   An EBX user {@link Session}.
	 * @param pDataset   The dataset in which the table occurrences must be created.
	 * @param pPredicate An XPath predicate matched by occurrences to be returned
	 *
	 * @return a list of {@link TableDTO}.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public List<T> read(final Session pSession, final Adaptation pDataset, final String pPredicate) throws BeansTechnicalException {
		List<T> list = new ArrayList<>();
		for (B bean : this.getDAO().readAll(pDataset, pPredicate, Optional.of(pSession.getPermissions()))) {
			list.add(this.createDTO(bean));
		}
		return list;
	}

	/**
	 * Retrieve a page of a collection of table occurrences from EBX given an XPath predicate.
	 *
	 * @param pSession   An EBX user {@link Session}.
	 * @param pDataset   The dataset in which the table occurrences must be created.
	 * @param pPredicate An XPath predicate matched by occurrences to be returned
	 * @param pLimit     Defines the size of a page and thus the maximum number of returned occurrences.
	 * @param pOffset    Defines the position of the page within the collection and thus the first occurrence to be returned.
	 *
	 * @return a list of {@link TableDTO}.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public List<T> read(final Session pSession, final Adaptation pDataset, final String pPredicate, final Integer pLimit, final Integer pOffset) throws BeansTechnicalException {
		List<T> list = new ArrayList<>();
		for (B bean : this.getDAO().readAll(pDataset, pPredicate, pLimit, pOffset, Optional.of(pSession.getPermissions()))) {
			list.add(this.createDTO(bean));
		}
		return list;
	}

	/**
	 * Update a table occurrence in EBX given its primary key as a String.
	 *
	 * @param pPK      The string representation of an EBX Primary Key discriminating the record to retrieve.
	 * @param pDTO     The object transferring values to be set for update.
	 * @param pSession An EBX user {@link Session}.
	 * @param pDataset The dataset in which the table occurrences must be created.
	 *
	 * @return the updated occurrence as data transfer object.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public T update(final String pPK, final T pDTO, final Session pSession, final Adaptation pDataset) throws BeansTechnicalException {
		ProgrammaticService srv = ProgrammaticService.createForSession(pSession, pDataset.getHome());
		final List<T> foundDTOs = new ArrayList<>();
		Procedure proc = new Procedure() {
			@Override
			public void execute(final ProcedureContext pContext) throws Exception {
				Optional<B> bean = Service.this.getDAO().read(pPK, pDataset, Optional.of(pSession.getPermissions()));
				B newBean = Service.this.getMapper().getBean(pDataset, pDTO, Optional.of(pSession.getPermissions()));
				if (!pPK.equals(Service.this.getDAO().getRecordPrimaryKey(newBean))) {
					throw OperationException.createError("DTO does not correspond to record to update");
				}
				if (bean.isPresent()) {
					Service.this.getDAO().update(pContext, pDataset, newBean, Optional.of(pSession.getPermissions()));
					foundDTOs.add(pDTO);
				}
			}
		};
		ProcedureResult result = srv.execute(proc);
		if (result.hasFailed()) {
			throw new BeansTechnicalException(result.getException());
		}
		if (foundDTOs.isEmpty()) {
			throw new BeansTechnicalException(NOT_DTO_RETURNED_FROM_SUCCESSFUL_PROCEDURE);
		}
		return foundDTOs.get(0);
	}

	/**
	 * Update a table occurrences in EBX, finding it based on values in a {@link TableDTO}.
	 *
	 * @param pDTO     The object transferring values to be set for update.
	 * @param pSession An EBX user {@link Session}.
	 * @param pDataset The dataset in which the table occurrences must be updated.
	 *
	 * @return The updated table occurrences as data transfer object.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public T update(final T pDTO, final Session pSession, final Adaptation pDataset) throws BeansTechnicalException {
		List<T> dtos = new ArrayList<>();
		ProgrammaticService srv = ProgrammaticService.createForSession(pSession, pDataset.getHome());
		ProcedureResult result = srv.execute(new Procedure() {
			@Override
			public void execute(final ProcedureContext pContext) throws Exception {
				B bean = Service.this.getDAO().update(pContext, pDataset, Service.this.getMapper().getBean(pDataset, pDTO, Optional.of(pSession.getPermissions())),
						Optional.of(pSession.getPermissions()));
				dtos.add(Service.this.getMapper().getDTO(bean));
			}
		});
		if (result.hasFailed()) {
			throw new BeansTechnicalException(result.getException());
		}
		if (dtos.isEmpty()) {
			throw new BeansTechnicalException(NOT_DTO_RETURNED_FROM_SUCCESSFUL_PROCEDURE);
		}
		return dtos.get(0);
	}

	/**
	 * Update a collection of table occurrences in EBX, finding them based on values in instances of {@link TableDTO}.
	 *
	 * @param pDTOs    The collection of objects transferring values to be set for update.
	 * @param pSession An EBX user {@link Session}.
	 * @param pDataset The dataset in which the table occurrences must be updated.
	 *
	 * @return The list of created and/or updated table occurrences as data transfer objects.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public List<T> update(final List<T> pDTOs, final Session pSession, final Adaptation pDataset) throws BeansTechnicalException {
		List<T> dtos = new ArrayList<>();
		ProgrammaticService srv = ProgrammaticService.createForSession(pSession, pDataset.getHome());
		ProcedureResult result = srv.execute(new Procedure() {
			@Override
			public void execute(final ProcedureContext pContext) throws Exception {
				for (T dto : pDTOs) {
					B bean = Service.this.getDAO().update(pContext, pDataset, Service.this.getMapper().getBean(pDataset, dto, Optional.of(pSession.getPermissions())),
							Optional.of(pSession.getPermissions()));
					dtos.add(Service.this.getMapper().getDTO(bean));
				}
			}
		});
		if (result.hasFailed()) {
			throw new BeansTechnicalException(result.getException());
		}
		return dtos;
	}

	/**
	 * Update a table occurrence in EBX, finding it based on values in a {@link TableDTO} and create it if it does not exist.
	 *
	 * @param pDTO     The object transferring values to be set for create or update
	 * @param pSession An EBX user {@link Session}.
	 * @param pDataset The dataset in which the table occurrences must be created or updated.
	 *
	 * @return The created or updated table occurrences as data transfer object.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public T createOrUpdate(final T pDTO, final Session pSession, final Adaptation pDataset) throws BeansTechnicalException {
		ProgrammaticService srv = ProgrammaticService.createForSession(pSession, pDataset.getHome());
		List<T> dtos = new ArrayList<>();
		ProcedureResult result = srv.execute(new Procedure() {
			@Override
			public void execute(final ProcedureContext pContext) throws Exception {
				B bean = Service.this.getDAO().createOrUpdate(pContext, pDataset, Service.this.getMapper().getBean(pDataset, pDTO, Optional.of(pSession.getPermissions())),
						Optional.of(pSession.getPermissions()));
				dtos.add(Service.this.getMapper().getDTO(bean));
			}
		});
		if (result.hasFailed()) {
			throw new BeansTechnicalException(result.getException());
		}
		if (dtos.isEmpty()) {
			throw new BeansTechnicalException(NOT_DTO_RETURNED_FROM_SUCCESSFUL_PROCEDURE);
		}
		return dtos.get(0);
	}

	/**
	 * Update a collection of table occurrences in EBX, finding them based on values in instance of {@link TableDTO} and create not found occurrences.
	 *
	 * @param pDTOS    The object transferring values to be set for create or update.
	 * @param pSession An EBX user {@link Session}.
	 * @param pDataset The dataset in which the table occurrences must be created or updated.
	 *
	 * @return The list of created and/or updated table occurrences as data transfer objects.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public List<T> createOrUpdate(final List<T> pDTOS, final Session pSession, final Adaptation pDataset) throws BeansTechnicalException {
		List<T> dtos = new ArrayList<>();
		for (T dto : pDTOS) {
			dtos.add(this.createOrUpdate(dto, pSession, pDataset));
		}
		return dtos;
	}

	/**
	 * Delete table occurrences in EBX, finding it based on values in instance of {@link TableDTO}.
	 *
	 * @param pDTO     The data transfer object representing the table occurrence to delete.
	 * @param pSession An EBX user {@link Session}.
	 * @param pDataset The dataset from which the table occurrences must be removed.
	 *
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public void delete(final T pDTO, final Session pSession, final Adaptation pDataset) throws BeansTechnicalException {
		final D dao = this.getDAO();
		ProgrammaticService srv = ProgrammaticService.createForSession(pSession, pDataset.getHome());
		ProcedureResult result = srv.execute(new Procedure() {
			@Override
			public void execute(final ProcedureContext pContext) throws Exception {
				dao.delete(pContext, pDataset, Service.this.getMapper().getBean(pDataset, pDTO, Optional.of(pSession.getPermissions())));
			}
		});
		if (result.hasFailed()) {
			throw new BeansTechnicalException(result.getException());
		}
	}

	/**
	 * Delete a table occurrence in EBX given its primary key as a String.
	 *
	 * @param pPK      The string representation of an EBX Primary Key discriminating the record to delete.
	 * @param pSession An EBX user {@link Session}.
	 * @param pDataset The dataset from which the table occurrences must be deleted.
	 *
	 * @return true if a record has been found and then updated.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public void delete(final String pPK, final Session pSession, final Adaptation pDataset) throws BeansTechnicalException {
		final D dao = this.getDAO();
		ProgrammaticService srv = ProgrammaticService.createForSession(pSession, pDataset.getHome());
		ProcedureResult result = srv.execute(new Procedure() {
			@Override
			public void execute(final ProcedureContext pContext) throws Exception {
				Optional<B> bean = dao.read(pPK, pDataset, Optional.of(pSession.getPermissions()));
				if (bean.isPresent()) {
					dao.delete(pContext, pDataset, bean.get());
				} else {
					throw OperationException.createError("No record found for primary key \"" + pPK + "\"");
				}
			}
		});
		if (result.hasFailed()) {
			throw new BeansTechnicalException(result.getException());
		}
	}

	protected abstract D getDAO();

	protected abstract BeanToDTOMapper<B, T> getMapper();

	protected abstract Class<B> getBeanClass();

	protected abstract Class<T> getDTOClass();
}
