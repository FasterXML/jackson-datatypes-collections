package com.fasterxml.jackson.datatype.guava.pojo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Marcin Kamionowski
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="_t")
@JsonSubTypes({
    @Type(name="add", value=AddOp.class), 
    @Type(name="mul", value=MulOp.class)})
public interface MathOp {

}
