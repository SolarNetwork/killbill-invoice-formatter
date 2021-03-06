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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.killbill.billing.ObjectType;
import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.InvoiceItemType;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory;
import org.killbill.billing.util.customfield.CustomField;
import org.killbill.billing.util.customfield.StringCustomField;
import org.killbill.billing.util.template.translation.TranslatorConfig;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import net.solarnetwork.billing.killbill.invoice.api.ExtendedInvoiceItemFormatter;
import net.solarnetwork.billing.killbill.invoice.core.SolarNetworkInvoiceItemFormatter;

/**
 * Test cases for the {@link SolarNetworkInvoiceFormatter} class.
 * 
 * @author matt
 */
@RunWith(MockitoJUnitRunner.class)
public class SolarNetworkInvoiceFormaterTests {

  private static final Locale EN_NZ = new Locale("en", "NZ");
  private static final Long TENANT_RECORD_ID = -1L;
  private static final Long ACCOUNT_RECORD_ID = -2L;
  private static final DateTimeZone FIXED_TIME_ZONE = DateTimeZone.forOffsetHours(12);
  private static final String ACCOUNT_FIELD = "accField";
  private static final String SUBSCRIPTION_FIELD = "subField";
  private static final BigDecimal AMOUNT_1 = new BigDecimal("1.99");
  private static final BigDecimal AMOUNT_2 = new BigDecimal("2.99");
  private static final BigDecimal AMOUNT_3 = new BigDecimal("3.99");
  private static final BigDecimal AMOUNT_4 = new BigDecimal("4.99");
  private static final String GST = "GST";
  private static final String VAT = "VAT";

  @Mock
  private TranslatorConfig translatorConfig;

  @Mock
  private CurrencyConversionApi currencyConversionApi;

  @Mock
  private ResourceBundleFactory resourceBundleFactory;

  private UUID accountId;
  private UUID subscriptionId;
  private DateTime now;
  private InternalTenantContext internalTenantContext;

  @Before
  public void setup() {
    now = new DateTime();
    accountId = UUID.randomUUID();
    subscriptionId = UUID.randomUUID();
    internalTenantContext = new InternalTenantContext(TENANT_RECORD_ID, ACCOUNT_RECORD_ID,
        FIXED_TIME_ZONE, now);
  }

  private SolarNetworkInvoiceFormatter createDefaultFormatter(Invoice invoice, Locale locale) {
    return createDefaultFormatter(invoice, locale, null);
  }

  private SolarNetworkInvoiceFormatter createDefaultFormatter(Invoice invoice, Locale locale,
      List<CustomField> fields) {
    return new SolarNetworkInvoiceFormatter(translatorConfig, invoice, locale,
        currencyConversionApi, resourceBundleFactory, internalTenantContext, fields);
  }

  private Invoice createInvoice(List<InvoiceItem> items) {
    Invoice invoice = Mockito.mock(Invoice.class);
    given(invoice.getInvoiceItems()).willReturn(items);
    return invoice;
  }

  private static void assertCustomFieldsEqual(String msg, CustomField field, CustomField expected) {
    assertThat(msg + " name", field.getFieldName(), equalTo(expected.getFieldName()));
    assertThat(msg + " value", field.getFieldValue(), equalTo(expected.getFieldValue()));
  }

  @Test
  public void invoiceCustomFields() {
    // given
    List<CustomField> fields = Arrays.asList(
        new StringCustomField(ACCOUNT_FIELD, "acc", ObjectType.ACCOUNT, accountId, now),
        new StringCustomField(SUBSCRIPTION_FIELD, "sub", ObjectType.SUBSCRIPTION, subscriptionId,
            now));

    SolarNetworkInvoiceFormatter fmt = createDefaultFormatter(createInvoice(emptyList()), EN_NZ,
        fields);

    // when
    List<CustomField> customFields = fmt.getCustomFields();
    assertThat("Custom fields", customFields, hasSize(2));

    // then
    assertCustomFieldsEqual("Field 1", customFields.get(0), fields.get(0));
    assertCustomFieldsEqual("Field 2", customFields.get(1), fields.get(1));
  }

  @Test
  public void invoiceItemSubscriptionCustomFields() {
    // given
    List<CustomField> fields = Arrays.asList(
        new StringCustomField(ACCOUNT_FIELD, "acc", ObjectType.ACCOUNT, accountId, now),
        new StringCustomField(SUBSCRIPTION_FIELD, "sub", ObjectType.SUBSCRIPTION, subscriptionId,
            now));

    InvoiceItem item = Mockito.mock(InvoiceItem.class);
    given(item.getSubscriptionId()).willReturn(subscriptionId);
    List<InvoiceItem> items = asList(item);

    SolarNetworkInvoiceFormatter fmt = createDefaultFormatter(createInvoice(items), EN_NZ, fields);

    // when
    List<InvoiceItem> fmtItems = fmt.getInvoiceItems();

    // then
    assertThat("Format items", fmtItems, hasSize(1));
    InvoiceItem fmtItem = fmtItems.get(0);
    assertThat("Format item class", fmtItem, instanceOf(SolarNetworkInvoiceItemFormatter.class));

    List<CustomField> subFields = ((ExtendedInvoiceItemFormatter) fmtItem)
        .getSubscriptionCustomFields();
    assertThat("Format items", fmtItems, hasSize(1));
    assertCustomFieldsEqual("Sub field", subFields.get(0), fields.get(1));
  }

  @Test
  public void aggregateTaxItem() {
    InvoiceItem item1 = createInvoiceItem(InvoiceItemType.USAGE, null, null);
    InvoiceItem item2 = createInvoiceItem(InvoiceItemType.RECURRING, null, null);
    InvoiceItem tax1 = createInvoiceItem(InvoiceItemType.TAX, GST, AMOUNT_1);
    InvoiceItem tax2 = createInvoiceItem(InvoiceItemType.TAX, GST, AMOUNT_2);

    // when
    SolarNetworkInvoiceFormatter fmt = createDefaultFormatter(
        createInvoice(asList(item1, item2, tax1, tax2)), EN_NZ);
    List<InvoiceItem> aggItems = fmt.getTaxInvoiceItemsGroupedByDescription();

    // then
    assertThat("Aggregate items", aggItems, hasSize(1));
    InvoiceItem agg = aggItems.iterator().next();
    assertThat("Aggregate description", agg.getDescription(), equalTo(GST));
    assertThat("Aggregate amount", agg.getAmount(), equalTo(AMOUNT_1.add(AMOUNT_2)));
  }

  private static InvoiceItem createInvoiceItem(InvoiceItemType type, String description,
      BigDecimal amount) {
    InvoiceItem item = Mockito.mock(InvoiceItem.class);
    given(item.getInvoiceItemType()).willReturn(type);
    if (description != null) {
      given(item.getDescription()).willReturn(description);
    }
    if (amount != null) {
      given(item.getAmount()).willReturn(amount);
    }
    given(item.getId()).willReturn(UUID.randomUUID());
    return item;
  }

  @Test
  public void aggregateTaxItems() {
    InvoiceItem item1 = createInvoiceItem(InvoiceItemType.USAGE, null, null);
    InvoiceItem item2 = createInvoiceItem(InvoiceItemType.RECURRING, null, null);
    InvoiceItem tax1 = createInvoiceItem(InvoiceItemType.TAX, GST, AMOUNT_1);
    InvoiceItem tax2 = createInvoiceItem(InvoiceItemType.TAX, GST, AMOUNT_2);
    InvoiceItem tax3 = createInvoiceItem(InvoiceItemType.TAX, VAT, AMOUNT_3);
    InvoiceItem tax4 = createInvoiceItem(InvoiceItemType.TAX, VAT, AMOUNT_4);

    // when
    SolarNetworkInvoiceFormatter fmt = createDefaultFormatter(
        createInvoice(asList(item1, item2, tax1, tax2, tax3, tax4)), EN_NZ);
    List<InvoiceItem> aggItems = fmt.getTaxInvoiceItemsGroupedByDescription();

    // then
    assertThat("Aggregate items", aggItems, hasSize(2));
    InvoiceItem gst = aggItems.get(0);
    assertThat("GST description", gst.getDescription(), equalTo(GST));
    assertThat("GST amount", gst.getAmount(), equalTo(AMOUNT_1.add(AMOUNT_2)));

    InvoiceItem vat = aggItems.get(1);
    assertThat("VAT description", vat.getDescription(), equalTo(VAT));
    assertThat("VAT amount", vat.getAmount(), equalTo(AMOUNT_3.add(AMOUNT_4)));
  }

}
