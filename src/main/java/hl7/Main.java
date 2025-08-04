package hl7;

import hl7.gui.GuiBase;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        String message = "msh|^~\\&|PHARMAPP|PHARMACY|HISAPP|HOSPITAL|20250726120000||ORM^O01|MSG123456|P|2.3\n" +
                "PID|1||123456^^^HOSP^MR||Doe^John^^^||19800101|M|||123 hl7.Main St^^Metropolis^NY^12345||555-1234|||S||123456789|987-65-4321\n" +
                "PV1|1|I|WARD^ROOM^BED^^^^^2341||||1234^Physician^Paul|||MED|||||1234567|||||||||||||||||||||||||20250726110000\n" +
                "ORC|NW|RX123456^PHARMAPP|ORD98765^HISAPP||||1^D||20250726120000|1234^Physician^Paul\n" +
                "RXE|1^TAB|Amoxicillin 500mg||500|MG|PO|Q8H|||10|D||||||||||20250727120000\n" +
                "RXR|PO^Oral\n";

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                var GuiBase = new GuiBase();
                GuiBase.setVisible(true);
            }
        });
    }
}