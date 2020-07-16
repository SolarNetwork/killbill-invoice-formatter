/*  Copyright 2020 SolarNetwork Foundation
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

package net.solarnetwork.billing.killbill.invoice.notification;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatter;
import org.killbill.billing.plugin.notification.api.InvoiceFormatterFactory;
import org.killbill.billing.util.api.CustomFieldUserApi;
import org.killbill.billing.util.callcontext.TenantContext;
import org.killbill.billing.util.customfield.CustomField;

/**
 * Extended {@link InvoiceFormatterFactory} implementation.
 * 
 * @author matt
 */
public class SolarNetworkInvoiceFormatterFactory implements InvoiceFormatterFactory {

  private final CustomFieldUserApi customFieldApi;

  /**
   * Constructor.
   * 
   * @param customFieldUserApi
   *          the custom field API to use
   */
  public SolarNetworkInvoiceFormatterFactory(CustomFieldUserApi customFieldUserApi) {
    super();
    this.customFieldApi = customFieldUserApi;
  }

  @Override
  public InvoiceFormatter createInvoiceFormatter(Map<String, String> translator, Invoice invoice,
      Locale locale, TenantContext context) {
    List<CustomField> customFields = null;
    if (customFieldApi != null) {
      customFields = customFieldApi.getCustomFieldsForAccount(invoice.getAccountId(), context);
    }
    return new SolarNetworkInvoiceFormatter(translator, invoice, locale, customFields);
  }

}
