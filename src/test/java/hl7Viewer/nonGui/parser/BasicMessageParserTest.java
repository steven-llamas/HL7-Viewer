package hl7Viewer.nonGui.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BasicMessageParserTest {
    private BasicMessageParser parser;
    private final String validHl7Message =
            """
                    MSH|^~\\\\&|HIS_System|Hospital_A|Lab_System|Lab_Dept|202603051000||ADT^A04|MSG00001|P|2.3
                    PID|||PI12345||DOE^JOHN^||19800101|M|||123 MAIN ST^^METROPOLIS^NY^10001||(555)555-5555|||S|
                    PV1||O|2000^2012^1||||1234^SMITH^JOHN^MD|||SUR||||ADM|A0""";


    @BeforeEach
    void setUp() {
        parser = new BasicMessageParser();
    }


    @Nested
    class ParserValidationTests {
        @Test
        void parseNullStrMsgShouldRetNullPointerException() {
            assertThrows(
                    NullPointerException.class,
                    () -> parser.parse(null, new HL7Message())
            );
        }


        @Test
        void parseNullHL7MsgShouldRetNullPointerException() {
            assertThrows(
                    NullPointerException.class,
                    () -> parser.parse("MSH|||", null)
            );
        }


        @Test
        void parseEmptyStrMsgShouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> parser.parse("", new HL7Message())
            );
        }


        @Test
        void parseStrMsgWithWhitespaceShouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> parser.parse("      ", new HL7Message())
            );
        }


        @Test
        void parseMsgNotHavingMSHinFirstFourShouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> parser.parse("Test Message", new HL7Message())
            );
        }


        @Test
        void parseEmptyMshEncodingCharShouldNotThrowException() {
            assertDoesNotThrow(
                    () -> parser.parse(
                            "MSH||test|test|test|", new HL7Message())
            );
        }

    }


    @Nested
    class ParserValidMsgTests {
        HL7Message result;


        @BeforeEach
        void setUp() {
            result = parser.parse(validHl7Message, new HL7Message());
        }


        @Test
        void parseValidMessageReturnNotNull() {
            assertNotNull(result);
        }


        @Test
        void parseValidMessageShouldHaveThreeSegments() {
            assertEquals(3, result.getSegments().size());
        }


        @Test
        void parseValidMessageShouldHaveCorrectSegmentHeaders() {
            final List<String> segHeaders = result
                    .getSegments()
                    .stream()
                    .map(HL7Segment::getSegmentName)
                    .toList();


            assertEquals("MSH", segHeaders.getFirst());
            assertEquals("PID", segHeaders.get(1));
            assertEquals("PV1", segHeaders.get(2));
        }


        @Test
        void parseValidMessageShouldHavePI12345InPIDThree() {
            final var patID = result
                    .getSegments().get(1)
                    .getFieldList().get(3)
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().getFirst();
            assertEquals("PI12345", patID);
        }

        @Test
        void parseValidMessageShouldHaveDoeJohninPIDFive() {
            final var nameField = result
                    .getSegments().get(1)
                    .getFieldList().get(5);

            final var lName = nameField
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().getFirst();

            final var fName = nameField
                    .getRepetitionList().getFirst()
                    .getComponentList().get(1)
                    .getSubcomponentList().getFirst();

            assertEquals("DOE" , lName);
            assertEquals("JOHN", fName);
        }

    }
}