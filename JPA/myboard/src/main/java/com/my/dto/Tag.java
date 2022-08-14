package com.my.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tag")
@IdClass(TagPK.class)
public class Tag {
//    @Id
//    @Column(name="board_no")
//    private Long boardNo;

    @Id
    @Column(name = "board_no")
    private Long boardNo;

    @Id
    @ManyToOne
    @JoinColumn(name="tag_no")
    private TagInfo tagInfo;

}
