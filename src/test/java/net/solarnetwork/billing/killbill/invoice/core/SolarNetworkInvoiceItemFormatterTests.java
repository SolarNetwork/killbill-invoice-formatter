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

package net.solarnetwork.billing.killbill.invoice.core;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.killbill.billing.ObjectType;
import org.killbill.billing.invoice.api.formatters.InvoiceItemFormatter;
import org.killbill.billing.util.customfield.CustomField;
import org.mockito.Mockito;

/**
 * Test cases for the {@link SolarNetworkInvoiceItemFormatter} class.
 * 
 * @author matt
 */
public class SolarNetworkInvoiceItemFormatterTests {

  @Test
  public void subscriptionCustomFieldsEmpty() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    given(field1.getObjectId()).willReturn(UUID.randomUUID());
    given(field1.getObjectType()).willReturn(ObjectType.SUBSCRIPTION);

    UUID subId = UUID.randomUUID();
    InvoiceItemFormatter item = Mockito.mock(InvoiceItemFormatter.class);
    given(item.getSubscriptionId()).willReturn(subId);

    // when
    SolarNetworkInvoiceItemFormatter fmt = new SolarNetworkInvoiceItemFormatter(item,
        Arrays.asList(field1));
    List<CustomField> fields = fmt.getSubscriptionCustomFields();
    assertThat("Field count", fields, hasSize(0));
  }

  @Test
  public void subscriptionIdMissing() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    given(field1.getObjectId()).willReturn(UUID.randomUUID());
    given(field1.getObjectType()).willReturn(ObjectType.SUBSCRIPTION);

    InvoiceItemFormatter item = Mockito.mock(InvoiceItemFormatter.class);
    given(item.getSubscriptionId()).willReturn(null);

    // when
    SolarNetworkInvoiceItemFormatter fmt = new SolarNetworkInvoiceItemFormatter(item,
        Arrays.asList(field1));
    List<CustomField> fields = fmt.getSubscriptionCustomFields();
    assertThat("Field count", fields, hasSize(0));
  }

  @Test
  public void subscriptionCustomFieldFiltered() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    given(field1.getObjectId()).willReturn(UUID.randomUUID());
    given(field1.getObjectType()).willReturn(ObjectType.SUBSCRIPTION);

    UUID subId = UUID.randomUUID();
    CustomField field2 = Mockito.mock(CustomField.class);
    given(field2.getObjectId()).willReturn(subId);
    given(field2.getObjectType()).willReturn(ObjectType.SUBSCRIPTION);

    InvoiceItemFormatter item = Mockito.mock(InvoiceItemFormatter.class);
    given(item.getSubscriptionId()).willReturn(subId);

    // when
    SolarNetworkInvoiceItemFormatter fmt = new SolarNetworkInvoiceItemFormatter(item,
        Arrays.asList(field1, field2));
    List<CustomField> fields = fmt.getSubscriptionCustomFields();
    assertThat("Field count", fields, hasSize(1));
    assertThat("Sub field", fields.get(0), Matchers.sameInstance(field2));
  }
}
