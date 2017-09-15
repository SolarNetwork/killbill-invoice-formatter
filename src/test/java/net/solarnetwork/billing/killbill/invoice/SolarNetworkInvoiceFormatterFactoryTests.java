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

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatter;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory;
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

  private SolarNetworkInvoiceFormatterFactory factory;

  @Before
  public void setup() {
    factory = new SolarNetworkInvoiceFormatterFactory();
  }

  @Test
  public void produce() {
    InvoiceFormatter formatter = factory.createInvoiceFormatter(config, invoice, Locale.US,
        currencyConversionApi, bundleFactory, context);

    assertThat("SolarNetworkInvoiceFormatter created", formatter,
        instanceOf(SolarNetworkInvoiceFormatter.class));
  }

  @Test
  public void produceDifferentInstances() {
    InvoiceFormatter formatter1 = factory.createInvoiceFormatter(config, invoice, Locale.US,
        currencyConversionApi, bundleFactory, context);
    InvoiceFormatter formatter2 = factory.createInvoiceFormatter(config, invoice, Locale.US,
        currencyConversionApi, bundleFactory, context);

    assertThat("SolarNetworkInvoiceFormatter created", formatter1, not(sameInstance(formatter2)));
  }

}
