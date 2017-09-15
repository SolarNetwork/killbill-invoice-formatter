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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.InvoiceItemType;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory;
import org.killbill.billing.invoice.template.formatters.DefaultInvoiceFormatter;
import org.killbill.billing.util.template.translation.TranslatorConfig;

/**
 * Implementation of {@link ExtendedInvoiceFormatter} for SolarNetwork.
 * 
 * @author matt
 */
public class SolarNetworkInvoiceFormatter extends DefaultInvoiceFormatter
    implements ExtendedInvoiceFormatter {

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
   */
  public SolarNetworkInvoiceFormatter(TranslatorConfig config, Invoice invoice, Locale locale,
      CurrencyConversionApi currencyConversionApi, ResourceBundleFactory bundleFactory,
      InternalTenantContext context) {
    super(config, invoice, locale, currencyConversionApi, bundleFactory, context);
  }

  private Stream<InvoiceItem> getNonTaxInvoiceItemStream() {
    return getInvoiceItems().stream()
        .filter(item -> !InvoiceItemType.TAX.equals(item.getInvoiceItemType()));
  }

  @Override
  public List<InvoiceItem> getNonTaxInvoiceItems() {
    return getNonTaxInvoiceItemStream().collect(Collectors.toList());
  }

  private Stream<InvoiceItem> getTaxInvoiceItemsStream() {
    return getInvoiceItems().stream()
        .filter(item -> InvoiceItemType.TAX.equals(item.getInvoiceItemType()));
  }

  @Override
  public List<InvoiceItem> getTaxInvoiceItems() {
    return getTaxInvoiceItemsStream().collect(Collectors.toList());
  }

  @Override
  public BigDecimal getTaxAmount() {
    return getTaxInvoiceItemsStream().map(e -> e.getAmount()).reduce(BigDecimal.ZERO,
        (l, r) -> l.add(r));
  }

  @Override
  public String getFormattedTaxAmount() {
    final NumberFormat number = NumberFormat.getCurrencyInstance(getLocale());
    number.setCurrency(java.util.Currency.getInstance(getCurrency().toString()));
    return number.format(getTaxAmount().doubleValue());
  }

  @Override
  public BigDecimal getNonTaxChargedAmount() {
    return getNonTaxInvoiceItemStream().map(e -> e.getAmount()).reduce(BigDecimal.ZERO,
        (l, r) -> l.add(r));
  }

  @Override
  public String getFormattedNonTaxChargedAmount() {
    final NumberFormat number = NumberFormat.getCurrencyInstance(getLocale());
    number.setCurrency(java.util.Currency.getInstance(getCurrency().toString()));
    return number.format(getNonTaxChargedAmount().doubleValue());
  }

}
