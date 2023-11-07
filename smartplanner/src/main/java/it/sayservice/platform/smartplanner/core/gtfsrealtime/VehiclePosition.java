/**
 * Copyright 2011-2016 SAYservice s.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

// Generated by http://code.google.com/p/protostuff/ ... DO NOT EDIT!
// Generated from proto

package it.sayservice.platform.smartplanner.core.gtfsrealtime;

import javax.annotation.Generated;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

import io.protostuff.GraphIOUtil;
import io.protostuff.Input;
import io.protostuff.Message;
import io.protostuff.Output;
import io.protostuff.Schema;


@Generated("java_bean")
public final class VehiclePosition implements Externalizable, Message<VehiclePosition>, Schema<VehiclePosition>
{
    @Generated("java_bean")
    public enum VehicleStopStatus implements io.protostuff.EnumLite<VehicleStopStatus>
    {
        INCOMING_AT(0),
        STOPPED_AT(1),
        IN_TRANSIT_TO(2);
        
        public final int number;
        
        private VehicleStopStatus (int number)
        {
            this.number = number;
        }
        
        public int getNumber()
        {
            return number;
        }
        
        public static VehicleStopStatus valueOf(int number)
        {
            switch(number) 
            {
                case 0: return INCOMING_AT;
                case 1: return STOPPED_AT;
                case 2: return IN_TRANSIT_TO;
                default: return null;
            }
        }
    }
    @Generated("java_bean")
    public enum CongestionLevel implements io.protostuff.EnumLite<CongestionLevel>
    {
        UNKNOWN_CONGESTION_LEVEL(0),
        RUNNING_SMOOTHLY(1),
        STOP_AND_GO(2),
        CONGESTION(3),
        SEVERE_CONGESTION(4);
        
        public final int number;
        
        private CongestionLevel (int number)
        {
            this.number = number;
        }
        
        public int getNumber()
        {
            return number;
        }
        
        public static CongestionLevel valueOf(int number)
        {
            switch(number) 
            {
                case 0: return UNKNOWN_CONGESTION_LEVEL;
                case 1: return RUNNING_SMOOTHLY;
                case 2: return STOP_AND_GO;
                case 3: return CONGESTION;
                case 4: return SEVERE_CONGESTION;
                default: return null;
            }
        }
    }
    @Generated("java_bean")
    public enum OccupancyStatus implements io.protostuff.EnumLite<OccupancyStatus>
    {
        EMPTY(0),
        MANY_SEATS_AVAILABLE(1),
        FEW_SEATS_AVAILABLE(2),
        STANDING_ROOM_ONLY(3),
        CRUSHED_STANDING_ROOM_ONLY(4),
        FULL(5),
        NOT_ACCEPTING_PASSENGERS(6);
        
        public final int number;
        
        private OccupancyStatus (int number)
        {
            this.number = number;
        }
        
        public int getNumber()
        {
            return number;
        }
        
        public static OccupancyStatus valueOf(int number)
        {
            switch(number) 
            {
                case 0: return EMPTY;
                case 1: return MANY_SEATS_AVAILABLE;
                case 2: return FEW_SEATS_AVAILABLE;
                case 3: return STANDING_ROOM_ONLY;
                case 4: return CRUSHED_STANDING_ROOM_ONLY;
                case 5: return FULL;
                case 6: return NOT_ACCEPTING_PASSENGERS;
                default: return null;
            }
        }
    }


    public static Schema<VehiclePosition> getSchema()
    {
        return DEFAULT_INSTANCE;
    }

    public static VehiclePosition getDefaultInstance()
    {
        return DEFAULT_INSTANCE;
    }

    static final VehiclePosition DEFAULT_INSTANCE = new VehiclePosition();

    static final VehicleStopStatus DEFAULT_CURRENT_STATUS = VehicleStopStatus.IN_TRANSIT_TO;
    
    private TripDescriptor trip;
    private Position position;
    private Integer currentStopSequence;
    private VehicleStopStatus currentStatus = DEFAULT_CURRENT_STATUS;
    private Long timestamp;
    private CongestionLevel congestionLevel;
    private String stopId;
    private VehicleDescriptor vehicle;
    private OccupancyStatus occupancyStatus;

    public VehiclePosition()
    {

    }

    // getters and setters

    // trip

    public TripDescriptor getTrip()
    {
        return trip;
    }


    public void setTrip(TripDescriptor trip)
    {
        this.trip = trip;
    }

    // position

    public Position getPosition()
    {
        return position;
    }


    public void setPosition(Position position)
    {
        this.position = position;
    }

    // currentStopSequence

    public Integer getCurrentStopSequence()
    {
        return currentStopSequence;
    }


    public void setCurrentStopSequence(Integer currentStopSequence)
    {
        this.currentStopSequence = currentStopSequence;
    }

    // currentStatus

    public VehicleStopStatus getCurrentStatus()
    {
        return currentStatus;
    }


    public void setCurrentStatus(VehicleStopStatus currentStatus)
    {
        this.currentStatus = currentStatus;
    }

    // timestamp

    public Long getTimestamp()
    {
        return timestamp;
    }


    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }

    // congestionLevel

    public CongestionLevel getCongestionLevel()
    {
        return congestionLevel;
    }


    public void setCongestionLevel(CongestionLevel congestionLevel)
    {
        this.congestionLevel = congestionLevel;
    }

    // stopId

    public String getStopId()
    {
        return stopId;
    }


    public void setStopId(String stopId)
    {
        this.stopId = stopId;
    }

    // vehicle

    public VehicleDescriptor getVehicle()
    {
        return vehicle;
    }


    public void setVehicle(VehicleDescriptor vehicle)
    {
        this.vehicle = vehicle;
    }

    // occupancyStatus

    public OccupancyStatus getOccupancyStatus()
    {
        return occupancyStatus;
    }


    public void setOccupancyStatus(OccupancyStatus occupancyStatus)
    {
        this.occupancyStatus = occupancyStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final VehiclePosition that = (VehiclePosition) obj;
        return
                Objects.equals(this.trip, that.trip) &&
                Objects.equals(this.position, that.position) &&
                Objects.equals(this.currentStopSequence, that.currentStopSequence) &&
                Objects.equals(this.currentStatus, that.currentStatus) &&
                Objects.equals(this.timestamp, that.timestamp) &&
                Objects.equals(this.congestionLevel, that.congestionLevel) &&
                Objects.equals(this.stopId, that.stopId) &&
                Objects.equals(this.vehicle, that.vehicle) &&
                Objects.equals(this.occupancyStatus, that.occupancyStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trip, position, currentStopSequence, currentStatus, timestamp, congestionLevel, stopId, vehicle, occupancyStatus);
    }

    @Override
    public String toString() {
        return "VehiclePosition{" +
                    "trip=" + trip +
                    ", position=" + position +
                    ", currentStopSequence=" + currentStopSequence +
                    ", currentStatus=" + currentStatus +
                    ", timestamp=" + timestamp +
                    ", congestionLevel=" + congestionLevel +
                    ", stopId=" + stopId +
                    ", vehicle=" + vehicle +
                    ", occupancyStatus=" + occupancyStatus +
                '}';
    }
    // java serialization

    public void readExternal(ObjectInput in) throws IOException
    {
        GraphIOUtil.mergeDelimitedFrom(in, this, this);
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        GraphIOUtil.writeDelimitedTo(out, this, this);
    }

    // message method

    public Schema<VehiclePosition> cachedSchema()
    {
        return DEFAULT_INSTANCE;
    }

    // schema methods

    public VehiclePosition newMessage()
    {
        return new VehiclePosition();
    }

    public Class<VehiclePosition> typeClass()
    {
        return VehiclePosition.class;
    }

    public String messageName()
    {
        return VehiclePosition.class.getSimpleName();
    }

    public String messageFullName()
    {
        return VehiclePosition.class.getName();
    }

    public boolean isInitialized(VehiclePosition message)
    {
        return true;
    }

    public void mergeFrom(Input input, VehiclePosition message) throws IOException
    {
        for(int number = input.readFieldNumber(this);; number = input.readFieldNumber(this))
        {
            switch(number)
            {
                case 0:
                    return;
                case 1:
                    message.trip = input.mergeObject(message.trip, TripDescriptor.getSchema());
                    break;

                case 2:
                    message.position = input.mergeObject(message.position, Position.getSchema());
                    break;

                case 3:
                    message.currentStopSequence = input.readUInt32();
                    break;
                case 4:
                    message.currentStatus = VehicleStopStatus.valueOf(input.readEnum());
                    break;

                case 5:
                    message.timestamp = input.readUInt64();
                    break;
                case 6:
                    message.congestionLevel = CongestionLevel.valueOf(input.readEnum());
                    break;

                case 7:
                    message.stopId = input.readString();
                    break;
                case 8:
                    message.vehicle = input.mergeObject(message.vehicle, VehicleDescriptor.getSchema());
                    break;

                case 9:
                    message.occupancyStatus = OccupancyStatus.valueOf(input.readEnum());
                    break;

                default:
                    input.handleUnknownField(number, this);
            }   
        }
    }


    public void writeTo(Output output, VehiclePosition message) throws IOException
    {
        if(message.trip != null)
             output.writeObject(1, message.trip, TripDescriptor.getSchema(), false);


        if(message.position != null)
             output.writeObject(2, message.position, Position.getSchema(), false);


        if(message.currentStopSequence != null)
            output.writeUInt32(3, message.currentStopSequence, false);

        if(message.currentStatus != null && message.currentStatus != DEFAULT_CURRENT_STATUS)
             output.writeEnum(4, message.currentStatus.number, false);

        if(message.timestamp != null)
            output.writeUInt64(5, message.timestamp, false);

        if(message.congestionLevel != null)
             output.writeEnum(6, message.congestionLevel.number, false);

        if(message.stopId != null)
            output.writeString(7, message.stopId, false);

        if(message.vehicle != null)
             output.writeObject(8, message.vehicle, VehicleDescriptor.getSchema(), false);


        if(message.occupancyStatus != null)
             output.writeEnum(9, message.occupancyStatus.number, false);
    }

    public String getFieldName(int number)
    {
        return Integer.toString(number);
    }

    public int getFieldNumber(String name)
    {
        return Integer.parseInt(name);
    }
    

}
