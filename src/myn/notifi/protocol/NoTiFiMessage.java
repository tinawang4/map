/*
 * Classname : NoTiFiMessage
 *
 * Version information : 1.0
 *
 * Date : 10/29/2015
 *
 * Copyright notice
 * 
 * Author : Tong Wang
 */
package myn.notifi.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Purpose of this class:
 * 
 * Represents generic portion of NoTiFi message (abstract)
 * 
 * */

public abstract class NoTiFiMessage {
    /*initial constant value for error message*/
    private final static String errorMsg = "Invalid id";
    private  static final String errorMsg2 = "Invalid code";
    private  static final int range = 255;
    private  static final int range2 = 65535;
    /*member variable for the parent class*/
    protected int version;    
    protected int msgId;
    
    /**
     * check validation of id
     * @param id - id to be checked
     * @param num - bytes of id
     * @throws IllegalArgumentException - if bad version or code
     */
    protected static void checkId(int id ,int num) throws IllegalArgumentException  {
        if(id < 0) {
            throw new IllegalArgumentException(errorMsg);
        }
        if(num == 1) {
            if(id > range)
                throw new IllegalArgumentException(errorMsg);
        }
        if(num == 2) {
            if(id > range2)
                throw new IllegalArgumentException(errorMsg);
        }
    }
    
    
    /**
     * Constructs base message using deserialization
     * 
     * @param in -input source
     * @throws IOException -if I/O problem
     * @throws IllegalArgumentException - if bad version or code
     */
    public NoTiFiMessage(DataInputStream in) throws IOException, IllegalArgumentException {
        
        byte b = in.readByte();
        checkId(b,1);
        int temp = Parser.readVer(b);
        checkId(temp,1);
        version=temp;
        temp=Parser.readInt(in, 1);
        checkId(temp,1);
        msgId=temp;
    }
    
    /**
     * Constructs base message with given values
     * 
     * @param msgId - message ID
     * @throws IllegalArgumentException - if bad version or code
     */
    public NoTiFiMessage(int msgId) throws IllegalArgumentException {
        checkId(msgId,1);
        this.msgId=msgId;
    }
    /**
     * Empty constructor used by children classes
     * 
     * @throws IllegalArgumentException - if bad version or code
     * @throws IOException - if I/O problem
     */
    public NoTiFiMessage() throws IllegalArgumentException, IOException {
        
    }
    
    /**
     * Deserializes from byte array
     * 
     * @param pkt- byte array to deserialize
     * @return a specific NoTiFi message resulting from deserialization
     * @throws IOException - if I/O problem 
     * @throws IllegalArgumentException - if bad version or code
     */
    static public NoTiFiMessage decode (byte[] pkt) throws IOException, IllegalArgumentException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(pkt));
        //get version and check valiation o
        byte b = in.readByte();
        checkId(b,1);
        int temp = Parser.readVer(b);
        checkId(temp,1);
        int version=temp;
        //get code and check validation
        temp=Parser.readCode(b);
        checkId(temp,1);
        int code = temp;
        //get MSG ID and check validation
        temp=Parser.readInt(in, 1);
        checkId(temp,1);
        int msgId=temp;
        //call specific child function based on code info
        NoTiFiMessage msg=null;
        
        switch(code) {
        case ConstVal.reg:
            msg=new NoTiFiRegister(in,msgId,version);
            break;
        case ConstVal.add:
            msg=new NoTiFiLocationAddition(in,msgId,version);
            break;
        case ConstVal.del:
            msg=new NoTiFiLocationDeletion(in,msgId,version);
            break;
        case ConstVal.deReg:
            msg=new NoTiFiDeregister(in,msgId,version);
            break;
        case ConstVal.error:
            msg=new NoTiFiError(in,msgId,version);
            break;
        case ConstVal.ACK:
            msg=new NoTiFiACK(in,msgId,version);
            break;
        default:
            throw new IllegalArgumentException(errorMsg2);
        }
        
        return msg;
    } 
    
    /**
     * Serializes message
     * 
     * @return serialized message bytes
     * @throws IOException - if I/O problem
     */
    public byte[] encode() throws IOException {
        
        ByteArrayOutputStream out = new ByteArrayOutputStream( );
        //encode header: version, code
        out.write(encodeHeader());
        //encode messageId
        out.write(Parser.intToByte(msgId, 1));
        //encode data
        out.write(encodeHelper());
        return out.toByteArray();
    }
    
    /**
     * abstract function, encode version and code info
     * 
     * @return Serializes message for header
     */
    abstract public byte[] encodeHeader();
    
    /**
     * abstract function, encode data. Type of data depends on code
     * 
     * @return - Serializes message for data
     * @throws IOException - if I/O problem
     */
    abstract public byte[] encodeHelper() throws IOException;
    
    /**
     * Get operation code
     * 
     * @return - operation code
     */
    public abstract int getCode();

    /**
     * Get message ID
     * 
     * @return - message ID
     */
    public int getMsgId() {
        return msgId;
    }

    /**
     * Set message ID
     * 
     * @param msgId - message ID
     * @throws IllegalArgumentException - if bad version or code
     */
    public void setMsgId(int msgId) throws IllegalArgumentException {
        checkId(msgId,1);
        this.msgId=msgId;
    }

    /**
     * Output result to human readable string
     * @return - result output
     * 
     * */
    public String toString() {
        return null;
    } 
    
}