package com.comercial.examples;

import com.comercial.adapters.ubl.InvoiceXmlGenerator;
import com.comercial.domain.billing.Invoice;
import com.comercial.domain.billing.InvoiceLine;
import com.comercial.domain.billing.MonetaryTotal;
import com.comercial.domain.billing.Party;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public final class InvoiceXmlExample {
    private InvoiceXmlExample() {
    }

    public static void main(String[] args) throws XMLStreamException, IOException {
        String xml = new InvoiceXmlGenerator().generate(sampleInvoice());
        if (args.length == 0) {
            System.out.print(xml);
        } else {
            Path output = Path.of(args[0]).toAbsolutePath();
            Files.createDirectories(output.getParent());
            Files.writeString(output, xml, StandardCharsets.UTF_8);
        }
    }

    public static Invoice sampleInvoice() {
        return new Invoice(
                "SYNTH-INV-001",
                LocalDate.of(2026, 11, 4),
                "USD",
                new Party("Proveedor ficticio & Asociados"),
                new Party("Cliente de prueba <sin datos fiscales>"),
                List.of(
                        new InvoiceLine("1", new BigDecimal("2.50"), "Arroz ilustrativo",
                                new BigDecimal("7.98"), new BigDecimal("19.95")),
                        new InvoiceLine("2", BigDecimal.ONE, "Producto de prueba",
                                new BigDecimal("15.00"), new BigDecimal("15.00"))),
                null,
                new MonetaryTotal(
                        new BigDecimal("34.95"),
                        new BigDecimal("34.95"),
                        new BigDecimal("34.95"),
                        new BigDecimal("34.95")));
    }
}
