/*  Copyright 2017 SolarNetwork Foundation
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.solarnetwork.billing.killbill.invoice;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.killbill.billing.ObjectType;
import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatter;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory;
import org.killbill.billing.util.customfield.dao.CustomFieldDao;
import org.killbill.billing.util.customfield.dao.CustomFieldModelDao;
import org.killbill.billing.util.template.translation.TranslatorConfig;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test cases for the {@link SolarNetworkInvoiceFormatterFactory} class.
 * 
 * @author matt
 */
@RunWith(MockitoJUnitRunner.class)
public class SolarNetworkInvoiceFormatterFactoryTests {

  private static final String ACCOUNT_FIELD = "accField";
  private static final String SUBSCRIPTION_FIELD = "subField";

  @Mock
  private TranslatorConfig config;

  @Mock
  Invoice invoice;

  @Mock
  CurrencyConversionApi currencyConversionApi;

  @Mock
  ResourceBundleFactory bundleFactory;

  @Mock
  InternalTenantContext context;

  @Mock
  private CustomFieldDao customFieldDao;

  private SolarNetworkInvoiceFormatterFactory factory;
  private DateTime now;
  private UUID accountId;
  private UUID subscriptionId;

  @Before
  public void setup() {
    now = new DateTime();
    accountId = UUID.randomUUID();
    subscriptionId = UUID.randomUUID();
    factory = new SolarNetworkInvoiceFormatterFactory();
  }

  @Test
  public void produce() {
    List<CustomFieldModelDao> daoCustomFields = Arrays.asList(
        new CustomFieldModelDao(now, ACCOUNT_FIELD, "acc", accountId, ObjectType.ACCOUNT),
        new CustomFieldModelDao(now, SUBSCRIPTION_FIELD, "sub", subscriptionId,
            ObjectType.SUBSCRIPTION));
    given(customFieldDao.getCustomFieldsForAccount(context)).willReturn(daoCustomFields);

    InvoiceFormatter formatter = factory.createInvoiceFormatter(config, invoice, Locale.US,
        currencyConversionApi, bundleFactory, context);

    assertThat("SolarNetworkInvoiceFormatter created", formatter,
        instanceOf(SolarNetworkInvoiceFormatter.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void produceDifferentInstances() {
    List<CustomFieldModelDao> daoCustomFields = Arrays.asList(
        new CustomFieldModelDao(now, ACCOUNT_FIELD, "acc", accountId, ObjectType.ACCOUNT),
        new CustomFieldModelDao(now, SUBSCRIPTION_FIELD, "sub", subscriptionId,
            ObjectType.SUBSCRIPTION));
    given(customFieldDao.getCustomFieldsForAccount(context)).willReturn(daoCustomFields,
        daoCustomFields);

    InvoiceFormatter formatter1 = factory.createInvoiceFormatter(config, invoice, Locale.US,
        currencyConversionApi, bundleFactory, context);
    InvoiceFormatter formatter2 = factory.createInvoiceFormatter(config, invoice, Locale.US,
        currencyConversionApi, bundleFactory, context);

    assertThat("SolarNetworkInvoiceFormatter created", formatter1, not(sameInstance(formatter2)));
  }

}
