/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.alienlabs.hatchetharry.persistence.dao;

import java.util.ArrayList;
import java.util.List;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.StandardBasicTypes;

/**
 * @author Kare Nuorteva
 */
public class MagicCardFinderQueryBuilder
{
	private List<String> parameters;
	private List<AbstractSingleColumnStandardBasicType<?>> types;
	private boolean count;
	private MagicCard filter = new MagicCard();
	private QueryParam queryParam;

	public String buildHql()
	{
		this.parameters = new ArrayList<String>();
		this.types = new ArrayList<AbstractSingleColumnStandardBasicType<?>>();
		final StringBuilder hql = new StringBuilder();
		this.addCountClause(hql);
		hql.append("from MagicCard target where 1=1 ");
		this.addMatchingCondition(hql, this.filter.getUuidObject().toString(), "uuid");
		this.addMatchingCondition(hql, Long.toString(this.filter.getGameId()), "gameId");
		this.addOrderByClause(hql);
		return hql.toString();
	}

	private void addCountClause(final StringBuilder hql)
	{
		if (this.count)
		{
			hql.append("select count(*) ");
		}
	}

	private void addMatchingCondition(final StringBuilder hql, final String value, final String name)
	{
		if (value != null)
		{
			hql.append("and target.");
			hql.append(name);
			hql.append(" = (?)");
			this.parameters.add("%" + value.toUpperCase() + "%");
			this.types.add(StandardBasicTypes.STRING);
		}
	}

	private void addOrderByClause(final StringBuilder hql)
	{
		if (!this.count && (this.queryParam != null) && this.queryParam.hasSort())
		{
			hql.append("order by upper(target.");
			hql.append(this.queryParam.getSort());
			hql.append(") ");
			hql.append(this.queryParam.isSortAsc() ? "asc" : "desc");
		}
	}

	public void setQueryParam(final QueryParam _queryParam)
	{
		this.queryParam = _queryParam;
	}

	public void setFilter(final MagicCard _filter)
	{
		if (_filter == null)
		{
			throw new IllegalArgumentException("Null value not allowed.");
		}
		this.filter = _filter;
	}

	public void setCount(final boolean _count)
	{
		this.count = _count;
	}

	public String[] getParameters()
	{
		return this.parameters.toArray(new String[0]);
	}

	public AbstractSingleColumnStandardBasicType<?>[] getTypes()
	{
		return this.types.toArray(new AbstractSingleColumnStandardBasicType[this.types.size()]);
	}
}
