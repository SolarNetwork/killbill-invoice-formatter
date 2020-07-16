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

package net.solarnetwork.billing.killbill.invoice.core;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.killbill.billing.ObjectType;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.invoice.api.InvoiceItemType;
import org.killbill.billing.invoice.api.formatters.InvoiceItemFormatter;
import org.killbill.billing.util.customfield.CustomField;

import net.solarnetwork.billing.killbill.invoice.api.ExtendedInvoiceItemFormatter;

/**
 * Implementation of {@link ExtendedInvoiceItemFormatter}.
 * 
 * @author matt
 * @version 2
 */
public class SolarNetworkInvoiceItemFormatter implements ExtendedInvoiceItemFormatter {

  private final InvoiceItemFormatter item;
  private final List<CustomField> customFields;

  /**
   * Construct out of another item formatter.
   * 
   * @param item
   *          the formatter to extend
   * @param customFields
   *          the custom fields
   */
  public SolarNetworkInvoiceItemFormatter(InvoiceItemFormatter item,
      List<CustomField> customFields) {
    super();
    this.item = item;
    this.customFields = customFields;
  }

  @Override
  public List<CustomField> getSubscriptionCustomFields() {
    if (customFields == null) {
      return Collections.emptyList();
    }
    final UUID subscriptionId = getSubscriptionId();
    if (subscriptionId == null) {
      return Collections.emptyList();
    }
    return customFields.stream().filter(f -> ObjectType.SUBSCRIPTION.equals(f.getObjectType())
        && subscriptionId.equals(f.getObjectId())).collect(Collectors.toList());
  }

  @Override
  public UUID getId() {
    return item.getId();
  }

  @Override
  public String getFormattedStartDate() {
    return item.getFormattedStartDate();
  }

  @Override
  public DateTime getCreatedDate() {
    return item.getCreatedDate();
  }

  @Override
  public String getFormattedEndDate() {
    return item.getFormattedEndDate();
  }

  @Override
  public String getFormattedAmount() {
    return item.getFormattedAmount();
  }

  @Override
  public InvoiceItemType getInvoiceItemType() {
    return item.getInvoiceItemType();
  }

  @Override
  public DateTime getUpdatedDate() {
    return item.getUpdatedDate();
  }

  @Override
  public UUID getInvoiceId() {
    return item.getInvoiceId();
  }

  @Override
  public UUID getAccountId() {
    return item.getAccountId();
  }

  @Override
  public UUID getChildAccountId() {
    return item.getChildAccountId();
  }

  @Override
  public LocalDate getStartDate() {
    return item.getStartDate();
  }

  @Override
  public LocalDate getEndDate() {
    return item.getEndDate();
  }

  @Override
  public BigDecimal getAmount() {
    return item.getAmount();
  }

  @Override
  public Currency getCurrency() {
    return item.getCurrency();
  }

  @Override
  public String getDescription() {
    return item.getDescription();
  }

  @Override
  public UUID getBundleId() {
    return item.getBundleId();
  }

  @Override
  public UUID getSubscriptionId() {
    return item.getSubscriptionId();
  }

  @Override
  public String getPlanName() {
    return item.getPlanName();
  }

  @Override
  public String getPhaseName() {
    return item.getPhaseName();
  }

  @Override
  public String getUsageName() {
    return item.getUsageName();
  }

  @Override
  public BigDecimal getRate() {
    return item.getRate();
  }

  @Override
  public UUID getLinkedItemId() {
    return item.getLinkedItemId();
  }

  @Override
  public boolean matches(Object other) {
    return item.matches(other);
  }

  @Override
  public String getProductName() {
    return item.getProductName();
  }

  @Override
  public String getPrettyProductName() {
    return item.getPrettyProductName();
  }

  @Override
  public String getPrettyPlanName() {
    return item.getPrettyPlanName();
  }

  @Override
  public String getPrettyPhaseName() {
    return item.getPrettyPhaseName();
  }

  @Override
  public String getPrettyUsageName() {
    return item.getPrettyUsageName();
  }

  @Override
  public Integer getQuantity() {
    return item.getQuantity();
  }

  @Override
  public String getItemDetails() {
    return item.getItemDetails();
  }

  @Override
  public DateTime getCatalogEffectiveDate() {
    return item.getCatalogEffectiveDate();
  }

}
