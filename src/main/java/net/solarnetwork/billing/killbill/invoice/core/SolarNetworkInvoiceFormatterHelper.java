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

package net.solarnetwork.billing.killbill.invoice.core;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static net.solarnetwork.billing.killbill.invoice.util.AggregateInvoiceItem.itemOfLocale;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.InvoiceItemType;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatter;
import org.killbill.billing.invoice.api.formatters.InvoiceItemFormatter;
import org.killbill.billing.util.customfield.CustomField;

import net.solarnetwork.billing.killbill.invoice.api.ExtendedInvoiceFormatter;
import net.solarnetwork.billing.killbill.invoice.api.ExtendedInvoiceItemFormatter;
import net.solarnetwork.billing.killbill.invoice.api.InvoiceFormatterExtensions;
import net.solarnetwork.billing.killbill.invoice.util.CustomFieldsThenDescriptionComparator;

/**
 * Helper implementation of {@link ExtendedInvoiceFormatter} that delegates non-extended methods to
 * some other {@link InvoiceFormatter} instance.
 * 
 * <p>
 * This helper class was designed so that the extended methods could be shared between the internal
 * formatter and notification formatter implementations, which each rely on different class
 * hierarchies.
 * </p>
 * 
 * @author matt
 */
public class SolarNetworkInvoiceFormatterHelper implements InvoiceFormatterExtensions {

  private final InvoiceFormatter delegate;
  private final Supplier<List<InvoiceItem>> itemsSupplier;
  private final Locale locale;
  private final List<CustomField> customFields;

  private List<InvoiceItem> invoiceItems;

  /**
   * Constructor.
   * 
   * @param delegate
   *          the delegate
   * @param itemsSupplier
   *          the supplier of items; this is used rather than {@code delegate} to prevent
   *          recursively calling {@link InvoiceFormatter#getInvoiceItems()} when
   *          {@link #getExtendedInvoiceItems()} is invoked
   * @param locale
   *          the locale
   * @param customFields
   *          the custom fields (optional)
   */
  public SolarNetworkInvoiceFormatterHelper(InvoiceFormatter delegate,
      Supplier<List<InvoiceItem>> itemsSupplier, Locale locale, List<CustomField> customFields) {
    super();
    this.delegate = delegate;
    this.itemsSupplier = itemsSupplier;
    this.locale = locale;
    this.customFields = (customFields != null ? customFields : Collections.emptyList());
  }

  private Stream<InvoiceItem> getNonTaxInvoiceItemStream() {
    return getExtendedInvoiceItems().stream()
        .filter(item -> !InvoiceItemType.TAX.equals(item.getInvoiceItemType()));
  }

  @Override
  public List<InvoiceItem> getNonTaxInvoiceItems() {
    return getNonTaxInvoiceItemStream().collect(Collectors.toList());
  }

  @Override
  public List<InvoiceItem> getNonTaxInvoiceItemsSortedBySubscriptionCustomFields() {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    Stream<InvoiceItemFormatter> itemStream = (Stream) getNonTaxInvoiceItemStream();
    return itemStream
        .sorted(CustomFieldsThenDescriptionComparator.INVOICE_ITEM_SORT_BY_CUSTOM_FIELDS_THEN_DESC)
        .collect(Collectors.toList());
  }

  private Stream<InvoiceItem> getTaxInvoiceItemsStream() {
    return getExtendedInvoiceItems().stream()
        .filter(item -> InvoiceItemType.TAX.equals(item.getInvoiceItemType()));
  }

  @Override
  public List<InvoiceItem> getTaxInvoiceItems() {
    return getTaxInvoiceItemsStream().collect(Collectors.toList());
  }

  /**
   * Get an aggregate of tax invoice items, grouped by their descriptions.
   * 
   * @return the aggregate items
   */
  @Override
  public List<InvoiceItem> getTaxInvoiceItemsGroupedByDescription() {
    List<InvoiceItem> taxItems = getTaxInvoiceItems();
    if (taxItems.isEmpty() || taxItems.size() == 1) {
      // shortcut for a common case
      return taxItems;
    }
    // maintain ordering based on original invoice items
    List<UUID> ordering = taxItems.stream().map(item -> item.getId()).collect(toList());

    // return list of AggregateInvoiceItem, grouped by InvoiceItem::getDescription
    return taxItems.stream()
        .collect(groupingBy(InvoiceItem::getDescription, of(itemOfLocale(locale),
            (agg, item) -> agg.addItem((ExtendedInvoiceItemFormatter) item), (agg1, agg2) -> {
              return agg1.addItems(agg2);
            })))
        .values().stream().sorted(Comparator.comparing(item -> ordering.indexOf(item.getId())))
        .collect(toList());
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
    return formattedCurrencyAmount(delegate.getPaidAmount());
  }

  @Override
  public String getBasicFormattedBalance() {
    return formattedCurrencyAmount(delegate.getBalance());
  }

  private String formattedCurrencyAmount(final BigDecimal amount) {
    final NumberFormat number = NumberFormat.getCurrencyInstance(locale);
    number.setCurrency(java.util.Currency.getInstance(delegate.getCurrency().toString()));
    return number.format(amount.doubleValue());
  }

  @Override
  public List<CustomField> getCustomFields() {
    return customFields;
  }

  /**
   * Get the invoice items as extended {@link SolarNetworkInvoiceItemFormatter} instances.
   * 
   * <p>
   * This method caches the extended instances created from the {@code itemsSupplier} passed to the
   * constructor of this class the first time it is called, as it assumes the same list will be
   * returned from the supplier each time it is invoked.
   * </p>
   * 
   * @return the extended invoice items
   */
  public List<InvoiceItem> getExtendedInvoiceItems() {
    List<InvoiceItem> items = invoiceItems;
    if (items == null) {
      items = itemsSupplier.get();
      if (items != null && !items.isEmpty()) {
        items = items.stream().map(item -> {
          return new SolarNetworkInvoiceItemFormatter((InvoiceItemFormatter) item, customFields);
        }).collect(toList());
        invoiceItems = items; // cache for subsequent calls
      }
    }
    return items;
  }

}
