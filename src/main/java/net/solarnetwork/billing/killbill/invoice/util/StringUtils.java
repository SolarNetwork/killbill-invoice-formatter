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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.killbill.billing.catalog.api.Currency;

/**
 * String utilities for invoice formatting.
 * 
 * @author matt
 */
public final class StringUtils {

  private StringUtils() {
    // don't construct me
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
