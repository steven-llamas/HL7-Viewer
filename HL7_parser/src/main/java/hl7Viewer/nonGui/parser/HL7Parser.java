package hl7Viewer.nonGui.parser;


public class HL7Parser {
    enum HL7Level {
        SEGMENT      ("\r"),
        FIELD        ("\\|"),
        COMPONENT    ("\\^"),
        SUBCOMPONENT ("&");

        private String _delimiter;

        HL7Level(String delimiter) {
            _delimiter = delimiter;
        }

        HL7Level next () {
            int ord = this.ordinal();
            return (ord < HL7Level.values().length - 1)
                    ? HL7Level.values() [ord + 1] : null;
        }
    }

    public HL7Node parse(String hl7) {
        HL7Node root = new HL7Node("MSG", -1);
        parseLevel(root, hl7, HL7Level.SEGMENT);
        return root;
    }

    private void parseLevel(HL7Node parent, String text, HL7Level level) {
        String[] tokens = text.split(level._delimiter, -1);

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.isEmpty()) continue;

            String tag;

            // Special handling for segments
            if (level == HL7Level.SEGMENT) {
                // First 3 characters of the token are the segment name
                String segmentName = token.length() >= 3 ? token.substring(0, 3) : "UNK";
                tag = segmentName;

                HL7Node segmentNode = new HL7Node(tag, i);
                // Now parse the rest of the segment (starting from index 1 or after segment name)
                parseLevel(segmentNode, token.substring(4), level.next()); // skip segment name and delimiter
                parent.children.add(segmentNode);
            } else {
                // For FIELD, COMPONENT, SUBCOMPONENT
                tag = parent.tag + "-" + i;
                HL7Node node = new HL7Node(tag, i);

                HL7Level next = level.next();
                if (next != null) {
                    parseLevel(node, token, next);
                } else {
                    node.value = token;
                }

                parent.children.add(node);
            }
        }
    }
}

