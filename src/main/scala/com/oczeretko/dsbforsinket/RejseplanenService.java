package com.oczeretko.dsbforsinket;

import com.google.gson.annotations.*;

import java.util.*;

import retrofit2.*;
import retrofit2.http.*;

public interface RejseplanenService {
    @GET("departureBoard?useBus=0&offsetTime=0&format=json")
    Call<DepartureResult> getDepartures(@Query("id") String stationId);

    class DepartureResult {
        @SerializedName("DepartureBoard")
        private DepartureBoard departureBoard;

        public DepartureBoard getDepartureBoard() {
            return departureBoard;
        }
    }

    class DepartureBoard {
        @SerializedName("Departure")
        private ArrayList<Departure> departures;

        public ArrayList<Departure> getDepartures() {
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
        @SerializedName("rtTime")
        private String updatedTime;
        @SerializedName("rtDate")
        private String updatedDate;
        @SerializedName("rtTrack")
        private String updatedTrack;
        private boolean cancelled;
        private String state;
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

        public boolean isCancelled() {
            return cancelled;
        }

        public String getState() {
            return state;
        }
    }
}

