package com.comercial.adapters.ubl;

import com.comercial.domain.billing.Invoice;
import com.comercial.domain.billing.InvoiceLine;
import com.comercial.domain.billing.MonetaryTotal;
import com.comercial.domain.billing.Party;
import com.comercial.domain.billing.TaxTotal;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class InvoiceXmlGenerator {
    public static final String INVOICE_NAMESPACE =
            "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2";
    public static final String CBC_NAMESPACE =
            "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    public static final String CAC_NAMESPACE =
            "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";

    public String generate(Invoice invoice) throws XMLStreamException {
        Objects.requireNonNull(invoice, "invoice");
        validateXmlText(invoice.id(), "id");
        validateXmlText(invoice.currencyCode(), "currencyCode");
        validateXmlText(invoice.supplier().name(), "supplier.name");
        validateXmlText(invoice.customer().name(), "customer.name");
        for (InvoiceLine line : invoice.lines()) {
            validateXmlText(line.id(), "line.id");
            validateXmlText(line.itemName(), "line.itemName");
        }

        StringWriter output = new StringWriter();
        XMLStreamWriter xml = XMLOutputFactory.newFactory().createXMLStreamWriter(output);
        xml.writeStartDocument("UTF-8", "1.0");
        xml.setPrefix("cbc", CBC_NAMESPACE);
        xml.setPrefix("cac", CAC_NAMESPACE);
        xml.writeStartElement("", "Invoice", INVOICE_NAMESPACE);
        xml.writeDefaultNamespace(INVOICE_NAMESPACE);
        xml.writeNamespace("cbc", CBC_NAMESPACE);
        xml.writeNamespace("cac", CAC_NAMESPACE);

        writeElement(xml, "cbc", CBC_NAMESPACE, "UBLVersionID", "2.1");
        writeElement(xml, "cbc", CBC_NAMESPACE, "ID", invoice.id());
        writeElement(xml, "cbc", CBC_NAMESPACE, "IssueDate",
                invoice.issueDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        writeElement(xml, "cbc", CBC_NAMESPACE, "DocumentCurrencyCode", invoice.currencyCode());
        writeParty(xml, "AccountingSupplierParty", invoice.supplier());
        writeParty(xml, "AccountingCustomerParty", invoice.customer());

        TaxTotal taxTotal = invoice.taxTotal();
        if (taxTotal != null) {
            xml.writeStartElement("cac", "TaxTotal", CAC_NAMESPACE);
            writeAmount(xml, "TaxAmount", taxTotal.amount(), invoice.currencyCode());
            xml.writeEndElement();
        }

        writeMonetaryTotal(xml, invoice.monetaryTotal(), invoice.currencyCode());
        for (InvoiceLine line : invoice.lines()) {
            writeInvoiceLine(xml, line, invoice.currencyCode());
        }

        xml.writeEndElement();
        xml.writeEndDocument();
        xml.close();
        return output.toString();
    }

    private static void writeParty(XMLStreamWriter xml, String role, Party party)
            throws XMLStreamException {
        xml.writeStartElement("cac", role, CAC_NAMESPACE);
        xml.writeStartElement("cac", "Party", CAC_NAMESPACE);
        xml.writeStartElement("cac", "PartyName", CAC_NAMESPACE);
        writeElement(xml, "cbc", CBC_NAMESPACE, "Name", party.name());
        xml.writeEndElement();
        xml.writeEndElement();
        xml.writeEndElement();
    }

    private static void writeInvoiceLine(XMLStreamWriter xml, InvoiceLine line, String currencyCode)
            throws XMLStreamException {
        xml.writeStartElement("cac", "InvoiceLine", CAC_NAMESPACE);
        writeElement(xml, "cbc", CBC_NAMESPACE, "ID", line.id());
        writeElement(xml, "cbc", CBC_NAMESPACE, "InvoicedQuantity", line.quantity().toPlainString());
        writeAmount(xml, "LineExtensionAmount", line.lineExtensionAmount(), currencyCode);

        xml.writeStartElement("cac", "Item", CAC_NAMESPACE);
        writeElement(xml, "cbc", CBC_NAMESPACE, "Name", line.itemName());
        xml.writeEndElement();

        xml.writeStartElement("cac", "Price", CAC_NAMESPACE);
        writeAmount(xml, "PriceAmount", line.unitPrice(), currencyCode);
        xml.writeEndElement();
        xml.writeEndElement();
    }

    private static void writeMonetaryTotal(XMLStreamWriter xml, MonetaryTotal total, String currencyCode)
            throws XMLStreamException {
        xml.writeStartElement("cac", "LegalMonetaryTotal", CAC_NAMESPACE);
        writeAmount(xml, "LineExtensionAmount", total.lineExtensionAmount(), currencyCode);
        writeAmount(xml, "TaxExclusiveAmount", total.taxExclusiveAmount(), currencyCode);
        writeAmount(xml, "TaxInclusiveAmount", total.taxInclusiveAmount(), currencyCode);
        writeAmount(xml, "PayableAmount", total.payableAmount(), currencyCode);
        xml.writeEndElement();
    }

    private static void writeAmount(
            XMLStreamWriter xml, String elementName, java.math.BigDecimal value, String currencyCode)
            throws XMLStreamException {
        xml.writeStartElement("cbc", elementName, CBC_NAMESPACE);
        xml.writeAttribute("currencyID", currencyCode);
        xml.writeCharacters(value.toPlainString());
        xml.writeEndElement();
    }

    private static void writeElement(
            XMLStreamWriter xml, String prefix, String namespace, String elementName, String value)
            throws XMLStreamException {
        validateXmlText(value, elementName);
        xml.writeStartElement(prefix, elementName, namespace);
        xml.writeCharacters(value);
        xml.writeEndElement();
    }

    private static void validateXmlText(String value, String field) {
        for (int offset = 0; offset < value.length();) {
            int codePoint = value.codePointAt(offset);
            boolean valid = codePoint == 0x9 || codePoint == 0xA || codePoint == 0xD
                    || (codePoint >= 0x20 && codePoint <= 0xD7FF)
                    || (codePoint >= 0xE000 && codePoint <= 0xFFFD)
                    || (codePoint >= 0x10000 && codePoint <= 0x10FFFF);
            if (!valid) {
                throw new IllegalArgumentException(
                        field + " contains a character not allowed by XML 1.0");
            }
            offset += Character.charCount(codePoint);
        }
    }
}
