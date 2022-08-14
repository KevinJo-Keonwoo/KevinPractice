package com.my.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@EqualsAndHashCode
public class TagPK implements Serializable {
    private Long boardNo;
    private TagInfo tagInfo;
}
