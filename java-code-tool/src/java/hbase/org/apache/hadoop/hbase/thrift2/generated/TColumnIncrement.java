/**
 * Autogenerated by Thrift Compiler (0.8.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.apache.hadoop.hbase.thrift2.generated;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a single cell and the amount to increment it by
 */
public class TColumnIncrement implements org.apache.thrift.TBase<TColumnIncrement, TColumnIncrement._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TColumnIncrement");

  private static final org.apache.thrift.protocol.TField FAMILY_FIELD_DESC = new org.apache.thrift.protocol.TField("family", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField QUALIFIER_FIELD_DESC = new org.apache.thrift.protocol.TField("qualifier", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField AMOUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("amount", org.apache.thrift.protocol.TType.I64, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TColumnIncrementStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TColumnIncrementTupleSchemeFactory());
  }

  public ByteBuffer family; // required
  public ByteBuffer qualifier; // required
  public long amount; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    FAMILY((short)1, "family"),
    QUALIFIER((short)2, "qualifier"),
    AMOUNT((short)3, "amount");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // FAMILY
          return FAMILY;
        case 2: // QUALIFIER
          return QUALIFIER;
        case 3: // AMOUNT
          return AMOUNT;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __AMOUNT_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);
  private _Fields optionals[] = {_Fields.AMOUNT};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.FAMILY, new org.apache.thrift.meta_data.FieldMetaData("family", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    tmpMap.put(_Fields.QUALIFIER, new org.apache.thrift.meta_data.FieldMetaData("qualifier", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    tmpMap.put(_Fields.AMOUNT, new org.apache.thrift.meta_data.FieldMetaData("amount", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TColumnIncrement.class, metaDataMap);
  }

  public TColumnIncrement() {
    this.amount = 1L;

  }

  public TColumnIncrement(
    ByteBuffer family,
    ByteBuffer qualifier)
  {
    this();
    this.family = family;
    this.qualifier = qualifier;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TColumnIncrement(TColumnIncrement other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetFamily()) {
      this.family = org.apache.thrift.TBaseHelper.copyBinary(other.family);
;
    }
    if (other.isSetQualifier()) {
      this.qualifier = org.apache.thrift.TBaseHelper.copyBinary(other.qualifier);
;
    }
    this.amount = other.amount;
  }

  public TColumnIncrement deepCopy() {
    return new TColumnIncrement(this);
  }

  @Override
  public void clear() {
    this.family = null;
    this.qualifier = null;
    this.amount = 1L;

  }

  public byte[] getFamily() {
    setFamily(org.apache.thrift.TBaseHelper.rightSize(family));
    return family == null ? null : family.array();
  }

  public ByteBuffer bufferForFamily() {
    return family;
  }

  public TColumnIncrement setFamily(byte[] family) {
    setFamily(family == null ? (ByteBuffer)null : ByteBuffer.wrap(family));
    return this;
  }

  public TColumnIncrement setFamily(ByteBuffer family) {
    this.family = family;
    return this;
  }

  public void unsetFamily() {
    this.family = null;
  }

  /** Returns true if field family is set (has been assigned a value) and false otherwise */
  public boolean isSetFamily() {
    return this.family != null;
  }

  public void setFamilyIsSet(boolean value) {
    if (!value) {
      this.family = null;
    }
  }

  public byte[] getQualifier() {
    setQualifier(org.apache.thrift.TBaseHelper.rightSize(qualifier));
    return qualifier == null ? null : qualifier.array();
  }

  public ByteBuffer bufferForQualifier() {
    return qualifier;
  }

  public TColumnIncrement setQualifier(byte[] qualifier) {
    setQualifier(qualifier == null ? (ByteBuffer)null : ByteBuffer.wrap(qualifier));
    return this;
  }

  public TColumnIncrement setQualifier(ByteBuffer qualifier) {
    this.qualifier = qualifier;
    return this;
  }

  public void unsetQualifier() {
    this.qualifier = null;
  }

  /** Returns true if field qualifier is set (has been assigned a value) and false otherwise */
  public boolean isSetQualifier() {
    return this.qualifier != null;
  }

  public void setQualifierIsSet(boolean value) {
    if (!value) {
      this.qualifier = null;
    }
  }

  public long getAmount() {
    return this.amount;
  }

  public TColumnIncrement setAmount(long amount) {
    this.amount = amount;
    setAmountIsSet(true);
    return this;
  }

  public void unsetAmount() {
    __isset_bit_vector.clear(__AMOUNT_ISSET_ID);
  }

  /** Returns true if field amount is set (has been assigned a value) and false otherwise */
  public boolean isSetAmount() {
    return __isset_bit_vector.get(__AMOUNT_ISSET_ID);
  }

  public void setAmountIsSet(boolean value) {
    __isset_bit_vector.set(__AMOUNT_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case FAMILY:
      if (value == null) {
        unsetFamily();
      } else {
        setFamily((ByteBuffer)value);
      }
      break;

    case QUALIFIER:
      if (value == null) {
        unsetQualifier();
      } else {
        setQualifier((ByteBuffer)value);
      }
      break;

    case AMOUNT:
      if (value == null) {
        unsetAmount();
      } else {
        setAmount((Long)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case FAMILY:
      return getFamily();

    case QUALIFIER:
      return getQualifier();

    case AMOUNT:
      return Long.valueOf(getAmount());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case FAMILY:
      return isSetFamily();
    case QUALIFIER:
      return isSetQualifier();
    case AMOUNT:
      return isSetAmount();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TColumnIncrement)
      return this.equals((TColumnIncrement)that);
    return false;
  }

  public boolean equals(TColumnIncrement that) {
    if (that == null)
      return false;

    boolean this_present_family = true && this.isSetFamily();
    boolean that_present_family = true && that.isSetFamily();
    if (this_present_family || that_present_family) {
      if (!(this_present_family && that_present_family))
        return false;
      if (!this.family.equals(that.family))
        return false;
    }

    boolean this_present_qualifier = true && this.isSetQualifier();
    boolean that_present_qualifier = true && that.isSetQualifier();
    if (this_present_qualifier || that_present_qualifier) {
      if (!(this_present_qualifier && that_present_qualifier))
        return false;
      if (!this.qualifier.equals(that.qualifier))
        return false;
    }

    boolean this_present_amount = true && this.isSetAmount();
    boolean that_present_amount = true && that.isSetAmount();
    if (this_present_amount || that_present_amount) {
      if (!(this_present_amount && that_present_amount))
        return false;
      if (this.amount != that.amount)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(TColumnIncrement other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    TColumnIncrement typedOther = (TColumnIncrement)other;

    lastComparison = Boolean.valueOf(isSetFamily()).compareTo(typedOther.isSetFamily());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFamily()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.family, typedOther.family);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetQualifier()).compareTo(typedOther.isSetQualifier());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetQualifier()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.qualifier, typedOther.qualifier);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAmount()).compareTo(typedOther.isSetAmount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAmount()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.amount, typedOther.amount);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TColumnIncrement(");
    boolean first = true;

    sb.append("family:");
    if (this.family == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.family, sb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("qualifier:");
    if (this.qualifier == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.qualifier, sb);
    }
    first = false;
    if (isSetAmount()) {
      if (!first) sb.append(", ");
      sb.append("amount:");
      sb.append(this.amount);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (family == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'family' was not present! Struct: " + toString());
    }
    if (qualifier == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'qualifier' was not present! Struct: " + toString());
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bit_vector = new BitSet(1);
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TColumnIncrementStandardSchemeFactory implements SchemeFactory {
    public TColumnIncrementStandardScheme getScheme() {
      return new TColumnIncrementStandardScheme();
    }
  }

  private static class TColumnIncrementStandardScheme extends StandardScheme<TColumnIncrement> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TColumnIncrement struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // FAMILY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.family = iprot.readBinary();
              struct.setFamilyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // QUALIFIER
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.qualifier = iprot.readBinary();
              struct.setQualifierIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // AMOUNT
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.amount = iprot.readI64();
              struct.setAmountIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TColumnIncrement struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.family != null) {
        oprot.writeFieldBegin(FAMILY_FIELD_DESC);
        oprot.writeBinary(struct.family);
        oprot.writeFieldEnd();
      }
      if (struct.qualifier != null) {
        oprot.writeFieldBegin(QUALIFIER_FIELD_DESC);
        oprot.writeBinary(struct.qualifier);
        oprot.writeFieldEnd();
      }
      if (struct.isSetAmount()) {
        oprot.writeFieldBegin(AMOUNT_FIELD_DESC);
        oprot.writeI64(struct.amount);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TColumnIncrementTupleSchemeFactory implements SchemeFactory {
    public TColumnIncrementTupleScheme getScheme() {
      return new TColumnIncrementTupleScheme();
    }
  }

  private static class TColumnIncrementTupleScheme extends TupleScheme<TColumnIncrement> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TColumnIncrement struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeBinary(struct.family);
      oprot.writeBinary(struct.qualifier);
      BitSet optionals = new BitSet();
      if (struct.isSetAmount()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetAmount()) {
        oprot.writeI64(struct.amount);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TColumnIncrement struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.family = iprot.readBinary();
      struct.setFamilyIsSet(true);
      struct.qualifier = iprot.readBinary();
      struct.setQualifierIsSet(true);
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.amount = iprot.readI64();
        struct.setAmountIsSet(true);
      }
    }
  }

}

