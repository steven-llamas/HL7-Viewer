package hl7Viewer.nonGui.hl7Parser;

import hl7Viewer.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("When testing HL7 Messages")
class HL7MessageTest {

    @Test
    @DisplayName("Carriage return combinations should convert to '\\r'")
    void sanitizeEnterChar() {
        String input = "A\nB\r\nC\rD";
        String result = HL7Message.sanitizeEnterChar(input);

        assertEquals("A\rB\rC\rD", result);
    }


    @Nested
    @DisplayName("When Testing Segments")
    class SegmentTests {


//        @Test
//        void setSegments_replacesExistingSegments() {
//            HL7Message msg = new HL7Message();
//
//            var list1 = new ArrayList<HL7Segment>();
//            list1.add(new HL7Segment("MSH", new ArrayList<>()));
//
//            var list2 = new ArrayList<HL7Segment>();
//            list2.add(new HL7Segment("PID", new ArrayList<>()));
//
//            msg.setItems(list1);
//            msg.setItems(list2);
//
//            assertEquals(1, msg.getItems().size());
//            assertEquals("PID", msg.getItems().getFirst().getSegmentName());
//        }


        @Test
        @DisplayName("adding a segment should add a segment to the segment list")
        void addSegment_addsToSegmentList() {
            HL7Message msg = new HL7Message();
            msg.setItems(new ArrayList<>());

            msg.add(new HL7Segment("MSH", new ArrayList<>()));

            assertEquals(1, msg.getItems().size());
        }


        @Test
        @DisplayName("Adding a null segment should not add it to the list")
        void addSegment_nullDoesNotAdd() {
            HL7Message msg = new HL7Message();
            msg.setItems(new ArrayList<>());
            msg.add(null);

            assertEquals(0, msg.getItems().size());
        }


        @Test
        @DisplayName("when calling getSegment, Segment List should be returned")
        void getSegment_returnsAList() {
            HL7Message msg = new HL7Message();
            var list = new ArrayList<HL7Segment>();

            msg.setItems(list);

            assertSame(list, msg.getItems());
        }
    }


    @Nested
    @DisplayName("When flattening an HL7 Message")
    class FlattenTests {
        private List<Pair<String, String>> rows;

        @BeforeEach
        void setUp() {
            final var parser = new BasicMessageParser();
            final String validHl7Message = """
                    MSH|^~\\&|TEST|TEST|  Lab_System  |Lab_Dept|202603051000||ADT^A04|32143214321|P|2.3
                    PID|||PI12345||DOE^JOHN^||19800101|M|||123 MAIN ST&APT 4B&METROPOLIS^OH^44123||(555)555-1212~(555)555-1313|||S|
                    PV1||O|2000^2012^1||||1234^SMITH^JOHN^MD|||SUR||||ADM|A0
                    """;
            rows = parser.parse(validHl7Message, new HL7Message()).flatten();
        }


        @Test
        @DisplayName("should return an empty list when message has no segments")
        void flatten_emptyMessage_returnsEmptyList() {
            final var msg = new HL7Message();
            msg.setItems(new ArrayList<>());

            assertTrue(msg.flatten().isEmpty());
        }


        @Test
        @DisplayName("should not contain any pairs with empty or whitespace values")
        void flatten_shouldNotContainEmptyValues() {
            assertTrue(rows.stream().noneMatch(r -> r.second().trim().isEmpty()));
        }


        @Test
        @DisplayName("should produce index PID-3 with value PI12345")
        void flatten_pidThree_shouldHaveCorrectIndexAndValue() {
            assertTrue(rows.stream().anyMatch(
                    r -> r.first().equals("PID-3") && r.second().equals("PI12345")));
        }


        @Test
        @DisplayName("MSH encoding chars field should have index MSH-2 due to field offset")
        void flatten_mshEncodingField_shouldApplyFieldOffset() {
            assertTrue(rows.stream().anyMatch(r -> r.first().startsWith("MSH-2")));
        }


        @Test
        @DisplayName("should include repetition index for PID-13 when field has multiple repetitions")
        void flatten_pidThirteenRepetitions_shouldIndexEachRepetition() {
            assertTrue(rows.stream().anyMatch(
                    r -> r.first().equals("PID-13.1") && r.second().equals("(555)555-1212")));
            assertTrue(rows.stream().anyMatch(
                    r -> r.first().equals("PID-13.2") && r.second().equals("(555)555-1313")));
        }


        @Test
        @DisplayName("should include component index for MSH-9 when field has multiple components")
        void flatten_mshNineComponents_shouldIndexEachComponent() {
            assertTrue(rows.stream().anyMatch(
                    r -> r.first().equals("MSH-9.1") && r.second().equals("ADT")));
            assertTrue(rows.stream().anyMatch(
                    r -> r.first().equals("MSH-9.2") && r.second().equals("A04")));
        }


        @Test
        @DisplayName("should include subcomponent index for PID-11 address subcomponents")
        void flatten_pidElevenAddress_shouldIndexEachSubcomponent() {
            assertTrue(rows.stream().anyMatch(
                    r -> r.first().equals("PID-11.1.1") && r.second().equals("123 MAIN ST")));
            assertTrue(rows.stream().anyMatch(
                    r -> r.first().equals("PID-11.1.2") && r.second().equals("APT 4B")));
            assertTrue(rows.stream().anyMatch(
                    r -> r.first().equals("PID-11.1.3") && r.second().equals("METROPOLIS")));
        }


        @Test
        @DisplayName("should produce rows from all segments")
        void flatten_allSegments_shouldProduceRowsFromEach() {
            assertTrue(rows.stream().anyMatch(r -> r.first().startsWith("MSH")));
            assertTrue(rows.stream().anyMatch(r -> r.first().startsWith("PID")));
            assertTrue(rows.stream().anyMatch(r -> r.first().startsWith("PV1")));
        }
    }
}
