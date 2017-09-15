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

import java.util.Locale;

import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatter;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatterFactory;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory;
import org.killbill.billing.util.template.translation.TranslatorConfig;

/**
 * Factory for {@link SolarNetworkInvoiceFormatter} instances.
 * 
 * @author matt
 */
public class SolarNetworkInvoiceFormatterFactory implements InvoiceFormatterFactory {

  public SolarNetworkInvoiceFormatterFactory() {
    super();
  }

  @Override
  public InvoiceFormatter createInvoiceFormatter(TranslatorConfig config, Invoice invoice,
      Locale locale, CurrencyConversionApi currencyConversionApi,
      ResourceBundleFactory bundleFactory, InternalTenantContext context) {
    return new SolarNetworkInvoiceFormatter(config, invoice, locale, currencyConversionApi,
        bundleFactory, context);
  }

}
