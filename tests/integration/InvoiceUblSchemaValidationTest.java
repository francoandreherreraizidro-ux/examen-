package com.comercial.ubl;

import com.comercial.adapters.ubl.InvoiceXmlGenerator;
import com.comercial.domain.billing.Invoice;
import com.comercial.domain.billing.InvoiceLine;
import com.comercial.domain.billing.MonetaryTotal;
import com.comercial.domain.billing.Party;
import com.comercial.domain.billing.TaxTotal;
import com.comercial.examples.InvoiceXmlExample;
import org.junit.jupiter.api.Test;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InvoiceUblSchemaValidationTest {
    private final InvoiceXmlGenerator generator = new InvoiceXmlGenerator();

    @Test
    void generatedInvoiceWithMultipleLinesAndTaxTotalPassesOfficialUbl21Xsd() throws Exception {
        Invoice invoice = new Invoice(
                "SYNTH-INV-XSD-001",
                LocalDate.of(2026, 11, 4),
                "USD",
                new Party("Synthetic Supplier"),
                new Party("Synthetic Customer"),
                List.of(
                        new InvoiceLine("1", new BigDecimal("2.5"), "Item A",
                                new BigDecimal("3.20"), new BigDecimal("8.00")),
                        new InvoiceLine("2", BigDecimal.ONE, "Item B",
                                new BigDecimal("4.00"), new BigDecimal("4.00"))),
                new TaxTotal(new BigDecimal("1.00")),
                new MonetaryTotal(
                        new BigDecimal("12.00"),
                        new BigDecimal("12.00"),
                        new BigDecimal("13.00"),
                        new BigDecimal("13.00")));

        String xml = generator.generate(invoice);
        URL schemaUrl = getClass().getResource("/ubl/2.1/xsd/maindoc/UBL-Invoice-2.1.xsd");

        assertDoesNotThrow(() -> officialSchema(schemaUrl).newValidator()
                .validate(new StreamSource(new StringReader(xml))));
    }

    @Test
    void checkedInExampleIsGeneratorOutputAndPassesOfficialUbl21Xsd() throws Exception {
        Path examplePath = Path.of("docs", "examples", "invoice-ubl21-ejemplo.xml");
        String example = Files.readString(examplePath);
        assertEquals(generator.generate(InvoiceXmlExample.sampleInvoice()), example);

        URL schemaUrl = getClass().getResource("/ubl/2.1/xsd/maindoc/UBL-Invoice-2.1.xsd");
        assertDoesNotThrow(() -> officialSchema(schemaUrl).newValidator()
                .validate(new StreamSource(new StringReader(example))));
    }

    private static javax.xml.validation.Schema officialSchema(URL schemaUrl) throws Exception {
        if (schemaUrl == null || !"file".equals(schemaUrl.getProtocol())) {
            throw new IllegalStateException("UBL XSD must be available as a local test resource");
        }

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "file");

        try (InputStream schemaStream = schemaUrl.openStream()) {
            StreamSource schemaSource = new StreamSource(schemaStream);
            schemaSource.setSystemId(schemaUrl.toExternalForm());
            return factory.newSchema(schemaSource);
        }
    }
}
