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
public final class TimeRange implements Externalizable, Message<TimeRange>, Schema<TimeRange>
{

    public static Schema<TimeRange> getSchema()
    {
        return DEFAULT_INSTANCE;
    }

    public static TimeRange getDefaultInstance()
    {
        return DEFAULT_INSTANCE;
    }

    static final TimeRange DEFAULT_INSTANCE = new TimeRange();

    
    private Long start;
    private Long end;

    public TimeRange()
    {

    }

    // getters and setters

    // start

    public Long getStart()
    {
        return start;
    }


    public void setStart(Long start)
    {
        this.start = start;
    }

    // end

    public Long getEnd()
    {
        return end;
    }


    public void setEnd(Long end)
    {
        this.end = end;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final TimeRange that = (TimeRange) obj;
        return
                Objects.equals(this.start, that.start) &&
                Objects.equals(this.end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "TimeRange{" +
                    "start=" + start +
                    ", end=" + end +
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

    public Schema<TimeRange> cachedSchema()
    {
        return DEFAULT_INSTANCE;
    }

    // schema methods

    public TimeRange newMessage()
    {
        return new TimeRange();
    }

    public Class<TimeRange> typeClass()
    {
        return TimeRange.class;
    }

    public String messageName()
    {
        return TimeRange.class.getSimpleName();
    }

    public String messageFullName()
    {
        return TimeRange.class.getName();
    }

    public boolean isInitialized(TimeRange message)
    {
        return true;
    }

    public void mergeFrom(Input input, TimeRange message) throws IOException
    {
        for(int number = input.readFieldNumber(this);; number = input.readFieldNumber(this))
        {
            switch(number)
            {
                case 0:
                    return;
                case 1:
                    message.start = input.readUInt64();
                    break;
                case 2:
                    message.end = input.readUInt64();
                    break;
                default:
                    input.handleUnknownField(number, this);
            }   
        }
    }


    public void writeTo(Output output, TimeRange message) throws IOException
    {
        if(message.start != null)
            output.writeUInt64(1, message.start, false);

        if(message.end != null)
            output.writeUInt64(2, message.end, false);
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
