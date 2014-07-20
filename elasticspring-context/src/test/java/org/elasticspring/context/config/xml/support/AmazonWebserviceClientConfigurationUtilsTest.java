/*
 * Copyright 2013-2014 the original author or authors.
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

package org.elasticspring.context.config.xml.support;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.elasticspring.config.AmazonWebserviceClientConfigurationUtils;
import org.elasticspring.core.credentials.credentials.CredentialsProviderFactoryBean;
import org.elasticspring.core.region.StaticRegionProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Agim Emruli
 */
public class AmazonWebserviceClientConfigurationUtilsTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void registerAmazonWebserviceClient_withMinimalConfiguration_returnsDefaultBeanDefinition() throws Exception {
		//Arrange
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.registerSingleton(CredentialsProviderFactoryBean.CREDENTIALS_PROVIDER_BEAN_NAME, new StaticAwsCredentialsProvider());


		BeanDefinitionHolder beanDefinitionHolder = AmazonWebserviceClientConfigurationUtils.
				registerAmazonWebserviceClient(new Object(), beanFactory, AmazonTestWebserviceClient.class.getName(), null, null);

		//Act
		beanFactory.preInstantiateSingletons();
		AmazonTestWebserviceClient client = beanFactory.getBean(beanDefinitionHolder.getBeanName(), AmazonTestWebserviceClient.class);

		//Assert
		assertNotNull(client);
		assertEquals("amazonTestWebservice", beanDefinitionHolder.getBeanName());
		assertEquals(Region.getRegion(Regions.DEFAULT_REGION), client.getRegion());
	}

	@Test
	public void registerAmazonWebserviceClient_withCustomRegionProviderConfiguration_returnsBeanDefinitionWithRegionConfiguredThatIsReturnedByTheRegionProvider() throws Exception {
		//Arrange
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.registerSingleton(CredentialsProviderFactoryBean.CREDENTIALS_PROVIDER_BEAN_NAME, new StaticAwsCredentialsProvider());
		beanFactory.registerSingleton("myRegionProvider", new StaticRegionProvider(Regions.AP_SOUTHEAST_2));

		BeanDefinitionHolder beanDefinitionHolder = AmazonWebserviceClientConfigurationUtils.
				registerAmazonWebserviceClient(new Object(), beanFactory, AmazonTestWebserviceClient.class.getName(), "myRegionProvider", null);

		//Act
		beanFactory.preInstantiateSingletons();
		AmazonTestWebserviceClient client = beanFactory.getBean(beanDefinitionHolder.getBeanName(), AmazonTestWebserviceClient.class);

		//Assert
		assertNotNull(client);
		assertEquals("amazonTestWebservice", beanDefinitionHolder.getBeanName());
		assertEquals(Region.getRegion(Regions.AP_SOUTHEAST_2), client.getRegion());
	}

	@Test
	public void registerAmazonWebserviceClient_withCustomRegionConfiguration_returnsBeanDefinitionWithRegionConfigured() throws Exception {
		//Arrange
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.registerSingleton(CredentialsProviderFactoryBean.CREDENTIALS_PROVIDER_BEAN_NAME, new StaticAwsCredentialsProvider());


		BeanDefinitionHolder beanDefinitionHolder = AmazonWebserviceClientConfigurationUtils.
				registerAmazonWebserviceClient(new Object(), beanFactory, AmazonTestWebserviceClient.class.getName(), null, "EU_WEST_1");

		//Act
		beanFactory.preInstantiateSingletons();
		AmazonTestWebserviceClient client = beanFactory.getBean(beanDefinitionHolder.getBeanName(), AmazonTestWebserviceClient.class);

		//Assert
		assertNotNull(client);
		assertEquals("amazonTestWebservice", beanDefinitionHolder.getBeanName());
		assertEquals(Region.getRegion(Regions.EU_WEST_1), client.getRegion());
	}

	@Test
	public void registerAmazonWebserviceClient_withCustomRegionAndRegionProviderConfigured_reportsError() throws Exception {
		//Arrange
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("Only region or regionProvider can be configured, but not both");

		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.registerSingleton(CredentialsProviderFactoryBean.CREDENTIALS_PROVIDER_BEAN_NAME, new StaticAwsCredentialsProvider());

		BeanDefinitionHolder beanDefinitionHolder = AmazonWebserviceClientConfigurationUtils.
				registerAmazonWebserviceClient(new Object(), beanFactory, AmazonTestWebserviceClient.class.getName(), "someProvider", "EU_WEST_1");

		//Act
		beanFactory.getBean(beanDefinitionHolder.getBeanName(), AmazonTestWebserviceClient.class);

		//Assert
	}


	private static class StaticAwsCredentialsProvider implements AWSCredentialsProvider {

		@Override
		public AWSCredentials getCredentials() {
			return new BasicAWSCredentials("test", "secret");
		}

		@Override
		public void refresh() {
		}
	}
}