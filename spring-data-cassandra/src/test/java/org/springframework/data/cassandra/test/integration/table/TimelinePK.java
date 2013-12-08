/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.cassandra.test.integration.table;

import java.util.Date;

import org.springframework.data.cassandra.mapping.Clustered;
import org.springframework.data.cassandra.mapping.CompositePrimaryKey;
import org.springframework.data.cassandra.mapping.Partitioned;

/**
 * This is an example of the users timeline dynamic table, where all columns are dynamically created by @ColumnId field
 * value. The rest fields are places in Cassandra value.
 * 
 * Timeline entity is used to store user's status updates that it follows in the site. Timeline always ordered by @ColumnId
 * field and we can retrieve last top status updates by using limits.
 * 
 * @author Alex Shvid
 */

@CompositePrimaryKey
public class TimelinePK {

	/*
	 * Row ID
	 */
	@Partitioned(ordinal = 1)
	private String username;

	/*
	 * Clustered Column
	 */
	@Clustered(ordinal = 1)
	private Date time;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
