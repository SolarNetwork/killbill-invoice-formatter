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

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory;
import org.killbill.billing.invoice.template.formatters.DefaultInvoiceFormatter;
import org.killbill.billing.util.customfield.CustomField;
import org.killbill.billing.util.template.translation.TranslatorConfig;

import net.solarnetwork.billing.killbill.invoice.api.ExtendedInvoiceFormatter;
import net.solarnetwork.billing.killbill.invoice.core.SolarNetworkInvoiceFormatterHelper;

/**
 * Implementation of {@link ExtendedInvoiceFormatter} for SolarNetwork.
 * 
 * @author matt
 * @version 2
 */
public class SolarNetworkInvoiceFormatter extends DefaultInvoiceFormatter
    implements ExtendedInvoiceFormatter, Supplier<List<InvoiceItem>> {

  private final SolarNetworkInvoiceFormatterHelper delegate;

  /**
   * Constructor.
   * 
   * @param config
   *          the translator config
   * @param invoice
   *          the invoice
   * @param locale
   *          the locale
   * @param currencyConversionApi
   *          the currency conversion API
   * @param bundleFactory
   *          the bundle factory
   * @param context
   *          the context
   * @param customFields
   *          account custom fields (optional)
   */
  public SolarNetworkInvoiceFormatter(TranslatorConfig config, Invoice invoice, Locale locale,
      CurrencyConversionApi currencyConversionApi, ResourceBundleFactory bundleFactory,
      InternalTenantContext context, List<CustomField> customFields) {
    super(config, invoice, locale, currencyConversionApi, bundleFactory, context);
    this.delegate = new SolarNetworkInvoiceFormatterHelper(this, this, locale, customFields);
  }

  @Override
  public List<InvoiceItem> get() {
    return super.getInvoiceItems();
  }

  @Override
  public List<InvoiceItem> getInvoiceItems() {
    return delegate.getExtendedInvoiceItems();
  }

  @Override
  public List<InvoiceItem> getNonTaxInvoiceItems() {
    return delegate.getNonTaxInvoiceItems();
  }

  @Override
  public List<InvoiceItem> getNonTaxInvoiceItemsSortedBySubscriptionCustomFields() {
    return delegate.getNonTaxInvoiceItemsSortedBySubscriptionCustomFields();
  }

  @Override
  public List<InvoiceItem> getTaxInvoiceItems() {
    return delegate.getTaxInvoiceItems();
  }

  @Override
  public List<InvoiceItem> getTaxInvoiceItemsGroupedByDescription() {
    return delegate.getTaxInvoiceItemsGroupedByDescription();
  }

  @Override
  public BigDecimal getTaxAmount() {
    return delegate.getTaxAmount();
  }

  @Override
  public String getFormattedTaxAmount() {
    return delegate.getFormattedTaxAmount();
  }

  @Override
  public BigDecimal getNonTaxChargedAmount() {
    return delegate.getNonTaxChargedAmount();
  }

  @Override
  public String getFormattedNonTaxChargedAmount() {
    return delegate.getFormattedNonTaxChargedAmount();
  }

  @Override
  public String getBasicFormattedPaidAmount() {
    return delegate.getBasicFormattedPaidAmount();
  }

  @Override
  public String getBasicFormattedBalance() {
    return delegate.getBasicFormattedBalance();
  }

  @Override
  public List<CustomField> getCustomFields() {
    return delegate.getCustomFields();
  }

}
