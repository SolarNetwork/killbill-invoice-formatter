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

import org.killbill.billing.util.customfield.CustomField;

/**
 * A comparator to sort custom fields by their names in a case-insensitive manner.
 * 
 * @author matt
 * @since 0.2
 */
public class CaseInsensitiveCustomFieldNameComparator implements Comparator<CustomField> {

  @Override
  public int compare(CustomField o1, CustomField o2) {
    String n1 = (o1 != null ? o1.getFieldName() : null);
    String n2 = (o2 != null ? o2.getFieldName() : null);
    if (n1 == null && n2 != null) {
      return -1;
    } else if (n1 != null && n2 == null) {
      return 1;
    }
    return (n1 != null && n2 != null ? n1.compareToIgnoreCase(n2) : 0);
  }

}