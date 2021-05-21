package com.vfts.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QuestionEntity class
 * @author Axl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionEntity {
    String uuid;
    int questionIndex;
    String answer;
}