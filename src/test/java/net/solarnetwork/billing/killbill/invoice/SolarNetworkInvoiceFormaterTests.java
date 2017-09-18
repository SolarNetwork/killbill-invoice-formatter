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

import static java.util.Collections.emptyList;
import static net.solarnetwork.billing.killbill.invoice.SolarNetworkInvoiceFormatter.formattedCurrencyAmountWithExplicitSymbol;
import static net.solarnetwork.billing.killbill.invoice.SolarNetworkInvoiceFormatter.formattedCurrencyAmountWithImplicitSymbol;
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
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory;
import org.killbill.billing.util.customfield.CustomField;
import org.killbill.billing.util.customfield.dao.CustomFieldDao;
import org.killbill.billing.util.customfield.dao.CustomFieldModelDao;
import org.killbill.billing.util.template.translation.TranslatorConfig;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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

  @Mock
  private CustomFieldDao customFieldDao;

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

  @Test
  public void basicMatchingCurrencyAndLocale() {
    String result = formattedCurrencyAmountWithImplicitSymbol(new BigDecimal("1.99"),
        Currency.NZD.toString(), EN_NZ);
    assertThat("Formatted amount", result, equalTo("$1.99"));
  }

  @Test
  public void basicRoundedMatchingCurrencyAndLocale() {
    String result = formattedCurrencyAmountWithImplicitSymbol(new BigDecimal("1.23456"),
        Currency.NZD.toString(), EN_NZ);
    assertThat("Formatted amount", result, equalTo("$1.23"));
  }

  @Test
  public void basicDifferingCurrencyAndLocale() {
    String result = formattedCurrencyAmountWithImplicitSymbol(new BigDecimal("1.99"),
        Currency.NZD.toString(), Locale.US);
    assertThat("Formatted amount", result, equalTo("NZD1.99"));
  }

  @Test
  public void basicDifferingCurrencyAndLocale2() {
    String result = formattedCurrencyAmountWithImplicitSymbol(new BigDecimal("1.99"),
        Currency.USD.toString(), EN_NZ);
    assertThat("Formatted amount", result, equalTo("USD1.99"));
  }

  @Test
  public void basicDifferingCurrencyAndLocale3() {
    String result = formattedCurrencyAmountWithImplicitSymbol(new BigDecimal("1.99"),
        Currency.NZD.toString(), Locale.GERMANY);
    assertThat("Formatted amount", result, equalTo("1,99 NZD"));
  }

  @Test
  public void explicitMatchingCurrencyAndLocale() {
    String result = formattedCurrencyAmountWithExplicitSymbol(new BigDecimal("1.99"),
        Currency.NZD.toString(), EN_NZ);
    assertThat("Formatted amount", result, equalTo("NZ$1.99"));
  }

  @Test
  public void explicitMatchingCurrencyAndLocale2() {
    String result = formattedCurrencyAmountWithExplicitSymbol(new BigDecimal("1.99"),
        Currency.USD.toString(), Locale.US);
    assertThat("Formatted amount", result, equalTo("US$1.99"));
  }

  @Test
  public void explicitRoundedMatchingCurrencyAndLocale() {
    String result = formattedCurrencyAmountWithExplicitSymbol(new BigDecimal("1.23456"),
        Currency.NZD.toString(), EN_NZ);
    assertThat("Formatted amount", result, equalTo("NZ$1.23"));
  }

  private SolarNetworkInvoiceFormatter createDefaultFormatter(Invoice invoice, Locale locale) {
    return new SolarNetworkInvoiceFormatter(translatorConfig, invoice, locale,
        currencyConversionApi, resourceBundleFactory, internalTenantContext, customFieldDao);
  }

  private Invoice createInvoice(List<InvoiceItem> items) {
    Invoice invoice = Mockito.mock(Invoice.class);
    given(invoice.getInvoiceItems()).willReturn(items);
    return invoice;
  }

  private static void assertCustomFieldsEqual(String msg, CustomField field,
      CustomFieldModelDao expected) {
    assertThat(msg + " name", field.getFieldName(), equalTo(expected.getFieldName()));
    assertThat(msg + " value", field.getFieldValue(), equalTo(expected.getFieldValue()));
  }

  @Test
  public void invoiceCustomFields() {
    // given
    List<CustomFieldModelDao> daoCustomFields = Arrays.asList(
        new CustomFieldModelDao(now, ACCOUNT_FIELD, "acc", accountId, ObjectType.ACCOUNT),
        new CustomFieldModelDao(now, SUBSCRIPTION_FIELD, "sub", subscriptionId,
            ObjectType.SUBSCRIPTION));
    given(customFieldDao.getCustomFieldsForAccount(internalTenantContext))
        .willReturn(daoCustomFields);

    SolarNetworkInvoiceFormatter fmt = createDefaultFormatter(createInvoice(emptyList()), EN_NZ);

    // when
    List<CustomField> customFields = fmt.getCustomFields();
    assertThat("Custom fields", customFields, hasSize(2));

    // then
    assertCustomFieldsEqual("Field 1", customFields.get(0), daoCustomFields.get(0));
    assertCustomFieldsEqual("Field 2", customFields.get(0), daoCustomFields.get(0));
  }

  @Test
  public void invoiceItemSubscriptionCustomFields() {
    // given
    List<CustomFieldModelDao> daoCustomFields = Arrays.asList(
        new CustomFieldModelDao(now, ACCOUNT_FIELD, "acc", accountId, ObjectType.ACCOUNT),
        new CustomFieldModelDao(now, SUBSCRIPTION_FIELD, "sub", subscriptionId,
            ObjectType.SUBSCRIPTION));
    given(customFieldDao.getCustomFieldsForAccount(internalTenantContext))
        .willReturn(daoCustomFields);

    InvoiceItem item = Mockito.mock(InvoiceItem.class);
    given(item.getSubscriptionId()).willReturn(subscriptionId);
    List<InvoiceItem> items = Arrays.asList(item);

    SolarNetworkInvoiceFormatter fmt = createDefaultFormatter(createInvoice(items), EN_NZ);

    // when
    List<InvoiceItem> fmtItems = fmt.getInvoiceItems();

    // then
    assertThat("Format items", fmtItems, hasSize(1));
    InvoiceItem fmtItem = fmtItems.get(0);
    assertThat("Format item class", fmtItem, instanceOf(SolarNetworkInvoiceItemFormatter.class));

    List<CustomField> subFields = ((ExtendedInvoiceItemFormatter) fmtItem)
        .getSubscriptionCustomFields();
    assertThat("Format items", fmtItems, hasSize(1));
    assertCustomFieldsEqual("Sub field", subFields.get(0), daoCustomFields.get(1));
  }

}
