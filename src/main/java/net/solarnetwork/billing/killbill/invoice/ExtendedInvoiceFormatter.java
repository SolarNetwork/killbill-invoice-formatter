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

import java.math.BigDecimal;
import java.util.List;

import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatter;
import org.killbill.billing.util.customfield.CustomField;

/**
 * API for extended attributes on {@link InvoiceFormatter}.
 * 
 * @author matt
 * @version 1
 */
public interface ExtendedInvoiceFormatter extends InvoiceFormatter {

  /**
   * Get a list of all non-tax invoice items.
   * 
   * @return the items, or an empty list
   */
  List<InvoiceItem> getNonTaxInvoiceItems();

  /**
   * Get a list of all tax invoice items.
   * 
   * @return the items, or an empty list
   */
  List<InvoiceItem> getTaxInvoiceItems();

  /**
   * Get the total amount of all tax invoice items.
   * 
   * @return the total tax amount
   */
  BigDecimal getTaxAmount();

  /**
   * Get the total tax amount, formatted for the locale of the invoice.
   * 
   * @return the formatted total tax amount
   */
  String getFormattedTaxAmount();

  /**
   * Get the total amount of all non-tax invoice items.
   * 
   * @return the total non-tax amount
   */
  BigDecimal getNonTaxChargedAmount();

  /**
   * Get the total non-tax amount, formatted for the locale of the invoice.
   * 
   * @return the formatted total non-tax amount
   */
  String getFormattedNonTaxChargedAmount();

  /**
   * Get the total paid amount, formatted for the locale of the invoice.
   * 
   * @return the formatted paid amount
   */
  String getBasicFormattedPaidAmount();

  /**
   * Get the invoice balance, formatted for the locale of the invoice.
   * 
   * @return the formatted balance amount
   */
  String getBasicFormattedBalance();

  /**
   * Get all custom fields available to the account owning this invoice.
   * 
   * @return all custom fields
   */
  List<CustomField> getCustomFields();

}
