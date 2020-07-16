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

package net.solarnetwork.billing.killbill.invoice.internal;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatter;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatterFactory;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory;
import org.killbill.billing.util.customfield.CustomField;
import org.killbill.billing.util.customfield.StringCustomField;
import org.killbill.billing.util.customfield.dao.CustomFieldDao;
import org.killbill.billing.util.customfield.dao.CustomFieldModelDao;
import org.killbill.billing.util.template.translation.TranslatorConfig;

/**
 * Factory for {@link SolarNetworkInvoiceFormatter} instances.
 * 
 * @author matt
 */
public class SolarNetworkInvoiceFormatterFactory implements InvoiceFormatterFactory {

  private final CustomFieldDao customFieldDao;

  public SolarNetworkInvoiceFormatterFactory() {
    this(null);
  }

  @Inject
  public SolarNetworkInvoiceFormatterFactory(CustomFieldDao customFieldDao) {
    super();
    this.customFieldDao = customFieldDao;
  }

  @Override
  public InvoiceFormatter createInvoiceFormatter(TranslatorConfig config, Invoice invoice,
      Locale locale, CurrencyConversionApi currencyConversionApi,
      ResourceBundleFactory bundleFactory, InternalTenantContext context) {
    List<CustomField> customFields = null;
    if (this.customFieldDao != null) {
      List<CustomFieldModelDao> fields = this.customFieldDao.getCustomFieldsForAccount(context);
      if (fields != null) {
        customFields = fields.stream().map(f -> new StringCustomField(f)).collect(toList());
      }
    }
    return new SolarNetworkInvoiceFormatter(config, invoice, locale, currencyConversionApi,
        bundleFactory, context, customFields);
  }

}
