package org.alienlabs.hatchetharry.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "ChatMessage", indexes = { @Index(columnList = "gameId") })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ChatMessage extends ParentMessage
{
	private static final long serialVersionUID = 1L;

}
