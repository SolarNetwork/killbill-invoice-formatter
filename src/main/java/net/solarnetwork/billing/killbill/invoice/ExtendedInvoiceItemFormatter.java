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

import java.util.List;

import org.killbill.billing.invoice.api.formatters.InvoiceItemFormatter;
import org.killbill.billing.util.customfield.CustomField;

/**
 * API for extended attributes on {@link InvoiceItemFormatter}.
 * 
 * @author matt
 * @version 1
 */
public interface ExtendedInvoiceItemFormatter extends InvoiceItemFormatter {

  /**
   * Get a list of subscription custom fields.
   * 
   * @return all custom fields for the item's subscription, or an empty list
   */
  List<CustomField> getSubscriptionCustomFields();
}
