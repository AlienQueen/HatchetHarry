package org.alienlabs.hatchetharry.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ConsoleLogMessage", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"gameId", "message" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ConsoleLogMessage extends ParentMessage
{
	private static final long serialVersionUID = 1L;

}
