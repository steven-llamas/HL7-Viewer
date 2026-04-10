package hl7Viewer.nonGui.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BasicMessageParserTest {
    private BasicMessageParser parser;


    @BeforeEach
    void setUp() {
        parser = new BasicMessageParser();
    }


    @Nested
    @DisplayName("When Validating Parser Exceptions")
    class ParserValidationTests {
        @Test
        @DisplayName("null message should throw Null Pointer Exception")
        void parse_NullStrMsgShouldRetNullPointerException() {
            assertThrows(
                    NullPointerException.class,
                    () -> parser.parse(null, new HL7Message())
            );
        }


        @Test
        @DisplayName("null HL7Message object should throw Null Pointer Exception")
        void parse_NullHL7MsgShouldRetNullPointerException() {
            assertThrows(
                    NullPointerException.class,
                    () -> parser.parse("MSH|||", null)
            );
        }


        @Test
        @DisplayName("Empty message should throw Illegal Argument Exception")
        void parse_EmptyStrMsgShouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> parser.parse("", new HL7Message())
            );
        }


        @Test
        @DisplayName("Message with only whitespace should throw Illegal Argument Exception")
        void parse_StrMsgWithWhitespaceShouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> parser.parse("      ", new HL7Message())
            );
        }


        @Test
        @DisplayName("message not containing MSH within in the first 3 characters should throw Illegal Argument Exception")
        void parse_MsgNotHavingMSHinFirstFourShouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> parser.parse("Test Message", new HL7Message())
            );
        }


        @Test
        @DisplayName("empty MSH encoding field should NOT throw Out of bounds exception")
        void parse_EmptyMshEncodingCharShouldNotThrowException() {
            assertDoesNotThrow(
                    () -> parser.parse(
                            "MSH||test|test|test|", new HL7Message())
            );
        }

    }


    @Nested
    @DisplayName("When parsing valid HL7 messages")
    class ParserValidMsgTests {
        private HL7Message result;


        @BeforeEach
        void setUp() {
            String validHl7Message = """
                    MSH|^~\\&|TEST|TEST|  Lab_System  |Lab_Dept|202603051000||ADT^A04|32143214321|P|2.3
                    PID|||PI12345||DOE^JOHN^||19800101|M|||123 MAIN ST&APT 4B&METROPOLIS^OH^44123||(555)555-1212~(555)555-1313|||S|
                    PV1||O|2000^2012^1||||1234^SMITH^JOHN^MD|||SUR||||ADM|A0
                    """;
            result = parser.parse(validHl7Message, new HL7Message());
        }


        @Test
        @DisplayName("should have 3 segments total")
        void msgShouldHaveThreeSegments() {
            assertEquals(3, result.getSegments().size());
        }


        @Test
        @DisplayName("should have MSH, PID and PV1 as their segment names")
        void msgShouldHaveCorrectSegmentHeaders() {
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
        @DisplayName("should contain PI12345 in PID-3 (Internal ID)")
        void msgShouldHavePI12345InPIDThree() {
            final var patID = result
                    .getSegments().get(1)
                    .getFieldList().get(3)
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().getFirst();
            assertEquals("PI12345", patID);
        }


        @Test
        @DisplayName("should have DOE, JOHN in PID-5 (Patient Name)")
        void msgShouldHaveDoeJohninPIDFive() {
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


        @Test
        @DisplayName("Should have (555)555-1212 and (555)555-1313 in PID-13")
        void msgShouldHandleFieldRepetitionsInPIDThirteen() {
            final var pidPhoneNumField = result
                    .getSegments().get(1)
                    .getFieldList().get(13);

            final var phoneOne = pidPhoneNumField
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().getFirst();

            final var phoneTwo = pidPhoneNumField
                    .getRepetitionList().get(1)
                    .getComponentList().getFirst()
                    .getSubcomponentList().getFirst();


            assertEquals("(555)555-1212", phoneOne);
            assertEquals("(555)555-1313", phoneTwo);
        }


        @Test
        @DisplayName("should trim and uppercase MSH-5 value (  Lab_System   )")
        void msgShouldTrimAndUpperCaseValuesInMSHFive() {
            final var mshFiveField = result
                    .getSegments().getFirst()
                    .getFieldList().get(4)
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().getFirst();

            assertEquals("LAB_SYSTEM", mshFiveField);
        }


        @Test
        @DisplayName("should contain 123 MAIN ST, APT 4B and METROPOLIS as subcomponents in PID-11 (Patient Address)")
        void msgShouldHandleSubcomponentInPIDEleven() {
            final var pidAddressField = result
                    .getSegments().get(1)
                    .getFieldList().get(11);

            final var pidAddressCompOne = pidAddressField
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().getFirst();

            final var pidAddressCompTwo = pidAddressField
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().get(1);

            final var pidAddressCompThree = pidAddressField
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().get(2);

            assertEquals("123 MAIN ST", pidAddressCompOne);
            assertEquals("APT 4B", pidAddressCompTwo);
            assertEquals("METROPOLIS", pidAddressCompThree);
        }

    }


    @Nested
    @DisplayName("When parsing HL7 message with custom encodings")
    class CustomEncodingTests {
        private HL7Message parsedMsg = new HL7Message();


        @BeforeEach
        void setUp() {
            parser = new BasicMessageParser();
            String msgWCustomEnc = """
                    MSH!:*\\#!SENDING_APP!SENDING_FAC!REC_APP!REC_FAC!202310271030!!ADT:A01!12345!P!2.5
                    EVN!!202310271030
                    PID!1!!PATID1234###Facility:MR!!Doe:John:Character:Suffix!!19800101!M!!!123 Main St##Anytown:NY:12345!!123-456-7987*126-555-0200!!!S!!P00123456
                    PV1!1!I!W4:402:A!!!!12345:Smith:Jane:Dr!!!SUR!!!!ADM!A0!
                    """;
            parsedMsg = parser.parse(msgWCustomEnc, parsedMsg);
        }


        @Test
        @DisplayName("should contain 4 segments total")
        void msgShouldContainFourSegments() {
            assertEquals(4, parsedMsg.getSegments().size());
        }

        @Test
        @DisplayName("should contain 12 fields in MSH")
        void msgShouldHaveTwelveFieldsInMsh() {
            var mshFieldsSize = parsedMsg.getSegments().getFirst().getFieldList().size();
            assertEquals(12, mshFieldsSize);
        }

        @Test
        @DisplayName("Should have 123-456-7987 and 126-555-0200 in PID-13")
        void msgShouldParseRepetitionsWithCustomEnc() {
            final var pidPhoneNumField = parsedMsg
                    .getSegments().get(2)
                    .getFieldList().get(13);

            final var phoneOne = pidPhoneNumField
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().getFirst();

            final var phoneTwo = pidPhoneNumField
                    .getRepetitionList().get(1)
                    .getComponentList().getFirst()
                    .getSubcomponentList().getFirst();


            assertEquals("123-456-7987", phoneOne);
            assertEquals("126-555-0200", phoneTwo);
        }


        @Test
        @DisplayName("should have ADT and A01 in MSH-9 (Message Type)")
        void msgShouldParseComponentsWithCustomEnc() {
            final var mshMsgTypeField = parsedMsg
                    .getSegments().getFirst()
                    .getFieldList().get(8);

            final var mshTypeFirstComp = mshMsgTypeField
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().getFirst();

            final var mshTypeSecComp = mshMsgTypeField
                    .getRepetitionList().getFirst()
                    .getComponentList().get(1)
                    .getSubcomponentList().getFirst();

            assertEquals("ADT", mshTypeFirstComp);
            assertEquals("A01", mshTypeSecComp);
        }


        @Test
        @DisplayName("should contain PATID1234 and FACILITY for PID-3")
        void msgShouldParseSubcomponentsWithCustomEnc() {
            final var getPidPatIDField = parsedMsg
                    .getSegments().get(2)
                    .getFieldList().get(3);

            final var pidPatIdFirstSubcomp = getPidPatIDField
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().getFirst();

            final var pidPatIdSecondSubcomp = getPidPatIDField
                    .getRepetitionList().getFirst()
                    .getComponentList().getFirst()
                    .getSubcomponentList().get(3);

            assertEquals("PATID1234", pidPatIdFirstSubcomp);
            assertEquals("FACILITY", pidPatIdSecondSubcomp);
        }
    }
}
