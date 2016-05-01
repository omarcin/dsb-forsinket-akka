package com.oczeretko.dsbforsinket;

import com.google.gson.annotations.*;

import java.util.*;

import retrofit2.*;
import retrofit2.http.*;

public interface RejseplanenService {
    @GET("departureBoard?id={stationId}&useBus=0&offsetTime=0&format=json")
    Call<DepartureBoard> getDepartures(@Query("stationId") String stationId);

    class DepartureBoard {
        @SerializedName("Departure") private List<Departure> departures;

        public List<Departure> getDepartures() {
            return departures;
        }
    }

    class Departure {
        private String name;
        private String type;
        private String stop;
        private String time;
        private String date;
        private String messages;
        private String track;
        @SerializedName("rtTime") private String updatedTime;
        @SerializedName("rtDate") private String updatedDate;
        @SerializedName("rtTrack") private String updatedTrack;
        private String finalStop;
        private String direction;

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getStop() {
            return stop;
        }

        public String getTime() {
            return time;
        }

        public String getDate() {
            return date;
        }

        public String getMessages() {
            return messages;
        }

        public String getTrack() {
            return track;
        }

        public String getUpdatedTime() {
            return updatedTime;
        }

        public String getUpdatedDate() {
            return updatedDate;
        }

        public String getUpdatedTrack() {
            return updatedTrack;
        }

        public String getFinalStop() {
            return finalStop;
        }

        public String getDirection() {
            return direction;
        }
    }
}

