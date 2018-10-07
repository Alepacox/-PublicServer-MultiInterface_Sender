package it.unicam.project.multiinterfacesender.Receive;

import java.io.*;

public class Message implements Serializable {

    private static final long serialVersionUID = 5950169519310163575L;

    String dtoken;
    String fileName;
    byte[] fileChunk;

    /**
     * @param _isFileName
     * @param _dtoken dtoken
     */
    public Message(String _dtoken,boolean _isFileName)
    {
        if(_isFileName==true)
        {
            this.dtoken = _dtoken;
            this.fileName = null;
            this.fileChunk = null;
        }
        else
        {
            this.dtoken = null;
            this.fileName = _dtoken;
            this.fileChunk = null;
        }

    }

    /**
     *
     * @param fileChunk byte_array
     */
    public Message(byte[] fileChunk)
    {
        this.dtoken = null;
        this.fileName = null;
        this.fileChunk = fileChunk;
    }




    /**
     * Serialize
     * @return byte[]
     */
    public byte[] serialize(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            yourBytes = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return yourBytes;
    }

    /**
     * Deserialize
     * @param byte_array byte[]
     * @return it.fica.com.Message
     */
    public Message deserialize(byte[] byte_array){
        ByteArrayInputStream bis = new ByteArrayInputStream(byte_array);
        ObjectInput in = null;
        Message o = null;
        try {
            in = new ObjectInputStream(bis);
            o = (Message) in.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return o;
    }
}
