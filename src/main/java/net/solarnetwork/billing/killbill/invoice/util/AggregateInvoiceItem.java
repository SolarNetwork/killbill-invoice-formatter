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

package net.solarnetwork.billing.killbill.invoice.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.invoice.api.InvoiceItemType;
import org.killbill.billing.util.customfield.CustomField;

import net.solarnetwork.billing.killbill.invoice.api.ExtendedInvoiceItemFormatter;

/**
 * An aggregate invoice item that sums the item amount for a group of items.
 * 
 * <p>
 * This class is designed to support combining similar invoice items into a single logical value,
 * for example to combine all tax items into a single value.
 * </p>
 * 
 * <p>
 * Unless otherwise documented, the methods of this class delegate to the <b>first</b> invoice item
 * added via {@link #addItem(ExtendedInvoiceItemFormatter)}. Documented methods like
 * {@link #getAmount()} return an aggregate value derived from all invoice items added.
 * </p>
 * 
 * @author matt
 * @version 2
 */
public class AggregateInvoiceItem implements ExtendedInvoiceItemFormatter {

  private final Locale locale;
  private List<ExtendedInvoiceItemFormatter> items = new ArrayList<>(4);
  private ExtendedInvoiceItemFormatter firstItem;

  /**
   * Constructor.
   * 
   * @param locale
   *          the desired locale
   */
  public AggregateInvoiceItem(Locale locale) {
    super();
    this.locale = locale;
  }

  /**
   * Add an invoice item.
   * 
   * @param item
   *          the item to add
   * @return this object
   */
  public AggregateInvoiceItem addItem(ExtendedInvoiceItemFormatter item) {
    items.add(item);
    if (firstItem == null) {
      firstItem = item;
    }
    return this;
  }

  /**
   * Get a supplier of aggregate items for a specific locale.
   * 
   * @param locale
   *          the locale to use for all supplied items
   * @return the supplier
   */
  public static Supplier<AggregateInvoiceItem> itemOfLocale(Locale locale) {
    return new Supplier<AggregateInvoiceItem>() {

      @Override
      public AggregateInvoiceItem get() {
        return new AggregateInvoiceItem(locale);
      }

    };
  }

  /**
   * Add items from another aggregate.
   * 
   * @param agg
   *          the aggregate
   * @return this object
   */
  public AggregateInvoiceItem addItems(AggregateInvoiceItem agg) {
    items.addAll(agg.items);
    return this;
  }

  @Override
  public UUID getId() {
    return firstItem.getId();
  }

  @Override
  public String getFormattedStartDate() {
    return firstItem.getFormattedStartDate();
  }

  @Override
  public DateTime getCreatedDate() {
    return firstItem.getCreatedDate();
  }

  @Override
  public String getFormattedEndDate() {
    return firstItem.getFormattedEndDate();
  }

  /**
   * Get the aggregate amount as a formatted string.
   * 
   * @return the {@link #getAmount()} value, formatted as a locale-specific string
   */
  @Override
  public String getFormattedAmount() {
    return StringUtils.formattedCurrencyAmountWithImplicitSymbol(getAmount(),
        getCurrency().toString(), locale);
  }

  @Override
  public InvoiceItemType getInvoiceItemType() {
    return firstItem.getInvoiceItemType();
  }

  @Override
  public DateTime getUpdatedDate() {
    return firstItem.getUpdatedDate();
  }

  @Override
  public UUID getInvoiceId() {
    return firstItem.getInvoiceId();
  }

  @Override
  public UUID getAccountId() {
    return firstItem.getAccountId();
  }

  @Override
  public UUID getChildAccountId() {
    return firstItem.getChildAccountId();
  }

  @Override
  public List<CustomField> getSubscriptionCustomFields() {
    return firstItem.getSubscriptionCustomFields();
  }

  @Override
  public LocalDate getStartDate() {
    return firstItem.getStartDate();
  }

  @Override
  public LocalDate getEndDate() {
    return firstItem.getEndDate();
  }

  /**
   * Get an aggregate amount of all configured invoice items.
   * 
   * <p>
   * This will return the sum of the amount of all invoice items configured via
   * {@link #addItem(ExtendedInvoiceItemFormatter)}.
   * </p>
   * 
   * @return the aggregate amount
   */
  @Override
  public BigDecimal getAmount() {
    return items.stream().map(item -> item.getAmount()).reduce(BigDecimal.ZERO, (l, r) -> l.add(r));
  }

  @Override
  public Currency getCurrency() {
    return firstItem.getCurrency();
  }

  @Override
  public String getDescription() {
    return firstItem.getDescription();
  }

  @Override
  public UUID getBundleId() {
    return firstItem.getBundleId();
  }

  @Override
  public UUID getSubscriptionId() {
    return firstItem.getSubscriptionId();
  }

  @Override
  public String getPlanName() {
    return firstItem.getPlanName();
  }

  @Override
  public String getPhaseName() {
    return firstItem.getPhaseName();
  }

  @Override
  public String getUsageName() {
    return firstItem.getUsageName();
  }

  @Override
  public BigDecimal getRate() {
    return firstItem.getRate();
  }

  @Override
  public UUID getLinkedItemId() {
    return firstItem.getLinkedItemId();
  }

  @Override
  public boolean matches(Object other) {
    return firstItem.matches(other);
  }

  @Override
  public String getProductName() {
    return firstItem.getProductName();
  }

  @Override
  public String getPrettyProductName() {
    return firstItem.getPrettyProductName();
  }

  @Override
  public String getPrettyPlanName() {
    return firstItem.getPrettyPlanName();
  }

  @Override
  public String getPrettyPhaseName() {
    return firstItem.getPrettyPhaseName();
  }

  @Override
  public String getPrettyUsageName() {
    return firstItem.getPrettyUsageName();
  }

  @Override
  public Integer getQuantity() {
    return firstItem.getQuantity();
  }

  @Override
  public String getItemDetails() {
    return firstItem.getItemDetails();
  }

  @Override
  public DateTime getCatalogEffectiveDate() {
    return firstItem.getCatalogEffectiveDate();
  }

}
