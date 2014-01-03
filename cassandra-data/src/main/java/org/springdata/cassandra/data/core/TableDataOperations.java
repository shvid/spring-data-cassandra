/*
 * Copyright 2014 the original author or authors.
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
package org.springdata.cassandra.data.core;

import org.springdata.cassandra.base.core.query.ExecuteOptions;

/**
 * TableDataOperations interface
 * 
 * @author Alex Shvid
 * 
 */
public interface TableDataOperations {

	/**
	 * Create a table with the name given and fields corresponding to the given class. If the table already exists and
	 * parameter <code>ifNotExists</code> is {@literal true}, this is a no-op and {@literal false} is returned. If the
	 * table doesn't exist, parameter <code>ifNotExists</code> is ignored, the table is created and {@literal true} is
	 * returned.
	 * 
	 * @param ifNotExists If true, will only create the table if it doesn't exist, else the create operation will be
	 *          ignored and the method will return {@literal false}.
	 * @param entityClass The class whose fields determine the columns created.
	 * @param optionsOrNull The Execute Options Object if exists
	 * @return Returns true if a table was created, false if not.
	 */
	boolean createTable(boolean ifNotExists, Class<?> entityClass, ExecuteOptions optionsOrNull);

	/**
	 * Add columns to the given table from the given class. If parameter dropRemovedAttributColumns is true, then this
	 * effectively becomes a synchronization operation between the class's fields and the existing table's columns.
	 * 
	 * @param entityClass The class whose fields determine the columns added.
	 * @param dropRemovedAttributeColumns Whether to drop columns that exist on the table but that don't have
	 *          corresponding fields in the class. If true, this effectively becomes a synchronziation operation.
	 * @param optionsOrNull The Execute Options Object if exists
	 */
	void alterTable(Class<?> entityClass, boolean dropRemovedAttributeColumns, ExecuteOptions optionsOrNull);

	/**
	 * Validate columns in the given table from the given class.
	 * 
	 * @param entityClass The class whose fields determine the columns added.
	 * @return Returns alter table statement or null
	 */
	String validateTable(Class<?> entityClass);

	/**
	 * Drops the named table.
	 * 
	 * @param optionsOrNull The Execute Options Object if exists
	 * 
	 */
	void dropTable(ExecuteOptions optionsOrNull);

}
