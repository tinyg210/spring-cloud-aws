/*
 * Copyright 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasticspring.jdbc.retry;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.retry.context.RetryContextSupport;

import java.sql.SQLException;
import java.sql.SQLTransientException;

/**
 *
 */
public class SqlRetryPolicyTest {

	@Test
	public void testRetryTransientExceptions() throws Exception {
		SqlRetryPolicy sqlRetryPolicy = new SqlRetryPolicy();
		RetryContextSupport retryContext = new RetryContextSupport(null);

		retryContext.registerThrowable(new SQLTransientException("foo"));
		Assert.assertTrue(sqlRetryPolicy.canRetry(retryContext));

		retryContext.registerThrowable(new TransientDataAccessResourceException("foo"));
		Assert.assertTrue(sqlRetryPolicy.canRetry(retryContext));
	}

	@Test
	public void testNoRetryPersistentExceptions() throws Exception {
		SqlRetryPolicy sqlRetryPolicy = new SqlRetryPolicy();
		RetryContextSupport retryContext = new RetryContextSupport(null);

		retryContext.registerThrowable(new SQLException("foo"));
		Assert.assertFalse(sqlRetryPolicy.canRetry(retryContext));

		retryContext.registerThrowable(new DataAccessResourceFailureException("foo"));
		Assert.assertFalse(sqlRetryPolicy.canRetry(retryContext));
	}
}