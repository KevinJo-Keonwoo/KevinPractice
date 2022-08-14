package com.my.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter


@Entity
@Table(name = "tag_info")
@SequenceGenerator(name = "taginfo_seq_generator", //밑의 GeneratedValue에서 찾아올 이름
        sequenceName = "taginfo_seq", //오라클에 생성될 시퀀스 이름
        initialValue = 1,
        allocationSize = 1)
public class TagInfo {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taginfo_seq_generator" )
    @Id
    @Column(name="tag_no")
    private Long tagNo;

    @Column(name="tag_name")
    private String tagName;

    public TagInfo(BigDecimal bd, String tagName){
        this.tagNo = bd.longValue();
        this.tagName = tagName;
    }
    public void setTagNo(BigDecimal bd){
        tagNo = bd.longValue();
    }
//    public BigDecimal getTagNo(){
//        return new BigDecimal(tagNo);
//    }
}
