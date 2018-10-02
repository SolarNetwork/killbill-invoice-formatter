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

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.killbill.billing.util.customfield.CustomField;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test cases for the {@link CustomFieldsThenDescriptionComparator} class.
 * 
 * @author matt
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomFieldsThenDescriptionComparatorTests {

  private CustomFieldsThenDescriptionComparator cmp;

  @Before
  public void setup() {
    cmp = new CustomFieldsThenDescriptionComparator();
  }

  private CustomField field(String name, String value) {
    CustomField f = Mockito.mock(CustomField.class);
    given(f.getFieldName()).willReturn(name);
    given(f.getFieldValue()).willReturn(value);
    return f;
  }

  private ExtendedInvoiceItemFormatter fmtWithDescriptionAndFields(String desc,
      List<CustomField> fields) {
    ExtendedInvoiceItemFormatter item = Mockito.mock(ExtendedInvoiceItemFormatter.class);
    given(item.getDescription()).willReturn(desc);
    given(item.getSubscriptionCustomFields()).willReturn(fields);
    return item;
  }

  @Test
  public void bothNull() {
    // then
    assertThat("Comparison", cmp.compare(null, null), equalTo(0));
  }

  @Test
  public void leftNull() {
    // given
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("B", null);

    // then
    assertThat("Comparison", cmp.compare(null, item2), equalTo(-1));
  }

  @Test
  public void rightNull() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", null);

    // then
    assertThat("Comparison", cmp.compare(item1, null), equalTo(1));
  }

  @Test
  public void noFieldsBothDescNull() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields(null, null);
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields(null, null);

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void noFieldsLeftDescNull() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields(null, null);
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("B", null);

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(-1));
  }

  @Test
  public void noFieldsRightDescNull() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", null);
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields(null, null);

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(1));
  }

  @Test
  public void noFieldsLeftDescEqualsRight() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", null);
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", null);

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void noFieldsLeftDescEqualsRightCaseInsensitive() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", null);
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("a", null);

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void noFieldsLeftDescAfterRight() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("B", null);
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", null);

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(1));
  }

  @Test
  public void noFieldsLeftDescBeforeRight() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", null);
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("B", null);

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(-1));
  }

  @Test
  public void oneFieldEachLeftFieldNameNull() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field(null, "1")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", asList(field("b", "1")));

    // then
    assertThat("Comparison ignores different field names", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void oneFieldEachRightFieldNameNull() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field("a", "1")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", asList(field(null, "1")));

    // then
    assertThat("Comparison ignores different field names", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void oneFieldEachBothFieldNamesNullBothValuesNull() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A",
        asList(field(null, null)));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A",
        asList(field(null, null)));

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void oneFieldEachBothFieldNamesNullLeftValueNull() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A",
        asList(field(null, null)));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", asList(field(null, "1")));

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(-1));
  }

  @Test
  public void oneFieldEachBothFieldNamesNullRightValueNull() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field(null, "1")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A",
        asList(field(null, null)));

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(1));
  }

  @Test
  public void oneFieldEachBothFieldNamesNullValuesEqual() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field(null, "1")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", asList(field(null, "1")));

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void oneFieldEachBothFieldNamesNullLeftValueBeforeRight() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field(null, "1")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", asList(field(null, "2")));

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(-1));
  }

  @Test
  public void oneFieldEachBothFieldNamesNullLeftValueAfterRight() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field(null, "2")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", asList(field(null, "1")));

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(1));
  }

  @Test
  public void oneFieldEachBothFieldNamesEqualLeftValueBeforeRight() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field("a", "1")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", asList(field("a", "2")));

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(-1));
  }

  @Test
  public void oneFieldEachBothFieldNamesEqualLeftValueAfterRight() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field("a", "2")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", asList(field("a", "1")));

    // then
    assertThat("Comparison", cmp.compare(item1, item2), equalTo(1));
  }

  @Test
  public void oneFieldEachDifferentFieldNamesLeftValueEqualsRight() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field("a", "1")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", asList(field("b", "1")));

    // then
    assertThat("Comparison ignores different field names", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void oneFieldEachDifferentFieldNamesLeftValueBeforeRight() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field("a", "1")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", asList(field("b", "2")));

    // then
    assertThat("Comparison ignores different field names", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void oneFieldEachDifferentFieldNamesLeftValueAfterRight() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field("a", "2")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A", asList(field("b", "1")));

    // then
    assertThat("Comparison ignores different field names", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void lopsidedFieldsEqualFieldNamesLeftValueEqualsRight() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field("a", "2")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A",
        asList(field("b", "1"), field("a", "2")));

    // then
    assertThat("Comparison ignores mismatched field names", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void lopsidedFieldsEqualFieldNamesMismtach() {
    // given
    ExtendedInvoiceItemFormatter item1 = fmtWithDescriptionAndFields("A", asList(field("b", "2")));
    ExtendedInvoiceItemFormatter item2 = fmtWithDescriptionAndFields("A",
        asList(field("a", "1"), field("b", "1")));

    // then
    assertThat("Comparison ignores mismatched field names", cmp.compare(item1, item2), equalTo(0));
  }

  @Test
  public void sortedList() {
    // given
    ExtendedInvoiceItemFormatter itemB3 = fmtWithDescriptionAndFields("C", asList(field("a", "2")));
    ExtendedInvoiceItemFormatter itemB2 = fmtWithDescriptionAndFields("B", asList(field("a", "2")));
    ExtendedInvoiceItemFormatter itemB1 = fmtWithDescriptionAndFields("A", asList(field("a", "2")));

    ExtendedInvoiceItemFormatter itemA3 = fmtWithDescriptionAndFields("C", asList(field("a", "1")));
    ExtendedInvoiceItemFormatter itemA2 = fmtWithDescriptionAndFields("B", asList(field("a", "1")));
    ExtendedInvoiceItemFormatter itemA1 = fmtWithDescriptionAndFields("A", asList(field("a", "1")));

    ExtendedInvoiceItemFormatter[] list = new ExtendedInvoiceItemFormatter[] { itemB3, itemB2,
        itemB1, itemA3, itemA2, itemA1 };

    // when
    Arrays.sort(list, cmp);

    // then
    assertThat("Sorted", list, arrayContaining(itemA1, itemA2, itemA3, itemB1, itemB2, itemB3));
  }
}
