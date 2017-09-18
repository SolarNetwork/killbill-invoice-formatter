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

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.money.CurrencyUnit;
import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.InvoiceItemType;
import org.killbill.billing.invoice.api.formatters.InvoiceItemFormatter;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory;
import org.killbill.billing.invoice.template.formatters.DefaultInvoiceFormatter;
import org.killbill.billing.util.customfield.CustomField;
import org.killbill.billing.util.customfield.StringCustomField;
import org.killbill.billing.util.customfield.dao.CustomFieldDao;
import org.killbill.billing.util.customfield.dao.CustomFieldModelDao;
import org.killbill.billing.util.template.translation.TranslatorConfig;

/**
 * Implementation of {@link ExtendedInvoiceFormatter} for SolarNetwork.
 * 
 * @author matt
 */
public class SolarNetworkInvoiceFormatter extends DefaultInvoiceFormatter
    implements ExtendedInvoiceFormatter {

  private final InternalTenantContext tenantContext;

  private List<CustomField> customFields;
  private List<InvoiceItem> invoiceItems;

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
   * @param customFieldDao
   *          DAO to lookup custom fields related to the invoice
   */
  public SolarNetworkInvoiceFormatter(TranslatorConfig config, Invoice invoice, Locale locale,
      CurrencyConversionApi currencyConversionApi, ResourceBundleFactory bundleFactory,
      InternalTenantContext context, CustomFieldDao customFieldDao) {
    super(config, invoice, locale, currencyConversionApi, bundleFactory, context);
    this.tenantContext = context;

    List<CustomFieldModelDao> daoCustomFields = Collections.emptyList();
    if (customFieldDao != null) {
      daoCustomFields = customFieldDao.getCustomFieldsForAccount(tenantContext);
    }
    this.customFields = daoCustomFields.stream().map(f -> new StringCustomField(f))
        .collect(toList());
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
    return formattedCurrencyAmount(getTaxAmount());
  }

  @Override
  public BigDecimal getNonTaxChargedAmount() {
    return getNonTaxInvoiceItemStream().map(e -> e.getAmount()).reduce(BigDecimal.ZERO,
        (l, r) -> l.add(r));
  }

  @Override
  public String getFormattedNonTaxChargedAmount() {
    return formattedCurrencyAmount(getNonTaxChargedAmount());
  }

  @Override
  public String getBasicFormattedPaidAmount() {
    return formattedCurrencyAmount(getPaidAmount());
  }

  @Override
  public String getBasicFormattedBalance() {
    return formattedCurrencyAmount(getBalance());
  }

  private String formattedCurrencyAmount(final BigDecimal amount) {
    final NumberFormat number = NumberFormat.getCurrencyInstance(getLocale());
    number.setCurrency(java.util.Currency.getInstance(getCurrency().toString()));
    return number.format(amount.doubleValue());
  }

  @Override
  public List<InvoiceItem> getInvoiceItems() {
    List<InvoiceItem> items = invoiceItems;
    if (items == null) {
      items = super.getInvoiceItems();
      if (items != null && !items.isEmpty()) {
        items = items.stream().map(item -> {
          return new SolarNetworkInvoiceItemFormatter((InvoiceItemFormatter) item, customFields);
        }).collect(toList());
        invoiceItems = items; // cache for subsequent calls
      }
    }
    return items;
  }

  @Override
  public List<CustomField> getCustomFields() {
    return customFields;
  }

  /**
   * Format a currency amount using the default JDK formatting rules.
   * 
   * @param amount
   *          the amount to format
   * @param currencyCode
   *          the currency code, e.g. {@literal USD}
   * @param locale
   *          the desired locale
   * @return the formatted amount
   */
  public static String formattedCurrencyAmountWithImplicitSymbol(BigDecimal amount,
      String currencyCode, Locale locale) {
    final NumberFormat number = NumberFormat.getCurrencyInstance(locale);
    number.setCurrency(java.util.Currency.getInstance(currencyCode));
    return number.format(amount.doubleValue());
  }

  /**
   * Format a currency amount, always including an explicit {@link Currency} symbol even when the
   * locale and currency permit omitting it.
   * 
   * <p>
   * For example, if the {@code currencyCode} is {@literal USD} and the {@code locale} is
   * {@literal en_US} then {@literal 1.99} would be formatted as {@literal US$1.99}. By contrast,
   * the {@link #formattedCurrencyAmountWithImplicitSymbol(BigDecimal, String, Locale)} method would
   * produce {@literal $1.99}.
   * </p>
   * 
   * @param amount
   *          the amount to format
   * @param currencyCode
   *          the currency code, e.g. {@literal USD}
   * @param locale
   *          the desired locale
   * @return the formatted amount
   */
  public static String formattedCurrencyAmountWithExplicitSymbol(BigDecimal amount,
      String currencyCode, Locale locale) {
    final CurrencyUnit currencyUnit = CurrencyUnit.of(currencyCode);
    final DecimalFormat numberFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);

    final DecimalFormatSymbols dfs = numberFormatter.getDecimalFormatSymbols();
    dfs.setInternationalCurrencySymbol(currencyUnit.getCurrencyCode());

    // override the JDK currency symbol with the Kill Bill version, e.g. US$ instead of USD
    try {
      Currency currency = Currency.fromCode(currencyCode);
      dfs.setCurrencySymbol(currency.getSymbol());
    } catch (final IllegalArgumentException e) {
      dfs.setCurrencySymbol(currencyUnit.getSymbol(locale));
    }

    numberFormatter.setDecimalFormatSymbols(dfs);

    return numberFormatter.format(amount.doubleValue());
  }
}
