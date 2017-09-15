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

import static net.solarnetwork.billing.killbill.invoice.SolarNetworkInvoiceFormatter.formattedCurrencyAmount;
import static net.solarnetwork.billing.killbill.invoice.SolarNetworkInvoiceFormatter.formattedCurrencyAmountWithExplicitSymbol;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.killbill.billing.catalog.api.Currency;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test cases for the {@link SolarNetworkInvoiceFormatter} class.
 * 
 * @author matt
 */
@RunWith(MockitoJUnitRunner.class)
public class SolarNetworkInvoiceFormaterTests {

  private static final Locale EN_NZ = new Locale("en", "NZ");

  @Test
  public void basicMatchingCurrencyAndLocale() {
    String result = formattedCurrencyAmount(new BigDecimal("1.99"), Currency.NZD.toString(), EN_NZ);
    assertThat("Formatted amount", result, equalTo("$1.99"));
  }

  @Test
  public void basicRoundedMatchingCurrencyAndLocale() {
    String result = formattedCurrencyAmount(new BigDecimal("1.23456"), Currency.NZD.toString(),
        EN_NZ);
    assertThat("Formatted amount", result, equalTo("$1.23"));
  }

  @Test
  public void basicDifferingCurrencyAndLocale() {
    String result = formattedCurrencyAmount(new BigDecimal("1.99"), Currency.NZD.toString(),
        Locale.US);
    assertThat("Formatted amount", result, equalTo("NZD1.99"));
  }

  @Test
  public void basicDifferingCurrencyAndLocale2() {
    String result = formattedCurrencyAmount(new BigDecimal("1.99"), Currency.USD.toString(), EN_NZ);
    assertThat("Formatted amount", result, equalTo("USD1.99"));
  }

  @Test
  public void basicDifferingCurrencyAndLocale3() {
    String result = formattedCurrencyAmount(new BigDecimal("1.99"), Currency.NZD.toString(),
        Locale.GERMANY);
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

}
