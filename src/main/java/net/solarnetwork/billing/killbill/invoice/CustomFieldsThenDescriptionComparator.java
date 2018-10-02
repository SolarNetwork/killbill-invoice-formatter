/*  Copyright 2018 SolarNetwork Foundation
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

import java.util.Comparator;
import java.util.List;

import org.killbill.billing.invoice.api.formatters.InvoiceItemFormatter;
import org.killbill.billing.util.customfield.CustomField;

/**
 * A comparator to sort custom fields by their names in a case-insensitive manner.
 * 
 * @author matt
 * @since 0.2
 */
public class CustomFieldsThenDescriptionComparator implements Comparator<InvoiceItemFormatter> {

  // CHECKSTYLE IGNORE LineLength FOR NEXT 1 LINE
  private static final Comparator<CustomField> CUSTOM_FIELD_SORT_BY_NAME = new CaseInsensitiveCustomFieldNameComparator();

  @Override
  public int compare(InvoiceItemFormatter o1, InvoiceItemFormatter o2) {
    if (o1 == null && o2 != null) {
      return -1;
    } else if (o1 != null && o2 == null) {
      return 1;
    } else if (o2 == o1) {
      return 0;
    }
    if (o1 instanceof ExtendedInvoiceItemFormatter && o2 instanceof ExtendedInvoiceItemFormatter) {
      List<CustomField> fields1 = ((ExtendedInvoiceItemFormatter) o1).getSubscriptionCustomFields();
      List<CustomField> fields2 = ((ExtendedInvoiceItemFormatter) o2).getSubscriptionCustomFields();
      if ((fields1 == null || fields1.isEmpty()) && !(fields2 == null || fields2.isEmpty())) {
        return -1;
      } else if (!(fields1 == null || fields1.isEmpty())
          && (fields2 == null || fields2.isEmpty())) {
        return 1;
      } else if (fields1 != null && !fields1.isEmpty() && fields2 != null && !fields2.isEmpty()) {
        // sort fields by name, then compare each in turn
        fields1.sort(CUSTOM_FIELD_SORT_BY_NAME);
        fields2.sort(CUSTOM_FIELD_SORT_BY_NAME);
        int max = Math.min(fields1.size(), fields2.size());
        for (int i = 0; i < max; i++) {
          CustomField f1 = fields1.get(i);
          CustomField f2 = fields2.get(i);
          String n1 = (f1 != null ? f1.getFieldName() : null);
          String n2 = (f2 != null ? f2.getFieldName() : null);
          if ((n1 == null && n2 != null) || (n1 != null && n2 == null)
              || (n1 != null && n2 != null && !n1.equalsIgnoreCase(n2))) {
            // stop comparing fields if their names differ
            break;
          }
          String v1 = (f1 != null ? f1.getFieldValue() : null);
          String v2 = (f2 != null ? f2.getFieldValue() : null);
          if (v1 == null && v2 != null) {
            return -1;
          } else if (v1 != null && v2 == null) {
            return 1;
          } else if (v1 != null && v2 != null) {
            int order = v1.compareToIgnoreCase(v2);
            if (order != 0) {
              return order;
            }
          }
        }
      }
    }

    // fields are sorted; compare description now
    String d1 = o1.getDescription();
    String d2 = o2.getDescription();
    if (d1 == null && d2 != null) {
      return -1;
    } else if (d1 != null && d2 == null) {
      return 1;
    } else if (d1 != null && d2 != null) {
      return d1.compareToIgnoreCase(d2);
    }
    return 0;
  }

}
