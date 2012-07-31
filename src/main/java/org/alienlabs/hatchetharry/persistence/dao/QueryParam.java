/*
 * $Id: QueryParam.java 371 2005-10-13 01:21:29 -0700 (Thu, 13 Oct 2005) gwynevans $
 * $Revision: 371 $
 * $Date: 2005-10-13 01:21:29 -0700 (Thu, 13 Oct 2005) $
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

import java.io.Serializable;

/**
 * Encapsulates the Query Paramaters to be passed to {@link MagicCardDao#find}
 * method.
 * 
 * @author igor
 */
public class QueryParam implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int first;
	private int count;
	private String sort;
	private boolean sortAsc;

	/**
	 * Set to return <tt>count</tt> elements, starting at the <tt>first</tt>
	 * element.
	 * 
	 * @param first
	 *            First element to return.
	 * @param count
	 *            Number of elements to return.
	 */
	public QueryParam(final int _first, final int _count)
	{
		this(_first, _count, null, true);
	}

	/**
	 * Set to return <tt>count</tt> sorted elements, starting at the
	 * <tt>first</tt> element.
	 * 
	 * @param first
	 *            First element to return.
	 * @param count
	 *            Number of elements to return.
	 * @param sort
	 *            Column to sort on.
	 * @param sortAsc
	 *            Sort ascending or descending.
	 */
	public QueryParam(final int _first, final int _count, final String _sort, final boolean _sortAsc)
	{
		this.first = _first;
		this.count = _count;
		this.sort = _sort;
		this.sortAsc = _sortAsc;
	}

	public void setSort(final String _sort)
	{
		this.sort = _sort;
	}

	public void setSortAsc(final boolean _sortAsc)
	{
		this.sortAsc = _sortAsc;
	}

	public int getCount()
	{
		return this.count;
	}

	public int getFirst()
	{
		return this.first;
	}

	public String getSort()
	{
		return this.sort;
	}

	public boolean isSortAsc()
	{
		return this.sortAsc;
	}

	public boolean hasSort()
	{
		return this.sort != null;
	}

	public void setFirst(final int _first)
	{
		this.first = _first;
	}

	public void setCount(final int _count)
	{
		this.count = _count;
	}
}
