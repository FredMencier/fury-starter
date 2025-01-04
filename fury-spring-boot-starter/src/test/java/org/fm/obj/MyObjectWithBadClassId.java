package org.fm.obj;

import org.fm.fury.annotation.FuryObject;

import java.math.BigDecimal;

@FuryObject(classId = 100)
public class MyObjectWithBadClassId {

    private String aString;

    private Long aLong;

    private Integer aInteger;

    private BigDecimal aBigDecimal;

    public MyObjectWithBadClassId(String aString, Long aLong, Integer aInteger, BigDecimal aBigDecimal) {
        this.aString = aString;
        this.aLong = aLong;
        this.aInteger = aInteger;
        this.aBigDecimal = aBigDecimal;
    }

    public String getaString() {
        return aString;
    }

    public void setaString(String aString) {
        this.aString = aString;
    }

    public Long getaLong() {
        return aLong;
    }

    public void setaLong(Long aLong) {
        this.aLong = aLong;
    }

    public Integer getaInteger() {
        return aInteger;
    }

    public void setaInteger(Integer aInteger) {
        this.aInteger = aInteger;
    }

    public BigDecimal getaBigDecimal() {
        return aBigDecimal;
    }

    public void setaBigDecimal(BigDecimal aBigDecimal) {
        this.aBigDecimal = aBigDecimal;
    }
}
