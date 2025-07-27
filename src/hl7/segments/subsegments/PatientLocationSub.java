package hl7.segments.subsegments;

import hl7.segments.Hl7segment;

public class PatientLocationSub extends Hl7segment {
        private String pointOfCare;
        private String room;
        private String bed;
        private String facility;
        private String locationStatus;
        private String personLocationType;
        private String building;
        private String floor;
        private String locationType;

        public PatientLocationSub(String field) {
            String[] parts = field.split("\\^");
            this.pointOfCare = get(parts, 0);
            this.room = get(parts, 1);
            this.bed = get(parts, 2);
            this.facility = get(parts, 3);
            this.locationStatus = get(parts, 4);
            this.personLocationType = get(parts, 5);
            this.building = get(parts, 6);
            this.floor = get(parts, 7);
            this.locationType = get(parts, 8);
        }
    }

