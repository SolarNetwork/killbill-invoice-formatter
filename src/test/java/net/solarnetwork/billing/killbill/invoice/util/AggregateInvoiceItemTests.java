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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.Test;
import org.killbill.billing.catalog.api.Currency;
import org.mockito.Mockito;

import net.solarnetwork.billing.killbill.invoice.api.ExtendedInvoiceItemFormatter;

/**
 * Test cases for the {@link AggregateInvoiceItem} class.
 * 
 * @author matt
 */
public class AggregateInvoiceItemTests {

  private static final BigDecimal AMOUNT_1 = new BigDecimal("1.99");
  private static final BigDecimal AMOUNT_2 = new BigDecimal("2.99");

  @Test
  public void emptyAmount() {
    AggregateInvoiceItem agg = new AggregateInvoiceItem(Locale.US);
    assertThat("Empty amount", agg.getAmount(), equalTo(BigDecimal.ZERO));
  }

  @Test
  public void addReturnsSame() {
    // given
    ExtendedInvoiceItemFormatter item = Mockito.mock(ExtendedInvoiceItemFormatter.class);

    // when
    AggregateInvoiceItem agg = new AggregateInvoiceItem(Locale.US);

    // then
    assertThat("Instance", agg.addItem(item), sameInstance(agg));
  }

  @Test
  public void singleAmount() {
    // given
    ExtendedInvoiceItemFormatter item = Mockito.mock(ExtendedInvoiceItemFormatter.class);
    given(item.getAmount()).willReturn(AMOUNT_1);
    given(item.getCurrency()).willReturn(Currency.USD);

    // when
    AggregateInvoiceItem agg = new AggregateInvoiceItem(Locale.US);
    agg.addItem(item);

    // then
    assertThat("Single amount", item.getAmount(), equalTo(AMOUNT_1));
    assertThat("Formatted amount", agg.getFormattedAmount(), equalTo("$1.99"));
  }

  @Test
  public void aggregateAmount() {
    // given
    ExtendedInvoiceItemFormatter item1 = Mockito.mock(ExtendedInvoiceItemFormatter.class);
    given(item1.getAmount()).willReturn(AMOUNT_1);
    given(item1.getCurrency()).willReturn(Currency.USD);

    ExtendedInvoiceItemFormatter item2 = Mockito.mock(ExtendedInvoiceItemFormatter.class);
    given(item2.getAmount()).willReturn(AMOUNT_2);
    given(item2.getCurrency()).willReturn(Currency.USD);

    // when
    AggregateInvoiceItem agg = new AggregateInvoiceItem(Locale.US);
    agg.addItem(item1).addItem(item2);

    // then
    assertThat("Aggregate amount", agg.getAmount(), equalTo(AMOUNT_1.add(AMOUNT_2)));
    assertThat("Formatted amount", agg.getFormattedAmount(), equalTo("$4.98"));
  }
}
