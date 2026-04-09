package hl7Viewer.nonGui.parser;

import org.junit.jupiter.api.BeforeEach;
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
        void msgShouldHaveThreeSegments() {
            assertEquals(3, result.getSegments().size());
        }


        @Test
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
    class CustomEncodingTests {
        private HL7Message parsedMsg = new HL7Message();


        @BeforeEach
        void setUp() {
            parser = new BasicMessageParser();
            String msgWCustomEnc = """
                    MSH!:*\\#|SENDING_APP|SENDING_FAC|REC_APP|REC_FAC|202310271030||ADT:A01|12345|P|2.5
                    EVN||202310271030
                    PID|1||PATID1234###Facility:MR||Doe:John:Character:Suffix||19800101|M|||123 Main St##Anytown:NY:12345||123-456-7987*126-555-0200|||S||P00123456
                    PV1|1|I|W4:402:A||||12345:Smith:Jane:Dr|||SUR||||ADM|A0|
                    """;
            parsedMsg = parser.parse(msgWCustomEnc, parsedMsg);
        }

        @Test
        void msgShouldParseSegmentsWithCustomEnc() {
            assertEquals(4, parsedMsg.getSegments().size());
        }


        @Test
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
