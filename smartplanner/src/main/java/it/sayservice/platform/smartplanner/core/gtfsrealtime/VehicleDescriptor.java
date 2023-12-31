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
public final class VehicleDescriptor implements Externalizable, Message<VehicleDescriptor>, Schema<VehicleDescriptor>
{

    public static Schema<VehicleDescriptor> getSchema()
    {
        return DEFAULT_INSTANCE;
    }

    public static VehicleDescriptor getDefaultInstance()
    {
        return DEFAULT_INSTANCE;
    }

    static final VehicleDescriptor DEFAULT_INSTANCE = new VehicleDescriptor();

    
    private String id;
    private String label;
    private String licensePlate;

    public VehicleDescriptor()
    {

    }

    // getters and setters

    // id

    public String getId()
    {
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }

    // label

    public String getLabel()
    {
        return label;
    }


    public void setLabel(String label)
    {
        this.label = label;
    }

    // licensePlate

    public String getLicensePlate()
    {
        return licensePlate;
    }


    public void setLicensePlate(String licensePlate)
    {
        this.licensePlate = licensePlate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final VehicleDescriptor that = (VehicleDescriptor) obj;
        return
                Objects.equals(this.id, that.id) &&
                Objects.equals(this.label, that.label) &&
                Objects.equals(this.licensePlate, that.licensePlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, licensePlate);
    }

    @Override
    public String toString() {
        return "VehicleDescriptor{" +
                    "id=" + id +
                    ", label=" + label +
                    ", licensePlate=" + licensePlate +
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

    public Schema<VehicleDescriptor> cachedSchema()
    {
        return DEFAULT_INSTANCE;
    }

    // schema methods

    public VehicleDescriptor newMessage()
    {
        return new VehicleDescriptor();
    }

    public Class<VehicleDescriptor> typeClass()
    {
        return VehicleDescriptor.class;
    }

    public String messageName()
    {
        return VehicleDescriptor.class.getSimpleName();
    }

    public String messageFullName()
    {
        return VehicleDescriptor.class.getName();
    }

    public boolean isInitialized(VehicleDescriptor message)
    {
        return true;
    }

    public void mergeFrom(Input input, VehicleDescriptor message) throws IOException
    {
        for(int number = input.readFieldNumber(this);; number = input.readFieldNumber(this))
        {
            switch(number)
            {
                case 0:
                    return;
                case 1:
                    message.id = input.readString();
                    break;
                case 2:
                    message.label = input.readString();
                    break;
                case 3:
                    message.licensePlate = input.readString();
                    break;
                default:
                    input.handleUnknownField(number, this);
            }   
        }
    }


    public void writeTo(Output output, VehicleDescriptor message) throws IOException
    {
        if(message.id != null)
            output.writeString(1, message.id, false);

        if(message.label != null)
            output.writeString(2, message.label, false);

        if(message.licensePlate != null)
            output.writeString(3, message.licensePlate, false);
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
