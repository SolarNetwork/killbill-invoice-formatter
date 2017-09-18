SolarNetwork Kill Bill Invoice Formatter
========================================

Extended version of the default Kill Bill invoice formatter, exposing some additional attributes for
invoice templates to work with.


Additional Template Attributes
------------------------------

| Attribute | Type | Description |
| --------- | ---- | ----------- |
| `nonTaxInvoiceItems` | List of invoice items | Filtered copy of all invoice items _except_ `TAX` items. |
| `taxInvoiceItems` | List of invoice items | Filtered copy of _only_ `TAX` invoice items. |
| `taxAmount` | Number | Sum total of all `TAX` invoice items. |
| `formattedTaxAmount` | String | Formatted version of `taxAmount`. |
| `nonTaxChargedAmount` | Number | Sum total of all `nonTaxInvoiceItems`. |
| `formattedNonTaxChargedAmount` | String | Formatted version of `nonTaxChargedAmount`. |
| `basicFormattedPaidAmount` | String | Formatted version of the built-in `paidAmount` without an implicit currency symbol. |
| `basicFormattedBalance` | String | Formatted version of the built-in `balance` without an implicit currency symbol. |

The two `basic*` attributes are there so the amounts are formatted consistently with invoice items.
The built-in equivalents format the amounts so even implicit currency symbols are included. For
example, if the invoice **currency** is `USD` and the **locale** is `en_US` then using the built-in
formatting for the amount `1.99` would result in `US$1.99` while the `basic*` equivalent would
produce `$1.99`.


Kill Bill compatibility
-----------------------

| Plugin version | Kill Bill version |
| -------------: | ----------------: |
| 0.1.y          | 0.18.z            |


Deployment
----------

The JAR must be included on the Kill Bill class path. This can be accomplished by
adding it to the `WEB-INF/lib` directory of the WAR.

Then, the following property must be updated in `killbill.properties`:

```
org.killbill.template.invoiceFormatterFactoryClass = net.solarnetwork.billing.killbill.invoice.SolarNetworkInvoiceFormatterFactory
```

Releases
--------

Releases can be executed like this:

```
./gradlew release --console plain
```
