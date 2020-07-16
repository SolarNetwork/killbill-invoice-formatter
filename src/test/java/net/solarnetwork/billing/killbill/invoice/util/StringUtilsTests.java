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

package net.solarnetwork.billing.killbill.invoice.util;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.killbill.billing.catalog.api.Currency;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test cases for the {@link StringUtils} class.
 * 
 * @author matt
 */
@RunWith(MockitoJUnitRunner.class)
public class StringUtilsTests {

  private static final Locale EN_NZ = new Locale("en", "NZ");

  @Test
  public void basicMatchingCurrencyAndLocale() {
    String result = StringUtils.formattedCurrencyAmountWithImplicitSymbol(new BigDecimal("1.99"),
        Currency.NZD.toString(), EN_NZ);
    assertThat("Formatted amount", result, equalTo("$1.99"));
  }

  @Test
  public void basicRoundedMatchingCurrencyAndLocale() {
    String result = StringUtils.formattedCurrencyAmountWithImplicitSymbol(new BigDecimal("1.23456"),
        Currency.NZD.toString(), EN_NZ);
    assertThat("Formatted amount", result, equalTo("$1.23"));
  }

  @Test
  public void basicDifferingCurrencyAndLocale() {
    String result = StringUtils.formattedCurrencyAmountWithImplicitSymbol(new BigDecimal("1.99"),
        Currency.NZD.toString(), Locale.US);
    assertThat("Formatted amount", result, anyOf(equalTo("NZD1.99"), equalTo("NZ$1.99")));
  }

  @Test
  public void basicDifferingCurrencyAndLocale2() {
    String result = StringUtils.formattedCurrencyAmountWithImplicitSymbol(new BigDecimal("1.99"),
        Currency.USD.toString(), EN_NZ);
    assertThat("Formatted amount", result, anyOf(equalTo("USD1.99"), equalTo("US$1.99")));
  }

  @Test
  public void basicDifferingCurrencyAndLocale3() {
    String result = StringUtils.formattedCurrencyAmountWithImplicitSymbol(new BigDecimal("1.99"),
        Currency.NZD.toString(), Locale.GERMANY);
    assertThat("Formatted amount", result, anyOf(equalTo("1,99 NZD"), equalTo("1,99 NZ$")));
  }

  @Test
  public void explicitMatchingCurrencyAndLocale() {
    String result = StringUtils.formattedCurrencyAmountWithExplicitSymbol(new BigDecimal("1.99"),
        Currency.NZD.toString(), EN_NZ);
    assertThat("Formatted amount", result, equalTo("NZ$1.99"));
  }

  @Test
  public void explicitMatchingCurrencyAndLocale2() {
    String result = StringUtils.formattedCurrencyAmountWithExplicitSymbol(new BigDecimal("1.99"),
        Currency.USD.toString(), Locale.US);
    assertThat("Formatted amount", result, equalTo("US$1.99"));
  }

  @Test
  public void explicitRoundedMatchingCurrencyAndLocale() {
    String result = StringUtils.formattedCurrencyAmountWithExplicitSymbol(new BigDecimal("1.23456"),
        Currency.NZD.toString(), EN_NZ);
    assertThat("Formatted amount", result, equalTo("NZ$1.23"));
  }

}
