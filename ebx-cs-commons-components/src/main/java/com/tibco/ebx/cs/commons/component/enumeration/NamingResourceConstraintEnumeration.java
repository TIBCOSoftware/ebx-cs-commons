package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;

/**
 * Enumerates resources in a JNDI context for technical configuration
 * 
 * @author Mickaël Chevalier
 *
 */
public class NamingResourceConstraintEnumeration implements ConstraintEnumeration<String> {

	private String prefix = "java:comp/env";
	private int maxDepth = 10;
	private String type;
	private Class<?> clazz;

	/**
	 * Sets the name prefix of the resources to enumerate. Default value is <tt>java:comp/env</tt>
	 *
	 * @param prefix the prefix value
	 */
	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Sets the maximum depth for recursive lookup in contexts
	 *
	 * @param maxDepth the maximum depth value
	 */
	public void setMaxDepth(final int maxDepth) {
		this.maxDepth = maxDepth;
	}

	/**
	 * Sets the optional allowed resource type. For instance <tt>javax.sql.DataSource</tt>
	 *
	 * @param type the resource type
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Returns the {@link InitialContext} to list and lookup resources
	 */
	protected InitialContext getContext() throws NamingException {
		return new InitialContext();
	}

	@Override
	public void setup(final ConstraintContext aContext) {
		try {
			if (!(this.getContext().lookup(this.prefix) instanceof Context)) {
				aContext.addWarning("No context at " + this.prefix);
			}
		} catch (NamingException ex) {
			aContext.addWarning(ex.toString());
		}
		if (this.type != null) {
			try {
				this.clazz = this.getType(this.type);
			} catch (ClassNotFoundException e) {
				aContext.addError(e.getLocalizedMessage());
			}
		}
	}

	private Class<?> getType(final String type) throws ClassNotFoundException {
		return Thread.currentThread().getContextClassLoader().loadClass(type);
	}

	private boolean isInstanceOfType(final Class<?> type, final NameClassPair pair) {
		try {
			return type == null || type.isAssignableFrom(this.getType(pair.getClassName()));
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	public void checkOccurrence(final String aValue, final ValueContextForValidation aValidationContext) throws InvalidSchemaException {
		try {
			this.clazz.cast(this.getContext().lookup(aValue));
		} catch (NamingException | ClassCastException ex) {
			aValidationContext.addError(ex.getMessage());
		}
	}

	@Override
	public List<String> getValues(final ValueContext aContext) throws InvalidSchemaException {
		try {
			List<String> values = new ArrayList<>();
			this.collect(1, this.prefix, this.getContext().listBindings(this.prefix), values);
			return values;
		} catch (NamingException ex) {
			return Collections.emptyList();
		}
	}

	private void collect(final int depth, final String localPrefix, final NamingEnumeration<Binding> enumeration, final List<String> values) throws NamingException {
		if (depth > this.maxDepth) {
			return;
		}
		for (Binding binding : Collections.list(enumeration)) {
			if (this.isInstanceOfType(Context.class, binding)) {
				this.collect(depth + 1, localPrefix + "/" + binding.getName(), ((Context) binding.getObject()).listBindings(""), values);
			}
			if (this.isInstanceOfType(this.clazz, binding)) {
				values.add(localPrefix + "/" + binding.getName());
			}
		}
	}

	@Override
	public String displayOccurrence(final String aValue, final ValueContext aContext, final Locale aLocale) throws InvalidSchemaException {
		return aValue;
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		if (this.clazz != null || !StringUtils.isBlank(this.prefix)) {
			return (this.clazz != null ? this.clazz.getName() : "") + (this.prefix != null && !this.prefix.isEmpty() ? " in " + this.prefix : "");
		}
		return null;
	}

}
