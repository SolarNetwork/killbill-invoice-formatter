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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.killbill.billing.util.customfield.CustomField;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test cases for the {@link CaseInsensitiveCustomFieldNameComparator} class.
 * 
 * @author matt
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseInsensitiveCustomFieldNameComparatorTests {

  @Test
  public void bothNull() {
    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(null, null), equalTo(0));
  }

  @Test
  public void leftNull() {
    // given
    CustomField field2 = Mockito.mock(CustomField.class);
    given(field2.getFieldName()).willReturn("B");

    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(null, field2), equalTo(-1));
  }

  @Test
  public void rightNull() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    given(field1.getFieldName()).willReturn("A");

    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(field1, null), equalTo(1));
  }

  @Test
  public void bothNamesNull() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    CustomField field2 = Mockito.mock(CustomField.class);

    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(field1, field2), equalTo(0));
  }

  @Test
  public void leftNameNull() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    CustomField field2 = Mockito.mock(CustomField.class);
    given(field2.getFieldName()).willReturn("B");

    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(field1, field2), equalTo(-1));
  }

  @Test
  public void rightNameNull() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    given(field1.getFieldName()).willReturn("A");

    CustomField field2 = Mockito.mock(CustomField.class);

    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(field1, field2), equalTo(1));
  }

  @Test
  public void leftNameEqualsRightName() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    given(field1.getFieldName()).willReturn("A");

    CustomField field2 = Mockito.mock(CustomField.class);
    given(field2.getFieldName()).willReturn("A");

    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(field1, field2), equalTo(0));
  }

  @Test
  public void leftNameEqualsRightNameCaseInsensitive() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    given(field1.getFieldName()).willReturn("A");

    CustomField field2 = Mockito.mock(CustomField.class);
    given(field2.getFieldName()).willReturn("a");

    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(field1, field2), equalTo(0));
  }

  @Test
  public void leftNameBeforeRightName() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    given(field1.getFieldName()).willReturn("A");

    CustomField field2 = Mockito.mock(CustomField.class);
    given(field2.getFieldName()).willReturn("B");

    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(field1, field2), equalTo(-1));
  }

  @Test
  public void leftNameBeforeRightNameCaseInsensitive() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    given(field1.getFieldName()).willReturn("a");

    CustomField field2 = Mockito.mock(CustomField.class);
    given(field2.getFieldName()).willReturn("B");

    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(field1, field2), equalTo(-1));
  }

  @Test
  public void leftNameAfterRightName() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    given(field1.getFieldName()).willReturn("B");

    CustomField field2 = Mockito.mock(CustomField.class);
    given(field2.getFieldName()).willReturn("A");

    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(field1, field2), equalTo(1));
  }

  @Test
  public void leftNameAfterRightNameCaseInsensitive() {
    // given
    CustomField field1 = Mockito.mock(CustomField.class);
    given(field1.getFieldName()).willReturn("b");

    CustomField field2 = Mockito.mock(CustomField.class);
    given(field2.getFieldName()).willReturn("A");

    // when
    CaseInsensitiveCustomFieldNameComparator cmp = new CaseInsensitiveCustomFieldNameComparator();
    assertThat("Comparison", cmp.compare(field1, field2), equalTo(1));
  }
}
