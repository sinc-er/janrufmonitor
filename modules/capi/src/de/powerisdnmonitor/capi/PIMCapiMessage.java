package de.powerisdnmonitor.capi;

import de.powerisdnmonitor.capi.util.ByteArray;

import org.capi.capi20.*;
import java.util.Vector;

/**
 *  This class is an implementation of a CAPI message object.
 *
 *@author     Thilo Brandt
 *@created    17. August 2002
 */
public class PIMCapiMessage implements CapiMessage {

    private int type = 0, appID = 0, number = 0;
    private byte[] dataB3 = null;
    private int[] types = null;
    private Vector fields = null, params = null;

    /**
     *  Description of the Field
     */
    public final static int ALERT_REQ = 0x180, ALERT_CONF = 0x181,
            CONNECT_REQ = 0x280, CONNECT_CONF = 0x281,
            CONNECT_IND = 0x282, CONNECT_RESP = 0x283,
            CONNECT_ACTIVE_IND = 0x382, CONNECT_ACTIVE_RESP = 0x383,
            CONNECT_B3_ACTIVE_IND = 0x8382, CONNECT_B3_ACTIVE_RESP = 0x8383,
            CONNECT_B3_REQ = 0x8280, CONNECT_B3_CONF = 0x8281,
            CONNECT_B3_IND = 0x8282, CONNECT_B3_RESP = 0x8283,
            DATA_B3_REQ = 0x8680, DATA_B3_CONF = 0x8681,
            DATA_B3_IND = 0x8682, DATA_B3_RESP = 0x8683,
            DISCONNECT_B3_REQ = 0x8480, DISCONNECT_B3_CONF = 0x8481,
            DISCONNECT_B3_IND = 0x8482, DISCONNECT_B3_RESP = 0x8483,
            DISCONNECT_REQ = 0x480, DISCONNECT_CONF = 0x481,
            DISCONNECT_IND = 0x482, DISCONNECT_RESP = 0x483,
            INFO_IND = 0x882, INFO_RESP = 0x883,
            LISTEN_REQ = 0x580, LISTEN_CONF = 0x581,
			FACILITY_CONF = 0x8081, FACILITY_IND = 0x8082, FACILITY_RESP = 0x8083;

    private final static int T_BYTE = 1, T_WORD = 2, T_DWORD = 4, T_STRUCT = 0;
    private static String[]
            ALERT_REQ_FIELDS = {"PLCI", "Additional info"},
            ALERT_CONF_FIELDS = {"PCLI", "Info"},
            CONNECT_REQ_FIELDS = {"Controller", "CIP Value", "Called party number", "Calling party number",
            "Called party subaddress", "Calling party subaddress", "B protocol", "BC", "LLC", "HLC",
            "Additional Info"},
            CONNECT_CONF_FIELDS = {"PLCI", "Info"},
            CONNECT_IND_FIELDS = {"PLCI", "CIP Value", "Called party number", "Calling party number",
            "Called party subaddress", "Calling party subaddress", "BC", "LLC", "HLC",
            "Additional Info", "Calling party number 2"},
            CONNECT_RESP_FIELDS = {"PLCI", "Reject", "B protocol", "Connected Number",
            "Connected subaddress", "LLC", "Additional Info"},
            CONNECT_ACTIVE_IND_FIELDS = {"PLCI", "Connected number", "Connected subaddress", "LLC"},
            CONNECT_ACTIVE_RESP_FIELDS = {"PLCI"},
            CONNECT_B3_ACTIVE_IND_FIELDS = {"NCCI", "NCPI"},
            CONNECT_B3_ACTIVE_RESP_FIELDS = {"NCCI"},
            CONNECT_B3_REQ_FIELDS = {"PLCI", "NCPI"},
            CONNECT_B3_CONF_FIELDS = {"NCCI", "Info"},
            CONNECT_B3_IND_FIELDS = {"NCCI", "NCPI"},
            CONNECT_B3_RESP_FIELDS = {"NCCI", "Reject", "NCPI"},
            DATA_B3_REQ_FIELDS = {"NCCI", "Data", "Data length", "Data handle", "Flags"},
            DATA_B3_CONF_FIELDS = {"NCCI", "Data handle", "Info"},
            DATA_B3_IND_FIELDS = {"NCCI", "Data", "Data length", "Data handle", "Flags"},
            DATA_B3_RESP_FIELDS = {"NCCI", "Data handle"},
            DISCONNECT_B3_REQ_FIELDS = {"NCCI", "NCPI"},
            DISCONNECT_B3_CONF_FIELDS = {"NCCI", "Info"},
            DISCONNECT_B3_IND_FIELDS = {"NCCI", "Reason_B3", "NCPI"},
            DISCONNECT_B3_RESP_FIELDS = {"NCCI"},
            DISCONNECT_REQ_FIELDS = {"PLCI", "Additional Info"},
            DISCONNECT_CONF_FIELDS = {"PLCI", "Info"},
            DISCONNECT_IND_FIELDS = {"PLCI", "Reason"},
            DISCONNECT_RESP_FIELDS = {"PLCI"},
            INFO_IND_FIELDS = {"Controller/PLCI", "Info number", "Info element"},
            INFO_RESP_FIELDS = {"Controller/PLCI"},
            LISTEN_REQ_FIELDS = {"Controller", "Info mask", "CIP Mask", "CIP Mask 2",
            "Calling party number", "Calling party subaddress"},
			LISTEN_CONF_FIELDS = {"Controller", "Info"},
			FACILITY_IND_FIELDS = {"Controller/PLCI/NCCI", "Facility selector", "Facility indication parameter"},
			FACILITY_CONF_FIELDS = {"Controller/PLCI/NCCI", "Info", "Facility selector", "Facility confirmation parameter"},
			FACILITY_RESP_FIELDS = {"Controller/PLCI/NCCI", "Facility selector", "Facility response parameter"};
	
    private static int[]
			FACILITY_IND_TYPES = {T_DWORD, T_WORD, T_STRUCT},
			FACILITY_RESP_TYPES = {T_DWORD, T_WORD, T_STRUCT},
			FACILITY_CONF_TYPES = {T_DWORD, T_WORD, T_WORD, T_STRUCT},
            ALERT_REQ_TYPES = {T_DWORD, T_STRUCT},
            ALERT_CONF_TYPES = {T_DWORD, T_WORD},
            CONNECT_REQ_TYPES = {T_DWORD, T_WORD, T_STRUCT, T_STRUCT, T_STRUCT, T_STRUCT,
            T_STRUCT, T_STRUCT, T_STRUCT, T_STRUCT, T_STRUCT},
            CONNECT_CONF_TYPES = {T_DWORD, T_WORD},
            CONNECT_IND_TYPES = {T_DWORD, T_WORD, T_STRUCT, T_STRUCT, T_STRUCT, T_STRUCT,
            T_STRUCT, T_STRUCT, T_STRUCT, T_STRUCT, T_STRUCT},
            CONNECT_RESP_TYPES = {T_DWORD, T_WORD, T_STRUCT, T_STRUCT, T_STRUCT, T_STRUCT, T_STRUCT},
            CONNECT_ACTIVE_IND_TYPES = {T_DWORD, T_STRUCT, T_STRUCT, T_STRUCT},
            CONNECT_ACTIVE_RESP_TYPES = {T_DWORD},
            CONNECT_B3_ACTIVE_IND_TYPES = {T_DWORD, T_STRUCT},
            CONNECT_B3_ACTIVE_RESP_TYPES = {T_DWORD},
            CONNECT_B3_REQ_TYPES = {T_DWORD, T_STRUCT},
            CONNECT_B3_CONF_TYPES = {T_DWORD, T_WORD},
            CONNECT_B3_IND_TYPES = {T_DWORD, T_STRUCT},
            CONNECT_B3_RESP_TYPES = {T_DWORD, T_WORD, T_STRUCT},
            DATA_B3_REQ_TYPES = {T_DWORD, T_DWORD, T_WORD, T_WORD, T_WORD},
            DATA_B3_CONF_TYPES = {T_DWORD, T_WORD, T_WORD},
            DATA_B3_IND_TYPES = {T_DWORD, T_DWORD, T_WORD, T_WORD, T_WORD},
            DATA_B3_RESP_TYPES = {T_DWORD, T_WORD},
            DISCONNECT_B3_REQ_TYPES = {T_DWORD, T_STRUCT},
            DISCONNECT_B3_CONF_TYPES = {T_DWORD, T_WORD},
            DISCONNECT_B3_IND_TYPES = {T_DWORD, T_WORD, T_STRUCT},
            DISCONNECT_B3_RESP_TYPES = {T_DWORD},
            DISCONNECT_REQ_TYPES = {T_DWORD, T_STRUCT},
            DISCONNECT_CONF_TYPES = {T_DWORD, T_WORD},
            DISCONNECT_IND_TYPES = {T_DWORD, T_WORD},
            DISCONNECT_RESP_TYPES = {T_DWORD},
            INFO_IND_TYPES = {T_DWORD, T_WORD, T_STRUCT},
            INFO_RESP_TYPES = {T_DWORD},
            LISTEN_REQ_TYPES = {T_DWORD, T_DWORD, T_DWORD, T_DWORD, T_STRUCT, T_STRUCT},
            LISTEN_CONF_TYPES = {T_DWORD, T_WORD};
    private static String
            MSG_JCM01 = "JCM01 error: Illegal message format",
            MSG_JCM02 = "JCM02 error: Corrupt message length",
            MSG_JCM03 = "JCM03 error: No BYTE field of this name",
            MSG_JCM04 = "JCM04 error: No WORD field of this name",
            MSG_JCM05 = "JCM05 error: No DWORD field of this name",
            MSG_JCM06 = "JCM06 error: No STRUCT field of this name",
            MSG_JCM07 = "JCM07 error: No field of this name: ",
            MSG_JCM08 = "JCM08 error: Unsupported message type",
            MSG_JCM09 = "JCM09 error: Illegal STRUCT in message",
            MSG_JCM10 = "JCM10 error: No data assigned to this message type",
            MSG_JCM11 = "JCM11 error: Cannot assign data to this message type";


    /**
     *  Constructor for the PIMCapiMessage object
     *
     *@param  appID              Description of the Parameter
     *@param  type               Description of the Parameter
     *@param  number             Description of the Parameter
     *@exception  CapiException  Description of the Exception
     */
    public PIMCapiMessage(int appID, int type, int number) throws CapiException {
        this.appID = appID;
        this.type = type;
        this.number = number;
        init(null);
    }


    /**
     *  Constructor for the PIMCapiMessage object
     *
     *@param  message            Description of the Parameter
     *@exception  CapiException  Description of the Exception
     */
    public PIMCapiMessage(byte[] message) throws CapiException {
        if (message.length < 8) {
            throw new CapiException(MSG_JCM01);
        }
        if (ByteArray.getLowOrderInt(message, 0, 2) != message.length) {
            throw new CapiException(MSG_JCM02);
        }
        appID = ByteArray.getLowOrderInt(message, 2, 2);
        type = ByteArray.getHighOrderInt(message, 4, 2);
        number = ByteArray.getLowOrderInt(message, 6, 2);
        init(message);
    }


    /**
     *  returns the identification number of the application this messages is
     *  assigned to.
     *
     *@return    the application identification number
     */
    public int getAppID() {
        return appID;
    }


    /**
     *  returns the data block of a DATA_B3_IND or DATA_B3_REQ message. For
     *  other messages an exception is thrown. <p>
     *
     *  Actual the data block of a DATA_B3_IND is copied to prevent the Java
     *  virtual machine from memory conflicts. Nevertheless the application has
     *  to respond to every DATA_B3_IND with a DATA_B3_RESP. Otherwise the
     *  native CAPI does not reuse the original memory block and would perhaps
     *  get a "buffer overrun".<p>
     *
     *
     *
     *@return                    the data block
     *@exception  CapiException  Description of the Exception
     */
    public byte[] getB3Data() throws CapiException {
        if (type != DATA_B3_IND && type != DATA_B3_REQ) {
            throw new CapiException(MSG_JCM10);
        }
        return dataB3;
    }



    /**
     *  returns the identification number of this message.
     *
     *@return    the message identification number
     */
    public int getMessageID() {
        return number;
    }


    /**
     *  returns the type of this message.
     *
     *@return    the message type, which correspond with the CAPI message fields
     *      "command" and "subcommand"
     */
    public int getType() {
        return type;
    }


    /**
     *  assigns a data block to a DATA_B3_REQ message. For other messages an
     *  exception is thrown.
     *
     *@param  data               the data block
     *@exception  CapiException  Description of the Exception
     */
    public void setB3Data(byte[] data) throws CapiException {
        if (type != DATA_B3_IND && type != DATA_B3_REQ) {
            throw new CapiException(MSG_JCM11);
        }
        dataB3 = data;
    }


    /**
     *  Sets the appID attribute of the PIMCapiMessage object
     *
     *@param  id  The new appID value
     */
    public void setAppID(int id) {
        appID = id;
    }


    /**
     *  Sets the messageID attribute of the PIMCapiMessage object
     *
     *@param  id  The new messageID value
     */
    public void setMessageID(int id) {
        number = id;
    }


    /**
     *  Gets the value attribute of the PIMCapiMessage object
     *
     *@param  field  Description of the Parameter
     *@return        The value value
     */
    public Object getValue(String field) {
        int i = fields.indexOf(field.toLowerCase());
        if (i < 0) {
            return null;
        }
        return params.elementAt(i);
    }


    /**
     *  Description of the Method
     *
     *@param  field              Description of the Parameter
     *@return                    Description of the Return Value
     *@exception  CapiException  Description of the Exception
     */
    private int checkByteValue(String field) throws CapiException {
        int i = fields.indexOf(field.toLowerCase());
        if (i < 0 || types[i] != T_BYTE) {
            throw new CapiException(MSG_JCM03);
        }
        return i;
    }


    /**
     *  Description of the Method
     *
     *@param  field              Description of the Parameter
     *@return                    Description of the Return Value
     *@exception  CapiException  Description of the Exception
     */
    private int checkWordValue(String field) throws CapiException {
        int i = fields.indexOf(field.toLowerCase());
        if (i < 0 || types[i] != T_WORD) {
            throw new CapiException(MSG_JCM04);
        }
        return i;
    }


    /**
     *  Description of the Method
     *
     *@param  field              Description of the Parameter
     *@return                    Description of the Return Value
     *@exception  CapiException  Description of the Exception
     */
    private int checkDwordValue(String field) throws CapiException {
        int i = fields.indexOf(field.toLowerCase());
        if (i < 0 || types[i] != T_DWORD) {
            throw new CapiException(MSG_JCM05);
        }
        return i;
    }


    /**
     *  Description of the Method
     *
     *@param  field              Description of the Parameter
     *@return                    Description of the Return Value
     *@exception  CapiException  Description of the Exception
     */
    private int checkStructValue(String field) throws CapiException {
        int i = fields.indexOf(field.toLowerCase());
        if (i < 0 || types[i] != T_STRUCT) {
            throw new CapiException(MSG_JCM06);
        }
        return i;
    }


    /**
     *  Gets the byteValue attribute of the PIMCapiMessage object
     *
     *@param  field              Description of the Parameter
     *@return                    The byteValue value
     *@exception  CapiException  Description of the Exception
     */
    public byte getByteValue(String field) throws CapiException {
        int i = checkByteValue(field);
        return ((Byte) params.elementAt(i)).byteValue();
    }


    /**
     *  Gets the wordValue attribute of the PIMCapiMessage object
     *
     *@param  field              Description of the Parameter
     *@return                    The wordValue value
     *@exception  CapiException  Description of the Exception
     */
    public short getWordValue(String field) throws CapiException {
        int i = checkWordValue(field);
        return ((Short) params.elementAt(i)).shortValue();
    }


    /**
     *  Gets the dwordValue attribute of the PIMCapiMessage object
     *
     *@param  field              Description of the Parameter
     *@return                    The dwordValue value
     *@exception  CapiException  Description of the Exception
     */
    public int getDwordValue(String field) throws CapiException {
        int i = checkDwordValue(field);
        return ((Integer) params.elementAt(i)).intValue();
    }


    /**
     *  Gets the structValue attribute of the PIMCapiMessage object
     *
     *@param  field              Description of the Parameter
     *@return                    The structValue value
     *@exception  CapiException  Description of the Exception
     */
    public byte[] getStructValue(String field) throws CapiException {
        int i = checkStructValue(field);
        return (byte[]) params.elementAt(i);
    }


    /**
     *  Sets the value attribute of the PIMCapiMessage object
     *
     *@param  field              The new value value
     *@param  value              The new value value
     *@exception  CapiException  Description of the Exception
     */
    public void setValue(String field, Object value) throws CapiException {
        int i = fields.indexOf(field.toLowerCase());
        if (i < 0) {
            throw new CapiException(MSG_JCM07 + field);
        }
        setValueAt(i, value);
    }


    /**
     *  Sets the valueAt attribute of the PIMCapiMessage object
     *
     *@param  pos    The new valueAt value
     *@param  value  The new valueAt value
     */
    private void setValueAt(int pos, Object value) {
        switch (types[pos]) {
            case T_BYTE:
                params.setElementAt((Byte) value, pos);
                break;
            case T_WORD:
                params.setElementAt((Short) value, pos);
                break;
            case T_DWORD:
                params.setElementAt((Integer) value, pos);
                break;
            case T_STRUCT:
                params.setElementAt((byte[]) value, pos);
                break;
        }
    }


    /**
     *  Sets the byteValue attribute of the PIMCapiMessage object
     *
     *@param  field              The new byteValue value
     *@param  val                The new byteValue value
     *@exception  CapiException  Description of the Exception
     */
    public void setByteValue(String field, int val) throws CapiException {
        setValueAt(checkByteValue(field), new Byte((byte) (val & 0xff)));
    }


    /**
     *  Sets the wordValue attribute of the PIMCapiMessage object
     *
     *@param  field              The new wordValue value
     *@param  val                The new wordValue value
     *@exception  CapiException  Description of the Exception
     */
    public void setWordValue(String field, int val) throws CapiException {
        setValueAt(checkWordValue(field), new Short((short) (val & 0xffff)));
    }


    /**
     *  Sets the dwordValue attribute of the PIMCapiMessage object
     *
     *@param  field              The new dwordValue value
     *@param  val                The new dwordValue value
     *@exception  CapiException  Description of the Exception
     */
    public void setDwordValue(String field, int val) throws CapiException {
        setValueAt(checkDwordValue(field), new Integer(val));
    }


    /**
     *  Sets the structValue attribute of the PIMCapiMessage object
     *
     *@param  field              The new structValue value
     *@param  val                The new structValue value
     *@exception  CapiException  Description of the Exception
     */
    public void setStructValue(String field, byte[] val) throws CapiException {
        setValueAt(checkStructValue(field), val);
    }


    /**
     *  calculates the encoded message as a byte array.
     *
     *@return    the binary message
     */
    public byte[] getBytes() {
        int len = 0;
        for (int i = 0; i < types.length; i++) {
            switch (types[i]) {
                case T_BYTE:
                    len++;
                    break;
                case T_WORD:
                    len += 2;
                    break;
                case T_DWORD:
                    len += 4;
                    break;
                case T_STRUCT:
                    len += ((byte[]) params.elementAt(i)).length + 1;
                    break;
            }
        }
        byte[] ba = new byte[len + 8];
        // 8 bytes for header
        int pos = 8;
        for (int i = 0; i < types.length; i++) {
            switch (types[i]) {
                case T_BYTE:
                    ba[pos] = ((Byte) params.elementAt(i)).byteValue();
                    pos++;
                    break;
                case T_WORD:
                    int vw = ((Short) params.elementAt(i)).shortValue();
                    ByteArray.copyBytes(ByteArray.lowOrderBytes(vw, 2), ba, pos);
                    pos += 2;
                    break;
                case T_DWORD:
                    int vd = ((Integer) params.elementAt(i)).intValue();
                    ByteArray.copyBytes(ByteArray.lowOrderBytes(vd, 4), ba, pos);
                    pos += 4;
                    break;
                case T_STRUCT:
                    byte[] v = (byte[]) params.elementAt(i);
                    ba[pos++] = (byte) v.length;
                    ByteArray.copyBytes(v, ba, pos);
                    pos += v.length;
                    break;
            }
        }
        ByteArray.copyBytes(ByteArray.lowOrderBytes(ba.length, 2), ba, 0);
        ByteArray.copyBytes(ByteArray.lowOrderBytes(appID, 2), ba, 2);
        ByteArray.copyBytes(ByteArray.highOrderBytes(type, 2), ba, 4);
        ByteArray.copyBytes(ByteArray.lowOrderBytes(number, 2), ba, 6);
        return ba;
    }


    /**
     *  Description of the Method
     *
     *@param  message            Description of the Parameter
     *@exception  CapiException  Description of the Exception
     */
    private void init(byte[] message) throws CapiException {
        String[] fieldsArray = null;
        switch (type) {
            case ALERT_REQ:
                types = ALERT_REQ_TYPES;
                fieldsArray = ALERT_REQ_FIELDS;
                break;
            case ALERT_CONF:
                types = ALERT_CONF_TYPES;
                fieldsArray = ALERT_CONF_FIELDS;
                break;
            case CONNECT_REQ:
                types = CONNECT_REQ_TYPES;
                fieldsArray = CONNECT_REQ_FIELDS;
                break;
            case CONNECT_CONF:
                types = CONNECT_CONF_TYPES;
                fieldsArray = CONNECT_CONF_FIELDS;
                break;
            case CONNECT_IND:
                types = CONNECT_IND_TYPES;
                fieldsArray = CONNECT_IND_FIELDS;
                break;
            case CONNECT_RESP:
                types = CONNECT_RESP_TYPES;
                fieldsArray = CONNECT_RESP_FIELDS;
                break;
            case CONNECT_ACTIVE_IND:
                types = CONNECT_ACTIVE_IND_TYPES;
                fieldsArray = CONNECT_ACTIVE_IND_FIELDS;
                break;
            case CONNECT_ACTIVE_RESP:
                types = CONNECT_ACTIVE_RESP_TYPES;
                fieldsArray = CONNECT_ACTIVE_RESP_FIELDS;
                break;
            case CONNECT_B3_ACTIVE_IND:
                types = CONNECT_B3_ACTIVE_IND_TYPES;
                fieldsArray = CONNECT_B3_ACTIVE_IND_FIELDS;
                break;
            case CONNECT_B3_ACTIVE_RESP:
                types = CONNECT_B3_ACTIVE_RESP_TYPES;
                fieldsArray = CONNECT_B3_ACTIVE_RESP_FIELDS;
                break;
            case CONNECT_B3_REQ:
                types = CONNECT_B3_REQ_TYPES;
                fieldsArray = CONNECT_B3_REQ_FIELDS;
                break;
            case CONNECT_B3_CONF:
                types = CONNECT_B3_CONF_TYPES;
                fieldsArray = CONNECT_B3_CONF_FIELDS;
                break;
            case CONNECT_B3_IND:
                types = CONNECT_B3_IND_TYPES;
                fieldsArray = CONNECT_B3_IND_FIELDS;
                break;
            case CONNECT_B3_RESP:
                types = CONNECT_B3_RESP_TYPES;
                fieldsArray = CONNECT_B3_RESP_FIELDS;
                break;
            case DATA_B3_REQ:
                types = DATA_B3_REQ_TYPES;
                fieldsArray = DATA_B3_REQ_FIELDS;
                break;
            case DATA_B3_CONF:
                types = DATA_B3_CONF_TYPES;
                fieldsArray = DATA_B3_CONF_FIELDS;
                break;
            case DATA_B3_IND:
                types = DATA_B3_IND_TYPES;
                fieldsArray = DATA_B3_IND_FIELDS;
                break;
            case DATA_B3_RESP:
                types = DATA_B3_RESP_TYPES;
                fieldsArray = DATA_B3_RESP_FIELDS;
                break;
            case DISCONNECT_B3_REQ:
                types = DISCONNECT_B3_REQ_TYPES;
                fieldsArray = DISCONNECT_B3_REQ_FIELDS;
                break;
            case DISCONNECT_B3_CONF:
                types = DISCONNECT_B3_CONF_TYPES;
                fieldsArray = DISCONNECT_B3_CONF_FIELDS;
                break;
            case DISCONNECT_B3_IND:
                types = DISCONNECT_B3_IND_TYPES;
                fieldsArray = DISCONNECT_B3_IND_FIELDS;
                break;
            case DISCONNECT_B3_RESP:
                types = DISCONNECT_B3_RESP_TYPES;
                fieldsArray = DISCONNECT_B3_RESP_FIELDS;
                break;
            case DISCONNECT_REQ:
                types = DISCONNECT_REQ_TYPES;
                fieldsArray = DISCONNECT_REQ_FIELDS;
                break;
            case DISCONNECT_CONF:
                types = DISCONNECT_CONF_TYPES;
                fieldsArray = DISCONNECT_CONF_FIELDS;
                break;
            case DISCONNECT_IND:
                types = DISCONNECT_IND_TYPES;
                fieldsArray = DISCONNECT_IND_FIELDS;
                break;
            case DISCONNECT_RESP:
                types = DISCONNECT_RESP_TYPES;
                fieldsArray = DISCONNECT_RESP_FIELDS;
                break;
            case INFO_IND:
                types = INFO_IND_TYPES;
                fieldsArray = INFO_IND_FIELDS;
                break;
            case INFO_RESP:
                types = INFO_RESP_TYPES;
                fieldsArray = INFO_RESP_FIELDS;
                break;
            case LISTEN_REQ:
                types = LISTEN_REQ_TYPES;
                fieldsArray = LISTEN_REQ_FIELDS;
                break;
            case LISTEN_CONF:
                types = LISTEN_CONF_TYPES;
                fieldsArray = LISTEN_CONF_FIELDS;
                break;
			case FACILITY_CONF:
				types = FACILITY_CONF_TYPES;
				fieldsArray = FACILITY_CONF_FIELDS;
				break;
			case FACILITY_IND:
				types = FACILITY_IND_TYPES;
				fieldsArray = FACILITY_IND_FIELDS;
				break;
			case FACILITY_RESP:
				types = FACILITY_RESP_TYPES;
				fieldsArray = FACILITY_RESP_FIELDS;
				break;
            default:
                throw new CapiException(MSG_JCM08);
        }
        int cnt = fieldsArray.length;
        fields = new Vector(cnt);
        params = new Vector(cnt);
        int pos = 8;
        int len = message == null ? 0 : message.length;
        for (int i = 0; i < cnt; i++) {
            fields.add(fieldsArray[i].toLowerCase());
            switch (types[i]) {
                case T_BYTE:
                    byte b = (message == null || pos >= len) ? 0 : message[pos];
                    params.add(new Byte(b));
                    pos++;
                    break;
                case T_WORD:
                    short s = (message == null || pos >= len - 1) ? 0 : (short) ByteArray.getLowOrderInt(message, pos, 2);
                    params.add(new Short(s));
                    pos += 2;
                    break;
                case T_DWORD:
                    int l = (message == null || pos >= len - 3) ? 0 : ByteArray.getLowOrderInt(message, pos, 4);
                    params.add(new Integer(l));
                    pos += 4;
                    break;
                case T_STRUCT:
                    if (message == null || pos >= len) {
                        params.add(new byte[0]);
                    } else {
                        int sl = message[pos];
                        if (sl < 0) {
                            sl = 0x100 + sl;
                        }
                        if (pos + sl >= len) {
                            throw new CapiException(MSG_JCM09);
                        }
                        params.add(ByteArray.getBytes(message, pos + 1, sl));
                        pos += sl + 1;
                    }
                    break;
            }
        }
    }

}
