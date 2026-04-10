package hl7Viewer.nonGui.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
//            msg.setSegments(list1);
//            msg.setSegments(list2);
//
//            assertEquals(1, msg.getSegments().size());
//            assertEquals("PID", msg.getSegments().getFirst().getSegmentName());
//        }


        @Test
        @DisplayName("adding a segment should add a segment to the segment list")
        void addSegment_addsToSegmentList() {
            HL7Message msg = new HL7Message();
            msg.setSegments(new ArrayList<>());

            msg.addSegment(new HL7Segment("MSH", new ArrayList<>()));

            assertEquals(1, msg.getSegments().size());
        }


        @Test
        @DisplayName("Adding a null segment should not add it to the list")
        void addSegment_nullDoesNotAdd() {
            HL7Message msg = new HL7Message();
            msg.setSegments(new ArrayList<>());
            msg.addSegment(null);

            assertEquals(0, msg.getSegments().size());
        }


        @Test
        @DisplayName("when calling getSegment, Segment List should be returned")
        void getSegment_returnsAList() {
            HL7Message msg = new HL7Message();
            var list = new ArrayList<HL7Segment>();

            msg.setSegments(list);

            assertSame(list, msg.getSegments());
        }
    }
}
