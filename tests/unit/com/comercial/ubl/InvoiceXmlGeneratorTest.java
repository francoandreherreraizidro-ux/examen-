package com.comercial.ubl;

import com.comercial.adapters.ubl.InvoiceXmlGenerator;
import com.comercial.domain.billing.Invoice;
import com.comercial.domain.billing.InvoiceLine;
import com.comercial.domain.billing.MonetaryTotal;
import com.comercial.domain.billing.Party;
import com.comercial.domain.billing.TaxTotal;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceXmlGeneratorTest {
    private static final String INVOICE_NS = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2";
    private static final String CBC_NS = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private static final String CAC_NS = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";

    private final InvoiceXmlGenerator generator = new InvoiceXmlGenerator();

    @Test
    void generatesWellFormedInvoiceWithUblRootAndNamespaces() throws Exception {
        String xml = generator.generate(invoice(null, twoLines()));
        Document document = parse(xml);

        assertEquals("Invoice", document.getDocumentElement().getLocalName());
        assertEquals(INVOICE_NS, document.getDocumentElement().getNamespaceURI());
        assertEquals(CBC_NS, document.getDocumentElement().lookupNamespaceURI("cbc"));
        assertEquals(CAC_NS, document.getDocumentElement().lookupNamespaceURI("cac"));
        assertNotNull(document.getDocumentElement());
    }

    @Test
    void mapsInvoiceIdentityDateCurrencyPartiesLinesPricesAndTotals() throws Exception {
        Document document = parse(generator.generate(invoice(null, twoLines())));

        assertEquals("SYNTH-INV-001", text(document, CBC_NS, "ID", 0));
        assertEquals("2026-11-04", text(document, CBC_NS, "IssueDate", 0));
        assertEquals("USD", text(document, CBC_NS, "DocumentCurrencyCode", 0));
        assertEquals("Proveedor ficticio & Asociados", text(document, CBC_NS, "Name", 0));
        assertEquals("Cliente de prueba <sin datos fiscales>", text(document, CBC_NS, "Name", 1));

        var lines = document.getElementsByTagNameNS(CAC_NS, "InvoiceLine");
        assertEquals(2, lines.getLength());
        Element firstLine = (Element) lines.item(0);
        assertEquals("1", text(firstLine, CBC_NS, "ID", 0));
        assertEquals("2.50", text(firstLine, CBC_NS, "InvoicedQuantity", 0));
        assertEquals("19.95", text(firstLine, CBC_NS, "LineExtensionAmount", 0));
        assertEquals("7.98", text(firstLine, CBC_NS, "PriceAmount", 0));
        assertEquals("USD", ((Element) firstLine.getElementsByTagNameNS(CBC_NS, "PriceAmount").item(0))
                .getAttribute("currencyID"));
        assertEquals("15.00", text((Element) lines.item(1), CBC_NS, "LineExtensionAmount", 0));
        assertEquals("34.95", text(document, CBC_NS, "PayableAmount", 0));
        assertEquals("34.95", text(document, CBC_NS, "TaxInclusiveAmount", 0));
    }

    @Test
    void serializesProvidedTaxTotalWithoutInventingTaxClassification() throws Exception {
        TaxTotal taxTotal = new TaxTotal(new BigDecimal("4.25"));
        Document document = parse(generator.generate(invoice(taxTotal, twoLines())));

        assertEquals("4.25", text(document, CBC_NS, "TaxAmount", 0));
        Element amount = (Element) document.getElementsByTagNameNS(CBC_NS, "TaxAmount").item(0);
        assertEquals("USD", amount.getAttribute("currencyID"));
        assertEquals(0, document.getElementsByTagNameNS(CAC_NS, "TaxSubtotal").getLength());
        assertEquals(0, document.getElementsByTagNameNS(CAC_NS, "TaxCategory").getLength());
    }

    @Test
    void escapesXmlSpecialCharactersAndPreservesParsedText() throws Exception {
        Invoice invoice = new Invoice(
                "SYNTH-&-001",
                LocalDate.of(2026, 11, 4),
                "USD",
                new Party("Ficticia & <Asociados>"),
                new Party("Cliente \"de prueba\""),
                List.of(new InvoiceLine("L&1", BigDecimal.ONE, "A < B & C", BigDecimal.ONE,
                        BigDecimal.ONE)),
                null,
                totals("1.00"));

        String xml = generator.generate(invoice);
        Document document = parse(xml);

        assertTrue(xml.contains("&amp;"));
        assertTrue(xml.contains("&lt;"));
        assertEquals("SYNTH-&-001", text(document, CBC_NS, "ID", 0));
        assertEquals("Ficticia & <Asociados>", text(document, CBC_NS, "Name", 0));
        assertEquals("A < B & C", text(document, CBC_NS, "Name", 2));
    }

    @Test
    void decimalSerializationIsIndependentOfDefaultLocale() throws Exception {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            Document document = parse(generator.generate(invoice(null, twoLines())));
            assertEquals("2.50", text(document, CBC_NS, "InvoicedQuantity", 0));
            assertEquals("7.98", text(document, CBC_NS, "PriceAmount", 0));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    void rejectsMissingRequiredInvoiceAndLineValues() {
        assertThrows(NullPointerException.class, () -> new Invoice(
                "ID", null, "USD", new Party("Emisor"), new Party("Cliente"),
                twoLines(), null, totals("34.95")));
        assertThrows(NullPointerException.class, () -> new Invoice(
                "ID", LocalDate.of(2026, 11, 4), "USD", null, new Party("Cliente"),
                twoLines(), null, totals("34.95")));
        assertThrows(IllegalArgumentException.class, () -> new Invoice(
                "ID", LocalDate.of(2026, 11, 4), "USD", new Party("Emisor"),
                new Party("Cliente"), List.of(), null, totals("34.95")));
        assertThrows(IllegalArgumentException.class, () -> new Party("  "));
        assertThrows(NullPointerException.class, () -> new InvoiceLine(
                "1", BigDecimal.ONE, "Producto", null, BigDecimal.ONE));
        assertThrows(NullPointerException.class, () -> generator.generate(null));
    }

    @Test
    void rejectsCharactersOutsideXml10() {
        Invoice invoice = new Invoice(
                "SYNTH-INV-001",
                LocalDate.of(2026, 11, 4),
                "USD",
                new Party("Proveedor\u0001ficticio"),
                new Party("Cliente"),
                twoLines(),
                null,
                totals("34.95"));

        assertThrows(IllegalArgumentException.class, () -> generator.generate(invoice));
    }

    private static Invoice invoice(TaxTotal taxTotal, List<InvoiceLine> lines) {
        return new Invoice(
                "SYNTH-INV-001",
                LocalDate.of(2026, 11, 4),
                "USD",
                new Party("Proveedor ficticio & Asociados"),
                new Party("Cliente de prueba <sin datos fiscales>"),
                lines,
                taxTotal,
                totals("34.95"));
    }

    private static List<InvoiceLine> twoLines() {
        return List.of(
                new InvoiceLine("1", new BigDecimal("2.50"), "Arroz ilustrativo",
                        new BigDecimal("7.98"), new BigDecimal("19.95")),
                new InvoiceLine("2", BigDecimal.ONE, "Producto de prueba",
                        new BigDecimal("15.00"), new BigDecimal("15.00")));
    }

    private static MonetaryTotal totals(String payable) {
        return new MonetaryTotal(
                new BigDecimal("34.95"),
                new BigDecimal("34.95"),
                new BigDecimal(payable),
                new BigDecimal(payable));
    }

    private static Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    private static String text(Document document, String namespace, String localName, int index) {
        return document.getElementsByTagNameNS(namespace, localName).item(index).getTextContent();
    }

    private static String text(Element element, String namespace, String localName, int index) {
        return element.getElementsByTagNameNS(namespace, localName).item(index).getTextContent();
    }
}
